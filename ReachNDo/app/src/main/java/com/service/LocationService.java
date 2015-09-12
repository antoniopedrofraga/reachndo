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

import com.reachndo.R;

import java.io.IOException;

/**
 * Created by Joao Nogueira on 08/09/2015.
 */
public class LocationService extends Service {

    public final static double EARTH_RADIUS_KM = 6371;

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

        Log.d("Distance", "D: " + getDistance(loc.getLatitude(), loc.getLongitude(), coords.getLatitude(), coords.getLongitude()) + " R: " + loc.getRadius() + " N: " + loc.getName());

        if (loc.getRadius() > getDistance(loc.getLatitude(), loc.getLongitude(), coords.getLatitude(), coords.getLongitude()) &&
                !loc.isInside()
                &&
               !loc.getName().equals(cont.getResources().getString(R.string.default_location)))
        {
            loc.runInEvents(cont);
            loc.inside = true;
            return true;
        }
        else if (loc.getRadius() <= getDistance(loc.getLatitude(), loc.getLongitude(), coords.getLatitude(), coords.getLongitude()) &&
                !loc.getName().equals(cont.getResources().getString(R.string.default_location)) &&
                loc.isInside())
        {
            loc.runOutEvents(cont);
            loc.inside = false;
            return false;
        }
        else if (loc.getRadius() <= getDistance(loc.getLatitude(), loc.getLongitude(), coords.getLatitude(), coords.getLongitude()) &&
                !loc.getName().equals(cont.getResources().getString(R.string.default_location)))
        {
            loc.inside = false;
            return false;
        }
        else
            return false;
    }

    /**
     * Gets the great circle distance in kilometers between two geographical points, using
     * the <a href="http://en.wikipedia.org/wiki/Haversine_formula">haversine formula</a>.
     *
     * @param latitude1 the latitude of the first point
     * @param longitude1 the longitude of the first point
     * @param latitude2 the latitude of the second point
     * @param longitude2 the longitude of the second point
     * @return the distance, in kilometers, between the two points
     */
    public static float getDistance(double latitude1, double longitude1, double latitude2,
                                    double longitude2) {
        double dLat = Math.toRadians(latitude2 - latitude1);
        double dLon = Math.toRadians(longitude2 - longitude1);
        double lat1 = Math.toRadians(latitude1);
        double lat2 = Math.toRadians(latitude2);
        double sqrtHaversineLat = Math.sin(dLat / 2);
        double sqrtHaversineLon = Math.sin(dLon / 2);
        double a = sqrtHaversineLat * sqrtHaversineLat + sqrtHaversineLon * sqrtHaversineLon
                * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (float) (EARTH_RADIUS_KM * c * 1000);
    }


}
