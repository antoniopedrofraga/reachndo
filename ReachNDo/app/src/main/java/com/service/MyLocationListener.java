package com.service;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.reachndo.R;
import com.reachndo.memory.Singleton;

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

            checkAllLocations(cont, location);
        }
    }

    private void checkAllLocations(Context cont, Location location) {
        boolean out = false;
        for (int i = 0; i < Singleton.getLocations().size(); i++)
            if (LocationService.checkIfInsideArea(Singleton.getLocations().get(i), new LocationCoords(location.getLatitude(), location.getLongitude()), cont))
                out = true;

        if (!out)
        {

            for (int i = 0; i < Singleton.getLocations().size(); i++)
            {
                if (Singleton.getLocations().get(i).getName().equals(cont.getResources().getString(R.string.default_location)))
                {
                    if (!Singleton.getLocations().get(i).isInside()) {

                        Singleton.getLocations().get(i).runInEvents(cont);
                        Singleton.getLocations().get(i).setInside(true);
                        break;
                    }
                }
            }
        }
        else
        {
            for (int i = 0; i < Singleton.getLocations().size(); i++)
            {
                if (Singleton.getLocations().get(i).getName() == cont.getResources().getString(R.string.default_location))
                {
                    Singleton.getLocations().get(i).setInside(false);
                    break;
                }
            }
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

}
