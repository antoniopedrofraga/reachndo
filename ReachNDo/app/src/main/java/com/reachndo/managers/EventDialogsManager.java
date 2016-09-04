package com.reachndo.managers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.reachndo.Contact;
import com.reachndo.ContactsCompletionView;
import com.reachndo.R;
import com.reachndo.activities.MainMenu;
import com.reachndo.memory.SaveAndLoad;
import com.reachndo.memory.Singleton;
import com.service.AlarmEvent;
import com.service.BluetoothEvent;
import com.service.Event;
import com.service.EventType;
import com.service.Location;
import com.service.MessageEvent;
import com.service.MobileDataEvent;
import com.service.NotificationEvent;
import com.service.SoundProfileEvent;
import com.service.WiFiEvent;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;
import com.utilities.Utilities;

import java.io.IOException;
import java.util.ArrayList;


public class EventDialogsManager {

    private static final int UNDEFINED = -1;
    private static final int IN = 0;
    private static final int OUT = 1;

    private static final int INITIAL_STATE = 0;
    private static final int EVENT_TYPE_STATE = 1;
    private static final int REMINDER = 2;
    private static final int SMS = 3;
    private static final int ALARM = 4;
    private static final int SOUND_PROFILE = 5;
    private static final int WIFI_PROFILE = 6;
    private static final int BLUETOOTH_PROFILE = 7;
    private static final int MOBILE_DATA_PROFILE = 8;

    private static EventDialogsManager instance;

    private Activity activity;
    private Resources resources;
    private FloatingButtonManager floatingButtonManager;

    private int selectedPosition;
    private int when = UNDEFINED;
    private int eventType = UNDEFINED;


    public EventDialogsManager(Activity activity, int currentSelection) {
        this.instance = this;
        this.activity = activity;
        this.resources = activity.getResources();
        this.floatingButtonManager = FloatingButtonManager.getInstance();
        this.selectedPosition = currentSelection;
    }

    private void setDefaults() {
        when = UNDEFINED;
        eventType = UNDEFINED;
    }

    private void showInOutPicker() {
        String type[] = {
                resources.getString(R.string.in_out_picker_dialog_in),
                resources.getString(R.string.in_out_picker_dialog_out)
        };

        new MaterialDialog.Builder(activity)
                .title(R.string.in_out_picker_dialog_title)
                .items(type)
                .itemsCallbackSingleChoice(when, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        when = which;
                        dialog.dismiss();
                        eventDialogsStateMachine(EVENT_TYPE_STATE);
                        return true;
                    }
                })
                .negativeText(android.R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        setDefaults();
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showEventPicker(final int state) {
        final int stateMachineOffset = 2;
        String type[] = {
                resources.getString(R.string.event_picker_dialog_remind),
                resources.getString(R.string.event_picker_dialog_sms),
                resources.getString(R.string.alarm_dialog_title),
                resources.getString(R.string.event_picker_dialog_sound_profile),
                resources.getString(R.string.event_picker_dialog_wifi),
                resources.getString(R.string.event_picker_dialog_bluetooth),
                resources.getString(R.string.event_picker_dialog_mobile_data)
        };

        new MaterialDialog.Builder(activity)
                .title(R.string.event_picker_dialog_title)
                .items(type)
                .itemsCallbackSingleChoice(eventType, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        eventType = which;
                        eventDialogsStateMachine(eventType + stateMachineOffset);
                        dialog.dismiss();
                        return true;
                    }


                })
                .neutralText(R.string.action_back)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        eventDialogsStateMachine(state - 1);
                        dialog.dismiss();
                    }
                })
                .negativeText(android.R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        setDefaults();
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showAlarmPicker() {
        new MaterialDialog.Builder(activity)
                .title(R.string.alarm_dialog_title)
                .customView(R.layout.alarm_layout, true)
                .negativeText(android.R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .neutralText(R.string.action_back)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        eventDialogsStateMachine(EVENT_TYPE_STATE);
                        dialog.dismiss();
                    }
                })
                .positiveText(R.string.action_conclude)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        TextView txtView = ((TextView) dialog.getView().findViewById(R.id.alarmTxt));
                        if (txtView.getText().length() <= 0 || txtView.getText() == null) {
                            Toast.makeText(activity, R.string.alarm_dialog_warning, Toast.LENGTH_SHORT).show();
                        } else {
                            ArrayList<Location> temp = Singleton.getLocations();
                            AlarmEvent alarmEvent = new AlarmEvent(activity, txtView.getText().toString());
                            alarmEvent.setName(resources.getString(R.string.alarm_dialog_title));

                            if (when == IN) {
                                temp.get(selectedPosition).getEventsIn().add(alarmEvent);
                            } else {
                                temp.get(selectedPosition).getEventsOut().add(alarmEvent);
                            }

                            Singleton.setLocations(temp);

                            try {
                                SaveAndLoad.saveInfo(activity);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            MainMenu menu = MainMenu.getInstance();
                            menu.notifyListView(selectedPosition);
                            dialog.dismiss();
                            floatingButtonManager.closeMenu();
                            setDefaults();
                        }
                    }
                })
                .autoDismiss(false)
                .show();
    }

    private void showWiFiProfilePicker() {
        if (existsProfileEvent(EventType.WIFI)) {
            Toast.makeText(activity, R.string.wifi_picker_dialog_warning, Toast.LENGTH_LONG).show();
            eventDialogsStateMachine(EVENT_TYPE_STATE);
            return;
        }

        String type[] = {
                resources.getString(R.string.wifi_picker_dialog_on),
                resources.getString(R.string.wifi_picker_dialog_off)
        };

        new MaterialDialog.Builder(activity)
                .title(R.string.wifi_picker_dialog_title)
                .items(type)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        WiFiEvent wifiEvent = new WiFiEvent(which);
                        wifiEvent.setName(resources.getString(R.string.wifi_picker_dialog_title));
                        ArrayList<Location> temp = Singleton.getLocations();
                        switch (which) {
                            case WiFiEvent.ON:
                                wifiEvent.setDescription(resources.getString(R.string.wifi_picker_dialog_on));
                                break;
                            case WiFiEvent.OFF:
                                wifiEvent.setDescription(resources.getString(R.string.wifi_picker_dialog_off));
                                break;
                        }

                        if (when == IN) {
                            temp.get(selectedPosition).getEventsIn().add(wifiEvent);
                        } else {
                            temp.get(selectedPosition).getEventsOut().add(wifiEvent);
                        }

                        Singleton.setLocations(temp);

                        try {
                            SaveAndLoad.saveInfo(activity);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        MainMenu main = MainMenu.getInstance();
                        main.notifyListView(selectedPosition);
                        floatingButtonManager.closeMenu();
                        setDefaults();
                        return true;
                    }
                })
                .negativeText(android.R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .neutralText(R.string.action_back)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        eventDialogsStateMachine(EVENT_TYPE_STATE);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showBluetoothProfilePicker() {
        if (existsProfileEvent(EventType.BLUETOOTH)) {
            Toast.makeText(activity, R.string.bluetooth_picker_dialog_warning, Toast.LENGTH_LONG).show();
            eventDialogsStateMachine(EVENT_TYPE_STATE);
            return;
        }

        String type[] = {
                resources.getString(R.string.bluetooth_picker_dialog_on),
                resources.getString(R.string.bluetooth_picker_dialog_off)
        };

        new MaterialDialog.Builder(activity)
                .title(R.string.bluetooth_picker_dialog_title)
                .items(type)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        BluetoothEvent bluetoothEvent = new BluetoothEvent(which);
                        bluetoothEvent.setName(resources.getString(R.string.bluetooth_picker_dialog_title));
                        ArrayList<Location> temp = Singleton.getLocations();
                        switch (which) {
                            case BluetoothEvent.ON:
                                bluetoothEvent.setDescription(resources.getString(R.string.bluetooth_picker_dialog_on));
                                break;
                            case BluetoothEvent.OFF:
                                bluetoothEvent.setDescription(resources.getString(R.string.bluetooth_picker_dialog_off));
                                break;
                        }

                        if (when == IN) {
                            temp.get(selectedPosition).getEventsIn().add(bluetoothEvent);
                        } else {
                            temp.get(selectedPosition).getEventsOut().add(bluetoothEvent);
                        }
                        Singleton.setLocations(temp);
                        Singleton.setLocations(temp);

                        try {
                            SaveAndLoad.saveInfo(activity);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        MainMenu main = MainMenu.getInstance();
                        main.notifyListView(selectedPosition);
                        floatingButtonManager.closeMenu();
                        setDefaults();
                        return true;
                    }
                })
                .negativeText(android.R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .neutralText(R.string.action_back)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        eventDialogsStateMachine(EVENT_TYPE_STATE);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showMobileDataProfilePicker() {
        if (existsProfileEvent(EventType.MOBILE_DATA)) {
            Toast.makeText(activity, R.string.mobile_data_picker_dialog_warning, Toast.LENGTH_LONG).show();
            eventDialogsStateMachine(EVENT_TYPE_STATE);
            return;
        }

        String type[] = {
                resources.getString(R.string.mobile_data_picker_dialog_on),
                resources.getString(R.string.mobile_data_picker_dialog_off)
        };

        new MaterialDialog.Builder(activity)
                .title(R.string.mobile_data_picker_dialog_title)
                .items(type)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        MobileDataEvent mobileDataEvent = new MobileDataEvent(which);
                        mobileDataEvent.setName(resources.getString(R.string.mobile_data_picker_dialog_title));
                        ArrayList<Location> temp = Singleton.getLocations();
                        switch (which) {
                            case MobileDataEvent.ON:
                                mobileDataEvent.setDescription(resources.getString(R.string.mobile_data_picker_dialog_on));
                                break;
                            case MobileDataEvent.OFF:
                                mobileDataEvent.setDescription(resources.getString(R.string.mobile_data_picker_dialog_off));
                                break;
                        }

                        if (when == IN) {
                            temp.get(selectedPosition).getEventsIn().add(mobileDataEvent);
                        } else {
                            temp.get(selectedPosition).getEventsOut().add(mobileDataEvent);
                        }
                        Singleton.setLocations(temp);
                        Singleton.setLocations(temp);

                        try {
                            SaveAndLoad.saveInfo(activity);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        MainMenu main = MainMenu.getInstance();
                        main.notifyListView(selectedPosition);
                        floatingButtonManager.closeMenu();
                        setDefaults();
                        return true;
                    }
                })
                .negativeText(android.R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .neutralText(R.string.action_back)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        eventDialogsStateMachine(EVENT_TYPE_STATE);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showSoundProfilePicker() {

        if (existsProfileEvent(EventType.SOUND_PROFILE)) {
            Toast.makeText(activity, R.string.sound_profile_picker_dialog_warning, Toast.LENGTH_LONG).show();
            eventDialogsStateMachine(EVENT_TYPE_STATE);
            return;
        }

        String type[] = {
                resources.getString(R.string.sound_profile_picker_dialog_silent),
                resources.getString(R.string.sound_profile_picker_dialog_vibrate),
                resources.getString(R.string.sound_profile_picker_dialog_normal)
        };

        new MaterialDialog.Builder(activity)
                .title(R.string.sound_profile_picker_dialog_title)
                .items(type)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        SoundProfileEvent spEvent = new SoundProfileEvent(which);
                        spEvent.setName(resources.getString(R.string.sound_profile_picker_dialog_title));
                        String changeTo = resources.getString(R.string.sound_profile_picker_dialog_change) + " ";
                        ArrayList<Location> temp = Singleton.getLocations();
                        switch (which) {
                            case SoundProfileEvent.SILENT:
                                spEvent.setDescription(changeTo +
                                        resources.getString(R.string.sound_profile_picker_dialog_silent));
                                break;
                            case SoundProfileEvent.VIBRATE:
                                spEvent.setDescription(changeTo +
                                        resources.getString(R.string.sound_profile_picker_dialog_vibrate));
                                break;
                            case SoundProfileEvent.NORMAL:
                                spEvent.setDescription(changeTo +
                                        resources.getString(R.string.sound_profile_picker_dialog_normal));
                                break;
                        }

                        if (when == IN) {
                            temp.get(selectedPosition).getEventsIn().add(spEvent);
                        } else {
                            temp.get(selectedPosition).getEventsOut().add(spEvent);
                        }

                        Singleton.setLocations(temp);

                        try {
                            SaveAndLoad.saveInfo(activity);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        MainMenu menu = MainMenu.getInstance();
                        menu.notifyListView(selectedPosition);
                        floatingButtonManager.closeMenu();
                        setDefaults();
                        return true;
                    }
                })
                .negativeText(android.R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .neutralText(R.string.action_back)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        eventDialogsStateMachine(EVENT_TYPE_STATE);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private boolean existsProfileEvent(EventType type) {
        ArrayList<Location> temp = Singleton.getLocations();
        if (when == IN) {
            for (Event e : temp.get(selectedPosition).getEventsIn()) {
                if (e.getType() == type)
                    return true;
            }
        } else {
            for (Event e : temp.get(selectedPosition).getEventsOut()) {
                if (e.getType() == type)
                    return true;
            }
        }
        return false;
    }

    public void showSMSPicker() {

        final ArrayList<Contact> contacts = getContactNumbers();
        final Contact number = new Contact("", "");
        contacts.add(0, number);

        final MaterialDialog smsPicker =
                new MaterialDialog.Builder(activity)
                        .title(R.string.sms_dialog_title)
                        .customView(R.layout.sms_layout, true)
                        .positiveText(android.R.string.ok)
                        .autoDismiss(false)
                        .negativeText(android.R.string.cancel)
                        .show();

        final ContactsCompletionView contactSearch = (ContactsCompletionView) activity.findViewById(R.id.searchView);

        View positive = smsPicker.getActionButton(DialogAction.POSITIVE);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contactSearch.getContacts().size() == 0) {
                    Toast.makeText(activity, R.string.sms_picker_contacts_warning, Toast.LENGTH_SHORT).show();
                    return;
                }

                String txt = ((EditText) smsPicker.getView().findViewById(R.id.smsTxt)).getText().toString();

                if (txt.length() == 0) {
                    showNoSMSTxtWarning(smsPicker, contactSearch);
                    return;
                }

                saveSMS(smsPicker, contactSearch);

                smsPicker.dismiss();
            }


        });
        View negative = smsPicker.getActionButton(DialogAction.NEGATIVE);
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smsPicker.dismiss();
            }
        });

        final FilteredArrayAdapter<Contact> adapter = new FilteredArrayAdapter<Contact>(activity, android.R.layout.simple_list_item_1, contacts) {
            @Override
            protected boolean keepObject(Contact obj, String mask) {
                mask = mask.toLowerCase();
                return obj.getName().toLowerCase().contains(mask) || obj.getNumber().toLowerCase().contains(mask);
            }
        };
        contactSearch.setTokenListener(new TokenCompleteTextView.TokenListener() {
            @Override
            public void onTokenAdded(Object o) {
                contactSearch.addContact(o);
            }

            @Override
            public void onTokenRemoved(Object o) {
                contactSearch.removeContact(o);
            }
        });
        contactSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isAPhoneNumber(charSequence)) {
                    number.setNumber(charSequence.toString());
                    number.setName("#" + charSequence.toString());
                    adapter.notifyDataSetChanged();
                } else {
                    if (!number.getNumber().equals("")) {
                        number.setNumber("");
                        number.setName("");
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        contactSearch.setAdapter(adapter);
    }

    private void saveSMS(MaterialDialog smsPicker, ContactsCompletionView contactSearch) {
        String number = ((EditText) smsPicker.getView().findViewById(R.id.searchView)).getText().toString();
        String txt = ((EditText) smsPicker.getView().findViewById(R.id.smsTxt)).getText().toString();

        ArrayList<Location> temp = Singleton.getLocations();
        MessageEvent sms;
        sms = new MessageEvent(number, txt);
        sms.setName(resources.getString(R.string.sms_dialog_title));

        sms.setDescription(resources.getString(R.string.event_message_to) + " " + contactSearch.getNames());
        sms.setContacts(contactSearch.getContacts());
        if (when == IN) {
            temp.get(selectedPosition).getEventsIn().add(sms);
        } else {
            temp.get(selectedPosition).getEventsOut().add(sms);
        }

        Singleton.setLocations(temp);

        try {
            SaveAndLoad.saveInfo(activity);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MainMenu menu = MainMenu.getInstance();
        menu.notifyListView(selectedPosition);
    }

    private void showReminderPicker() {
        new MaterialDialog.Builder(activity)
                .title(R.string.reminder_dialog_title)
                .customView(R.layout.reminder_layout, true)
                .negativeText(android.R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .neutralText(R.string.action_back)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        eventDialogsStateMachine(EVENT_TYPE_STATE);
                        dialog.dismiss();
                    }
                })
                .positiveText(R.string.action_conclude)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        TextView txtView = ((TextView) dialog.getView().findViewById(R.id.reminderTxt));
                        if (txtView.getText().length() <= 0 || txtView.getText() == null) {
                            Toast.makeText(activity, R.string.reminder_dialog_warning, Toast.LENGTH_SHORT).show();
                        } else {
                            ArrayList<Location> temp = Singleton.getLocations();
                            NotificationEvent notificationEvent = new NotificationEvent(resources.getString(R.string.reminder_dialog_title) + "",
                                    txtView.getText().toString());
                            if (when == IN) {
                                temp.get(selectedPosition).getEventsIn().add(notificationEvent);
                            } else {
                                temp.get(selectedPosition).getEventsOut().add(notificationEvent);
                            }
                            Singleton.setLocations(temp);

                            try {
                                SaveAndLoad.saveInfo(activity);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            MainMenu menu = MainMenu.getInstance();
                            menu.notifyListView(selectedPosition);
                            dialog.dismiss();
                            floatingButtonManager.closeMenu();
                            setDefaults();
                        }
                    }
                })
                .autoDismiss(false)
                .show();


    }


    private boolean isAPhoneNumber(CharSequence charSequence) {
        if (charSequence.length() != 0) {
            if (charSequence.charAt(0) == '+' || Character.isDigit(charSequence.charAt(0))) {
                for (int i = 1; i < charSequence.length(); i++) {
                    if (!Character.isDigit(charSequence.charAt(i)))
                        return false;
                }
            } else {
                return false;
            }

            return true;
        }
        return false;
    }

    public ArrayList<Contact> getContactNumbers() {
        ArrayList<Contact> contacts = new ArrayList<>();
        Cursor phones = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        if (phones != null) {

            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contacts.add(new Contact(name, number));
            }

            phones.close();

        }

        return contacts;
    }

    public void showNoSMSTxtWarning(final MaterialDialog smsPicker, final ContactsCompletionView contactSearch) {
        final MaterialDialog alert = new MaterialDialog.Builder(activity)
                .title(R.string.sms_dialog_contact_empty_alert_title)
                .content(R.string.sms_dialog_contact_empty_alert_txt)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .show();
        View positive = alert.getActionButton(DialogAction.POSITIVE);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSMS(smsPicker, contactSearch);
                smsPicker.dismiss();
                alert.dismiss();
            }
        });

    }

    public void requestContactsPermissions() {

        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(activity, "Could not request permission to read contacts.", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        Utilities.REQUEST_READ_CONTACTS);
            }
        }
    }

    public void startEventPick() {
        eventDialogsStateMachine(INITIAL_STATE);
    }

    private void eventDialogsStateMachine(int state) {
        switch (state) {
            case INITIAL_STATE:
                showInOutPicker();
                break;
            case EVENT_TYPE_STATE:
                showEventPicker(state);
                break;
            case REMINDER:
                showReminderPicker();
                break;
            case SMS:
                requestContactsPermissions();
                break;
            case ALARM:
                showAlarmPicker();
                break;
            case SOUND_PROFILE:
                showSoundProfilePicker();
                break;
            case WIFI_PROFILE:
                showWiFiProfilePicker();
                break;
            case BLUETOOTH_PROFILE:
                showBluetoothProfilePicker();
                break;
            case MOBILE_DATA_PROFILE:
                showMobileDataProfilePicker();
                break;
        }

    }

    public static EventDialogsManager getInstance() {
        return instance;
    }

}
