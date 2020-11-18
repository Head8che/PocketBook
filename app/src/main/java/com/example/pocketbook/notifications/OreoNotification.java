package com.example.pocketbook.notifications;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.pocketbook.model.Notification;

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
    private void createChannel(){
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights((false));
        channel.enableVibration(true);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager(){
        if (notificationManager == null){
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return notificationManager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public NotificationCompat.Builder getOreoNotification(String title, String body, int icon){
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(icon);
            }
}

