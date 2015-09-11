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

    public Location(double a, double b, String n, double r, boolean bo) {
        super(a, b);
        this.name = n;
        this.radius = r;
        eventsIn = new ArrayList<>();
        eventsOut = new ArrayList<>();
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
        for (int i = 0; i < eventsIn.size(); i++)
        {
            if (eventsIn.get(i).getType() == EventType.NOTIFICATION)
            {
                NotificationEvent notif = (NotificationEvent) eventsIn.get(i);
                notif.throwNotification(cont);
            }
            else if (eventsIn.get(i).getType() == EventType.MESSAGE)
            {
                MessageEvent sms = (MessageEvent) eventsIn.get(i);
                sms.sendMessage();
            }
            else if (eventsIn.get(i).getType() == EventType.WIFI)
            {
                WiFiEvent ev = (WiFiEvent) eventsIn.get(i);
                if (ev.getStatus() == 0)
                    ev.turnOn(cont);
                else
                   ev.turnOff(cont);
            }
        }
    }

    public void setInside(boolean inside) {
        this.inside = inside;
    }

    public boolean isInside() {
        return inside;
    }

    public void removeEvent(Event event){
        eventsIn.remove(event);
        eventsOut.remove(event);
    }
}
