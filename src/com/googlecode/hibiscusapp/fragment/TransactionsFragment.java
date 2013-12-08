package com.googlecode.hibiscusapp.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.googlecode.hibiscusapp.R;
import com.googlecode.hibiscusapp.database.AccountTable;
import com.googlecode.hibiscusapp.database.dao.AccountTransactionDao;
import com.googlecode.hibiscusapp.model.AccountTransaction;
import com.googlecode.hibiscusapp.util.Constants;
import com.googlecode.hibiscusapp.util.UiUtil;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Package: com.googlecode.hibiscusapp.fragment
 * Date: 10/09/13
 * Time: 23:48
 *
 * @author eike
 */
public class TransactionsFragment extends Fragment implements ActionBar.OnNavigationListener
{
    public static final String PARAMETER_ACCOUNT_ID = "account_id";

    private SimpleCursorAdapter accountItemAdapter;

    private OnTransactionSelectedCallback transactionSelectedCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.transactions, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        // init the transactions
        AccountTransactionDao accountTransactionDao = new AccountTransactionDao(getActivity());
        List<AccountTransaction> transactions = accountTransactionDao.getAccountTransactions(2, 0, Long.MAX_VALUE);
        if (transactions.size() > 25) {
            transactions = transactions.subList(0, 25);
        }

        LinearLayout transactionList = (LinearLayout) view.findViewById(R.id.transactions_list);

        for (AccountTransaction item : transactions) {
            View itemView = createAccountTransactionItemView(item);
            transactionList.addView(itemView);
        }

        //
        long[] minMax = accountTransactionDao.getMinMaxTimestamp();
        Log.d(Constants.LOG_TAG, Arrays.toString(minMax));
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        // make sure, that the containing activity implements the callback interface
        try {
            transactionSelectedCallback = (OnTransactionSelectedCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement OnTransactionSelectedCallback");
        }
    }

    private View createAccountTransactionItemView(final AccountTransaction item)
    {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.transaction_item, null, false);

        // format the date
        TextView transactionDate = (TextView) view.findViewById(R.id.transaction_item_date);
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());
        transactionDate.setText(dateFormat.format(item.getDate()));

        // set the transaction type
        TextView transactionType = (TextView) view.findViewById(R.id.transaction_item_type);
        transactionType.setText(item.getTransactionType());

        // set the transaction reference
        TextView transactionReference = (TextView) view.findViewById(R.id.transaction_item_reference);
        transactionReference.setText(item.getReference());

        // set the transaction value
        TextView transactionValue = (TextView) view.findViewById(R.id.transaction_item_value);
        UiUtil.setCurrencyValueAndTextColor(getActivity(), transactionValue, item.getValue());

        // add the onclicklistener, that calls the transaction selected callback method
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                transactionSelectedCallback.onTransactionSelected(item.getId());
            }
        });

        return view;
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId)
    {
        Cursor item = (Cursor)accountItemAdapter.getItem(itemPosition);
        Toast.makeText(getActivity(), item.getString(item.getColumnIndex(AccountTable.COLUMN_ACCOUNT_NUMBER)), Toast.LENGTH_SHORT).show();

        return true;
    }

    public static interface OnTransactionSelectedCallback
    {
        public void onTransactionSelected(int transactionId);
    }
}