package com.googlecode.hibiscusapp.database.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.googlecode.hibiscusapp.database.AccountTransactionProvider;
import com.googlecode.hibiscusapp.database.AccountTransactionTable;
import com.googlecode.hibiscusapp.database.HibiscusDatabaseHelper;
import com.googlecode.hibiscusapp.model.AccountTransaction;
import com.googlecode.hibiscusapp.model.MonthlyTransactionBalance;
import com.googlecode.hibiscusapp.model.Recipient;
import com.googlecode.hibiscusapp.util.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Package: com.googlecode.hibiscusapp.database.dao
 * Date: 24/11/13
 * Time: 21:01
 *
 * @author eike
 */
public class AccountTransactionDao extends AbstractDao
{
    public AccountTransactionDao(Context context)
    {
        super(context);
    }

    /**
     * This method calculates the monthly transaction balances for the specified account.
     * For each month, a MonthlyTransactionBalance instance will be created, that contains
     * the month and the transaction balance.
     * If the accountId id < 1, all accounts are included in the balance.
     *
     * @param accountId
     *
     * @return list of transaction balances
     */
    public List<MonthlyTransactionBalance> getMonthlyTransactionBalances(int accountId)
    {
        // filter by account_id if accountId is > 0
        String selection = accountId > 0 ? AccountTransactionTable.COLUMN_ACCOUNT_ID + "=?" : "";
        String[] selectionArgs = accountId > 0 ? new String[] {String.valueOf(accountId)} : new String[] {};

        // get the db connection
        SQLiteOpenHelper databaseHelper = new HibiscusDatabaseHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        // perform the db query
        Cursor cursor = database.query(
            AccountTransactionTable.TABLE_ACCOUNT_TRANSACTION,
            new String[] {
                "strftime('%Y-%m', datetime(date, 'unixepoch')) AS month",
                "sum(value) as transaction_balance"
            },
            selection,
            selectionArgs,
            "month",
            "",
            "month DESC"
        );

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        List<MonthlyTransactionBalance> transactionBalances = new ArrayList<MonthlyTransactionBalance>();
        while (cursor.moveToNext()) {
            // format the date
            Date date = null;
            try {
                String dateString = cursor.getString(cursor.getColumnIndexOrThrow("month"));
                date = sdf.parse(dateString);
            } catch (ParseException pe) {
                Log.e(Constants.LOG_TAG, "can not parse date identifier", pe);
            }
            double balance = cursor.getDouble(cursor.getColumnIndexOrThrow("transaction_balance"));

            transactionBalances.add(new MonthlyTransactionBalance(date, balance));
        }

        return transactionBalances;
    }

    /**
     * This method load the details one account transaction.
     *
     * @param transactionId the transaction id
     *
     * @return the transaction instance
     */
    public AccountTransaction getAccountTransaction(int transactionId)
    {
        Cursor cursor = context.getContentResolver().query(
            AccountTransactionProvider.CONTENT_URI,
            AccountTransactionTable.COLUMNS_ALL,
            AccountTransactionTable.COLUMN_ID + "=?",
            new String[] {String.valueOf(transactionId)},
            ""
        );

        if (!cursor.moveToFirst()) {
            throw new IllegalArgumentException("can not load transaction with id " + transactionId);
        }

        return cursorToAccountTransaction(cursor);
    }

    /**
     * This method returns all account transactions for the specified account.
     *
     * @param accountId the accountId
     * @return list of all account transactions.
     */
    public List<AccountTransaction> getAccountTransactions(int accountId)
    {
        return getAccountTransactions(accountId, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    /**
     * This method returns all account transactions for the specified account,
     * that occured between {@code tsFrom} and {@code tsTo}.
     *
     * @param accountId the accountId
     * @param tsFrom    the from timestamp
     * @param tsTo      the to timestamp
     * @return list of account transactions
     */
    public List<AccountTransaction> getAccountTransactions(int accountId, long tsFrom, long tsTo)
    {
        String selection = "";
        String[] selectionArgs = {};
        if (accountId > 0) {
            selection = String.format(
                "%s > ? AND %s < ? AND %s = ?",
                AccountTransactionTable.COLUMN_DATE,
                AccountTransactionTable.COLUMN_DATE,
                AccountTransactionTable.COLUMN_ACCOUNT_ID
            );
            selectionArgs = new String[]{String.valueOf(tsFrom), String.valueOf(tsTo), String.valueOf(accountId)};
        } else {
            selection = String.format(
                "%s > ? AND %s < ?",
                AccountTransactionTable.COLUMN_DATE,
                AccountTransactionTable.COLUMN_DATE
            );
            selectionArgs = new String[]{String.valueOf(tsFrom), String.valueOf(tsTo)};
        }

        Cursor cursor = context.getContentResolver().query(
            AccountTransactionProvider.CONTENT_URI,
            AccountTransactionTable.COLUMNS_ALL,
            selection,
            selectionArgs,
            AccountTransactionTable.COLUMN_DATE + " DESC, " + AccountTransactionTable.COLUMN_ID + " ASC"
        );

        List<AccountTransaction> accountTransactions = new ArrayList<AccountTransaction>();

        // create the account transaction instances
        while (cursor.moveToNext()) {
            AccountTransaction transaction = cursorToAccountTransaction(cursor);
            accountTransactions.add(transaction);
        }

        return accountTransactions;
    }

    /**
     * This method converts a Cursor to a AccountTransaction instance.
     *
     * @param cursor the cursor, that points to the AccountTransaction data
     *
     * @return the AccountTransaction instance
     */
    private AccountTransaction cursorToAccountTransaction(Cursor cursor)
    {
        int transactionId = cursor.getInt(cursor.getColumnIndexOrThrow(AccountTransactionTable.COLUMN_ID));
        int accountId = cursor.getInt(cursor.getColumnIndexOrThrow(AccountTransactionTable.COLUMN_ACCOUNT_ID));
        String recipientName = cursor.getString(cursor.getColumnIndexOrThrow(AccountTransactionTable.COLUMN_RECIPIENT_NAME));
        String recipientAccountNumber = cursor.getString(cursor.getColumnIndexOrThrow(AccountTransactionTable.COLUMN_RECIPIENT_ACCOUNT_NUMBER));
        String recipientBankNumber = cursor.getString(cursor.getColumnIndexOrThrow(AccountTransactionTable.COLUMN_RECIPIENT_BANK_IDENTIFICATION_NUMBER));
        String transactionType = cursor.getString(cursor.getColumnIndexOrThrow(AccountTransactionTable.COLUMN_TRANSACTION_TYPE));
        double value = cursor.getDouble(cursor.getColumnIndexOrThrow(AccountTransactionTable.COLUMN_VALUE));
        long dateTs = cursor.getLong(cursor.getColumnIndexOrThrow(AccountTransactionTable.COLUMN_DATE));
        String reference = cursor.getString(cursor.getColumnIndexOrThrow(AccountTransactionTable.COLUMN_REFERENCE));
        double balance = cursor.getDouble(cursor.getColumnIndexOrThrow(AccountTransactionTable.COLUMN_BALANCE));
        String comment = cursor.getString(cursor.getColumnIndexOrThrow(AccountTransactionTable.COLUMN_COMMENT));

        Recipient recipient = new Recipient(recipientName, recipientAccountNumber, recipientBankNumber);
        Date date = new Date(dateTs * 1000);

        return new AccountTransaction(transactionId, accountId, recipient, transactionType, value, date, reference, balance, comment);
    }
}
