package com.reachndo.managers;


import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconCompat;


/**
 * Created by Pedro Fraga on 29-Aug-16.
 */
public class ActionBarManager {

    private CharSequence title = "Reach N' Do";
    private ActionBar actionBar;
    private MaterialMenuIconCompat materialMenu;

    public ActionBarManager (ActionBar actionBar, AppCompatActivity activity) {
        this.actionBar = actionBar;
        this.title = activity.getTitle();
        this.materialMenu = new MaterialMenuIconCompat(activity, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);
    }

    public void updateTitle(CharSequence title) {
        this.title = title;
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
    }

    public CharSequence getTitle() {
        return title;
    }

    public void animateToBurger() {
        materialMenu.animateState(MaterialMenuDrawable.IconState.BURGER);
    }

    public void animateToArrow() {
        materialMenu.animateState(MaterialMenuDrawable.IconState.ARROW);
    }

    public MaterialMenuIconCompat getMaterialMenu() {
        return materialMenu;
    }
}
