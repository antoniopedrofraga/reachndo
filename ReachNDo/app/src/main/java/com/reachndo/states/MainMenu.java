package com.reachndo.states;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconCompat;
import com.reachndo.adapters.EventListAdapter;
import com.reachndo.managers.ActionBarManager;
import com.reachndo.states.fragments.NavigationDrawerFragment;
import com.reachndo.R;
import com.reachndo.states.fragments.PlaceholderFragment;
import com.service.Event;
import com.service.LocationService;
import com.service.MessageEvent;
import com.reachndo.memory.SaveAndLoad;
import com.reachndo.memory.Singleton;
import com.utilities.Theme;

import java.io.IOException;
import java.util.ArrayList;

public class MainMenu extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    ActionBarManager actionBarManager;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private MaterialMenuIconCompat materialMenu;

    public TextView warningLocMainText;
    public TextView warningLocSubText;

    public TextView warningEvnMainText;
    public TextView warningEvnSubText;

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

        actionBarManager = new ActionBarManager(getSupportActionBar(), this);

        materialMenu = new MaterialMenuIconCompat(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);
        listAdapter = new EventListAdapter(getBaseContext(), new ArrayList<Event>());
        clickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        };

        SaveAndLoad.loadInfo(this);

        setContentView(R.layout.activity_main_menu);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        Theme.showActionOverflowMenu(this);

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
            materialMenu.animateState(MaterialMenuDrawable.IconState.BURGER);
            return true;
        } else {
            if (Singleton.getLocations().size() != 0)
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

        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(MainMenu.this, SettingsActivity.class));
                break;
            case R.id.about_us:
                startActivity(new Intent(MainMenu.this, AboutUsActivity.class));
                break;
            case android.R.id.home:
                if (mNavigationDrawerFragment.isDrawerOpen())
                    materialMenu.animateState(MaterialMenuDrawable.IconState.BURGER);
                 else
                    materialMenu.animateState(MaterialMenuDrawable.IconState.ARROW);
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

        if (warningEvnMainText != null && warningEvnSubText != null &&
                (Singleton.getLocations().get(index).getEventsIn().size() != 0 ||
                        Singleton.getLocations().get(index).getEventsOut().size() != 0)) {
            warningEvnMainText.setVisibility(View.INVISIBLE);
            warningEvnSubText.setVisibility(View.INVISIBLE);
        }

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

                if (warningEvnMainText != null && warningEvnSubText != null &&
                        (Singleton.getLocations().get(index).getEventsIn().size() == 0 &&
                                Singleton.getLocations().get(index).getEventsOut().size() == 0)) {
                    warningEvnMainText.setVisibility(View.VISIBLE);
                    warningEvnSubText.setVisibility(View.VISIBLE);
                }

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

    /* Getters */

    public EventListAdapter getListAdapter() {
        return listAdapter;
    }

    public AdapterView.OnItemClickListener getClickListener() {
        return clickListener;
    }

    public TextView getWarningLocMainText() {
        return warningLocMainText;
    }

    public TextView getWarningLocSubText() {
        return warningLocSubText;
    }

    public TextView getWarningEvnSubText() {
        return warningEvnSubText;
    }

    public TextView getWarningEvnMainText() {
        return warningEvnMainText;
    }

    public MaterialMenuIconCompat getMaterialMenu() {
        return materialMenu;
    }

    public ActionBarManager getActionBarManager() {
        return actionBarManager;
    }

}