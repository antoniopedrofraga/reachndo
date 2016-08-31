package com.reachndo.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.reachndo.fragments.AboutUsFragment;
import com.reachndo.R;
import com.utilities.Theme;

/**
 * Created by Francisco on 11/09/2015.
 */

public class About extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {

        Theme.setThemeAccordingAPI(this);
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new AboutUsFragment())
                .commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.about, root, false);
        bar.setTitleTextColor(getResources().getColor(R.color.White));
        root.addView(bar, 0); // insert at top

        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
