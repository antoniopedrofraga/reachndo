package com.reachndo.memory;

import android.content.Context;
import android.util.Log;

import com.service.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class SaveAndLoad {
    private SaveAndLoad() {
    }

    public static void saveInfo(Context cont) throws IOException {
        try {
            FileOutputStream tempo = cont.openFileOutput("locations.dat", cont.MODE_PRIVATE);
            ObjectOutputStream objectOut = new ObjectOutputStream(tempo);
            objectOut.writeObject(Singleton.getLocations());
            objectOut.flush();
            objectOut.close();
            tempo.close();
        } catch (IOException a) {
            a.printStackTrace();
        }
    }

    public static void loadInfo(Context cont)  {

        try {

            ArrayList<Location> toRet;
            File fileTester = cont.getFileStreamPath("locations.dat");
            if (fileTester.exists()) {
                Log.d("Loading Debug", "Started loading locations from .dat file");

                FileInputStream tempLocations = cont.openFileInput("locations.dat");

                ObjectInputStream objectIn = new ObjectInputStream(tempLocations);
                toRet = (ArrayList<Location>) objectIn.readObject();
                Singleton.setLocations(toRet);
                objectIn.close();
                tempLocations.close();

                Log.d("Loading Debug", "Finished loading locations from .dat file");
            } else {
                Log.d("Loading Debug", "locations.dat file does not exist");

                ArrayList<Location> tempo2 = new ArrayList<Location>();
                Singleton.setLocations(tempo2);

                for (int i = 0; i < Singleton.getLocations().size(); i++)
                    Log.d("Loading Debug", "Loading location: " + Singleton.getLocations().get(i).getName());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}