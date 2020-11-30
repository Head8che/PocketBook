package com.example.pocketbook.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.example.pocketbook.R;

// class to handle displaying notifications on android oreo and above
public class OreoNotification extends ContextWrapper {

    private static final String CHANNEL_ID = "com.example.pocketbook";
    private static final String CHANNEL_NAME = "pocketbook";
    private NotificationManager notificationManager;
    public OreoNotification(Context base){
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
        }
    }


    @TargetApi(Build.VERSION_CODES.O)
    /**
     * creating a channel for the app's notifications, required for android oreo and above
     */
    private void createChannel(){
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights((false));
        channel.enableVibration(true);

        getManager().createNotificationChannel(channel);
    }

    /**
     * getter function for the notificationManager to build and display notifications
     * @return notificationManager object
     */
    public NotificationManager getManager(){
        if (notificationManager == null){
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return notificationManager;
    }

    /**
     * getter method for a Notification.Builder object
     * @param title the title of the notification displayed in the phone's notifications as a String
     * @param body the body of  the notification
     * @param icon the icon of the app
     * @param group the group the notification belongs to
     * @param pendingIntent a pendingIntent to direct the user to the NotificationFragment in the HomeActivity in the app
     * @return Notification.Builder object used to display the notification
     */
    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getOreoNotification(String title, String body, String icon, String group, PendingIntent pendingIntent){
        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(Integer.parseInt(icon))
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent))
                .setContentIntent(pendingIntent);
            }
}

