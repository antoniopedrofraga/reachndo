package com.utilities;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import com.reachndo.R;

import java.lang.reflect.Field;

/**
 * Created by Pedro Fraga on 28-Aug-16.
 */
public class Theme {

    public static void setThemeAccordingAPI(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.setTheme(R.style.MaterialDesign);
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity.getBaseContext(), R.color.DarkMaterialPurple));
        } else {
            activity.setTheme(R.style.AppTheme);
        }

    }

    public static void showActionOverflowMenu(Activity activity) {
        //devices with hardware menu button (e.g. Samsung Note) don't show action overflow menu
        try {
            ViewConfiguration config = ViewConfiguration.get(activity);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            Log.e("Error getting overflow", e.getLocalizedMessage());
        }
    }
}
