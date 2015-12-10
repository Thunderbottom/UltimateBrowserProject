package io.github.UltimateBrowserProject.Browser;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.MailTo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rey.material.app.DialogFragment;

import org.xdevs23.debugUtils.Logging;
import org.xdevs23.debugUtils.StackTraceParser;

import java.io.ByteArrayInputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.UltimateBrowserProject.Activity.BrowserActivity;
import io.github.UltimateBrowserProject.R;
import io.github.UltimateBrowserProject.Unit.BrowserUnit;
import io.github.UltimateBrowserProject.Unit.IntentUnit;
import io.github.UltimateBrowserProject.Unit.ViewUnit;
import io.github.UltimateBrowserProject.View.UltimateBrowserProjectWebView;

public class UltimateBrowserProjectWebViewClient extends WebViewClient {
    private UltimateBrowserProjectWebView ultimateBrowserProjectWebView;
    private Context context;

    private AdBlock adBlock;

    private boolean white;

    static final Pattern ACCEPTED_URI_SCHEMA = Pattern.compile(
            "(?i)" + // switch on case insensitive matching
                    "(" +    // begin group for schema
                    "(?:http|https|file|ftp):\\/\\/" +
                    "|(?:data|about|javascript):" +
                    "|(?:.*:.*@)" +
                    ")" +
                    "(.*)");

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
        if(ultimateBrowserProjectWebView.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams pw = (RelativeLayout.LayoutParams) ultimateBrowserProjectWebView.getLayoutParams();

            pw.setMargins(0, 0, 0, 0);
            pw.height = ViewUnit.getAdjustedWindowHeight(context);
            ultimateBrowserProjectWebView.setLayoutParams(pw);
        } else {
            CoordinatorLayout.LayoutParams pw = (CoordinatorLayout.LayoutParams) ultimateBrowserProjectWebView.getLayoutParams();

            pw.setMargins(0, 0, 0, 0);
            pw.height = ViewUnit.getAdjustedWindowHeight(context);
            ultimateBrowserProjectWebView.setLayoutParams(pw);
        }

        if(BrowserActivity.anchor == 0)ultimateBrowserProjectWebView.animate().translationY(ViewUnit.goh(context));

        super.onPageStarted(view, url, favicon);

        if (view.getTitle() == null || view.getTitle().isEmpty())
             ultimateBrowserProjectWebView.update(context.getString(R.string.album_untitled), url);
        else ultimateBrowserProjectWebView.update(view.getTitle(), url);

        
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        ultimateBrowserProjectWebView.getSettings().setLoadsImagesAutomatically( 
            (!ultimateBrowserProjectWebView.getSettings().getLoadsImagesAutomatically()) );


        if (view.getTitle() == null || view.getTitle().isEmpty())
             ultimateBrowserProjectWebView.update(context.getString(R.string.album_untitled), url);
        else ultimateBrowserProjectWebView.update(view.getTitle(), url);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            ultimateBrowserProjectWebView.evaluateJavascript(
                    "(function() { window.JsIface.postHeadColor(document.querySelector(\"meta[name='theme-color']\").getAttribute(\"content\")) })();",
                    null);

            Logging.logd("Evaluated post head color javascript");
        }



        if (ultimateBrowserProjectWebView.isForeground())
             ultimateBrowserProjectWebView.invalidate();
        else ultimateBrowserProjectWebView.postInvalidate();


    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith(BrowserUnit.URL_SCHEME_MAIL_TO)) {
            Intent intent = IntentUnit.getEmailIntent(MailTo.parse(url));
            context.startActivity(intent);
            view.reload();
            return true;
        }
        final String intentUri = getActivityIntentUriForUrl(url);
        if (intentUri != null) {
            final Intent intent = new Intent(BrowserActivity.SNACKBAR_BROADCAST_ACTION_NAME);
            intent.putExtra(BrowserActivity.EXTRA_SNACKBAR_TITLE, context.getString(R.string.open_in_app_snackbar_title));
            intent.putExtra(BrowserActivity.EXTRA_SNACKBAR_ACTION_TITLE, context.getString(R.string.open_in_app_snackbar_action_title));
            intent.putExtra(BrowserActivity.EXTRA_SNACKBAR_ACTION_INTENT_URI, intentUri);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

        white = adBlock.isWhite(url);
        return super.shouldOverrideUrlLoading(view, url);
    }

    /*
    from
    https://github.com/android/platform_packages_apps_browser/blob/41c050d8ff87c95377a80646b6e6683983be8ab7/src/com/android/browser/UrlHandler.java#L118
     */
    String getActivityIntentUriForUrl(String url) {
        Intent intent;
        // perform generic parsing of the URI to turn it into an Intent.
        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
        } catch (URISyntaxException ex) {
            Log.w("Browser", "Bad URI " + url + ": " + StackTraceParser.parse(ex));
            return null;
        }

        // check whether the intent can be resolved. If not, we will see
        // whether we can download it from the Market.
        if (context.getPackageManager().resolveActivity(intent, 0) == null) {
            String packagename = intent.getPackage();
            if (packagename != null) {
                intent = new Intent(Intent.ACTION_VIEW, Uri
                        .parse("market://search?q=pname:" + packagename));
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                return intent.toUri(0);
            } else {
                return null;
            }
        }

        // sanitize the Intent, ensuring web pages can not bypass browser
        // security (only access to BROWSABLE activities).
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setComponent(null);
        Intent selector = intent.getSelector();
        if (selector != null) {
            selector.addCategory(Intent.CATEGORY_BROWSABLE);
            selector.setComponent(null);
        }

        // Make sure webkit can handle it internally before checking for specialized
        // handlers. If webkit can't handle it internally, we need to call
        // startActivityIfNeeded
        Matcher m = ACCEPTED_URI_SCHEMA.matcher(url);
        if (m.matches() && !isSpecializedHandlerAvailable(intent)) {
            return null;
        }
        return intent.toUri(0);
    }

    /**
     * Search for intent handlers that are specific to this URL
     * aka, specialized apps like google maps or youtube
     */
    private boolean isSpecializedHandlerAvailable(Intent intent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> handlers = pm.queryIntentActivities(intent,
                PackageManager.GET_RESOLVED_FILTER);
        // Could cause java.lang.NullPointerException on ... handlers.size() ... because
        // handlers could be null, so .size() might not be available
        try {
            if (handlers == null || handlers.size() == 0) {
                return false;
            }
        } catch(NullPointerException ex) { /* do nothing, only prevent crash */ }
        
        for (ResolveInfo resolveInfo : handlers) {
            IntentFilter filter = resolveInfo.filter;
            if (filter == null) {
                // No intent filter matches this intent?
                // Error on the side of staying in the browser, ignore
                continue;
            }
            if (filter.countDataAuthorities() == 0 && filter.countDataPaths() == 0) continue; // Generic handler, skip
            return true;
        }
        return false;
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
        if (error.getPrimaryError() == SslError.SSL_UNTRUSTED) dialog.show();
        else handler.proceed();
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, @NonNull final HttpAuthHandler handler, String host, String realm) {
        Context holder = IntentUnit.getContext();
        if (holder == null || !(holder instanceof Activity)) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(holder);
        builder.setCancelable(false);
        builder.setTitle(R.string.dialog_title_sign_in);

        LinearLayout signInLayout = (LinearLayout) LayoutInflater.from(holder).inflate(R.layout.dialog_sign_in, null, false);
        final com.rey.material.widget.EditText userEdit
                = (com.rey.material.widget.EditText) signInLayout.findViewById(R.id.dialog_sign_in_username);
        final com.rey.material.widget.EditText passEdit
                = (com.rey.material.widget.EditText) signInLayout.findViewById(R.id.dialog_sign_in_password);
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
