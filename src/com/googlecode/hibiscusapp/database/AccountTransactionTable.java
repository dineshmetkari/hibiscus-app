package com.googlecode.hibiscusapp.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Package: com.googlecode.hibiscusapp.database
 * Date: 11/09/13
 * Time: 22:09
 *
 * @author eike
 */
public class AccountTransactionTable
{
    // Database table
    public static final String TABLE_ACCOUNT_TRANSACTION = "account_transaction";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ACCOUNT_ID = "account_id";
    public static final String COLUMN_RECIPIENT_NAME = "recipient_name";
    public static final String COLUMN_RECIPIENT_ACCOUNT_NUMBER = "recipient_account_number";
    public static final String COLUMN_RECIPIENT_BANK_IDENTIFICATION_NUMBER = "recipient_bank_identification_number";
    public static final String COLUMN_TRANSACTION_TYPE = "transaction_type";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_REFERENCE = "reference";
    public static final String COLUMN_BALANCE = "balance";
    public static final String COLUMN_COMMENT = "comment";


    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
        + TABLE_ACCOUNT_TRANSACTION
        + "("
        + COLUMN_ID + " integer primary key autoincrement, "
        + COLUMN_ACCOUNT_ID + " integer not null, "
        + COLUMN_RECIPIENT_NAME + " text not null, "
        + COLUMN_RECIPIENT_ACCOUNT_NUMBER + " text not null, "
        + COLUMN_RECIPIENT_BANK_IDENTIFICATION_NUMBER + " text not null, "
        + COLUMN_TRANSACTION_TYPE + " text not null, "
        + COLUMN_VALUE + " real not null, "
        + COLUMN_DATE + " integer not null, "
        + COLUMN_REFERENCE + " text not null, "
        + COLUMN_BALANCE + " real not null, "
        + COLUMN_COMMENT + " text not null "
        + ");";

    public static void onCreate(SQLiteDatabase database)
    {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        Log.w(
            AccountTransactionTable.class.getName(), "Upgrading database from version "
            + oldVersion + " to " + newVersion
            + ", which will destroy all old data!"
        );
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT_TRANSACTION);
        onCreate(database);
    }
}
