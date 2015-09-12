package com.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Joao Nogueira on 11/09/2015.
 */
public class AutoStart extends BroadcastReceiver
{
    @Override
    public void onReceive(Context arg0, Intent arg1)
    {
        Intent i = new Intent(arg0, LocationService.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        arg0.startService(i);
    }
}
