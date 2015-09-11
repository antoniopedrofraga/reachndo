package com.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Joao Nogueira on 11/09/2015.
 */
public class AutoStart extends BroadcastReceiver
{
    public void onReceive(Context arg0, Intent arg1)
    {
        Intent intent = new Intent(arg0,LocationService.class);
        arg0.startService(intent);
        Log.i("Autostart", "started");
    }
}
