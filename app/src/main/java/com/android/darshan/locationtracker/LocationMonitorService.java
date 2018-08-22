package com.android.darshan.locationtracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.android.darshan.locationtracker.Utils.Consts;

public class LocationMonitorService extends Service {
    private static final String TAG = "LocationMonitorService";

    // This is the Notification Channel ID.
    public static final String NOTIFICATION_CHANNEL_ID = "channel_id";
    //UserEntry visible Channel Name
    public static final String CHANNEL_NAME = "Notification Channel";
    public static final int NOTIFICATION_ID = 1000;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        if (intent.getAction().equals(Consts.STARTFOREGROUND_ACTION)) {
            Log.d(TAG, "onStartCommand: Starting foreground service");

            showMediaNotification();
            return START_STICKY;

        } else if (intent.getAction().equals(Consts.STOPFOREGROUND_ACTION)) {
            Log.i(TAG, "Stopping foreground service");
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }

        return START_STICKY;
    }



    private void showMediaNotification() {
        createNotificationChannel();

        //To open an activity when clicked on Notification, anywhere other than action buttons
        Intent tapIntent = new Intent(this, MapsActivity.class);
        tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent tapPendingIntent =
                PendingIntent.getActivity(this, 0, tapIntent, 0);

        // Apply the layouts to the notification
        Notification customNotification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_action_gps)
                .setContentTitle(getText(R.string.notification_title))
                .setContentText(getText(R.string.notification_message))
                .setSound(null)
                .setContentIntent(tapPendingIntent)
                //to clear out the notification when tapped
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, customNotification);

        startForeground(NOTIFICATION_ID, customNotification);
    }




    private void createNotificationChannel() {
        //Notification channel should only be created for devices running Android 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "createNotificationChannel: " + Build.VERSION.SDK_INT);
            // Importance applicable to all the notifications in this Channel
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, importance);

            String channelDescription = "This is channel description";
            notificationChannel.setDescription(channelDescription);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            //finally create notification channel
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }
}
