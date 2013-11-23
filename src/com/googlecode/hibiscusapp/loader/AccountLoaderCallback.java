package com.googlecode.hibiscusapp.loader;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import com.googlecode.hibiscusapp.database.AccountProvider;

/**
 * Package: com.googlecode.hibiscusapp.loader
 * Date: 23/11/13
 * Time: 18:42
 *
 * @author eike
 */
public class AccountLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor>
{
    private final Context context;
    private final CursorAdapter cursorAdapter;

    public AccountLoaderCallback(Context context, CursorAdapter cursorAdapter)
    {
        this.context = context;
        this.cursorAdapter = cursorAdapter;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        return new CursorLoader(context, AccountProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        // set the new cursor in the list adapter
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        // reset the cursor of the list adapter
        cursorAdapter.swapCursor(null);
    }
}
