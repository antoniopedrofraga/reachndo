package com.service;

import android.content.Context;
import android.net.wifi.WifiManager;
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

    public void turnOn(Context cont) {
        WifiManager wifiManager = (WifiManager) cont.getSystemService(cont.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    public void turnOff(Context cont) {
        WifiManager wifiManager = (WifiManager) cont.getSystemService(cont.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);

    }

    public int getStatus() {
        return status;
    }
}