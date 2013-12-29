package com.googlecode.hibiscusapp.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import com.googlecode.hibiscusapp.database.AccountProvider;
import com.googlecode.hibiscusapp.database.AccountTable;
import com.googlecode.hibiscusapp.database.AccountTransactionProvider;
import com.googlecode.hibiscusapp.database.AccountTransactionTable;
import com.googlecode.hibiscusapp.model.Account;
import com.googlecode.hibiscusapp.model.AccountTransaction;
import com.googlecode.hibiscusapp.model.Recipient;
import com.googlecode.hibiscusapp.util.Constants;
import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
        Log.d(Constants.LOG_TAG, "SynchronizationTask running...");

        Context context = params[0];

        try {
            XMLRPCClient client = createClient(context);

            // synchronize bank accounts
            long start = System.currentTimeMillis();
            synchronizeBankAccounts(client, context);
            Log.i(Constants.LOG_TAG, String.format("Synchronizing bank accounts finished in %s seconds", (System.currentTimeMillis() - start) / 1000.0));

            // synchronize account transactions
            start = System.currentTimeMillis();
            synchronizeAccountTransactions(client, context);
            Log.i(Constants.LOG_TAG, String.format("Synchronizing account transactions finished in %s seconds", (System.currentTimeMillis() - start) / 1000.0));
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
        Cursor accountsCursor = getAccounts(context);

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
     * This method calls the XMLRPC interface of the hibiscus server to retrieve new account transactions.
     * For each bank account, a XMLRPC call will be send with the parameters konto_id and id:min.
     *
     * @param client the xml rpc client instance
     */
    private void synchronizeAccountTransactions(XMLRPCClient client, Context context) throws XMLRPCException, ParseException
    {
        Cursor accountsCursor = getAccounts(context);

        while (accountsCursor.moveToNext()) {
            int accountId = accountsCursor.getInt(accountsCursor.getColumnIndex(AccountTable.COLUMN_ID));

            // query the max transaction id for the current account

            int maxId = 0;
            Cursor cursor = context.getContentResolver().query(
                AccountTransactionProvider.CONTENT_URI,
                new String[] {"MAX(" + AccountTransactionTable.COLUMN_ID + ") AS max_id"},
                AccountTransactionTable.COLUMN_ACCOUNT_ID + "=?",
                new String[] {String.valueOf(accountId)},
                ""
            );
            if (cursor.moveToNext()) {
                maxId = cursor.getInt(cursor.getColumnIndexOrThrow("max_id"));
            }

            // call the xmlrpc interface to retrieve new account transactions
            List<AccountTransaction> accountTransactions = getAccountTransactionsFromHibiscus(client, accountId, maxId + 1);

            // insert the new account transactions
            for (AccountTransaction transaction : accountTransactions) {
                ContentValues cv = new ContentValues();
                cv.put(AccountTransactionTable.COLUMN_ID, transaction.getId());
                cv.put(AccountTransactionTable.COLUMN_ACCOUNT_ID, transaction.getAccountId());
                cv.put(AccountTransactionTable.COLUMN_RECIPIENT_NAME, transaction.getRecipient().getName());
                cv.put(AccountTransactionTable.COLUMN_RECIPIENT_ACCOUNT_NUMBER, transaction.getRecipient().getAccountNumber());
                cv.put(AccountTransactionTable.COLUMN_RECIPIENT_BANK_IDENTIFICATION_NUMBER, transaction.getRecipient().getBankIdentificationNumber());
                cv.put(AccountTransactionTable.COLUMN_TRANSACTION_TYPE, transaction.getTransactionType());
                cv.put(AccountTransactionTable.COLUMN_VALUE, transaction.getValue());
                cv.put(AccountTransactionTable.COLUMN_DATE, (int)(transaction.getDate().getTime() / 1000));
                cv.put(AccountTransactionTable.COLUMN_REFERENCE, transaction.getReference());
                cv.put(AccountTransactionTable.COLUMN_BALANCE, transaction.getBalance());
                cv.put(AccountTransactionTable.COLUMN_COMMENT, transaction.getComment());

                context.getContentResolver().insert(
                    AccountTransactionProvider.CONTENT_URI,
                    cv
                );
            }

            if (accountTransactions.size() > 0) {
                Log.d(Constants.LOG_TAG, String.format("Added %s new account transactions for account %s", accountTransactions.size(), accountId));
            }
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
        for (Object o:list) {
            Map accountData = (Map) o;

            int id = Integer.parseInt(String.valueOf(accountData.get("id")));
            String accountHolder = String.valueOf(accountData.get("name"));
            String accountNumber = String.valueOf(accountData.get("kontonummer"));

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

    /**
     *
     * @param client
     * @return
     * @throws XMLRPCException
     * @throws ParseException
     */
    private List<AccountTransaction> getAccountTransactionsFromHibiscus(XMLRPCClient client, int accountId, int minTransactionId)
        throws XMLRPCException, ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        List<AccountTransaction> transactions = new ArrayList<AccountTransaction>();

        Map params = new HashMap();
        params.put("id:min", minTransactionId);
        params.put("konto_id", accountId);

        Object[] list = (Object[]) client.call("hibiscus.xmlrpc.umsatz.list", params);
        for (Object o:list) {
            Map transactionData = (Map) o;

            int id = Integer.parseInt(String.valueOf(transactionData.get("id")));
            String recipientName = String.valueOf(transactionData.get("empfaenger_name"));
            String recipientAccountNumber = String.valueOf(transactionData.get("empfaenger_konto"));
            String recipientBankNumber = String.valueOf(transactionData.get("empfaenger_blz"));
            String transactionType = String.valueOf(transactionData.get("art"));

            // parse the value string
            String valueString = String.valueOf(transactionData.get("betrag")).replace(',', '.');
            double value = Double.parseDouble(valueString);

            // parse the transaction date
            String valueDateString = String.valueOf(transactionData.get("valuta"));
            Date valueDate = sdf.parse(valueDateString);

            String reference = String.valueOf(transactionData.get("zweck"));
            String comment = String.valueOf(transactionData.get("kommentar"));

            // parse the balance string
            String balanceString = String.valueOf(transactionData.get("saldo")).replace(',', '.');
            double balance = Double.parseDouble(balanceString);

            Recipient recipient = new Recipient(recipientName, recipientAccountNumber, recipientBankNumber);

            AccountTransaction transaction = new AccountTransaction(id, accountId, recipient, transactionType, value, valueDate, reference, balance, comment);
            transactions.add(transaction);
        }

        return transactions;
    }

    /**
     * Returns all accounts from the account table.
     *
     * @param context the context instance
     *
     * @return
     */
    private Cursor getAccounts(Context context)
    {
        Cursor accountsCursor = context.getContentResolver().query(
            AccountProvider.CONTENT_URI,
            AccountTable.COLUMNS_ALL,
            "",
            new String[]{},
            ""
        );

        if (accountsCursor == null) {
            throw new RuntimeException("Unable to access local bank accounts");
        }

        return accountsCursor;
    }
}