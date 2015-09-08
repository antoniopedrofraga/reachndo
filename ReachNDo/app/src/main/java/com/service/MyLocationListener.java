package com.service;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Joao Nogueira on 08/09/2015.
 */
public class MyLocationListener implements LocationListener {
    @Override
    public void onLocationChanged(Location location) {
        if (location != null)
        {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            //To test
            Log.v("Location", "Longitude: " + longitude + "\tLatitude: " + latitude);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}