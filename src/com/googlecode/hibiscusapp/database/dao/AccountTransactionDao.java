package com.googlecode.hibiscusapp.database.dao;

import android.content.Context;
import android.database.Cursor;
import com.googlecode.hibiscusapp.database.AccountTransactionProvider;
import com.googlecode.hibiscusapp.database.AccountTransactionTable;
import com.googlecode.hibiscusapp.model.AccountTransaction;
import com.googlecode.hibiscusapp.model.Recipient;

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
     * This method determines the minimum timestamp and maximum timestamp
     * of available account transactions.
     *
     * @return the min/max timestamp
     */
    public long[] getMinMaxTimestamp()
    {
        Cursor cursor = context.getContentResolver().query(
            AccountTransactionProvider.CONTENT_URI,
            new String[] {
                "MAX(" + AccountTransactionTable.COLUMN_DATE + ") AS max_date",
                "MIN(" + AccountTransactionTable.COLUMN_DATE + ") AS min_date",
            },
            "",
            new String[] {},
            ""
        );

        if (!cursor.moveToFirst()) {
            return null;
        }

        long minDate = cursor.getLong(cursor.getColumnIndexOrThrow("min_date"));
        long maxDate = cursor.getLong(cursor.getColumnIndexOrThrow("max_date"));

        return new long[] {minDate, maxDate};
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
        Cursor cursor = context.getContentResolver().query(
            AccountTransactionProvider.CONTENT_URI,
            AccountTransactionTable.COLUMNS_ALL,
            String.format(
                "%s > ? AND %s < ? AND %s = ?",
                AccountTransactionTable.COLUMN_DATE,
                AccountTransactionTable.COLUMN_DATE,
                AccountTransactionTable.COLUMN_ACCOUNT_ID
            ),
            new String[]{String.valueOf(tsFrom), String.valueOf(tsTo), String.valueOf(accountId)},
            AccountTransactionTable.COLUMN_DATE + " ASC, " + AccountTransactionTable.COLUMN_ID + " ASC"
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
