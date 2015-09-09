package com.service;

import java.util.ArrayList;

/**
 * Created by Joao Nogueira on 09/09/2015.
 */
public class Singleton {
    Singleton()
    {
        locationCoords = new LocationCoords(-8.5985816, 41.1771498);
    }

    private static ArrayList<Location> locations;

    private static Singleton singleton = new Singleton();

    private static LocationCoords locationCoords;

    public static LocationCoords getLocationCoords() {
        return locationCoords;
    }

    public static ArrayList<Location> getLocations() {
        return locations;
    }

    public static void setLocations(ArrayList<Location> locations) {
        Singleton.locations = locations;
    }
}
