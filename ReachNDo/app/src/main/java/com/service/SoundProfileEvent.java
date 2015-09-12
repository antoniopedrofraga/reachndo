package com.service;

import com.reachndo.R;

import java.io.Serializable;

/**
 * Created by Pedro on 11/09/2015.
 */
public class SoundProfileEvent extends Event implements Serializable {

    public static final int SILENT = 0;
    public static final int VIBRATE = 1;
    public static final int NORMAL = 2;

    private int profile;

    public SoundProfileEvent(int type){
        super(EventType.SOUND_PROFILE);
        this.profile = type;
    }

    public int getProfile(){
        return profile;
    }
}