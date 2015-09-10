package com.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.reachndo.R;

import java.io.Serializable;

public class NotificationEvent extends Event implements Serializable {
    public static int id = 1;

    String title;
    String text;

    public NotificationEvent(String ti, String tex) {
        super(EventType.NOTIFICATION);

        title = ti;
        text = tex;
    }

    public void throwNotification(Context cont)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(cont);
        builder.setAutoCancel(true);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setSmallIcon(R.mipmap.ic_launcher);

        Log.d("Notification Service", "Launches Notification");

        Notification notification = builder.build();

        NotificationManager nm = (NotificationManager) cont.getSystemService(cont.NOTIFICATION_SERVICE);
        nm.notify(id, notification);

        id++;
    }

}
