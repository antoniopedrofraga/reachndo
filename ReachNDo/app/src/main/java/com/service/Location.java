package com.service;


import java.io.Serializable;
import java.util.ArrayList;

public class Location extends LocationCoords implements Serializable {

    private String name;
    private String user_description;
    private double radius;
    private ArrayList<Event> events;

    public Location(double a, double b, String n, String u_d, double r) {
        super(a, b);
        this.name = n;
        this.user_description = u_d;
        this.radius = r;
        events = new ArrayList<>();
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

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void addEvent(Event e){
        events.add(e);
    }

    public String getUser_description() {
        return user_description;
    }

    public void setUser_description(String user_description) {
        this.user_description = user_description;
    }
}
