package com.reachndo;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;

/**
 * Created by Pedro on 01/09/2015.
 */
public class ContactsCompletionView extends TokenCompleteTextView<Contact> {

    ArrayList<Contact> contacts;

    public ContactsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.allowDuplicates(false);
        contacts = new ArrayList<>();
    }

    @Override
    protected View getViewForObject(Contact contact) {

        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout)l.inflate(R.layout.contact_token, (ViewGroup) ContactsCompletionView.this.getParent(), false);
        ((TextView)view.findViewById(R.id.name)).setText(contact.getName());

        return view;
    }

    @Override
    protected Contact defaultObject(String completionText) {
        //Stupid simple example of guessing if we have an email or not
        return null;
    }

    public void addContact(Object o) {
        contacts.add((Contact) o);
    }

    public void removeContact(Object o){
        contacts.remove(o);
    }

    public ArrayList<Contact> getContacts(){
        return contacts;
    }
}