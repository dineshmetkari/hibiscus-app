package com.googlecode.hibiscusapp.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import com.googlecode.hibiscusapp.R;
import com.googlecode.hibiscusapp.database.AccountProvider;
import com.googlecode.hibiscusapp.database.AccountTable;

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
        return inflater.inflate(R.layout.transactions, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        // init the account loader
        getLoaderManager().initLoader(0, null, new AccountLoaderCallback());

        Spinner spinner = (Spinner) view.findViewById(R.id.transactions_accounts_spinner);

        accountItemAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_item, null,
            new String[] {AccountTable.COLUMN_ACCOUNT_NUMBER}, new int[] {android.R.id.text1}, 0);
        accountItemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(accountItemAdapter);
        spinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        Cursor item = (Cursor) accountItemAdapter.getItem(position);
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
