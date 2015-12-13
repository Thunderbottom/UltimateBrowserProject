package io.github.UltimateBrowserProject.Browser;

import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import org.xdevs23.debugUtils.Logging;

import io.github.UltimateBrowserProject.Activity.BrowserActivity;
import io.github.UltimateBrowserProject.Unit.BrowserUnit;
import io.github.UltimateBrowserProject.View.UltimateBrowserProjectWebView;

public class UltimateBrowserProjectWebChromeClient extends WebChromeClient {
    private UltimateBrowserProjectWebView ultimateBrowserProjectWebView;

    public UltimateBrowserProjectWebChromeClient(UltimateBrowserProjectWebView UltimateBrowserProjectWebView) {
        super();
        this.ultimateBrowserProjectWebView = UltimateBrowserProjectWebView;
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        ultimateBrowserProjectWebView.getBrowserController().onCreateView(view, resultMsg);
        return isUserGesture;
    }

    @Override
    public void onCloseWindow(WebView view) {
        super.onCloseWindow(view);
    }

    @Override
    public void onProgressChanged(WebView view, int progress) {
        super.onProgressChanged(view, progress);
        ultimateBrowserProjectWebView.update(progress);
        if(! (progress < BrowserUnit.PROGRESS_MAX)) {
            ultimateBrowserProjectWebView.jsInterface.startTinting(view);
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        ultimateBrowserProjectWebView.update(title, view.getUrl());
        ultimateBrowserProjectWebView.jsInterface.startTinting(view);

    }

    @Deprecated
    @Override
    public void onShowCustomView(View view, int requestedOrientation, WebChromeClient.CustomViewCallback callback) {
        ultimateBrowserProjectWebView.getBrowserController().onShowCustomView(view, requestedOrientation, callback);
        super.onShowCustomView(view, requestedOrientation, callback);
    }

    @Override
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        ultimateBrowserProjectWebView.getBrowserController().onShowCustomView(view, callback);
        super.onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {
        ultimateBrowserProjectWebView.getBrowserController().onHideCustomView();
        super.onHideCustomView();
    }

    /* For 4.1 to 4.4 */
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        ultimateBrowserProjectWebView.getBrowserController().openFileChooser(uploadMsg);
    }

    /* For 4.1 to 4.4 */
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        ultimateBrowserProjectWebView.getBrowserController().openFileChooser(uploadMsg);
    }

    /* For 4.1 to 4.4 */
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        ultimateBrowserProjectWebView.getBrowserController().openFileChooser(uploadMsg);
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        ultimateBrowserProjectWebView.getBrowserController().showFileChooser(filePathCallback, fileChooserParams);
        return true;
    }

    /**
     * TODO: ?support this method... maybe you need to show a alertdialog asking to allow location
     * TODO: and then just do callback.invoke(origin, true, true) for allowing,
     * TODO: callback.invoke(origin, false, true) for disallowing...?
     * @link http://developer.android.com/reference/android/webkit/WebChromeClient.html#onGeolocationPermissionsShowPrompt%28java.lang.String,%20android.webkit.GeolocationPermissions.Callback%29
     * @param origin The origin of the web content attempting to use the Geolocation API.
     * @param callback The callback to use to set the permission state for the origin
     */
    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        super.onGeolocationPermissionsShowPrompt(origin, callback);
    }
}
