package com.reachndo.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.reachndo.adapters.EventListAdapter;
import com.reachndo.managers.ActionBarManager;
import com.reachndo.fragments.NavigationDrawerFragment;
import com.reachndo.R;
import com.reachndo.fragments.PlaceholderFragment;
import com.reachndo.managers.EventDialogsManager;
import com.reachndo.managers.FloatingButtonManager;
import com.reachndo.managers.WarningTextsManager;
import com.service.Event;
import com.service.Location;
import com.service.LocationService;
import com.service.MessageEvent;
import com.reachndo.memory.SaveAndLoad;
import com.reachndo.memory.Singleton;
import com.utilities.CustomTypefaceSpan;
import com.utilities.Theme;
import com.utilities.Utilities;

import java.io.IOException;
import java.util.ArrayList;

public class MainMenu extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private ActionBarManager actionBarManager;
    private WarningTextsManager warningTextsManager;
    private EventDialogsManager eventDialogsManager;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private EventListAdapter listAdapter;
    private AdapterView.OnItemClickListener clickListener;

    private static MainMenu instance;


    public static MainMenu getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Theme.setThemeAccordingAPI(this);
        super.onCreate(savedInstanceState);
        instance = this;

        listAdapter = new EventListAdapter(getBaseContext(), new ArrayList<Event>());
        clickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        };

        actionBarManager = new ActionBarManager(getSupportActionBar(), this);
        warningTextsManager = new WarningTextsManager();

        SaveAndLoad.loadInfo(this);

        setContentView(R.layout.activity_main_menu);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        Theme.showActionOverflowMenu(this);

        int currentSelection = NavigationDrawerFragment.getInstance().getCurrentSelection();
        eventDialogsManager = new EventDialogsManager(this, currentSelection);

        startService(new Intent(this, LocationService.class));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();

    }

    public void onSectionAttached(int index) {
        int locationIndex = -1;
        CharSequence locationName;
        if (index > 0) {
            locationName = Singleton.getLocations().get(index - 1).getName();
            locationIndex = index - 1;
        } else {
            locationName = getResources().getString(R.string.no_locations);
        }
        actionBarManager.updateTitle(locationName);
        notifyListView(locationIndex);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main_menu, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {

        switch (requestCode) {
            case Utilities.REQUEST_PLACE_PICKER:
                FloatingButtonManager.getInstance().getLocationsButton().setIndeterminate(false);
                if (resultCode == Activity.RESULT_OK) {
                    // The user has selected a place. Extract the name and address.
                    final Place place = PlacePicker.getPlace(data, this);
                    showLocationNamePicker(place);

                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(MainMenu.this, Settings.class));
                break;
            case R.id.about_us:
                
                String version = getString(R.string.version) + " " + getString(R.string.versionName);
                MaterialDialog dialog = new MaterialDialog.Builder(this)
                        /*.typeface("WolfInTheCityLight.ttf", null)
                        .titleColor(getColor(R.color.SlateGray))*/
                        .icon(getDrawable(R.drawable.ic_launcher))
                        .limitIconToDefaultSize()
                        .title(R.string.app_name)
                        .content(version)
                        .contentGravity(GravityEnum.CENTER)
                        .show();
                //startActivity(new Intent(MainMenu.this, About.class));
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    public void removeLocation(int position) {
        Singleton.getLocations().remove(position);

        NavigationDrawerFragment nd = NavigationDrawerFragment.getInstance();
        if (position == nd.getCurrentSelection()) {
            if (Singleton.getLocations().size() != 0) {
                nd.selectItem(0);
            } else {
                nd.selectItem(-1);
            }
        } else if (position < nd.getCurrentSelection()) {
            nd.selectItem(nd.getCurrentSelection() - 1);
        }
        try {
            SaveAndLoad.saveInfo(MainMenu.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void notifyListView(final
                               int index) {

        if (index == -1 && listAdapter != null) {
            listAdapter.clear();
            listAdapter.notifyDataSetChanged();
            return;
        }

        warningTextsManager.setWarningEvnVisible(index);

        if (listAdapter != null) {
            listAdapter.clear();
            if (Singleton.getLocations().get(index).getEventsIn().size() != 0) {
                listAdapter.add(new Event(getResources().getString(R.string.in)));
                listAdapter.addAll(Singleton.getLocations().get(index).getEventsIn());
            }

            if (Singleton.getLocations().get(index).getEventsOut().size() != 0) {
                listAdapter.add(new Event(getResources().getString(R.string.out)));
                listAdapter.addAll(Singleton.getLocations().get(index).getEventsOut());
            }
        }

        clickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showDialog(listAdapter.getItem(i), index);
            }
        };

        listAdapter.notifyDataSetChanged();
    }

    public void showDialog(final Event event, final int index) {
        String title;
        String content;
        switch (event.getType()) {
            case MESSAGE:
                title = getResources().getString(R.string.sms_dialog_title);
                content = event.getDescription() + "\n\n" + getResources().getString(R.string.sms_dialog_text_info)
                        + " " + ((MessageEvent) event).getTextMessage();
                break;
            case NOTIFICATION:
                title = event.getName();
                content = getResources().getString(R.string.sms_dialog_text_info) + " " +  event.getDescription();
                break;
            default:
                title = event.getName();
                content = event.getDescription();
                break;
        }
        final MaterialDialog infoDialog = new MaterialDialog.Builder(MainMenu.this)
                .title(title)
                .content(content)
                .neutralText(android.R.string.ok)
                .negativeText(R.string.action_delete)
                .show();

        View negative = infoDialog.getActionButton(DialogAction.NEGATIVE);
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Singleton.getLocations().get(index).removeEvent(event);

                warningTextsManager.setWarningEvnVisible(index);

                try {
                    SaveAndLoad.saveInfo(MainMenu.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                infoDialog.dismiss();
                notifyListView(index);
            }
        });
    }

    private void showLocationNamePicker(final Place selectedLocation) {
        final Activity activity = this;
        final MaterialDialog locationPicker =
                new MaterialDialog.Builder(activity)
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
                    new MaterialDialog.Builder(activity)
                            .title(R.string.location_picker_warning_title)
                            .content(R.string.location_picker_warning_name_content)
                            .neutralText(android.R.string.ok)
                            .show();
                    return;
                } else if (radius.getText().length() == 0 || Integer.parseInt(radius.getText().toString()) == 0) {
                    new MaterialDialog.Builder(activity)
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
                    SaveAndLoad.saveInfo(activity);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("Debug Location added", Singleton.getLocations().size() + "");
                NavigationDrawerFragment nd = NavigationDrawerFragment.getInstance();
                nd.setLocationAdapter(Singleton.getLocations());
                nd.selectItem(Singleton.getLocations().size() - 1);
                MainMenu.getInstance().getActionBarManager().updateTitle(name.getText().toString());
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

    /* Getters */

    public EventListAdapter getListAdapter() {
        return listAdapter;
    }

    public AdapterView.OnItemClickListener getClickListener() {
        return clickListener;
    }

    public ActionBarManager getActionBarManager() {
        return actionBarManager;
    }

    public WarningTextsManager getWarningTextsManager() {
        return warningTextsManager;
    }

    public EventDialogsManager getEventDialogsManager() {
        return eventDialogsManager;
    }
}