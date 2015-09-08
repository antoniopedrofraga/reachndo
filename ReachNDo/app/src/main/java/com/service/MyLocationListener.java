package com.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Joao Nogueira on 08/09/2015.
 */
public class MyLocationListener implements LocationListener {
    private Context cont;

    MyLocationListener(Context context) {
        cont = context;
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.v("Location Service", "location changed");

        if (location != null)
        {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            //To test
            Log.v("Location Service", "Longitude: " + longitude + "\tLatitude: " + latitude);

            sendNotification(45435435, "Coordenadas", "Longitude: " + longitude + "\tLatitude: " + latitude);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Location Service", "Provider Enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Location Service", "Provider Disabled");
    }

    public void sendNotification(int id, String title, String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(cont);
        builder.setAutoCancel(true);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setSmallIcon(android.R.drawable.sym_def_app_icon);

        Log.d("Notification Service", "Launches Notification");

        Notification notification = builder.build();

        NotificationManager nm = (NotificationManager) cont.getSystemService(cont.NOTIFICATION_SERVICE);
        nm.notify(id, notification);
    }

}
