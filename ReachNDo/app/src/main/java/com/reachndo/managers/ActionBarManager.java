package com.reachndo.managers;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;

import com.reachndo.CustomTypefaceSpan;
import com.reachndo.R;

/**
 * Created by Pedro Fraga on 29-Aug-16.
 */
public class ActionBarManager {

    CharSequence title = "Reach N' Do";
    ActionBar actionBar;
    SpannableStringBuilder spannableStringBuilder;

    public ActionBarManager (ActionBar actionBar, Activity activity) {
        this.actionBar = actionBar;
        this.title = activity.getTitle();
        this.spannableStringBuilder = new SpannableStringBuilder(activity.getResources().getString(R.string.app_name));
        loadTitleFont(activity);
    }

    public void loadTitleFont(Activity activity) {
        Typeface font = Typeface.createFromAsset(activity.getAssets(), "fonts/WolfintheCity.otf");
        spannableStringBuilder.setSpan(new RelativeSizeSpan(2f), 0, spannableStringBuilder.length(), 0);
        spannableStringBuilder.setSpan(new CustomTypefaceSpan("", font), 0, spannableStringBuilder.length(), Spanned.SPAN_COMPOSING);
    }

    public void updateTitle(CharSequence title) {
        this.title = title;
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
    }

    public void updateTitleToAppName() {
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(spannableStringBuilder);
    }

    public CharSequence getTitle() {
        return title;
    }
}
