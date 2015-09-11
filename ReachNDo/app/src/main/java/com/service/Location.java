package com.service;


import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;

public class Location extends LocationCoords implements Serializable {

    private String name;
    private double radius;
    private ArrayList<Event> eventsIn;
    private ArrayList<Event> eventsOut;
    public boolean inside;

    public Location(double a, double b, String n, double r) {
        super(a, b);
        this.name = n;
        this.radius = r;
        eventsIn = new ArrayList<>();
        eventsOut = new ArrayList<>();
        inside = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public ArrayList<Event> getEventsIn() {
        return eventsIn;
    }

    public void addEventIn(Event e){
        eventsIn.add(e);
    }

    public ArrayList<Event> getEventsOut() {
        return eventsOut;
    }

    public void addEventOut(Event e){
        eventsOut.add(e);
    }

    public void runEvents(Context cont) {
        
    }
}
