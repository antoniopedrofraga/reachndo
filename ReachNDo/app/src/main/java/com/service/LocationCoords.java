package com.service;

/**
 * Created by Joao Nogueira on 09/09/2015.
 */
public class LocationCoords {
    LocationCoords(double a, double b)
    {
        longitude = a;
        latitude = b;
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
