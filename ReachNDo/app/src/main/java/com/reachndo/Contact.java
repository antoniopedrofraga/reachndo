package com.reachndo;

import java.io.Serializable;

/**
 * Created by Pedro on 01/09/2015.
 */
public class Contact implements Serializable {
    private String name;
    private String number;

    Contact(String name, String number){
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        if(name != null && name.length() != 0)
            return name;
        else if(number != null && number.length() != 0)
            return number;
        else
            return "";
    }
}
