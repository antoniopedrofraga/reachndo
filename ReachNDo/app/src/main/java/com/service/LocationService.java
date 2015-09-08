package com.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.service.MyLocationListener;

import static android.support.v4.app.ActivityCompat.requestPermissions;

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
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
}
