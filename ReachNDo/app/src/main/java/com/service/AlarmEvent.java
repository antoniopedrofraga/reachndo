package com.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;

import com.reachndo.R;

import java.io.Serializable;

/**
 * Created by Joao Nogueira on 09/09/2015.
 */
public class AlarmEvent extends Event implements Serializable {

    public AlarmEvent(Context cont) throws InterruptedException {
        super(EventType.ALARM);

        NotificationEvent notif = new NotificationEvent("Alarm", "a disparar");
        notif.throwNotification(cont);

        Intent dialogIntent = new Intent(cont, DialogAlarm.class);
        Bundle extrasBundle = new Bundle();
        extrasBundle.putStringArray("info", new String[]{"Alarme", "aqui esta"});
        dialogIntent.putExtras(extrasBundle);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        cont.startActivity(dialogIntent);

        Log.d("Alarm Event", "Alarm set");
    }

}