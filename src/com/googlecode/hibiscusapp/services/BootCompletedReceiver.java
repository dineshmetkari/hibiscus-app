package com.googlecode.hibiscusapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.googlecode.hibiscusapp.R;

/**
 * Package: com.googlecode.hibiscusapp.services
 * Date: 29/09/13
 * Time: 01:13
 *
 * @author eike
 */
public class BootCompletedReceiver extends BroadcastReceiver
{
    // The offset in seconds
    private static final int SERVICE_START_OFFSET = 30;

    /**
     * This method will be called once the smartphone finished booting.
     * The synchronization service is started, if the user set the sync_active preference.
     *
     * @param context the context
     * @param intent the intent
     */
    public void onReceive(Context context, Intent intent)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean syncActive = sharedPref.getBoolean(context.getString(R.string.pref_sync_active_key), true);

        if (syncActive) {
            SynchronizationService.startService(context, SERVICE_START_OFFSET);
        }
    }
}
