package com.googlecode.hibiscusapp.model;

import java.util.Date;

/**
 * Package: com.googlecode.hibiscusapp.model
 * Date: 11/09/13
 * Time: 21:50
 *
 * @author eike
 */
public class AccountTransaction
{

    private final int id;
    private final int accountId;
    private final Recipient recipient;
    private final TransactionType transactionType;
    private final double value;
    private final Date date;
    private final String reference;
    private final double balance;
    private final String comment;

    public AccountTransaction(
        int id,
        int accountId,
        Recipient recipient,
        TransactionType transactionType,
        double value,
        Date date,
        String reference,
        double balance,
        String comment
    )
    {
        this.id = id;
        this.accountId = accountId;
        this.recipient = recipient;
        this.transactionType = transactionType;
        this.value = value;
        this.date = date;
        this.reference = reference;
        this.balance = balance;
        this.comment = comment;
    }

    public int getId()
    {
        return id;
    }

    public int getAccountId()
    {
        return accountId;
    }

    public Recipient getRecipient()
    {
        return recipient;
    }

    public TransactionType getTransactionType()
    {
        return transactionType;
    }

    public double getValue()
    {
        return value;
    }

    public Date getDate()
    {
        return date;
    }

    public String getReference()
    {
        return reference;
    }

    public double getBalance()
    {
        return balance;
    }

    public String getComment()
    {
        return comment;
    }
}
