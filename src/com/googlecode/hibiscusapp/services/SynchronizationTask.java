package com.googlecode.hibiscusapp.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import com.googlecode.hibiscusapp.database.AccountProvider;
import com.googlecode.hibiscusapp.database.AccountTable;
import com.googlecode.hibiscusapp.model.Account;
import com.googlecode.hibiscusapp.util.Constants;
import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class synchronizes
 *
 * Package: com.googlecode.hibiscusapp.services
 * Date: 29/09/13
 * Time: 12:43
 *
 * @author eike
 */
public class SynchronizationTask extends AsyncTask<Context, Void, Void>
{
    @Override
    protected Void doInBackground(Context... params)
    {
        if (params.length == 0) {
            throw new IllegalArgumentException("you need to provide at least one context");
        }

        Context context = params[0];

        try {
            XMLRPCClient client = createClient(context);

            // synchronize bank accounts
            synchronizeBankAccounts(client, context);

            // synchronize account transactions
            // TODO: implement
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "Unable to execute SynchronizationTask", e);
        }

        return null;
    }

    /**
     * This method creates a XMLRPCClient instance.
     * The url, username and password are read from the
     * context object.
     *
     * @param context the context instance
     *
     * @return the XMLRPCClient instance
     */
    private XMLRPCClient createClient(Context context)
    {
        String url = "http://192.168.178.20:8080/xmlrpc/"; // TODO: read from config

        XMLRPCClient client = new XMLRPCClient(url);

        // Client-Config erzeugen
        client.setBasicAuthentication("admin", "pass4jameica"); // TODO: read from user input

        return client;
    }

    /**
     * Synchronizes the local bank accounts with
     * the data that comes from the xmlrpc interface.
     *
     * @param client the XMLRPCClient instance
     * @param context the context
     */
    private void synchronizeBankAccounts(XMLRPCClient client, Context context) throws ParseException, XMLRPCException
    {
        // get all local accounts
        Cursor accountsCursor = context.getContentResolver().query(
            AccountProvider.CONTENT_URI,
            AccountTable.COLUMNS_ALL,
            "",
            new String[]{},
            ""
        );

        if (accountsCursor == null)
        {
            throw new RuntimeException("Unable to access local bank accounts");
        }

        Map<Integer, Account> accounts = getBankAccountsFromHibiscus(client);

        while (accountsCursor.moveToNext()) {
            int id = accountsCursor.getInt(accountsCursor.getColumnIndex(AccountTable.COLUMN_ID));

            if (accounts.containsKey(id)) {
                // update the bank account data
                Account newAccountData = accounts.get(id);

                ContentValues cv = new ContentValues();
                cv.put(AccountTable.COLUMN_ACCOUNT_NUMBER, newAccountData.getAccountNumber());
                cv.put(AccountTable.COLUMN_ACCOUNT_HOLDER, newAccountData.getAccountHolder());
                cv.put(AccountTable.COLUMN_BALANCE, newAccountData.getBalance());
                cv.put(AccountTable.COLUMN_BALANCE_DATE, (int)(newAccountData.getBalanceDate().getTime() / 1000));

                context.getContentResolver().update(
                    AccountProvider.CONTENT_URI,
                    cv,
                    AccountTable.COLUMN_ID + " = ?",
                    new String[] {String.valueOf(id)}
                );

                Log.d(Constants.LOG_TAG, "Updating bank account with number " + newAccountData.getAccountNumber());

                // remove the account from the map, becauce it has been processed
                accounts.remove(id);
            } else {
                // delete the bank account, becauce it is not present anymore
                context.getContentResolver().delete(
                    AccountProvider.CONTENT_URI,
                    AccountTable.COLUMN_ID + " = ?",
                    new String[] {String.valueOf(id)}
                );

                Log.d(Constants.LOG_TAG, "Deleting bank account with id " + id);
            }
        }

        // now add new bank accounts
        for (Map.Entry<Integer, Account> entry : accounts.entrySet()) {
            Account newAccountData = entry.getValue();

            ContentValues cv = new ContentValues();
            cv.put(AccountTable.COLUMN_ID, newAccountData.getId());
            cv.put(AccountTable.COLUMN_ACCOUNT_NUMBER, newAccountData.getAccountNumber());
            cv.put(AccountTable.COLUMN_ACCOUNT_HOLDER, newAccountData.getAccountHolder());
            cv.put(AccountTable.COLUMN_BALANCE, newAccountData.getBalance());
            cv.put(AccountTable.COLUMN_BALANCE_DATE, (int)(newAccountData.getBalanceDate().getTime() / 1000));

            context.getContentResolver().insert(
                AccountProvider.CONTENT_URI,
                cv
            );

            Log.d(Constants.LOG_TAG, "Adding new bank account with number " + newAccountData.getAccountNumber());
        }
    }

    /**
     * This method returns a list of all bank accounts that are available in the hibiscus server.
     * The list of bank accounts is retrieved via a XMLRPC call.
     *
     * @param client the XMLRPCClient instance
     *
     * @return a map of the bank accounts
     */
    private Map<Integer, Account> getBankAccountsFromHibiscus(XMLRPCClient client) throws XMLRPCException, ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        Map<Integer, Account> accounts = new HashMap<Integer, Account>();

        Object[] list = (Object[]) client.call("hibiscus.xmlrpc.konto.find");
        for (Object o:list)
        {
            Map accountData = (Map) o;

            int id = Integer.parseInt(String.valueOf(accountData.get("id")));
            String accountHolder = String.valueOf(accountData.get("name"));
            int accountNumber = Integer.parseInt(String.valueOf(accountData.get("kontonummer")));

            // parse the balance
            String balanceString = String.valueOf(accountData.get("saldo")).replace(',', '.');
            double balance = Double.parseDouble(balanceString);

            // parse the saldo date
            String balanceDateString = String.valueOf(accountData.get("saldo_datum"));
            Date balanceDate = sdf.parse(balanceDateString);

            Account account = new Account(id, accountNumber, accountHolder, balance, balanceDate);
            accounts.put(id, account);
        }

        return accounts;
    }
}