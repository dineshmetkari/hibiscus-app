package com.googlecode.hibiscusapp.fragment;

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

import java.text.DateFormat;
import java.text.NumberFormat;
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
    private ListView accountList;
    private AccountOverviewAdapter accountOverviewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        // inflate the overview layout
        View view = inflater.inflate(R.layout.overview, container, false);

        // get the account list
        accountList = (ListView)view.findViewById(R.id.overview_accounts_list);

        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // get the overview data
        AccountDao accountDao = new AccountDao(getActivity());
        List<AccountOverview> accountOverviews = accountDao.getAccountOverviews();

        accountOverviewAdapter = new AccountOverviewAdapter(getActivity(), accountOverviews);
        accountList.setAdapter(accountOverviewAdapter);
    }

    private class AccountOverviewAdapter extends ArrayAdapter<AccountOverview>
    {
        private AccountOverviewAdapter(Context context, List<AccountOverview> items)
        {
            super(context, R.layout.overview_account, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.overview_account, parent, false);

            final AccountOverview accountOverview = getItem(position);

            // set the account number
            TextView accountNumber = (TextView) rowView.findViewById(R.id.overview_account_number);
            accountNumber.setText(accountOverview.getAccount().getAccountNumber());

            // set the balance date
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
            TextView accountDate = (TextView) rowView.findViewById(R.id.overview_account_date);
            accountDate.setText(dateFormat.format(accountOverview.getAccount().getBalanceDate()));

            // set the balance
            TextView balance = (TextView) rowView.findViewById(R.id.overview_account_balance);
            setCurrencyValueAndTextColor(balance, accountOverview.getAccount().getBalance());

            // set the transaction balance
            TextView transactionBalance = (TextView) rowView.findViewById(R.id.overview_account_transaction_balance);
            setCurrencyValueAndTextColor(transactionBalance, accountOverview.getTransactionBalance());
            TextView transactionReceipts = (TextView) rowView.findViewById(R.id.overview_account_receipts);
            setCurrencyValueAndTextColor(transactionReceipts, accountOverview.getReceipts());
            TextView transactionExpenses = (TextView) rowView.findViewById(R.id.overview_account_expenses);
            setCurrencyValueAndTextColor(transactionExpenses, accountOverview.getExpenses());

            // set the onclicklistener on the account transactions button
            LinearLayout accountTransactions = (LinearLayout) rowView.findViewById(R.id.overview_account_transactions);
            accountTransactions.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // show the transactions fragment and preselect the current account
                    int accountId = accountOverview.getAccount().getId();

                    Toast.makeText(getContext(), "Konto " + accountId + " gedrückt.", Toast.LENGTH_SHORT).show();

                    // TODO: wechsel zum Transactions fragment implementieren
                }
            });

            return rowView;
        }

        /**
         * This method sets the text color of a TextView instance.
         * If the value is zero, the text color will be set to the default color,
         * If the value is negative, the text color will be red.
         * If the value if positive, the text color will be green
         *
         * @param view the text view instance
         * @param value the value
         */
        private void setTextColor(TextView view, double value)
        {
            int color = 0;
            if (value == 0.0) {
                // set the default TextView color
                color = new TextView(getContext()).getCurrentTextColor();
            } else if (value < 0) {
                color = getResources().getColor(R.color.balance_negative);
            } else if (value > 0) {
                color = getResources().getColor(R.color.balance_positive);
            }

            view.setTextColor(color);
        }

        /**
         * This method sets the value and text color of a currency TextView.
         *
         * @param view the text view instance
         * @param value the value
         */
        private void setCurrencyValueAndTextColor(TextView view, double value)
        {
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
            view.setText(numberFormat.format(value));

            setTextColor(view, value);
        }
    }

}
