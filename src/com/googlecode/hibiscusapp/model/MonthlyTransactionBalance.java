package com.googlecode.hibiscusapp.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Package: com.googlecode.hibiscusapp.model
 * Date: 11/12/13
 * Time: 21:46
 *
 * @author eike
 */
public class MonthlyTransactionBalance
{
    private Date month;
    private double transactionBalance;

    public MonthlyTransactionBalance(Date month, double transactionBalance)
    {
        this.month = month;
        this.transactionBalance = transactionBalance;
    }

    public Date getMonth()
    {
        return month;
    }

    public double getTransactionBalance()
    {
        return transactionBalance;
    }

    @Override
    public String toString()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        return "MonthlyTransactionBalance{" +
            "month=" + sdf.format(month) +
            ", transactionBalance=" + transactionBalance +
            '}';
    }
}
