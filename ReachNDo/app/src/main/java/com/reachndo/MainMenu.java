package com.reachndo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconCompat;
import com.faizmalkani.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.service.Event;
import com.service.Location;
import com.service.LocationService;
import com.service.SaveAndLoad;
import com.service.Singleton;

import java.io.IOException;
import java.util.ArrayList;

public class MainMenu extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    private static ListView listView;
    private static EventListAdapter listAdapter;
    private static MaterialMenuIconCompat materialMenu;

    private static MainMenu instance;

    private static final int REQUEST_PLACE_PICKER = 1;

    public static MainMenu getInstance(){
        return instance;
    }

    public EventListAdapter getEventAdapter(){
        return listAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        startService(new Intent(this, LocationService.class));

        //Setting style according to API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setTheme(R.style.MaterialDesign);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.DarkMaterialPurple));
        }

        try {
            SaveAndLoad.loadInfo(this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        materialMenu = new MaterialMenuIconCompat(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);


        setContentView(R.layout.activity_main_menu);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        listAdapter = new EventListAdapter(getBaseContext(), new ArrayList<Event>());

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
        if(index > 0) {
            mTitle = Singleton.getLocations().get(index - 1).getName();
            listAdapter.clear();
            listAdapter.addAll(Singleton.getLocations().get(index - 1).getEvents());
            listAdapter.notifyDataSetChanged();
        }else mTitle = "No Locations";
    }

    public void updateActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main_menu, menu);
            updateActionBar();
            materialMenu.animateState(MaterialMenuDrawable.IconState.BURGER);
            return true;
        }else{
            materialMenu.animateState(MaterialMenuDrawable.IconState.ARROW);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id  == android.R.id.home) {
            if (mNavigationDrawerFragment.isDrawerOpen()) {
                materialMenu.animateState(MaterialMenuDrawable.IconState.BURGER);
            }else{
                materialMenu.animateState(MaterialMenuDrawable.IconState.ARROW);
            }
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
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
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);
            showFloatingActionButton(rootView);
            listView = (ListView) rootView.findViewById(R.id.list);
            listView.setAdapter(listAdapter);
            return rootView;
        }


        public void showFloatingActionButton(View v) {
            FloatingActionButton mFab = (FloatingActionButton) v.findViewById(R.id.fab);
            if(mFab != null) {
                mFab.setDrawable(getResources().getDrawable(R.drawable.ic_plusicon));
                mFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {

                            PlacePicker.IntentBuilder intentBuilder;
                            intentBuilder = new PlacePicker.IntentBuilder();
                            Intent intent = intentBuilder.build(getContext());
                            startActivityForResult(intent, REQUEST_PLACE_PICKER);

                        } catch ( GooglePlayServicesRepairableException e ) {
                            Log.d( "PlacesAPI Demo", "GooglePlayServicesRepairableException thrown" );
                        } catch ( GooglePlayServicesNotAvailableException e ) {
                            Log.d( "PlacesAPI Demo", "GooglePlayServicesNotAvailableException thrown" );
                        }
                    }
                });
            }
            else
                Log.d("Debug", "Error in mFab (NULL)");

        }

        @Override
        public void onActivityResult(int requestCode,
                                        int resultCode, Intent data) {

            if (requestCode == REQUEST_PLACE_PICKER
                    && resultCode == Activity.RESULT_OK) {


                // The user has selected a place. Extract the name and address.
                final Place place = PlacePicker.getPlace(data, getContext());
                showLocationNamePicker(place);

            } else {
                super.onActivityResult(requestCode, resultCode, data);
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
                    EditText radius = ((EditText)locationPicker.getView().findViewById(R.id.radiusText));
                    EditText name = ((EditText)locationPicker.getView().findViewById(R.id.nameTxt));
                    if(name.getText().length() == 0){
                        new MaterialDialog.Builder(getContext())
                                .title(R.string.location_picker_warning_title)
                                .content(R.string.location_picker_warning_name_content)
                                .neutralText(android.R.string.ok)
                                .show();
                        return;
                    }else if(radius.getText().length() == 0 || Integer.parseInt(radius.getText().toString()) == 0){
                        new MaterialDialog.Builder(getContext())
                                .title(R.string.location_picker_warning_title)
                                .content(R.string.location_picker_warning_radius_content)
                                .neutralText(android.R.string.ok)
                                .show();
                        return;
                    }

                    Log.d("Debug Location - add", Singleton.getLocations().size() + "");

                    ArrayList<Location> temp = Singleton.getLocations();
                    Location newLocation = new Location(selectedLocation.getLatLng().longitude,
                            selectedLocation.getLatLng().latitude,
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
            ((MainMenu) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }


}
