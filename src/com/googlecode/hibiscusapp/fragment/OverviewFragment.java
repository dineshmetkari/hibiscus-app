package com.googlecode.hibiscusapp.fragment;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.googlecode.hibiscusapp.R;
import com.googlecode.hibiscusapp.database.AccountTable;
import com.googlecode.hibiscusapp.loader.AccountLoaderCallback;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Package: com.googlecode.hibiscusapp.fragment
 * Date: 09/09/13
 * Time: 22:10
 *
 * @author eike
 */
public class OverviewFragment extends Fragment
{
    private ListView accountList;
    private SimpleCursorAdapter accountItemAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        // inflate the overview layout
        View view = inflater.inflate(R.layout.overview, container, false);

        // get the account list
        accountList = (ListView) view.findViewById(R.id.overview_accounts_list);

        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // initialize the account adapter, that displays the list of accounts
        accountItemAdapter = new SimpleCursorAdapter(
            getActivity(),
            R.layout.overview_account,
            null,
            new String[] {AccountTable.COLUMN_ACCOUNT_NUMBER, AccountTable.COLUMN_BALANCE_DATE, AccountTable.COLUMN_BALANCE},
            new int[] {R.id.overview_account_number, R.id.overview_account_date, R.id.overview_account_balance},
            0
        );
        accountItemAdapter.setViewBinder(new OverviewAccountsViewBinder());
        accountList.setAdapter(accountItemAdapter);

        // initialize the accountloader
        getLoaderManager().initLoader(0, null, new AccountLoaderCallback(getActivity(), accountItemAdapter));
    }

    /**
     * This ViewBinder implementation binds some values to the overview_account view
     * that are not directly supported by the SimpleCursorAdapter.
     */
    private class OverviewAccountsViewBinder implements SimpleCursorAdapter.ViewBinder
    {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int column)
        {
            if (column == cursor.getColumnIndexOrThrow(AccountTable.COLUMN_BALANCE_DATE)) {
                // set the date when the balance occured
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                Date date = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(AccountTable.COLUMN_BALANCE_DATE)) * 1000);
                ((TextView) view).setText(sdf.format(date));

                return true;
            }

            if (column == cursor.getColumnIndexOrThrow(AccountTable.COLUMN_BALANCE)) {
                // format the balance, according to locale
                DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                dfs.setDecimalSeparator(',');
                dfs.setGroupingSeparator('.');
                DecimalFormat df = new DecimalFormat();
                df.setDecimalFormatSymbols(dfs);

                double balance = cursor.getDouble(cursor.getColumnIndexOrThrow(AccountTable.COLUMN_BALANCE));
                String balanceString = df.format(balance) + " Euro";
                TextView textView = (TextView) view;
                textView.setText(balanceString);

                // change the color of the text. Green if the balance if positive, red if negative, black otherwise
                int textColor = 0;
                if (balance > 0) {
                    textColor =  R.color.balance_positive;
                } else if (balance < 0) {
                    textColor =  R.color.balance_negative;
                } else {
                    textColor = R.color.balance_neutral;
                }

                textView.setTextColor(OverviewFragment.this.getResources().getColor(textColor));

                return true;
            }

            return false;
        }
    }
}
