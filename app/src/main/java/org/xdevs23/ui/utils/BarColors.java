package org.xdevs23.ui.utils;

import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

import io.github.UltimateBrowserProject.R;

public class BarColors {

    public static void enableBarColoring(Window window) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public static void enableBarColoring(Window window, int colorID) {
        enableBarColoring(window);
        updateBarsColor(window, colorID);
    }

    public static void updateBarsColor(int color, Window window, boolean applyDarken) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int mColor = color;
            if(applyDarken) {
                float[] hsvc = new float[3];
                Color.colorToHSV(color, hsvc);
                hsvc[2] *= 0.78;
                mColor = Color.HSVToColor(hsvc);
            }
            window.setStatusBarColor(mColor);
        }
    }

    public static void setBarsColor(int color, Window window, boolean applyDarken) {
        updateBarsColor(color, window, applyDarken);
    }

    public static void resetBarsColor(Window window) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(window.getContext(), R.color.black));
        }
    }

    public static void updateBarsColor(Window window, int colorID, boolean applyDarken) {
        updateBarsColor(ContextCompat.getColor(window.getContext(), colorID), window);
    }

    public static void updateBarsColor(int color, Window window) {
        updateBarsColor(color, window, false);
    }

    public static void updateBarsColor(Window window, int colorID) {
        updateBarsColor(window, colorID, false);
    }

}
