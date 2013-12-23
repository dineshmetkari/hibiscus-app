package com.googlecode.hibiscusapp.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.googlecode.hibiscusapp.R;
import com.googlecode.hibiscusapp.database.AccountTable;
import com.googlecode.hibiscusapp.database.dao.AccountTransactionDao;
import com.googlecode.hibiscusapp.model.AccountTransaction;
import com.googlecode.hibiscusapp.model.MonthlyTransactionBalance;
import com.googlecode.hibiscusapp.util.UiUtil;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

        List<MonthlyTransactionBalance> transactionBalances = accountTransactionDao.getMonthlyTransactionBalances(0);

        ArrayAdapter<MonthlyTransactionBalance> adapter = new MonthyTransactionBalancesAdapter(getActivity(), transactionBalances);
        ListView listView = (ListView) view.findViewById(R.id.transactions_monthly_list);
        listView.setAdapter(adapter);
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

    private class MonthyTransactionBalancesAdapter extends ArrayAdapter<MonthlyTransactionBalance>
    {
        private String[] monthNames;

        private MonthyTransactionBalancesAdapter(Context context, List<MonthlyTransactionBalance> objects)
        {
            super(context, R.layout.transactions_monthly_item, objects);

            monthNames = context.getResources().getStringArray(R.array.months);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.transactions_monthly_item, parent, false);

            final MonthlyTransactionBalance item = getItem(position);

            // set the month name and year
            TextView monthView = (TextView) rowView.findViewById(R.id.transactions_monthly_item_month);
            final Date monthDate = item.getMonth();
            Calendar calendar = new GregorianCalendar(monthDate.getYear() + 1900, monthDate.getMonth(), 1);
            monthView.setText(monthNames[calendar.get(Calendar.MONTH)] + " " + calendar.get(Calendar.YEAR));

            // set the transaction balance
            TextView balanceView = (TextView) rowView.findViewById(R.id.transactions_monthly_item_balance);
            UiUtil.setCurrencyValueAndTextColor(getContext(), balanceView, item.getTransactionBalance());

            // add the onclick listener, that shows the transactions of the selected month
            LinearLayout firstRow = (LinearLayout) rowView.findViewById(R.id.transactions_list_trigger);
            final LinearLayout transactionsList = (LinearLayout) rowView.findViewById(R.id.transactions_list);
            firstRow.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (transactionsList.getVisibility() == View.VISIBLE) {
                        transactionsList.removeAllViews();
                        transactionsList.setVisibility(View.GONE);
                    } else {
                        // get the account transactions of the selected month
                        Calendar cal = new GregorianCalendar(monthDate.getYear() + 1900, monthDate.getMonth(), 1, 0, 0, 0);
                        long tsFrom = cal.getTimeInMillis() / 1000;
                        cal.add(Calendar.MONTH, 1);
                        long tsTo = cal.getTimeInMillis() / 1000;

                        AccountTransactionDao dao = new AccountTransactionDao(getContext());
                        List<AccountTransaction> transactions = dao.getAccountTransactions(0, tsFrom, tsTo);

                        for (AccountTransaction transaction : transactions) {
                            View itemView = createAccountTransactionItemView(transaction);
                            transactionsList.addView(itemView);
                        }

                        transactionsList.setVisibility(View.VISIBLE);
                    }
                }
            });

            return rowView;
        }
    }
}