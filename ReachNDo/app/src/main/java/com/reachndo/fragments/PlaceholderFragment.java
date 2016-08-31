package com.reachndo.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.reachndo.R;
import com.reachndo.adapters.EventListAdapter;
import com.reachndo.managers.ActionBarManager;
import com.reachndo.activities.MainMenu;
import com.reachndo.managers.EventDialogsManager;
import com.reachndo.managers.FloatingButtonManager;
import com.reachndo.managers.WarningTextsManager;
import com.service.Location;
import com.reachndo.memory.SaveAndLoad;
import com.reachndo.memory.Singleton;
import com.utilities.Utilities;

import java.io.IOException;
import java.util.ArrayList;


public class PlaceholderFragment extends Fragment {

    protected ListView listView;

    private ActionBarManager actionBarManager;
    private WarningTextsManager warningTextsManager;
    private FloatingButtonManager floatingButtonManager;
    private EventDialogsManager eventDialogsManager;

    private EventListAdapter listAdapter;
    private AdapterView.OnItemClickListener clickListener;


    private static final String ARG_SECTION_NUMBER = "section_number";
    private static PlaceholderFragment fragment;


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

        actionBarManager = MainMenu.getInstance().getActionBarManager();
        warningTextsManager = MainMenu.getInstance().getWarningTextsManager();
        floatingButtonManager = new FloatingButtonManager();
        eventDialogsManager = MainMenu.getInstance().getEventDialogsManager();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);


        floatingButtonManager.getViews(rootView);
        floatingButtonManager.setClickListeners();

        listView = (ListView) rootView.findViewById(R.id.list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(clickListener);

        warningTextsManager.getViews(rootView);
        warningTextsManager.updateTextsVisibility(listAdapter);

        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        actionBarManager.getMaterialMenu().onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainMenu) getActivity()).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Utilities.REQUEST_READ_CONTACTS: {
                /* If request is cancelled, the result arrays are empty. */
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    eventDialogsManager.showSMSPicker();

                } else {

                    Toast.makeText(getContext(), "Since the permission to read contacts was not granted you can't access this functionality.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            default:
                Toast.makeText(getContext(), "WRONG CODE", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}


