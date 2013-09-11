package com.googlecode.hibiscusapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Package: com.googlecode.hibiscusapp.database
 * Date: 11/09/13
 * Time: 22:18
 *
 * @author eike
 */
public class HibiscusDatabaseHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "hibiscus.db";
    private static final int DATABASE_VERSION = 1;

    public HibiscusDatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database)
    {
        AccountTable.onCreate(database);
        AccountTransactionTable.onCreate(database);
    }

    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        AccountTable.onUpgrade(database, oldVersion, newVersion);
        AccountTransactionTable.onUpgrade(database, oldVersion, newVersion);
    }
}
