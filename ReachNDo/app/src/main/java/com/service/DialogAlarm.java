package com.service;

import android.app.Activity;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import java.io.Serializable;

/**
 * Created by Joao Nogueira on 09/09/2015.
 */

public class DialogAlarm extends Activity implements Serializable {
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle extrasBundle = getIntent().getExtras();
        String [] params = extrasBundle.getStringArray("info");

        Uri alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        final Ringtone ringtoneAlarm = RingtoneManager.getRingtone(getBaseContext(), alarmTone);
        ringtoneAlarm.play();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(params[0]);
        builder.setMessage(params[1]);
        builder.setPositiveButton("OK", null);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Handle OK button
                ringtoneAlarm.stop();
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
