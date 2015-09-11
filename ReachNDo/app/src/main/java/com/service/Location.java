package com.service;


import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;

public class Location extends LocationCoords implements Serializable {

    private String name;
    private double radius;
    private ArrayList<Event> events;
    public boolean inside;

    public Location(double a, double b, String n, double r) {
        super(a, b);
        this.name = n;
        this.radius = r;
        events = new ArrayList<>();
        inside = false;
    }

    public Location(double a, double b, String n, double r, boolean bo) {
        super(a, b);
        this.name = n;
        this.radius = r;
        events = new ArrayList<>();
        inside = bo;
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

    public void runEvents(Context cont) {
        for (int i = 0; i < events.size(); i++)
        {
            if (events.get(i).getType() == EventType.NOTIFICATION)
            {
                NotificationEvent notif = new NotificationEvent(events.get(i).getName(), events.get(i).getDescription());
                notif.throwNotification(cont);
            }
            else if (events.get(i).getType() == EventType.MESSAGE)
            {
                MessageEvent sms = (MessageEvent) events.get(i);
                sms.sendMessage();
            }
        }
    }

    public void setInside(boolean inside) {
        this.inside = inside;
    }

    public boolean isInside() {
        return inside;
    }
}
