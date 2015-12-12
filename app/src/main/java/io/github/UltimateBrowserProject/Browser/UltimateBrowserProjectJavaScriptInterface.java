package io.github.UltimateBrowserProject.Browser;

import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.webkit.WebView;

import org.xdevs23.debugUtils.DebugTestException;
import org.xdevs23.debugUtils.Logging;

import io.github.UltimateBrowserProject.Activity.BrowserActivity;
import io.github.UltimateBrowserProject.R;

public class UltimateBrowserProjectJavaScriptInterface {

    public String headColor = "";

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

    public void applyColor() {
        BrowserActivity.omnibox.setBackgroundResource(0);
        //noinspection ResourceType
        BrowserActivity.omnibox.setBackgroundColor(Color.parseColor(headColor));
    }

    private void applyColor(String color) {
        if(color.equals(headColor)) return;
        BrowserActivity.omnibox.setBackgroundResource(0);
        BrowserActivity.omnibox.setBackgroundColor(Color.parseColor(color));
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
    }

    @android.webkit.JavascriptInterface
    public void reportError(String error) {
        Logging.logd("JavaScript error: " + error);
    }

    @android.webkit.JavascriptInterface
    public void postHeadColor(String color) {
        headColor = color.toUpperCase().replace(" ", "");
        Logging.logd("Posted head color: '" + headColor + "'");
        if(headColor.length() > 0) {
            Logging.logd("Applying color");
            applyColor(color);
        } else {
            Logging.logd("No theme color, applying default.");
            applyDefaultColor();
        }
    }
}
