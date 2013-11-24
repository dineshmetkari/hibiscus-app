package com.googlecode.hibiscusapp.database.dao;

import android.content.Context;
import android.database.Cursor;
import com.googlecode.hibiscusapp.database.AccountProvider;
import com.googlecode.hibiscusapp.database.AccountTable;
import com.googlecode.hibiscusapp.model.Account;
import com.googlecode.hibiscusapp.model.AccountOverview;
import com.googlecode.hibiscusapp.model.AccountTransaction;

import java.util.*;

/**
 * Package: com.googlecode.hibiscusapp.database.dao
 * Date: 24/11/13
 * Time: 20:30
 *
 * @author eike
 */
public class AccountDao extends AbstractDao
{
    public AccountDao(Context context)
    {
        super(context);
    }

    /**
     * This method returns a list of account overviews.
     * For each account, that is present in the database, an instance of AccountOverview is created.
     * This instance contains the current account data, such as the account number, balance, etc. In
     * addition to that, all transactions of the current month are queried.
     *
     * @return a list of account overviews.
     */
    public List<AccountOverview> getAccountOverviews()
    {
        // get all accounts
        List<Account> accounts = getAccounts();

        // calculate the timestamp of the first day of this month and the first of the next month
        Calendar now = new GregorianCalendar();
        now.set(Calendar.DAY_OF_MONTH, 1);
        now.set(Calendar.HOUR, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        long tsFrom = now.getTimeInMillis() / 1000;

        // add one month
        now.add(Calendar.MONTH, 1);
        long tsTo = now.getTimeInMillis() / 1000;

        // query the transactions for each account in the current month, using the two timestamps from above
        List<AccountOverview> accountOverviews = new ArrayList<AccountOverview>();
        AccountTransactionDao accountTransactionDao = new AccountTransactionDao(context);
        for (Account account : accounts) {
            List<AccountTransaction> accountTransactions = accountTransactionDao.getAccountTransactions(account.getId(), tsFrom, tsTo);

            AccountOverview accountOverview = new AccountOverview(account, accountTransactions);
            accountOverviews.add(accountOverview);
        }

        return accountOverviews;
    }

    /**
     * This method returns a list of accounts that are present in the database.
     *
     * @return list of accounts
     */
    public List<Account> getAccounts()
    {
        Cursor accountCursor = context.getContentResolver().query(
            AccountProvider.CONTENT_URI,
            AccountTable.COLUMNS_ALL,
            null,
            null,
            null
        );

        List<Account> accounts = new ArrayList<Account>();

        // create the account instance
        while (accountCursor.moveToNext()) {
            int accountId = accountCursor.getInt(accountCursor.getColumnIndexOrThrow(AccountTable.COLUMN_ID));
            String accountNumber = accountCursor.getString(accountCursor.getColumnIndexOrThrow(AccountTable.COLUMN_ACCOUNT_NUMBER));
            String accountHolder = accountCursor.getString(accountCursor.getColumnIndexOrThrow(AccountTable.COLUMN_ACCOUNT_HOLDER));
            double balance = accountCursor.getDouble(accountCursor.getColumnIndexOrThrow(AccountTable.COLUMN_BALANCE));
            long balanceDate = accountCursor.getLong(accountCursor.getColumnIndexOrThrow(AccountTable.COLUMN_BALANCE_DATE));
            Date date = new Date(balanceDate * 1000);

            Account account = new Account(accountId, accountNumber, accountHolder, balance, date);
            accounts.add(account);
        }

        return accounts;
    }
}
