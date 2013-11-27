package com.googlecode.hibiscusapp.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.googlecode.hibiscusapp.R;
import com.googlecode.hibiscusapp.database.AccountProvider;
import com.googlecode.hibiscusapp.database.AccountTable;
import com.googlecode.hibiscusapp.database.dao.AccountTransactionDao;
import com.googlecode.hibiscusapp.model.AccountTransaction;
import com.googlecode.hibiscusapp.util.UiUtil;

import java.text.DateFormat;
import java.util.List;

/**
 * Package: com.googlecode.hibiscusapp.fragment
 * Date: 10/09/13
 * Time: 23:48
 *
 * @author eike
 */
public class TransactionsFragment extends Fragment implements AdapterView.OnItemSelectedListener
{
    private SimpleCursorAdapter accountItemAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.transactions, container, false);

        // init the transactions
        AccountTransactionDao accountTransactionDao = new AccountTransactionDao(getActivity());
        List<AccountTransaction> transactions = accountTransactionDao.getAccountTransactions(2, 0, Long.MAX_VALUE);
        if (transactions.size() > 25) {
            transactions = transactions.subList(0, 25);
        }

        LinearLayout listView = (LinearLayout) view.findViewById(R.id.transactions_list);

        for (AccountTransaction item : transactions) {
            View itemView = createAccountTransactionItemView(item, container);

            listView.addView(itemView);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        // init the account loader
        getLoaderManager().initLoader(0, null, new AccountLoaderCallback());

        Spinner spinner = (Spinner) view.findViewById(R.id.transactions_accounts_spinner);

        accountItemAdapter = new SimpleCursorAdapter(
            getActivity(),
            android.R.layout.simple_spinner_item,
            null,
            new String[]{AccountTable.COLUMN_ACCOUNT_NUMBER},
            new int[]{android.R.id.text1},
            0
        );
        accountItemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(accountItemAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    private View createAccountTransactionItemView(AccountTransaction item, ViewGroup container)
    {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.transaction_item, container, false);

        // format the date
        TextView transactionDate = (TextView) rowView.findViewById(R.id.transaction_item_date);
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());
        transactionDate.setText(dateFormat.format(item.getDate()));

        // set the transaction reference
        TextView transactionReference = (TextView) rowView.findViewById(R.id.transaction_item_reference);
        transactionReference.setText(item.getReference());

        // set the transaction value
        TextView transactionValue = (TextView) rowView.findViewById(R.id.transaction_item_value);
        UiUtil.setCurrencyValueAndTextColor(getActivity(), transactionValue, item.getValue());

        return rowView;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        Cursor item = (Cursor)accountItemAdapter.getItem(position);
        Log.d("DEV", item.getString(item.getColumnIndex(AccountTable.COLUMN_ACCOUNT_NUMBER)));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
        Log.d("DEV", "nothing selected!!!!");
    }

    private class AccountLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor>
    {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args)
        {
            return new CursorLoader(TransactionsFragment.this.getActivity(), AccountProvider.CONTENT_URI, null, null, null, null);
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
}
