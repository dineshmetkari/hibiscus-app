package com.googlecode.hibiscusapp.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.googlecode.hibiscusapp.R;
import com.googlecode.hibiscusapp.database.dao.AccountDao;
import com.googlecode.hibiscusapp.model.AccountOverview;
import com.googlecode.hibiscusapp.util.UiUtil;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Package: com.googlecode.hibiscusapp.fragment
 * Date: 09/09/13
 * Time: 22:10
 *
 * @author eike
 */
public class OverviewFragment extends Fragment
{
    private OnAccountSelectedCallback accountSelectedCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.overview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        updateAccountListAndSummary();
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        // make sure, that the containing activity implements the callback interface
        try {
            accountSelectedCallback = (OnAccountSelectedCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement OnAccountSelectedCallback");
        }
    }

    private void updateAccountListAndSummary()
    {
        View view = getView();

        // render the overview data
        AccountDao accountDao = new AccountDao(getActivity());
        List<AccountOverview> accountOverviews = accountDao.getAccountOverviews();

        ListView accountList = (ListView) view.findViewById(R.id.overview_accounts_list);
        accountList.setAdapter(new AccountOverviewArrayAdapter(getActivity(), accountOverviews));

        // update the summary
        double balance = 0.0;
        double transactionBalance = 0.0;
        for (AccountOverview item : accountOverviews) {
            balance += item.getAccount().getBalance();
            transactionBalance += item.getTransactionBalance();
        }

        TextView balanceSummary = (TextView) view.findViewById(R.id.overview_account_summary_balance);
        UiUtil.setCurrencyValueAndTextColor(getActivity(), balanceSummary, balance);

        TextView transactionSummary = (TextView) view.findViewById(R.id.overview_account_summary_transaction_balance);
        UiUtil.setCurrencyValueAndTextColor(getActivity(), transactionSummary, transactionBalance);

        // update the month name
        String[] months = getResources().getStringArray(R.array.months);
        TextView monthTextView = (TextView) view.findViewById(R.id.overview_summary_month);
        Calendar cal = new GregorianCalendar();
        int currentMonth = cal.get(Calendar.MONTH);
        monthTextView.setText(months[currentMonth]);
    }

    private class AccountOverviewArrayAdapter extends ArrayAdapter<AccountOverview>
    {
        private AccountOverviewArrayAdapter(Context context, List<AccountOverview> objects)
        {
            super(context, R.layout.overview_account, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.overview_account, null, false);

            final AccountOverview item = getItem(position);

            // set the account number
            TextView accountNumber = (TextView) rowView.findViewById(R.id.overview_account_number);
            accountNumber.setText(item.getAccount().getAccountNumber());

            // set the balance date
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
            TextView accountDate = (TextView) rowView.findViewById(R.id.overview_account_date);
            accountDate.setText(dateFormat.format(item.getAccount().getBalanceDate()));

            // set the balance
            TextView balance = (TextView) rowView.findViewById(R.id.overview_account_balance);
            UiUtil.setCurrencyValueAndTextColor(getContext(), balance, item.getAccount().getBalance());

            // set the transaction balance
            TextView transactionBalance = (TextView) rowView.findViewById(R.id.overview_account_transaction_balance);
            UiUtil.setCurrencyValueAndTextColor(getContext(), transactionBalance, item.getTransactionBalance());
            TextView transactionReceipts = (TextView) rowView.findViewById(R.id.overview_account_receipts);
            UiUtil.setCurrencyValueAndTextColor(getContext(), transactionReceipts, item.getReceipts());
            TextView transactionExpenses = (TextView) rowView.findViewById(R.id.overview_account_expenses);
            UiUtil.setCurrencyValueAndTextColor(getContext(), transactionExpenses, item.getExpenses());

            // set the onclicklistener on the account transactions button
            LinearLayout accountTransactions = (LinearLayout) rowView.findViewById(R.id.overview_account_transactions);
            accountTransactions.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // show the transactions fragment and preselect the current account
                    int accountId = item.getAccount().getId();
                    accountSelectedCallback.accountTransactionsSelected(accountId);
                }
            });

            return rowView;

        }
    }

    public static interface OnAccountSelectedCallback
    {
        /**
         * This method is called when presses the "show account transactions" button.
         *
         * @param accountId
         */
        public void accountTransactionsSelected(int accountId);
    }
}
