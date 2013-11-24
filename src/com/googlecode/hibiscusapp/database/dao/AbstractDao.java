package com.googlecode.hibiscusapp.database.dao;

import android.content.Context;

/**
 * Package: com.googlecode.hibiscusapp.database.dao
 * Date: 24/11/13
 * Time: 20:30
 *
 * @author eike
 */
abstract public class AbstractDao
{
    protected Context context;

    public AbstractDao(Context context)
    {
        this.context = context;
    }
}
