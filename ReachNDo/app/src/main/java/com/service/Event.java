package com.service;


import java.io.Serializable;

public class Event implements Serializable {

    public String name;
    public String description;
    private EventType type;

    public Event(String name, String description){
        this.name = name;
        this.description = description;
    }

    public int getIcon(){
        return -1;
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
}
