package com.googlecode.hibiscusapp.model;

/**
 * Package: com.googlecode.hibiscusapp.model
 * Date: 11/09/13
 * Time: 21:50
 *
 * @author eike
 */
public class Recipient
{

    final private String name;
    final private int accountNumber;
    final private int bankIdentificationNumber;

    public Recipient(String name, int accountNumber, int bankIdentificationNumber)
    {
        this.name = name;
        this.accountNumber = accountNumber;
        this.bankIdentificationNumber = bankIdentificationNumber;
    }

    public String getName()
    {
        return name;
    }

    public int getAccountNumber()
    {
        return accountNumber;
    }

    public int getBankIdentificationNumber()
    {
        return bankIdentificationNumber;
    }
}
