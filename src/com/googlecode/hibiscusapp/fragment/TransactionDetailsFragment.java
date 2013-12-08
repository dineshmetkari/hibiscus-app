package com.googlecode.hibiscusapp.fragment;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.googlecode.hibiscusapp.R;
import com.googlecode.hibiscusapp.database.dao.AccountTransactionDao;
import com.googlecode.hibiscusapp.model.AccountTransaction;
import com.googlecode.hibiscusapp.util.UiUtil;

import java.text.DateFormat;

/**
 * Package: com.googlecode.hibiscusapp.fragment
 * Date: 10/09/13
 * Time: 23:48
 *
 * @author eike
 */
public class TransactionDetailsFragment extends Fragment
{
    public static final String PARAMETER_TRANSACTION_ID = "transaction_id";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.transaction_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        // load the transaction details
        int transactionId = getArguments().getInt(PARAMETER_TRANSACTION_ID);
        AccountTransactionDao transactionDao = new AccountTransactionDao(getActivity());
        AccountTransaction transaction = transactionDao.getAccountTransaction(transactionId);

        // set the transaction type
        TextView transactionType = (TextView) view.findViewById(R.id.transaction_details_type);
        transactionType.setText(transaction.getTransactionType());

        // set the date
        TextView transactionDate = (TextView) view.findViewById(R.id.transaction_details_date);
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());
        transactionDate.setText(dateFormat.format(transaction.getDate()));

        // set the value
        TextView transactionValue = (TextView) view.findViewById(R.id.transaction_details_value);
        UiUtil.setCurrencyValueAndTextColor(getActivity(), transactionValue, transaction.getValue());

        // set the name
        TextView transactionRecipient = (TextView) view.findViewById(R.id.transaction_details_name);
        transactionRecipient.setText(transaction.getRecipient().getName());

        // set the account number
        TextView transactionAccountNumber = (TextView) view.findViewById(R.id.transaction_details_account_number);
        transactionAccountNumber.setText(transaction.getRecipient().getAccountNumber());

        // set the bank identifier
        TextView transactionBankIdentifier = (TextView) view.findViewById(R.id.transaction_details_bank_identifier);
        transactionBankIdentifier.setText(transaction.getRecipient().getBankIdentificationNumber());

        // set the reference
        TextView transactionReference = (TextView) view.findViewById(R.id.transaction_details_reference);
        transactionReference.setText(transaction.getReference());
    }

    @Override
    public void onStart()
    {
        super.onStart();

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }
}
