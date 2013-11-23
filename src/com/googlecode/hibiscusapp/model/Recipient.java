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
    final private String accountNumber;
    final private String bankIdentificationNumber;

    public Recipient(String name, String accountNumber, String bankIdentificationNumber)
    {
        this.name = name;
        this.accountNumber = accountNumber;
        this.bankIdentificationNumber = bankIdentificationNumber;
    }

    public String getName()
    {
        return name;
    }

    public String getAccountNumber()
    {
        return accountNumber;
    }

    public String getBankIdentificationNumber()
    {
        return bankIdentificationNumber;
    }
}
