package io.github.UltimateBrowserProject.Browser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import io.github.UltimateBrowserProject.R;
import io.github.UltimateBrowserProject.Unit.BrowserUnit;

public class UltimateBrowserProjectDownloadListener implements DownloadListener {
    private Context context;

    public UltimateBrowserProjectDownloadListener(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
        BrowserUnit.download(context, url, contentDisposition, mimeType);
    }
}
