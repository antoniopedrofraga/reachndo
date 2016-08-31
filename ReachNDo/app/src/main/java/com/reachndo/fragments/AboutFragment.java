package com.reachndo.fragments;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.Window;
import android.view.WindowManager;

import com.reachndo.R;

/**
 * Created by Joao Nogueira on 12/09/2015.
 */
public class AboutFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().setTheme(R.style.MaterialDesign);
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.DarkMaterialPurple));
        }

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.about);
    }
}
