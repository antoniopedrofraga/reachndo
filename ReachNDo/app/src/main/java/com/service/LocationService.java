package com.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Joao Nogueira on 08/09/2015.
 */
public class LocationService extends Service {

    long minTime;
    float minDistance;

    LocationManager lm;
    LocationListener ll;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        try {
            SaveAndLoad.loadInfo(getBaseContext());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Log.d("Service Debug", "Checkpoint on creation");

        ll = new MyLocationListener(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }

        minTime = 0;
        minDistance = 0;
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, ll);

        Log.d("Location Service", "First Created");

    }

    @Override
    public void onDestroy() {
        //TODO
        }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {

        Log.d("Location Service", "Start Command");

        return START_STICKY;
    }

    public static boolean checkIfInsideArea(Location loc, LocationCoords coords, Context cont)
    {
        Log.d("Distance", "D: " + distFrom(loc.getLatitude(), loc.getLongitude(), coords.getLatitude(), coords.getLongitude()) + " R: " + loc.getRadius());

        if (loc.getRadius() > distFrom(coords.getLatitude(), coords.getLongitude(), loc.getLatitude(), loc.getLongitude()))
        {
            loc.runEvents(cont);
            loc.inside = true;
            return true;
        }
        else
        {
            loc.inside = false;
            return false;
        }

    }

    public static double distFrom(double lng1, double lat1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        if (dist < 0)
            dist = -dist;

        return dist;
    }

}
