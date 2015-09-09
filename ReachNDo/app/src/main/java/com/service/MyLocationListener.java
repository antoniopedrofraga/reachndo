package com.service;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
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

            sendNotification("Coordenadas", "Longitude: " + longitude + "\tLatitude: " + latitude);
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

    public void sendNotification(String title, String text) {
        NotificationEvent notif = new NotificationEvent(title, text);
        notif.throwNotification(cont);
    }

}
