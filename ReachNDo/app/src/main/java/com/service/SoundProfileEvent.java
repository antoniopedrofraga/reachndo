package com.service;

import android.content.Context;
import android.media.AudioManager;

import com.reachndo.R;

import java.io.Serializable;

/**
 * Created by Pedro on 11/09/2015.
 */
public class SoundProfileEvent extends Event implements Serializable {

    public static final int SILENT = 0;
    public static final int VIBRATE = 1;
    public static final int NORMAL = 2;

    int type;

    public SoundProfileEvent(int type){
        super(EventType.SOUND_PROFILE);
        this.type = type;
    }

    public void change(Context cont)
    {
        if (type == 1) {
            AudioManager audioManager = (AudioManager)cont.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

            NotificationEvent notif = new NotificationEvent("Sound Profile", "Vibrate mode ON");
            notif.throwNotification(cont);
        }
        else if (type == 0) {
        AudioManager audioManager = (AudioManager)cont.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        NotificationEvent notif = new NotificationEvent("Sound Profile", "Silent mode ON");
        notif.throwNotification(cont);
        }
        else if (type == 2) {
        AudioManager audioManager = (AudioManager)cont.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

        NotificationEvent notif = new NotificationEvent("Sound Profile", "Sound mode ON");
        notif.throwNotification(cont);
        }
    }

}