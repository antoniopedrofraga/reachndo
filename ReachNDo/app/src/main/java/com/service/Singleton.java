package com.service;

/**
 * Created by Joao Nogueira on 09/09/2015.
 */
public class Singleton {
    Singleton()
    {
        locationCoords = new LocationCoords(-8.5985816, 41.1771498);
    }

    private static Singleton singleton = new Singleton();

    private static LocationCoords locationCoords;

    public static LocationCoords getLocationCoords() {
        return locationCoords;
    }

}
