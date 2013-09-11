package com.googlecode.hibiscusapp.model;

import java.util.Date;

/**
 * Package: com.googlecode.hibiscusapp.model
 * Date: 11/09/13
 * Time: 21:38
 *
 * @author eike
 */
public class Account
{

    private final int id;
    private final int accountNumber;
    private final String accountHolder;
    private final double balance;
    private final Date balanceDate;

    public Account(int id, int accountNumber, String accountHolder, double balance, Date balanceDate)
    {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = balance;
        this.balanceDate = balanceDate;
    }

    public int getId()
    {
        return id;
    }

    public int getAccountNumber()
    {
        return accountNumber;
    }

    public String getAccountHolder()
    {
        return accountHolder;
    }

    public double getBalance()
    {
        return balance;
    }

    public Date getBalanceDate()
    {
        return balanceDate;
    }
}
