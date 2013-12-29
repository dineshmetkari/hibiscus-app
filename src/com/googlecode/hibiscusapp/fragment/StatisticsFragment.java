package com.googlecode.hibiscusapp.fragment;

import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.googlecode.hibiscusapp.R;
import com.googlecode.hibiscusapp.activity.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * Package: com.googlecode.hibiscusapp.fragment
 * Date: 09/09/13
 * Time: 22:10
 *
 * @author eike
 */
public class StatisticsFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.statistics, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        Button b = (Button)getActivity().findViewById(R.id.notification);
        b.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(), "Notification", Toast.LENGTH_SHORT).show();

                NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");

                // Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(getActivity(), MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
// Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                        0,
                        FLAG_UPDATE_CURRENT
                    );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                    (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
                mNotificationManager.notify(1234, mBuilder.build());
            }
        });
    }
}
