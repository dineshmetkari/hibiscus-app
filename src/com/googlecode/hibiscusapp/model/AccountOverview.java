package com.googlecode.hibiscusapp.model;

import java.util.Collections;
import java.util.List;

/**
 * Package: com.googlecode.hibiscusapp.model
 * Date: 24/11/13
 * Time: 20:32
 *
 * @author eike
 */
public class AccountOverview
{
    final private Account account;
    final private List<AccountTransaction> accountTransactions;

    public AccountOverview(Account account, List<AccountTransaction> accountTransactions)
    {
        this.account = account;
        this.accountTransactions = Collections.unmodifiableList(accountTransactions);
    }

    public Account getAccount()
    {
        return account;
    }

    public List<AccountTransaction> getAccountTransactions()
    {
        return accountTransactions;
    }

    /**
     * This method adds all positive transactions and returns the result.
     *
     * @return the receipts on this account
     */
    public double getReceipts()
    {
        double result = 0.0;

        for (AccountTransaction transaction : accountTransactions) {
            if (transaction.getValue() > 0) {
                result += transaction.getValue();
            }
        }

        return result;
    }

    /**
     * This method add all negative transactions and returns the result.
     *
     * @return the expenses on this account
     */
    public double getExpenses()
    {
        double result = 0.0;

        for (AccountTransaction transaction : accountTransactions) {
            if (transaction.getValue() < 0) {
                result += transaction.getValue();
            }
        }

        return result;
    }

    /**
     * Returns the balance of all transactions.
     * All transactions are summed up, and the result is returned.
     *
     * @return the transaction balance
     */
    public double getTransactionBalance()
    {
        return getReceipts() + getExpenses();
    }

    @Override
    public String toString()
    {
        return "AccountOverview{" +
            "account=" + account +
            ", accountTransactions=" + accountTransactions.size() +
            '}';
    }
}
