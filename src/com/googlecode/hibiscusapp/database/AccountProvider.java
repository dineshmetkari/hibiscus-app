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
public class AccountProvider extends ContentProvider
{
    private HibiscusDatabaseHelper database;

    // Used for the UriMacher
    private static final int ACCOUNTS = 10;
    private static final int ACCOUNT_ID = 20;

    private static final String AUTHORITY = "com.googlecode.hibiscusapp.accounts.contentprovider";

    private static final String BASE_PATH = "accounts";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/accounts";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/account";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, ACCOUNTS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", ACCOUNT_ID);
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
        queryBuilder.setTables(AccountTable.TABLE_ACCOUNT);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case ACCOUNTS:
                break;
            case ACCOUNT_ID:
                // Adding the ID to the original query
                queryBuilder.appendWhere(AccountTable.COLUMN_ID + "=" + uri.getLastPathSegment());
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
            case ACCOUNTS:
                id = sqlDB.insert(AccountTable.TABLE_ACCOUNT, null, values);
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
            case ACCOUNTS:
                rowsDeleted = sqlDB.delete(AccountTable.TABLE_ACCOUNT, selection, selectionArgs);
                break;
            case ACCOUNT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(AccountTable.TABLE_ACCOUNT, AccountTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(AccountTable.TABLE_ACCOUNT, AccountTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
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
            case ACCOUNTS:
                rowsUpdated = sqlDB.update(AccountTable.TABLE_ACCOUNT, values, selection, selectionArgs);
                break;
            case ACCOUNT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(AccountTable.TABLE_ACCOUNT, values, AccountTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(AccountTable.TABLE_ACCOUNT, values, AccountTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
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
    private void checkColumns(String[] projection) {
        String[] available = { AccountTable.COLUMN_ID, AccountTable.COLUMN_ACCOUNT_NUMBER, AccountTable.COLUMN_ACCOUNT_HOLDER,
            AccountTable.COLUMN_BALANCE, AccountTable.COLUMN_BALANCE_DATE};

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