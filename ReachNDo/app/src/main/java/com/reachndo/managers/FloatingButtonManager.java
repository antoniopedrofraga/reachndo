package com.reachndo.managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.reachndo.R;
import com.reachndo.activities.MainMenu;
import com.reachndo.memory.Singleton;
import com.utilities.Utilities;

public class FloatingButtonManager {

    private static FloatingButtonManager instance;

    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton floatingActionButtonLocat;
    private FloatingActionButton floatingActionButtonEvents;

    public FloatingButtonManager() {
        this.instance = this;
    }

    public void getViews(View rootView) {

        floatingActionMenu = (FloatingActionMenu) rootView.findViewById(R.id.faMenu);
        floatingActionButtonEvents = (FloatingActionButton) rootView.findViewById(R.id.fabEvents);
        floatingActionButtonLocat = (FloatingActionButton) rootView.findViewById(R.id.fabLocals);

    }

    public void setClickListeners() {
        final Activity activity = MainMenu.getInstance();
        if (floatingActionMenu != null) {

            floatingActionMenu.setClosedOnTouchOutside(true);

            floatingActionButtonLocat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!Utilities.isNetworkAvailable(activity)) {
                        final MaterialDialog alert = new MaterialDialog.Builder(activity)
                                .title(R.string.wifi_on_dialog_title)
                                .content(R.string.wifi_on_dialog_warning)
                                .positiveText(android.R.string.yes)
                                .negativeText(android.R.string.no)
                                .show();
                        View positive = alert.getActionButton(DialogAction.POSITIVE);
                        positive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                activity.startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                                alert.dismiss();
                            }
                        });
                        return;
                    }

                    if (!Utilities.isLocationEnabled(activity)) {
                        final MaterialDialog alert = new MaterialDialog.Builder(activity)
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
                                activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        });
                        View negative = alert.getActionButton(DialogAction.NEGATIVE);
                        negative.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alert.dismiss();
                                startLocationPick(activity);
                            }
                        });
                        return;
                    }

                    startLocationPick(activity);
                }
            });

            if (Singleton.getLocations().size() == 0) {
                floatingActionButtonEvents.setEnabled(false);
            } else {
                floatingActionButtonEvents.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EventDialogsManager.getInstance().showInOutPicker();
                    }
                });
            }
        } else
            Log.d("Debug", "Error in mFab (NULL)");

    }

    public void startLocationPick(Activity activity) {
        try {
            floatingActionButtonLocat.setIndeterminate(true);
            PlacePicker.IntentBuilder intentBuilder;
            intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(activity);
            activity.startActivityForResult(intent, Utilities.REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException e) {
            Log.d("PlacesAPI Demo", "GooglePlayServicesRepairableException thrown");
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d("PlacesAPI Demo", "GooglePlayServicesNotAvailableException thrown");
        }
    }

    public static FloatingButtonManager getInstance() {
        return instance;
    }

    public FloatingActionButton getLocationsButton() {
        return floatingActionButtonLocat;
    }

}
