package com.reachndo;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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

import java.io.IOException;
import java.util.ArrayList;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    private static final int IN = 0;
    private static final int OUT = 1;


    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    private int when;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);



        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        ArrayList<Location> l = Singleton.getLocations();
        ArrayList<String> s = new ArrayList<>();

        for(int i=0; i < l.size(); i++){
            s.add(l.get(i).getName());
        }

        mDrawerListView.setAdapter(new ArrayAdapter<String>(
                getActionBar().getThemedContext(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                s));
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if (!isAdded()) {
                    return;
                }

                ArrayList<Location> l = Singleton.getLocations();
                ArrayList<String> s = new ArrayList<>();

                for(int i=0; i < l.size(); i++){
                    s.add(l.get(i).getName());
                }

                mDrawerListView.setAdapter(new ArrayAdapter<String>(
                        getActionBar().getThemedContext(),
                        android.R.layout.simple_list_item_activated_1,
                        android.R.id.text1,
                        s));

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }


        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_example) {
            showInOutPicker();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showInOutPicker() {
        String type [] = {
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
        String type [] = {
                getResources().getString(R.string.event_picker_dialog_remind),
                getResources().getString(R.string.event_picker_dialog_sms),
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
                                showSMSPicker();
                                break;
                            case 2:
                                showSoundProfilePicker();
                                break;
                            case 3:
                                showWiFiProfilePicker();
                                break;
                            case 4:
                                showBluetoothProfilePicker();
                                break;
                            case 5:
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

    private void showWiFiProfilePicker() {
        if(existsProfileEvent(EventType.WIFI)) {
            Toast.makeText(getContext(), R.string.wifi_picker_dialog_warning ,Toast.LENGTH_LONG).show();
            return;
        }

        String type [] = {
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

                        MainMenu main =  MainMenu.getInstance();
                        main.notifyListView(mCurrentSelectedPosition);
                        return true;
                    }
                })
                .negativeText(android.R.string.cancel)
                .show();
    }

    private void showBluetoothProfilePicker() {
        if(existsProfileEvent(EventType.BLUETOOTH)) {
            Toast.makeText(getContext(), R.string.bluetooth_picker_dialog_warning ,Toast.LENGTH_LONG).show();
            return;
        }

        String type [] = {
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

                        MainMenu main =  MainMenu.getInstance();
                        main.notifyListView(mCurrentSelectedPosition);
                        return true;
                    }
                })
                .negativeText(android.R.string.cancel)
                .show();
    }

    private void showMobileDataProfilePicker() {
        if(existsProfileEvent(EventType.MOBILE_DATA)) {
            Toast.makeText(getContext(), R.string.mobile_data_picker_dialog_warning ,Toast.LENGTH_LONG).show();
            return;
        }

        String type [] = {
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

                        MainMenu main =  MainMenu.getInstance();
                        main.notifyListView(mCurrentSelectedPosition);
                        return true;
                    }
                })
                .negativeText(android.R.string.cancel)
                .show();
    }

    private void showSoundProfilePicker() {
        if(existsProfileEvent(EventType.SOUND_PROFILE)) {
            Toast.makeText(getContext(), R.string.sound_profile_picker_dialog_warning ,Toast.LENGTH_LONG).show();
            return;
        }

        String type [] = {
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
        if(when == IN) {
            for (Event e : temp.get(mCurrentSelectedPosition).getEventsIn()) {
                if (e.getType() == type)
                    return true;
            }
        }else {
            for (Event e : temp.get(mCurrentSelectedPosition).getEventsOut()) {
                if (e.getType() == type)
                    return true;
            }
        }
        return false;
    }

    private void showSMSPicker() {

        final ArrayList<Contact> contacts = getContactNumbers();
        final Contact number = new Contact("","");
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
                if(contactSearch.getContacts().size() == 0){
                    Toast.makeText(getContext(), R.string.sms_picker_contacts_warning, Toast.LENGTH_SHORT).show();
                    return;
                }

                String txt = ((EditText) smsPicker.getView().findViewById(R.id.smsTxt)).getText().toString();

                if(txt.length() == 0){
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
        MessageEvent sms = new MessageEvent(number, txt);
        sms.setName(getResources().getString(R.string.sms_dialog_title));

        sms.setDescription(getResources().getString(R.string.event_message_to) + " " + contactSearch.getNames());
        sms.setContacts(contactSearch.getContacts());
        if(when == IN) {
            temp.get(mCurrentSelectedPosition).getEventsIn().add(sms);
        }else{
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
                    if(when == IN) {
                        temp.get(mCurrentSelectedPosition).getEventsIn().add(notificationEvent);
                    }else{
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

    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        Typeface font2 = Typeface.createFromAsset(getContext().getAssets(), "fonts/WolfInTheCity.ttf");
        SpannableStringBuilder ss = new SpannableStringBuilder("Reach N' Do");
        ss.setSpan(new RelativeSizeSpan(2f), 0, ss.length(), 0);
        ss.setSpan(new CustomTypefaceSpan("", font2), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        actionBar.setTitle(ss);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    private boolean isAPhoneNumber(CharSequence charSequence) {
        if(charSequence.length() != 0) {
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
        ArrayList<Contact> contacts =  new ArrayList<>();
        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext())
        {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contacts.add(new Contact(name, number));
        }
        return contacts;
    }

    private void showNoSMSTxtWarning(final MaterialDialog smsPicker,final ContactsCompletionView contactSearch) {
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
}
