package com.service;


import android.content.Context;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Location extends LocationCoords implements Serializable {

    private String name;
    private double radius;
    private ArrayList<Event> eventsIn;
    private ArrayList<Event> eventsOut;


    private boolean checked = false;
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

    public void runInEvents(Context cont) {
        runEvents(cont, getEventsIn());
    }

    public void runOutEvents(Context cont) {
        runEvents(cont, getEventsOut());
    }

    public void runEvents(Context cont, ArrayList<Event> events)
    {
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getType() == EventType.NOTIFICATION) {
                NotificationEvent notif = (NotificationEvent) events.get(i);
                notif.throwNotification(cont);
            } else if (events.get(i).getType() == EventType.MESSAGE) {
                MessageEvent sms = (MessageEvent) events.get(i);
                sms.sendMessage();
            } else if (events.get(i).getType() == EventType.WIFI) {
                WiFiEvent ev = (WiFiEvent) events.get(i);
                if (ev.getStatus() == 0)
                    ev.turnOn(cont);
                else
                    ev.turnOff(cont);
            } else if (events.get(i).getType() == EventType.SOUND_PROFILE) {
                SoundProfileEvent ev = (SoundProfileEvent) events.get(i);
                ev.change(cont);
            } else if (events.get(i).getType() == EventType.MOBILE_DATA) {
                MobileDataEvent ev = (MobileDataEvent) events.get(i);
                if (ev.getStatus() == MobileDataEvent.OFF) {
                    ev.turnOff(cont);
                    NotificationEvent notif = new NotificationEvent("Data Connection", "Turned Off");
                    notif.throwNotification(cont);
                }
                else {
                    ev.turnOn(cont);
                    NotificationEvent notif = new NotificationEvent("Data Connection", "Turned On");
                    notif.throwNotification(cont);
                }
            } else if (events.get(i).getType() == EventType.BLUETOOTH) {
                BluetoothEvent ev = (BluetoothEvent) events.get(i);
                if (ev.getStatus() == BluetoothEvent.OFF) {
                    ev.turnOff();
                    NotificationEvent notif = new NotificationEvent("Bluetooth", "Turned Off");
                    notif.throwNotification(cont);
                } else {
                    ev.turnOn();
                    NotificationEvent notif = new NotificationEvent("Bluetooth", "Turned On");
                    notif.throwNotification(cont);
                }
            }else if (events.get(i).getType() == EventType.ALARM){
                AlarmEvent alarmEvent = (AlarmEvent) events.get(i);
                alarmEvent.fireAlarm(cont);
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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}
