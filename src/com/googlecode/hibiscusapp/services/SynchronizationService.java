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
import android.widget.Toast;
import com.googlecode.hibiscusapp.R;

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
    private final IBinder binder = new LocalBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Toast.makeText(this, "Service start", Toast.LENGTH_SHORT).show();

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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int intervalMinutes = Integer.parseInt(sharedPref.getString(context.getString(R.string.pref_sync_interval_key), "30"));
        long intervalMillis = TimeUnit.MINUTES.toMillis(intervalMinutes);
        intervalMillis = 20000; // TODO: remove in production code

        // schedue the synchronization service with the Android AlarmManager service
        Intent intent = new Intent(context, SynchronizationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        // add the offset
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, (int)offsetInSeconds);

        // inexactRepeating allows Android to optimize the energy consumption
        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), intervalMillis, pendingIntent);
    }

    /**
     * Calls the start {@link SynchronizationService#startService(Context, long)} method
     * with an offset of 0 seconds.
     *
     * @param context the context
     */
    public static void startService(Context context)
    {
        startService(context, 0);
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
