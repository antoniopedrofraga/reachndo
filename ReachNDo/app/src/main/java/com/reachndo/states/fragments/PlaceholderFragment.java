package com.reachndo.states.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconCompat;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.reachndo.Contact;
import com.reachndo.ContactsCompletionView;
import com.reachndo.R;
import com.reachndo.adapters.EventListAdapter;
import com.reachndo.states.MainMenu;
import com.service.AlarmEvent;
import com.service.BluetoothEvent;
import com.service.Event;
import com.service.EventType;
import com.service.Location;
import com.service.MessageEvent;
import com.service.MobileDataEvent;
import com.service.NotificationEvent;
import com.service.SaveAndLoad;
import com.service.Singleton;
import com.service.SoundProfileEvent;
import com.service.WiFiEvent;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;
import com.utilities.Utilities;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Pedro Fraga on 26-Aug-16.
 */
public class PlaceholderFragment extends Fragment {

    private int when;

    private int mCurrentSelectedPosition;

    private static final int IN = 0;
    private static final int OUT = 1;


    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 355;

    private static ListView listView;

    private MaterialMenuIconCompat materialMenu;

    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton floatingActionButtonLocals;
    private FloatingActionButton floatingActionButtonEvents;

    public TextView warningLocMainText;
    public TextView warningLocSubText;

    public TextView warningEvnMainText;
    public TextView warningEvnSubText;

    private EventListAdapter listAdapter;
    private AdapterView.OnItemClickListener clickListener;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static PlaceholderFragment fragment;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaceholderFragment() {
        listAdapter = MainMenu.getInstance().getListAdapter();
        clickListener = MainMenu.getInstance().getClickListener();

        warningEvnMainText = MainMenu.getInstance().getWarningEvnMainText();
        warningEvnSubText = MainMenu.getInstance().getWarningEvnSubText();

        warningLocMainText = MainMenu.getInstance().getWarningLocMainText();
        warningLocSubText = MainMenu.getInstance().getWarningLocSubText();

        materialMenu = MainMenu.getInstance().getMaterialMenu();

        mCurrentSelectedPosition = NavigationDrawerFragment.getInstance().getCurrentSelection();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);
        showFloatingActionButton(rootView);

        listView = (ListView) rootView.findViewById(R.id.list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(clickListener);

        warningLocMainText = (TextView) rootView.findViewById(R.id.txtLocView);
        warningLocSubText = (TextView) rootView.findViewById(R.id.subTxtLocView);
        warningEvnMainText = (TextView) rootView.findViewById(R.id.txtEvnView);
        warningEvnSubText = (TextView) rootView.findViewById(R.id.subTxtEvnView);

        NavigationDrawerFragment nd = NavigationDrawerFragment.getInstance();
        if (Singleton.getLocations().size() == 0) {
            warningLocMainText.setVisibility(View.VISIBLE);
            warningLocSubText.setVisibility(View.VISIBLE);
        } else if (Singleton.getLocations().get(nd.getCurrentSelection()).getEventsIn().size() == 0 &&
                Singleton.getLocations().get(nd.getCurrentSelection()).getEventsOut().size() == 0) {
            listAdapter.clear();
            listAdapter.notifyDataSetChanged();
            warningEvnMainText.setVisibility(View.VISIBLE);
            warningEvnSubText.setVisibility(View.VISIBLE);
        }
        return rootView;
    }


    public void showFloatingActionButton(View v) {

        floatingActionMenu = (FloatingActionMenu) v.findViewById(R.id.faMenu);
        floatingActionButtonEvents = (FloatingActionButton) v.findViewById(R.id.fabEvents);
        floatingActionButtonLocals = (FloatingActionButton) v.findViewById(R.id.fabLocals);

        if (floatingActionMenu != null) {

            floatingActionMenu.setClosedOnTouchOutside(true);

            floatingActionButtonLocals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!Utilities.isNetworkAvailable(getContext())) {
                        final MaterialDialog alert = new MaterialDialog.Builder(getContext())
                                .title(R.string.wifi_on_dialog_title)
                                .content(R.string.wifi_on_dialog_warning)
                                .positiveText(android.R.string.yes)
                                .negativeText(android.R.string.no)
                                .show();
                        View positive = alert.getActionButton(DialogAction.POSITIVE);
                        positive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                                alert.dismiss();
                            }
                        });
                        return;
                    }

                    if (!Utilities.isLocationEnabled(getContext())) {
                        final MaterialDialog alert = new MaterialDialog.Builder(getContext())
                                .title(R.string.location_on_dialog_title)
                                .content(R.string.location_on_dialog_warning)
                                .positiveText(android.R.string.yes)
                                .negativeText(android.R.string.no)
                                .show();
                        View positive = alert.getActionButton(DialogAction.POSITIVE);
                        positive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alert.dismiss();
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        });
                        View negative = alert.getActionButton(DialogAction.NEGATIVE);
                        negative.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alert.dismiss();
                                startLocationPick();
                            }
                        });
                        return;
                    }

                    startLocationPick();
                }
            });

            if (Singleton.getLocations().size() == 0) {
                floatingActionButtonEvents.setEnabled(false);
            } else {
                floatingActionButtonEvents.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showInOutPicker();
                    }
                });
            }
        } else
            Log.d("Debug", "Error in mFab (NULL)");

    }

    public void startLocationPick() {
        try {
            floatingActionButtonLocals.setIndeterminate(true);
            PlacePicker.IntentBuilder intentBuilder;
            intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(getContext());
            startActivityForResult(intent, Utilities.REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException e) {
            Log.d("PlacesAPI Demo", "GooglePlayServicesRepairableException thrown");
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d("PlacesAPI Demo", "GooglePlayServicesNotAvailableException thrown");
        }
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {

        switch (requestCode) {
            case Utilities.REQUEST_PLACE_PICKER:
                floatingActionButtonLocals.setIndeterminate(false);
                if (resultCode == Activity.RESULT_OK) {
                    // The user has selected a place. Extract the name and address.
                    final Place place = PlacePicker.getPlace(data, getContext());
                    showLocationNamePicker(place);

                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }
                break;
        }

    }

    private void showLocationNamePicker(final Place selectedLocation) {
        final MaterialDialog locationPicker =
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.location_picker_title)
                        .customView(R.layout.location_picker_layout, true)
                        .positiveText(android.R.string.ok)
                        .autoDismiss(false)
                        .negativeText(android.R.string.cancel)
                        .cancelable(false)
                        .show();

        View positive = locationPicker.getActionButton(DialogAction.POSITIVE);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText radius = ((EditText) locationPicker.getView().findViewById(R.id.radiusText));
                EditText name = ((EditText) locationPicker.getView().findViewById(R.id.nameTxt));
                if (name.getText().length() == 0) {
                    new MaterialDialog.Builder(getContext())
                            .title(R.string.location_picker_warning_title)
                            .content(R.string.location_picker_warning_name_content)
                            .neutralText(android.R.string.ok)
                            .show();
                    return;
                } else if (radius.getText().length() == 0 || Integer.parseInt(radius.getText().toString()) == 0) {
                    new MaterialDialog.Builder(getContext())
                            .title(R.string.location_picker_warning_title)
                            .content(R.string.location_picker_warning_radius_content)
                            .neutralText(android.R.string.ok)
                            .show();
                    return;
                }

                Log.d("Debug Location - add", Singleton.getLocations().size() + "");

                ArrayList<Location> temp = Singleton.getLocations();
                Location newLocation = new Location(selectedLocation.getLatLng().latitude,
                        selectedLocation.getLatLng().longitude,
                        name.getText().toString(),
                        Double.parseDouble(radius.getText().toString()), false);
                temp.add(newLocation);
                Singleton.setLocations(temp);
                try {
                    SaveAndLoad.saveInfo(getContext());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("Debug Location added", Singleton.getLocations().size() + "");
                NavigationDrawerFragment nd = NavigationDrawerFragment.getInstance();
                nd.setLocationAdapter((ArrayList<Location>) Singleton.getLocations());
                nd.selectItem(Singleton.getLocations().size() - 1);
                MainMenu menu = MainMenu.getInstance();
                menu.updateTitle(name.getText().toString());
                locationPicker.dismiss();
            }
        });

        View negative = locationPicker.getActionButton(DialogAction.NEGATIVE);
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationPicker.dismiss();
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        materialMenu.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainMenu) getActivity()).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    private void showInOutPicker() {
        String type[] = {
                getResources().getString(R.string.in_out_picker_dialog_in),
                getResources().getString(R.string.in_out_picker_dialog_out)
        };

        new MaterialDialog.Builder(getContext())
                .title(R.string.in_out_picker_dialog_title)
                .items(type)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        when = which;
                        showEventPicker();
                        return true;
                    }


                })
                .negativeText(android.R.string.cancel)
                .show();
    }

    private void showEventPicker() {
        String type[] = {
                getResources().getString(R.string.event_picker_dialog_remind),
                getResources().getString(R.string.event_picker_dialog_sms),
                getResources().getString(R.string.alarm_dialog_title),
                getResources().getString(R.string.event_picker_dialog_sound_profile),
                getResources().getString(R.string.event_picker_dialog_wifi),
                getResources().getString(R.string.event_picker_dialog_bluetooth),
                getResources().getString(R.string.event_picker_dialog_mobile_data)
        };

        new MaterialDialog.Builder(getContext())
                .title(R.string.event_picker_dialog_title)
                .items(type)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                showReminderPicker();
                                break;
                            case 1:
                                requestContactsPermissions();
                                break;
                            case 2:
                                showAlarmPicker();
                                break;
                            case 3:
                                showSoundProfilePicker();
                                break;
                            case 4:
                                showWiFiProfilePicker();
                                break;
                            case 5:
                                showBluetoothProfilePicker();
                                break;
                            case 6:
                                showMobileDataProfilePicker();
                                break;
                            default:
                                return false;
                        }
                        return true;
                    }


                })
                .negativeText(android.R.string.cancel)
                .show();
    }

    private void showAlarmPicker() {
        final MaterialDialog alarmPicker = new MaterialDialog.Builder(getContext())
                .title(R.string.alarm_dialog_title)
                .customView(R.layout.alarm_layout, true)
                .negativeText(android.R.string.cancel)
                .positiveText(android.R.string.ok)
                .autoDismiss(false)
                .build();

        alarmPicker.show();

        View negative = alarmPicker.getActionButton(DialogAction.NEGATIVE);
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarmPicker.dismiss();
            }
        });

        View positive = alarmPicker.getActionButton(DialogAction.POSITIVE);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView txtView = ((TextView) alarmPicker.getView().findViewById(R.id.alarmTxt));
                if (txtView.getText().length() <= 0 || txtView.getText() == null) {
                    Toast.makeText(getContext(), R.string.alarm_dialog_warning, Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<Location> temp = Singleton.getLocations();
                    AlarmEvent alarmEvent = new AlarmEvent(getContext(), txtView.getText().toString());
                    alarmEvent.setName(getResources().getString(R.string.alarm_dialog_title));

                    if (when == IN) {
                        temp.get(mCurrentSelectedPosition).getEventsIn().add(alarmEvent);
                    } else {
                        temp.get(mCurrentSelectedPosition).getEventsOut().add(alarmEvent);
                    }
                    Singleton.setLocations(temp);

                    try {
                        SaveAndLoad.saveInfo(getContext());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    MainMenu menu = MainMenu.getInstance();
                    menu.notifyListView(mCurrentSelectedPosition);
                    alarmPicker.dismiss();
                }
            }
        });
    }

    private void showWiFiProfilePicker() {
        if (existsProfileEvent(EventType.WIFI)) {
            Toast.makeText(getContext(), R.string.wifi_picker_dialog_warning, Toast.LENGTH_LONG).show();
            return;
        }

        String type[] = {
                getResources().getString(R.string.wifi_picker_dialog_on),
                getResources().getString(R.string.wifi_picker_dialog_off)
        };

        new MaterialDialog.Builder(getContext())
                .title(R.string.wifi_picker_dialog_title)
                .items(type)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        WiFiEvent wifiEvent = new WiFiEvent(which);
                        wifiEvent.setName(getResources().getString(R.string.wifi_picker_dialog_title));
                        ArrayList<Location> temp = Singleton.getLocations();
                        switch (which) {
                            case WiFiEvent.ON:
                                wifiEvent.setDescription(getResources().getString(R.string.wifi_picker_dialog_on));
                                break;
                            case WiFiEvent.OFF:
                                wifiEvent.setDescription(getResources().getString(R.string.wifi_picker_dialog_off));
                                break;
                        }

                        if (when == IN) {
                            temp.get(mCurrentSelectedPosition).getEventsIn().add(wifiEvent);
                        } else {
                            temp.get(mCurrentSelectedPosition).getEventsOut().add(wifiEvent);
                        }
                        Singleton.setLocations(temp);
                        Singleton.setLocations(temp);

                        try {
                            SaveAndLoad.saveInfo(getContext());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        MainMenu main = MainMenu.getInstance();
                        main.notifyListView(mCurrentSelectedPosition);
                        return true;
                    }
                })
                .negativeText(android.R.string.cancel)
                .show();
    }

    private void showBluetoothProfilePicker() {
        if (existsProfileEvent(EventType.BLUETOOTH)) {
            Toast.makeText(getContext(), R.string.bluetooth_picker_dialog_warning, Toast.LENGTH_LONG).show();
            return;
        }

        String type[] = {
                getResources().getString(R.string.bluetooth_picker_dialog_on),
                getResources().getString(R.string.bluetooth_picker_dialog_off)
        };

        new MaterialDialog.Builder(getContext())
                .title(R.string.bluetooth_picker_dialog_title)
                .items(type)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        BluetoothEvent bluetoothEvent = new BluetoothEvent(which);
                        bluetoothEvent.setName(getResources().getString(R.string.bluetooth_picker_dialog_title));
                        ArrayList<Location> temp = Singleton.getLocations();
                        switch (which) {
                            case BluetoothEvent.ON:
                                bluetoothEvent.setDescription(getResources().getString(R.string.bluetooth_picker_dialog_on));
                                break;
                            case BluetoothEvent.OFF:
                                bluetoothEvent.setDescription(getResources().getString(R.string.bluetooth_picker_dialog_off));
                                break;
                        }

                        if (when == IN) {
                            temp.get(mCurrentSelectedPosition).getEventsIn().add(bluetoothEvent);
                        } else {
                            temp.get(mCurrentSelectedPosition).getEventsOut().add(bluetoothEvent);
                        }
                        Singleton.setLocations(temp);
                        Singleton.setLocations(temp);

                        try {
                            SaveAndLoad.saveInfo(getContext());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        MainMenu main = MainMenu.getInstance();
                        main.notifyListView(mCurrentSelectedPosition);
                        return true;
                    }
                })
                .negativeText(android.R.string.cancel)
                .show();
    }

    private void showMobileDataProfilePicker() {
        if (existsProfileEvent(EventType.MOBILE_DATA)) {
            Toast.makeText(getContext(), R.string.mobile_data_picker_dialog_warning, Toast.LENGTH_LONG).show();
            return;
        }

        String type[] = {
                getResources().getString(R.string.mobile_data_picker_dialog_on),
                getResources().getString(R.string.mobile_data_picker_dialog_off)
        };

        new MaterialDialog.Builder(getContext())
                .title(R.string.mobile_data_picker_dialog_title)
                .items(type)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        MobileDataEvent mobileDataEvent = new MobileDataEvent(which);
                        mobileDataEvent.setName(getResources().getString(R.string.mobile_data_picker_dialog_title));
                        ArrayList<Location> temp = Singleton.getLocations();
                        switch (which) {
                            case MobileDataEvent.ON:
                                mobileDataEvent.setDescription(getResources().getString(R.string.mobile_data_picker_dialog_on));
                                break;
                            case MobileDataEvent.OFF:
                                mobileDataEvent.setDescription(getResources().getString(R.string.mobile_data_picker_dialog_off));
                                break;
                        }

                        if (when == IN) {
                            temp.get(mCurrentSelectedPosition).getEventsIn().add(mobileDataEvent);
                        } else {
                            temp.get(mCurrentSelectedPosition).getEventsOut().add(mobileDataEvent);
                        }
                        Singleton.setLocations(temp);
                        Singleton.setLocations(temp);

                        try {
                            SaveAndLoad.saveInfo(getContext());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        MainMenu main = MainMenu.getInstance();
                        main.notifyListView(mCurrentSelectedPosition);
                        return true;
                    }
                })
                .negativeText(android.R.string.cancel)
                .show();
    }

    private void showSoundProfilePicker() {
        if (existsProfileEvent(EventType.SOUND_PROFILE)) {
            Toast.makeText(getContext(), R.string.sound_profile_picker_dialog_warning, Toast.LENGTH_LONG).show();
            return;
        }

        String type[] = {
                getResources().getString(R.string.sound_profile_picker_dialog_silent),
                getResources().getString(R.string.sound_profile_picker_dialog_vibrate),
                getResources().getString(R.string.sound_profile_picker_dialog_normal)
        };

        new MaterialDialog.Builder(getContext())
                .title(R.string.sound_profile_picker_dialog_title)
                .items(type)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        SoundProfileEvent spEvent = new SoundProfileEvent(which);
                        spEvent.setName(getResources().getString(R.string.sound_profile_picker_dialog_title));
                        String changeTo = getResources().getString(R.string.sound_profile_picker_dialog_change) + " ";
                        ArrayList<Location> temp = Singleton.getLocations();
                        switch (which) {
                            case SoundProfileEvent.SILENT:
                                spEvent.setDescription(changeTo +
                                        getResources().getString(R.string.sound_profile_picker_dialog_silent));
                                break;
                            case SoundProfileEvent.VIBRATE:
                                spEvent.setDescription(changeTo +
                                        getResources().getString(R.string.sound_profile_picker_dialog_vibrate));
                                break;
                            case SoundProfileEvent.NORMAL:
                                spEvent.setDescription(changeTo +
                                        getResources().getString(R.string.sound_profile_picker_dialog_normal));
                                break;
                        }

                        if (when == IN) {
                            temp.get(mCurrentSelectedPosition).getEventsIn().add(spEvent);
                        } else {
                            temp.get(mCurrentSelectedPosition).getEventsOut().add(spEvent);
                        }

                        Singleton.setLocations(temp);

                        try {
                            SaveAndLoad.saveInfo(getContext());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        MainMenu menu = MainMenu.getInstance();
                        menu.notifyListView(mCurrentSelectedPosition);
                        return true;
                    }
                })
                .negativeText(android.R.string.cancel)
                .show();
    }

    private boolean existsProfileEvent(EventType type) {
        ArrayList<Location> temp = Singleton.getLocations();
        if (when == IN) {
            for (Event e : temp.get(mCurrentSelectedPosition).getEventsIn()) {
                if (e.getType() == type)
                    return true;
            }
        } else {
            for (Event e : temp.get(mCurrentSelectedPosition).getEventsOut()) {
                if (e.getType() == type)
                    return true;
            }
        }
        return false;
    }

    private void showSMSPicker() {

        final ArrayList<Contact> contacts = getContactNumbers();
        final Contact number = new Contact("", "");
        contacts.add(0, number);

        boolean wrapInScrollView = true;
        final MaterialDialog smsPicker =
                new MaterialDialog.Builder(getContext())
                        .title(R.string.sms_dialog_title)
                        .customView(R.layout.sms_layout, wrapInScrollView)
                        .positiveText(android.R.string.ok)
                        .autoDismiss(false)
                        .negativeText(android.R.string.cancel)
                        .show();

        final ContactsCompletionView contactSearch = (ContactsCompletionView) smsPicker.getCustomView().findViewById(R.id.searchView);

        View positive = smsPicker.getActionButton(DialogAction.POSITIVE);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contactSearch.getContacts().size() == 0) {
                    Toast.makeText(getContext(), R.string.sms_picker_contacts_warning, Toast.LENGTH_SHORT).show();
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

        final FilteredArrayAdapter<Contact> adapter = new FilteredArrayAdapter<Contact>(getContext(), android.R.layout.simple_list_item_1, contacts) {
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
                    if (number.getNumber() != "") {
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
        sms.setName(getResources().getString(R.string.sms_dialog_title));

        sms.setDescription(getResources().getString(R.string.event_message_to) + " " + contactSearch.getNames());
        sms.setContacts(contactSearch.getContacts());
        if (when == IN) {
            temp.get(mCurrentSelectedPosition).getEventsIn().add(sms);
        } else {
            temp.get(mCurrentSelectedPosition).getEventsOut().add(sms);
        }

        Singleton.setLocations(temp);

        try {
            SaveAndLoad.saveInfo(getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        MainMenu menu = MainMenu.getInstance();
        menu.notifyListView(mCurrentSelectedPosition);
    }

    private void showReminderPicker() {
        final MaterialDialog reminderPicker = new MaterialDialog.Builder(getContext())
                .title(R.string.reminder_dialog_title)
                .customView(R.layout.reminder_layout, true)
                .negativeText(android.R.string.cancel)
                .positiveText(android.R.string.ok)
                .autoDismiss(false)
                .build();

        reminderPicker.show();

        View negative = reminderPicker.getActionButton(DialogAction.NEGATIVE);
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reminderPicker.dismiss();
            }
        });

        View positive = reminderPicker.getActionButton(DialogAction.POSITIVE);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView txtView = ((TextView) reminderPicker.getView().findViewById(R.id.reminderTxt));
                if (txtView.getText().length() <= 0 || txtView.getText() == null) {
                    Toast.makeText(getContext(), R.string.reminder_dialog_warning, Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<Location> temp = Singleton.getLocations();
                    NotificationEvent notificationEvent = new NotificationEvent(getResources().getString(R.string.reminder_dialog_title) + "",
                            txtView.getText().toString());
                    if (when == IN) {
                        temp.get(mCurrentSelectedPosition).getEventsIn().add(notificationEvent);
                    } else {
                        temp.get(mCurrentSelectedPosition).getEventsOut().add(notificationEvent);
                    }
                    Singleton.setLocations(temp);

                    try {
                        SaveAndLoad.saveInfo(getContext());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    MainMenu menu = MainMenu.getInstance();
                    menu.notifyListView(mCurrentSelectedPosition);
                    reminderPicker.dismiss();
                }
            }
        });


    }


    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
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
        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contacts.add(new Contact(name, number));
        }
        return contacts;
    }

    private void showNoSMSTxtWarning(final MaterialDialog smsPicker, final ContactsCompletionView contactSearch) {
        final MaterialDialog alert = new MaterialDialog.Builder(getContext())
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

    private void requestContactsPermissions() {

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(getContext(), "Could not request permission to read contacts.", Toast.LENGTH_SHORT);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    showSMSPicker();

                } else {

                    Toast.makeText(getContext(), "Since the permission to read contacts was not granted you can't access this functionality.", Toast.LENGTH_SHORT);
                }
                return;
            }
            default:
                Toast.makeText(getContext(), "WRONG CODE", Toast.LENGTH_SHORT);
                break;
        }
    }
}


