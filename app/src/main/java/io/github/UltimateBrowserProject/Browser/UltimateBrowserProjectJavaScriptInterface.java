package io.github.UltimateBrowserProject.Browser;

import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.webkit.WebView;

import org.xdevs23.debugUtils.Logging;
import org.xdevs23.debugUtils.StackTraceParser;

import io.github.UltimateBrowserProject.Activity.BrowserActivity;
import io.github.UltimateBrowserProject.R;

public class UltimateBrowserProjectJavaScriptInterface {

    public String headColor = "";

    public void startTinting(WebView view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateGetColorJS(view);
            tint();
        }
    }

    public static void evaluateGetColorJS(WebView view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.evaluateJavascript(
                    "(function() { try { " +
                            "var hc = document.querySelector(\"meta[name='theme-color']\").getAttribute(\"content\").toString();" +
                            "if(hc.indexOf(\"#\") !== -1) window.JsIface.postHeadColor(hc);" +
                            "else window.JsIface.postHeadColor(\"\");" +
                            "} catch(ex) { try {" +
                            "if(ex.toString().indexOf(\"'getAttribute' of null\") !== -1) window.JsIface.postHeadColor(\"\");" +
                            "else window.JsIface.reportError(ex.toString());" +
                            "} catch(e) {}" +
                            "} })();",
                    null);
            Logging.logd("Evaluated post head color javascript");
        }

    }

    @SuppressWarnings("ResourceType")
    public void applyColor() {
        Logging.logd("Alternative: setting bg color...");
        BrowserActivity.omnibox.setBackgroundResource(0);
        BrowserActivity.omnibox.setBackgroundColor(Color.parseColor(headColor));

        if(BrowserActivity.omnibox.getBackground() == null) {
            BrowserActivity.getContentFrame().setBackgroundColor(Color.parseColor(headColor));
            BrowserActivity.omnibox.setBackgroundResource(0);
            if(BrowserActivity.getContentFrame().getBackground() == null) applyDefaultColor();
        }

    }

    public void applyColor(String color) {
        try {
            Logging.logd("Setting bg color...");
            BrowserActivity.omnibox.setBackgroundResource(0);
            BrowserActivity.omnibox.setBackgroundColor(Color.parseColor(color));
            BrowserActivity.updateOverflowColor(Color.parseColor(color));
            if (BrowserActivity.omnibox.getBackground() == null) {
                Logging.logd("Background is null o_O");
                applyColor();
            }
            Logging.logd("bgcolor set");
        } catch(Exception ex) {
            StackTraceParser.logStackTrace(ex);
        }
    }

    public void reapplyColor() {
        Logging.logd("Reapplying color...");
        if(BrowserActivity.omnibox.getBackground() != null) {
            if (headColor.length() > 0) applyColor();
            else applyDefaultColor();
        }
    }

    public void applyDefaultColor() {
        BrowserActivity.omnibox.setBackgroundResource(R.color.background_dark);
        BrowserActivity.omnibox.setBackgroundColor(ContextCompat.getColor(BrowserActivity.getContext(), R.color.background_dark));
        BrowserActivity.getContentFrame().setBackgroundColor(Color.WHITE);
        BrowserActivity.updateOverflowColor(ContextCompat.getColor(BrowserActivity.getContext(), R.color.background_dark));
    }

    @android.webkit.JavascriptInterface
    public void reportError(String error) {
        Logging.logd("JavaScript error: " + error);
    }

    @android.webkit.JavascriptInterface
    public void postHeadColor(String color) {
        color = color.toUpperCase().replace(" ", "");
        Logging.logd("Posted head color: '" + color + "'");
        headColor = color;
    }

    @SuppressWarnings("ResourceType")
    public void tint() {
        if(headColor.length() > 0) {
            Logging.logd("Applying color");
            applyColor(headColor);
            BrowserActivity.updateBarsColor(Color.parseColor(headColor));
            Logging.logd("Applied");
        } else {
            Logging.logd("No theme color, applying default.");
            applyDefaultColor();
            BrowserActivity.resetBarsColor();
        }
    }

}
