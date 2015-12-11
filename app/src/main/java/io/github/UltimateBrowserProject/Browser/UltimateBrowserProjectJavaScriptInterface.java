package io.github.UltimateBrowserProject.Browser;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import org.xdevs23.debugUtils.Logging;

import io.github.UltimateBrowserProject.Activity.BrowserActivity;
import io.github.UltimateBrowserProject.R;

public class UltimateBrowserProjectJavaScriptInterface {

    public String headColor = "";

    public void applyColor() {
        float[] hsv = new float[3];
        //noinspection ResourceType
        int mColor = Color.parseColor(headColor);
        BrowserActivity.omnibox.setBackgroundColor(mColor);
        Color.colorToHSV(mColor, hsv);
        hsv[2] *= 0.86f;
        mColor = Color.HSVToColor(hsv);
        BrowserActivity.omnibox.setBackgroundColor(mColor);
    }

    public void reapplyColor() {
        Logging.logd("Reapplying color...");
        if(headColor.length() > 0) applyColor();
        else                applyDefaultColor();
    }

    public void applyDefaultColor() {
        BrowserActivity.omnibox.setBackgroundColor(ContextCompat.getColor(BrowserActivity.getContext(),
                R.color.background_dark));
    }

    @android.webkit.JavascriptInterface
    public void postHeadColor(String color) {
        headColor = color;
        Logging.logd("Posted head color: " + headColor);
        if(headColor.length() > 0) {
            Logging.logd("Applying color");
            applyColor();
        } else {
            Logging.logd("No theme color, applying default.");
            applyDefaultColor();
        }
    }
}
