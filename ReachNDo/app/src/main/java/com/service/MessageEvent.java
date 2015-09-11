package com.service;

import android.telephony.SmsManager;
import android.util.Log;

import com.reachndo.Contact;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Joao Nogueira on 09/09/2015.
 */
public class MessageEvent extends Event implements Serializable {

    private String destinationNumber;
    private String textMessage;
    private ArrayList<Contact> contacts;

    public MessageEvent(String nr, String text) {
        super(EventType.MESSAGE);

        destinationNumber = nr;
        textMessage = text;
    }

   public void sendMessage() {
       SmsManager sms = SmsManager.getDefault();

       for (int i = 0; i < contacts.size(); i++) {
           sms.sendTextMessage(contacts.get(i).getNumber(), null, textMessage, null, null);
       }

   }

    public String getDestinationNumber()
    {
        return destinationNumber;
    }

    public String getTextMessage()
    {
        return textMessage;
    }

    public void setContacts(ArrayList<Contact> contacts){
        this.contacts = contacts;
    }

    public ArrayList<Contact> getContacts(){
        return contacts;
    }


}
