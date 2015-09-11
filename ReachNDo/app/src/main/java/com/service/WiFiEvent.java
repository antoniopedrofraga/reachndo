package com.service;

import android.telephony.SmsManager;

import com.reachndo.Contact;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Pedro on 11/09/2015.
 */
public class WiFiEvent extends Event implements Serializable {

    public final static int ON = 0;
    public final static int OFF = 1;

    private int status;

    public WiFiEvent(int status){
        super(EventType.WIFI);
        this.status = status;
    }


}