package com.googlecode.hibiscusapp.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.googlecode.hibiscusapp.R;
import com.googlecode.hibiscusapp.database.AccountTable;
import com.googlecode.hibiscusapp.fragment.OverviewFragment;
import com.googlecode.hibiscusapp.fragment.StatisticsFragment;
import com.googlecode.hibiscusapp.fragment.TransactionDetailsFragment;
import com.googlecode.hibiscusapp.fragment.TransactionsFragment;
import com.googlecode.hibiscusapp.loader.AccountLoaderCallback;
import com.googlecode.hibiscusapp.menu.DrawerItem;
import com.googlecode.hibiscusapp.menu.DrawerItemAdapter;
import com.googlecode.hibiscusapp.services.SynchronizationService;
import com.googlecode.hibiscusapp.util.Constants;

/**
 * Package: com.googlecode.hibiscusapp.activity
 * Date: 09/09/13
 * Time: 21:43
 *
 * @author eike
 */
public class MainActivity extends ActionBarActivity implements
    TransactionsFragment.OnTransactionSelectedCallback,
    OverviewFragment.OnAccountSelectedCallback

{
    private CharSequence appTitle;
    private CharSequence fragmentTitle;

    private ListView menuList;
    private DrawerItemAdapter menuItemAdapter;

    private ListView accountList;
    private SimpleCursorAdapter accountItemAdapter;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private View drawerContainer;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appTitle = fragmentTitle = getTitle();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerContainer = findViewById(R.id.drawer_container);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_navigation_drawer, R.string.drawer_open,R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(fragmentTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(appTitle);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // create the menu items
        menuList = (ListView) findViewById(R.id.drawer_menu_list);
        menuList.setOnItemClickListener(new DrawerItemClickListener());
        menuItemAdapter = new DrawerItemAdapter(getApplicationContext());
        menuList.setAdapter(menuItemAdapter);
        menuItemAdapter.add(new DrawerItem(Constants.MENU_ITEM_OVERVIEW, R.drawable.ic_menu_overview, R.string.menu_item_overview));
        menuItemAdapter.add(new DrawerItem(Constants.MENU_ITEM_TRANSACTIONS, R.drawable.ic_menu_activities, R.string.menu_item_activities));
        menuItemAdapter.add(new DrawerItem(Constants.MENU_ITEM_STATISTICS, R.drawable.ic_action_statistics, R.string.menu_item_statistics));
        menuItemAdapter.add(new DrawerItem(Constants.MENU_ITEM_SETTINGS, R.drawable.ic_menu_settings, R.string.menu_item_settings));

        // create account items
        accountList = (ListView) findViewById(R.id.drawer_accounts_list);
        accountList.setOnItemClickListener(new AccountItemClickListener());
        accountItemAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.menu_account_item, null,
            new String[] {AccountTable.COLUMN_ACCOUNT_NUMBER}, new int[] {R.id.account_item_title}, 0);
        accountList.setAdapter(accountItemAdapter);

        // init the account loader
        getLoaderManager().initLoader(0, null, new AccountLoaderCallback(this, accountItemAdapter));

        if (savedInstanceState == null) {
            // set the overview fragment as default
            selectItem(0);
        }

        // load the sync settings from the shared preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean syncActive = sharedPref.getBoolean(getString(R.string.pref_sync_active_key), true);
        if (syncActive) {
            SynchronizationService.startService(this);
        }
    }

    /**
     * This method shows the transactions of the selected account.
     *
     * @param accountId the account id that the user selected
     */
    @Override
    public void accountTransactionsSelected(int accountId)
    {
        TransactionsFragment fragment = new TransactionsFragment();

        // set the account id as a parameter
        Bundle arguments = new Bundle();
        arguments.putInt(TransactionsFragment.PARAMETER_ACCOUNT_ID, accountId);
        fragment.setArguments(arguments);

        switchToFragment(fragment, R.string.menu_item_activities);
    }

    /**
     * This method is called when the user selects an account transaction.
     * It shows the details of the selected transaction.
     *
     * @param transactionId the transaction id that the user selected
     */
    @Override
    public void onTransactionSelected(int transactionId)
    {
        TransactionDetailsFragment fragment = new TransactionDetailsFragment();

        // set the transaction id as a parameter
        Bundle arguments = new Bundle();
        arguments.putInt(TransactionDetailsFragment.PARAMETER_TRANSACTION_ID, transactionId);
        fragment.setArguments(arguments);

        switchToFragment(fragment, R.string.title_transaction_details);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.action_refresh:
                Toast.makeText(this, "REFRESHING !!!", Toast.LENGTH_LONG).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class AccountItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO: Intent auf Detailansicht des Kontos
            Cursor item = (Cursor) accountItemAdapter.getItem(position);
            Toast.makeText(getApplicationContext(), item.getString(item.getColumnIndexOrThrow(AccountTable.COLUMN_ACCOUNT_NUMBER)) + " wurde gedrückt!", Toast.LENGTH_LONG).show();
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        DrawerItem item = menuItemAdapter.getItem(position);

        Fragment fragment = null;
        switch (item.getId()) {
            case Constants.MENU_ITEM_STATISTICS:
                fragment = new StatisticsFragment();
                break;
            case Constants.MENU_ITEM_TRANSACTIONS:
                fragment = new TransactionsFragment();
                break;
            case Constants.MENU_ITEM_OVERVIEW:
                fragment = new OverviewFragment();
                break;

            case Constants.MENU_ITEM_SETTINGS:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return;
        }

        // switch to the new fragment
        switchToFragment(fragment, item.getTitleRes());

        // update selected item and title, then close the drawer
        menuList.setItemChecked(position, true);
        drawerLayout.closeDrawer(drawerContainer);
    }

    /**
     * This method handles the transition to another fragment.
     * The new fragment will be loaded and the title of the actionbar will be updated.
     *
     * @param fragment the fragment instance
     * @param titleRes the title ressource identifiert
     */
    private void switchToFragment(Fragment fragment, int titleRes)
    {
        // update the main content by replacing the content frame
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        // update the action bar title
        setTitle(getString(titleRes));
    }

    @Override
    public void setTitle(CharSequence title) {
        fragmentTitle = title;
        getActionBar().setTitle(fragmentTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Pass any configuration change to the drawer toggls
        drawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * This method fixes an issue with the FragmentActivity, so that the back button
     * works with the FragmentManager back stack.
     *
     * {@see http://stackoverflow.com/questions/13418436/android-4-2-back-stack-behaviour-with-nested-fragments/14030872#14030872}
     */
    @Override
    public void onBackPressed()
    {
        // If the fragment exists and has some back-stack entry
        if (getFragmentManager().getBackStackEntryCount() > 0){
            // Get the fragment fragment manager - and pop the backstack
            getFragmentManager().popBackStack();
        } else {
            // Let super handle the back press
            super.onBackPressed();
        }
    }

}