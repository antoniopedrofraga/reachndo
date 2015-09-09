package com.service;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class SaveAndLoad {
    private SaveAndLoad() {}

    public static void saveGame(Context cont) throws IOException {
        try
        {
            FileOutputStream tempo = cont.openFileOutput("saved.dat", cont.MODE_PRIVATE);
            ObjectOutputStream objectOut = new ObjectOutputStream(tempo);
            objectOut.writeObject(Singleton.getLocations());
            objectOut.flush();
            objectOut.close();
            tempo.close();
        }
        catch (IOException a)
        {
            a.printStackTrace();
        }
    }

    public static void loadGame(Context cont) throws IOException, ClassNotFoundException {
        ArrayList<Location> toRet;
        FileInputStream tempo = cont.openFileInput("saved.dat");
        if (tempo != null)
        {

        }
        else
        {
            ObjectInputStream objectIn = new ObjectInputStream(tempo);
            toRet = (ArrayList<Location>) objectIn.readObject();
            Singleton.setLocations(toRet);
            objectIn.close();
            tempo.close();
        }
    }

}