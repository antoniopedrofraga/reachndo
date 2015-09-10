package com.service;

import android.content.Context;
import android.util.Log;

import com.reachndo.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class SaveAndLoad {
    private SaveAndLoad() {}

    public static void saveInfo(Context cont) throws IOException {
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

    public static void loadInfo(Context cont) throws IOException, ClassNotFoundException {
        ArrayList<Location> toRet;

        File fileTester = cont.getFileStreamPath("saved.dat");
        if (fileTester.exists())
        {
            Log.d("Loading Debug", "Started loading");

            FileInputStream tempo = cont.openFileInput("saved.dat");

            ObjectInputStream objectIn = new ObjectInputStream(tempo);
            toRet = (ArrayList<Location>) objectIn.readObject();
            Singleton.setLocations(toRet);
            objectIn.close();
            tempo.close();

            Log.d("Loading Debug", "Finished loading");
        }
        else
        {
            Log.d("Loading Debug", "Does not exist");

            ArrayList<Location> tempo2 = new ArrayList<Location>();
            tempo2.add(new Location(1,2, cont.getResources().getString(R.string.default_location), 0));
            Singleton.setLocations(tempo2);

            for (int i = 0; i < Singleton.getLocations().size(); i++)
                Log.d("Loading Debug", "Name: " + Singleton.getLocations().get(i).getName());
        }
    }

}