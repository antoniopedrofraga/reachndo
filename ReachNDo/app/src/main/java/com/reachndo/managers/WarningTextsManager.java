package com.reachndo.managers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.reachndo.R;
import com.reachndo.adapters.EventListAdapter;
import com.reachndo.fragments.NavigationDrawerFragment;
import com.reachndo.memory.Singleton;

/**
 * Created by Pedro Fraga on 31-Aug-16.
 */
public class WarningTextsManager {

    private TextView warningLocMainText;
    private TextView warningLocSubText;

    private TextView warningEvnMainText;
    private TextView warningEvnSubText;

    public WarningTextsManager() {}

    public void setWarningEvnVisible(int index) {
        if (warningEvnMainText != null && warningEvnSubText != null &&
                (Singleton.getLocations().get(index).getEventsIn().size() != 0 ||
                        Singleton.getLocations().get(index).getEventsOut().size() != 0)) {
            warningEvnMainText.setVisibility(View.INVISIBLE);
            warningEvnSubText.setVisibility(View.INVISIBLE);
        }
    }

    public void updateTextsVisibility(EventListAdapter listAdapter) {
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
    }

    public void getViews(View rootView) {
        warningLocMainText = (TextView) rootView.findViewById(R.id.txtLocView);
        warningLocSubText = (TextView) rootView.findViewById(R.id.subTxtLocView);
        warningEvnMainText = (TextView) rootView.findViewById(R.id.txtEvnView);
        warningEvnSubText = (TextView) rootView.findViewById(R.id.subTxtEvnView);
    }

}
