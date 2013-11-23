package com.googlecode.hibiscusapp.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Package: com.googlecode.hibiscusapp.database
 * Date: 12/09/13
 * Time: 20:33
 *
 * @author eike
 */
public class AccountTransactionProvider extends ContentProvider
{

    private HibiscusDatabaseHelper database;

    // Used for the UriMacher
    private static final int ACCOUNT_TRANSACTIONS = 10;
    private static final int ACCOUNT_TRANSACTION_ID = 20;

    private static final String AUTHORITY = "com.googlecode.hibiscusapp.accounttransactions.contentprovider";

    private static final String BASE_PATH = "accounttransactions";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/account_transactions";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/account_transaction";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, ACCOUNT_TRANSACTIONS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", ACCOUNT_TRANSACTION_ID);
    }

    @Override
    public boolean onCreate()
    {
        database = new HibiscusDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(AccountTransactionTable.TABLE_ACCOUNT_TRANSACTION);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case ACCOUNT_TRANSACTIONS:
                break;
            case ACCOUNT_TRANSACTION_ID:
                // Adding the ID to the original query
                queryBuilder.appendWhere(AccountTransactionTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri)
    {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();

        int rowsDeleted = 0;
        long id = 0;
        switch (uriType) {
            case ACCOUNT_TRANSACTIONS:
                id = sqlDB.insert(AccountTransactionTable.TABLE_ACCOUNT_TRANSACTION, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case ACCOUNT_TRANSACTIONS:
                rowsDeleted = sqlDB.delete(AccountTransactionTable.TABLE_ACCOUNT_TRANSACTION, selection, selectionArgs);
                break;
            case ACCOUNT_TRANSACTION_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(AccountTransactionTable.TABLE_ACCOUNT_TRANSACTION, AccountTransactionTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(
                        AccountTransactionTable.TABLE_ACCOUNT_TRANSACTION,
                        AccountTransactionTable.COLUMN_ID + "=" + id + " and " + selection,
                        selectionArgs
                    );
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case ACCOUNT_TRANSACTIONS:
                rowsUpdated = sqlDB.update(AccountTransactionTable.TABLE_ACCOUNT_TRANSACTION, values, selection, selectionArgs);
                break;
            case ACCOUNT_TRANSACTION_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(AccountTransactionTable.TABLE_ACCOUNT_TRANSACTION, values, AccountTransactionTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(
                        AccountTransactionTable.TABLE_ACCOUNT_TRANSACTION,
                        values,
                        AccountTransactionTable.COLUMN_ID + "=" + id + " and " + selection,
                        selectionArgs
                    );
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    /**
     * Validates that projection array contains only valid columns.
     *
     * @param projection
     */
    private void checkColumns(String[] projection)
    {
        String[] available = {
            AccountTransactionTable.COLUMN_ID,
            AccountTransactionTable.COLUMN_ACCOUNT_ID,
            AccountTransactionTable.COLUMN_BALANCE,
            AccountTransactionTable.COLUMN_COMMENT,
            AccountTransactionTable.COLUMN_DATE,
            AccountTransactionTable.COLUMN_RECIPIENT_ACCOUNT_NUMBER,
            AccountTransactionTable.COLUMN_RECIPIENT_BANK_IDENTIFICATION_NUMBER,
            AccountTransactionTable.COLUMN_RECIPIENT_NAME,
            AccountTransactionTable.COLUMN_REFERENCE,
            AccountTransactionTable.COLUMN_TRANSACTION_TYPE,
            AccountTransactionTable.COLUMN_VALUE,
            "MAX(_id) AS max_id",
        };

        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // Check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}