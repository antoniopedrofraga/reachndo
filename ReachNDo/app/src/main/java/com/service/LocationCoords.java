package com.service;

import java.io.Serializable;

/**
 * Created by Joao Nogueira on 09/09/2015.
 */
public class LocationCoords implements Serializable {
    public LocationCoords(double a, double b)
    {
        latitude = a;
        longitude = b;
    }

    private double longitude;
    private double latitude;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
