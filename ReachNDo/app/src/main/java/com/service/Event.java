package com.service;


import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.reachndo.R;

import java.io.Serializable;

public class Event implements Serializable {

    public String name;
    public String description;
    private EventType type;
    private boolean groupHeader = false;

    public Event(String name, String description){
        this.name = name;
        this.description = description;
    }

    public Event(String name){
        this.name = name;
        groupHeader = true;
    }

    public int getIcon(){
        switch (type){
            case MESSAGE:
                return R.drawable.sms;
            case NOTIFICATION:
                return R.drawable.notif;
            case WIFI:
                if(((WiFiEvent)this).getStatus() == WiFiEvent.OFF)
                    return R.drawable.wifi_off;
                else
                    return R.drawable.wifi_on;
            case SOUND_PROFILE:
                if(((SoundProfileEvent)this).getProfile() == SoundProfileEvent.NORMAL)
                    return R.drawable.normal;
                else if(((SoundProfileEvent)this).getProfile() == SoundProfileEvent.SILENT)
                    return R.drawable.silent;
                else
                    return R.drawable.vibrate;
            case BLUETOOTH:
                if(((BluetoothEvent)this).getStatus() == BluetoothEvent.ON)
                    return R.drawable.bluetooth;
                else
                    return R.drawable.bluetooth_off;
            case MOBILE_DATA:
                if(((MobileDataEvent)this).getStatus() == MobileDataEvent.ON)
                    return R.drawable.mobile_data;
                else
                    return R.drawable.mobile_data_off;
            case ALARM:
                return R.drawable.alarm;
            default:
                return -1;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Event(EventType type) {
        this.type = type;
    }

    public Event(EventType type, String name, String desc) {
        this.type = type;
        this.name = name;
        this.description = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public boolean isGroupHeader(){
        return groupHeader;
    }



}
