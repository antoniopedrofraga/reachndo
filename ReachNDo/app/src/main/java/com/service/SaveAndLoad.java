package com.service;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

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

//TODO falta load
    public static void loadGame() throws IOException, ClassNotFoundException {
        int[] toRet;
        if (!Gdx.files.local("savedGame.dat").file().exists())
        {
            toRet = new int[10];
            for (int i = 0; i < 10; i++)
                toRet[i] = -1;
            SingletonVandC.totalScore = toRet;
        }
        else
        {
            try
            {
                FileInputStream tempo = new FileInputStream(Gdx.files.local("savedGame.dat").file().getAbsolutePath());
                ObjectInputStream objectIn = new ObjectInputStream(tempo);
                toRet = (int[]) objectIn.readObject();
                SingletonVandC.totalScore = toRet;
                objectIn.close();
                tempo.close();
            }
            catch (IOException a) {
                a.printStackTrace();
            }
        }
    }

}