package com.googlecode.hibiscusapp.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import com.googlecode.hibiscusapp.R;
import com.googlecode.hibiscusapp.util.Constants;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Package: com.googlecode.hibiscusapp.services
 * Date: 28/09/13
 * Time: 20:24
 *
 * @author eike
 */
public class SynchronizationService extends Service
{
    private static final int ALARM_ID = 0;

    private final IBinder binder = new LocalBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        new SynchronizationTask().execute(this);

        return Service.START_NOT_STICKY;
    }

    /**
     * This method starts the synchronization service       .
     * The context intance will be used to retrieve the shared preference store of
     * this app.
     *
     * @param context the context
     * @param offsetInSeconds the offset in seconds
     */
    public static void startService(Context context, long offsetInSeconds)
    {
        // TODO: prüfen ob der alarm schon gesetzt ist, ist vielleicht durch FLAG_UPDATE_CURRENT schon gegeben
        // http://stackoverflow.com/questions/4556670/how-to-check-if-alarmmamager-already-has-an-alarm-set
        // http://stackoverflow.com/questions/14485368/delete-alarm-from-alarmmanager-using-cancel-android

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int intervalMinutes = Integer.parseInt(sharedPref.getString(context.getString(R.string.pref_sync_interval_key), "30"));
        long intervalMillis = TimeUnit.MINUTES.toMillis(intervalMinutes);

        // schedue the synchronization service with the Android AlarmManager service
            Intent intent = new Intent(context, SynchronizationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, ALARM_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // add the start offset
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, (int)offsetInSeconds);

        // inexactRepeating allows Android to optimize the energy consumption
        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), intervalMillis, pendingIntent);

        Log.d(Constants.LOG_TAG, "Started/updated SynchronizationService via AlarmManager with an interval of " + (intervalMillis / 1000 / 60) + " minutes");
    }

    /**
     * Calls the start {@link SynchronizationService#startService(Context, long)} method
     * with an offset of 0 seconds.
     *
     * @param context the context
     */
    public static void startService(Context context)
    {
        startService(context, 5);
    }

    public IBinder onBind(Intent intent)
    {
        return binder;
    }

    public class LocalBinder extends Binder
    {
        public SynchronizationService getService()
        {
            return SynchronizationService.this;
        }
    }
}
