package com.googlecode.hibiscusapp.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import com.googlecode.hibiscusapp.R;
import com.googlecode.hibiscusapp.database.AccountProvider;
import com.googlecode.hibiscusapp.database.AccountTable;
import com.googlecode.hibiscusapp.fragment.ActivitiesFragment;
import com.googlecode.hibiscusapp.fragment.OverviewFragment;
import com.googlecode.hibiscusapp.fragment.StatisticsFragment;
import com.googlecode.hibiscusapp.menu.DrawerItem;
import com.googlecode.hibiscusapp.menu.DrawerItemAdapter;
import com.googlecode.hibiscusapp.util.Constants;

/**
 * Package: com.googlecode.hibiscusapp.activity
 * Date: 09/09/13
 * Time: 21:43
 *
 * @author eike
 */
public class MainActivity extends ActionBarActivity
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
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open,R.string.drawer_close) {
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
        menuItemAdapter.add(new DrawerItem(Constants.MENU_ITEM_ACTIVITIES, R.drawable.ic_menu_activities, R.string.menu_item_activities));
        menuItemAdapter.add(new DrawerItem(Constants.MENU_ITEM_STATISTICS, R.drawable.ic_action_statistics, R.string.menu_item_statistics));
        menuItemAdapter.add(new DrawerItem(Constants.MENU_ITEM_SETTINGS, R.drawable.ic_menu_settings, R.string.menu_item_settings));

        // create account items
        accountList = (ListView) findViewById(R.id.drawer_accounts_list);
        accountList.setOnItemClickListener(new AccountItemClickListener());
        accountItemAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.account_item, null,
            new String[] {AccountTable.COLUMN_ACCOUNT_NUMBER}, new int[] {R.id.account_item_title}, 0);
        accountList.setAdapter(accountItemAdapter);


        getLoaderManager().initLoader(0, null, new AccountLoaderCallback());

        if (savedInstanceState == null) {
            // set the overview fragment as default
            selectItem(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerContainer);
        menu.findItem(R.id.action_refresh).setVisible(!drawerOpen);

        return super.onPrepareOptionsMenu(menu);
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
            //AccountItem item = accountItemAdapter.getItem(position);
            //Toast.makeText(getApplicationContext(), item.getAccount().getAccountNumber() + " wurde gedrückt!", Toast.LENGTH_LONG).show();
            //selectItem(position);
        }
    }

    private class AccountLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor>
    {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args)
        {
            return new CursorLoader(MainActivity.this, AccountProvider.CONTENT_URI, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
        {
            // set the new cursor in the list adapter
            accountItemAdapter.swapCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader)
        {
            // reset the cursor of the list adapter
            accountItemAdapter.swapCursor(null);
        }
    }

    private void selectItem(int position) {
        DrawerItem item = menuItemAdapter.getItem(position);

        Fragment fragment = null;
        switch (item.getId()) {
            case Constants.MENU_ITEM_STATISTICS:
                fragment = new StatisticsFragment();
                break;
            case Constants.MENU_ITEM_ACTIVITIES:
                fragment = new ActivitiesFragment();
                break;
            case Constants.MENU_ITEM_OVERVIEW:
                fragment = new OverviewFragment();
                break;

            case Constants.MENU_ITEM_SETTINGS:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return;
        }

        // update the main content by replacing the content frame
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        menuList.setItemChecked(position, true);
        setTitle(getString(item.getTitleRes()));
        drawerLayout.closeDrawer(drawerContainer);
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
}