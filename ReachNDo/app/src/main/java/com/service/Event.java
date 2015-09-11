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
