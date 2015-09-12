package com.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import java.io.Serializable;

/**
 * Created by Francisco on 12/09/2015.
 */
public class BluetoothEvent extends Event implements Serializable {

    public final static int ON = 0;
    public final static int OFF = 1;

    private int status;

    public BluetoothEvent(int status) {
        super(EventType.BLUETOOTH);
        this.status = status;
    }

    public void turnOn() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()){
            bluetoothAdapter.enable();
        }

    }

    public void turnOff() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter.isEnabled()){
            bluetoothAdapter.disable();
        }

    }

    public int getStatus() {
        return status;
    }
}
