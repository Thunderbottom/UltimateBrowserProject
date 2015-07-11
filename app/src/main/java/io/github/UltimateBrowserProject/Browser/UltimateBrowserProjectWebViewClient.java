package io.github.UltimateBrowserProject.Browser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.MailTo;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.ByteArrayInputStream;

import io.github.UltimateBrowserProject.R;
import io.github.UltimateBrowserProject.Unit.BrowserUnit;
import io.github.UltimateBrowserProject.Unit.IntentUnit;
import io.github.UltimateBrowserProject.View.UltimateBrowserProjectWebView;

public class UltimateBrowserProjectWebViewClient extends WebViewClient {
    private UltimateBrowserProjectWebView ultimateBrowserProjectWebView;
    private Context context;

    private AdBlock adBlock;

    private boolean white;
    public void updateWhite(boolean white) {
        this.white = white;
    }

    private boolean enable;
    public void enableAdBlock(boolean enable) {
        this.enable = enable;
    }

    public UltimateBrowserProjectWebViewClient(UltimateBrowserProjectWebView UltimateBrowserProjectWebView) {
        super();
        this.ultimateBrowserProjectWebView = UltimateBrowserProjectWebView;
        this.context = UltimateBrowserProjectWebView.getContext();
        this.adBlock = UltimateBrowserProjectWebView.getAdBlock();
        this.white = false;
        this.enable = true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

        if (view.getTitle() == null || view.getTitle().isEmpty()) {
            ultimateBrowserProjectWebView.update(context.getString(R.string.album_untitled), url);
        } else {
            ultimateBrowserProjectWebView.update(view.getTitle(), url);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        if (!ultimateBrowserProjectWebView.getSettings().getLoadsImagesAutomatically()) {
            ultimateBrowserProjectWebView.getSettings().setLoadsImagesAutomatically(true);
        }

        if (view.getTitle() == null || view.getTitle().isEmpty()) {
            ultimateBrowserProjectWebView.update(context.getString(R.string.album_untitled), url);
        } else {
            ultimateBrowserProjectWebView.update(view.getTitle(), url);
        }

        if (ultimateBrowserProjectWebView.isForeground()) {
            ultimateBrowserProjectWebView.invalidate();
        } else {
            ultimateBrowserProjectWebView.postInvalidate();
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith(BrowserUnit.URL_SCHEME_MAIL_TO)) {
            Intent intent = IntentUnit.getEmailIntent(MailTo.parse(url));
            context.startActivity(intent);
            view.reload();
            return true;
        } else if (url.startsWith(BrowserUnit.URL_SCHEME_INTENT)) {
            Intent intent;
            try {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                context.startActivity(intent);
                return true;
            } catch (Exception e) {} // When intent fail will crash
        }

        white = adBlock.isWhite(url);
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Deprecated
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (enable && !white && adBlock.isAd(url)) {
            return new WebResourceResponse(
                    BrowserUnit.MIME_TYPE_TEXT_PLAIN,
                    BrowserUnit.URL_ENCODING,
                    new ByteArrayInputStream("".getBytes())
            );
        }

        return super.shouldInterceptRequest(view, url);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (enable && !white && adBlock.isAd(request.getUrl().toString())) {
                return new WebResourceResponse(
                        BrowserUnit.MIME_TYPE_TEXT_PLAIN,
                        BrowserUnit.URL_ENCODING,
                        new ByteArrayInputStream("".getBytes())
                );
            }
        }

        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public void onFormResubmission(WebView view, @NonNull final Message dontResend, final Message resend) {
        Context holder = IntentUnit.getContext();
        if (holder == null || !(holder instanceof Activity)) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(holder);
        builder.setCancelable(false);
        builder.setTitle(R.string.dialog_title_resubmission);
        builder.setMessage(R.string.dialog_content_resubmission);
        builder.setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resend.sendToTarget();
            }
        });
        builder.setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dontResend.sendToTarget();
            }
        });

        builder.create().show();
    }

    @Override
    public void onReceivedSslError(WebView view, @NonNull final SslErrorHandler handler, SslError error) {
        Context holder = IntentUnit.getContext();
                if (holder == null || !(holder instanceof Activity)) {
                    return;
                }

        AlertDialog.Builder builder = new AlertDialog.Builder(holder);
        builder.setCancelable(false);
        builder.setTitle(R.string.dialog_title_warning);
        builder.setMessage(R.string.dialog_content_ssl_error);
        builder.setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.proceed();
            }
        });
        builder.setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        if (error.getPrimaryError() == SslError.SSL_UNTRUSTED) {
            dialog.show();
        } else {
            handler.proceed();
        }
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, @NonNull final HttpAuthHandler handler, String host, String realm) {
        Context holder = IntentUnit.getContext();
        if (holder == null || !(holder instanceof Activity)) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(holder);
        builder.setCancelable(false);
        builder.setTitle(R.string.dialog_title_sign_in);

        LinearLayout signInLayout = (LinearLayout) LayoutInflater.from(holder).inflate(R.layout.dialog_sign_in, null, false);
        final EditText userEdit = (EditText) signInLayout.findViewById(R.id.dialog_sign_in_username);
        final EditText passEdit = (EditText) signInLayout.findViewById(R.id.dialog_sign_in_password);
        passEdit.setTypeface(Typeface.DEFAULT);
        passEdit.setTransformationMethod(new PasswordTransformationMethod());
        builder.setView(signInLayout);

        builder.setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String user = userEdit.getText().toString().trim();
                String pass = passEdit.getText().toString().trim();
                handler.proceed(user, pass);
            }
        });

        builder.setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.cancel();
            }
        });

        builder.create().show();
    }
}
