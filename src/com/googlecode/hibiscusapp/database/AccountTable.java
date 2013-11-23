package com.googlecode.hibiscusapp.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Package: com.googlecode.hibiscusapp.database
 * Date: 11/09/13
 * Time: 22:04
 *
 * @author eike
 */
public class AccountTable
{
    // Database table
    public static final String TABLE_ACCOUNT = "account";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ACCOUNT_NUMBER = "account_number";
    public static final String COLUMN_ACCOUNT_HOLDER = "account_holder";
    public static final String COLUMN_BALANCE = "balance";
    public static final String COLUMN_BALANCE_DATE = "balance_date";

    // helper contant to select all columns of this table
    public static final String[] COLUMNS_ALL = new String[] {
        COLUMN_ID, COLUMN_ACCOUNT_NUMBER, COLUMN_ACCOUNT_HOLDER, COLUMN_BALANCE, COLUMN_BALANCE_DATE
    };

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
        + TABLE_ACCOUNT
        + "("
        + COLUMN_ID + " integer primary key autoincrement, "
        + COLUMN_ACCOUNT_NUMBER + " text not null, "
        + COLUMN_ACCOUNT_HOLDER + " text not null, "
        + COLUMN_BALANCE + " real not null, "
        + COLUMN_BALANCE_DATE + " integer not null"
        + ");";

    public static void onCreate(SQLiteDatabase database)
    {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        Log.w(
            AccountTable.class.getName(), "Upgrading database from version "
            + oldVersion + " to " + newVersion
            + ", which will destroy all old data!"
        );
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        onCreate(database);
    }
}
