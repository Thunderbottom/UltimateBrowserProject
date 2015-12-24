package io.github.UltimateBrowserProject.Activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.widget.ProgressView;

import io.github.UltimateBrowserProject.R;

import org.xdevs23.config.AppConfig;
import org.xdevs23.debugUtils.Logging;
import org.xdevs23.net.DownloadUtils;
import org.xdevs23.root.utils.RootController;
import org.xdevs23.ui.dialog.MessageDialog;
import org.xdevs23.ui.dialog.templates.NegativeButtonCancel;
import org.xdevs23.ui.utils.BarColors;

import java.io.File;

@SuppressWarnings("unused")
public class UpdateActivity extends AppCompatActivity {
	
	private static String appversion = AppConfig.versionName;

	private Activity thisActivity = this;
	boolean mgo = false;
	
	
	private static Context staticContext = null;
	private        Context     myContext = this;
	
	private static boolean enableRoot = false;
	
	private static void setStaticContext(Context context) {
		staticContext = context;
	}
	
	public static void startUpdateImmediately(Activity activity, String url) {
		activity.startActivity(new Intent(activity.getApplicationContext(), UpdateActivity.class));
		readyToInstallUrl = url;
	}

    protected static void logt(String msg) {
        Logging.logd("[Updater] " + msg);
    }
	
	
	private static       String updateRoot = AppConfig.updateRootSec;
	private static 		 String updaterDir = updateRoot;
	private static final String updaterUrl = updateRoot + "updater.php?version=" + appversion;
	
	private static       String readyToInstallUrl = "";
	
	private static       String updatedApk;
	private static		 String updApkData = Environment.getDataDirectory() 
										+ "/data/io.github.UltimateBrowserProject/files/mobile/UltimateBrowserProject.apk";
	
	public static ProgressView updateBar;
	public static TextView     updateStatus;

	private static class UpdateStatus {
		
		public
		  static String
		    downloading = "Downloading"			    ,
		        loading = "Loading"			        ,
		      launching = "Starting update"			,
		           none = ""						;
		    
		public static void setStatus(String status) {
			updateStatus.setText(status);
		}

        public static void applyLanguages() {
            downloading = staticContext.getString(R.string.updater_downloading);
            loading     = staticContext.getString(R.string.updater_loading);
            launching   = staticContext.getString(R.string.updater_updating);
        }
	}


    WebView myWebView;
    static boolean isDownloadingUpdate = false;

    private boolean webloaded = false;


    private static void startNRUpdateInstallation() {
		File newUpdate   = new File(updatedApk);
		
		File newUpdDir   = new File(updatedApk.replace("ubp-update.apk", ""));
		
		boolean successCRND = newUpdDir.mkdirs();

		if(!successCRND) logt("Error while creating dirs");
		
    	Intent installSpIntent = new Intent(Intent.ACTION_VIEW)
  	                  .setDataAndType(Uri.fromFile(newUpdate), 
  	                		  "application/vnd.android.package-archive");
    	
    	installSpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	
		staticContext.startActivity(installSpIntent);
	}



	public static void startOverallInstallation(String url) {
		File spexfile = new File(updatedApk);

        if(!spexfile.delete()) logt("File not deleted.");

		
		UpdateStatus.setStatus(UpdateStatus.downloading);
		
		
	    DownloadUtils.setProgressBar(R.id.updateProgressBar, staticContext);
		
		updateBar.applyStyle(R.style.ProgressView_MainStyle_Horiz_Determinate);
		updateBar.setVisibility(View.VISIBLE);
        updateBar.start();

		
        try {
            
            DownloadUtils.setCustomFileLength((long) 2000);
             
        } catch (Exception e) {
            // Ignore
        } 
	    
	    DownloadUtils.downloadFile(url, updatedApk);
	   
	    
	    isDownloadingUpdate = true;
	}
	
	public static void startUpdateInstallation() {
		
		UpdateStatus.setStatus(UpdateStatus.launching);
		
		try {
            String endR = "";
			if(enableRoot) endR = RootController.runCommand(
                    "am force-stop io.github.UltimateBrowserProject && " +
                    "pm install -r " + updatedApk + " && " +
					"am start -n io.github.UltimateBrowserProject/.Activity.BrowserActivity && " +
					"am start -n io.github.UltimateBrowserProject/.Activity.UpdateActivity && exit");
			else startNRUpdateInstallation();

            if(endR.length() > 0 && endR.toLowerCase().contains("failed")) {
                Toast.makeText(staticContext,
                        staticContext.getString(R.string.updater_root_install_error),
                        Toast.LENGTH_LONG)
                        .show();
                startNRUpdateInstallation();
            }
		} catch(Exception ex) {
			startNRUpdateInstallation();
		}
	
		UpdateStatus.setStatus(UpdateStatus.none);

		
	}
	
	public static void updateProgress(int progress) {
		updateBar.setProgress(progress);
	}
	
	public static void endProgress() {
    	updateBar.applyStyle(R.style.ProgressView_MainStyle_Horiz);
    	updateBar.setProgress(0);
        updateBar.stop();
	}

    @SuppressLint("SetJavaScriptEnabled")
    protected void initThisActivity() {
        if(!webloaded) {
            updatedApk = (getApplicationContext().getExternalCacheDir() + "/spa-update.apk")
                    .replace("//", "/");

            logt("Loading URL " + updaterUrl);

            updateBar = (ProgressView) findViewById(R.id.updateProgressBar);
            updateBar.setVisibility(View.VISIBLE);

            updateStatus = (TextView) findViewById(R.id.updateStatus);

            myWebView = (WebView) findViewById(R.id.wVUpdate);
            myWebView.setWebViewClient(new WebViewClient() {

                @SuppressWarnings("deprecation")
                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    myWebView.loadUrl("file:///android_asset/error.html");
                }

                @TargetApi(android.os.Build.VERSION_CODES.M)
                @Override
                public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                    onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.contains(".apk")) {
                        startOverallInstallation(url);
                        return true;
                    }
                    else if ( url.startsWith("basichandler://loadurl") ) { view.loadUrl(updaterUrl); return true; }
                    else 	{ view.loadUrl(url);
                        return true; }
                }

            });


            myWebView.setWebChromeClient(new WebChromeClient() {

                @Override
                public void onProgressChanged(WebView view, int progress) {

                    if(progress < 100)	{ updateBar.start();
                        UpdateStatus.setStatus(UpdateStatus.loading); }

                    else  				{ updateBar.stop();
                        UpdateStatus.setStatus(UpdateStatus.none); }

                }

            });

            myWebView.getSettings().setJavaScriptEnabled(true);
            myWebView.loadUrl(updaterUrl);
            webloaded = true;
        }
    }

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updater);

        Toolbar toolbar = (Toolbar) findViewById(R.id.updaterToolbar);
        setSupportActionBar(toolbar);

        try {
            BarColors.enableBarColoring(getWindow(), R.color.UpdaterDark);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch(Exception ex) {/* */}


        staticContext = myContext;

        UpdateStatus.applyLanguages();
		
		
		if(RootController.isSuInstalled())
			if(RootController.isBusyboxInstalled()) {
				AlertDialog.Builder adB = new AlertDialog.Builder(staticContext);
				adB.setTitle(getString(R.string.rootutils_root_detect_title))
				   .setMessage(getString(R.string.rootutils_root_detect_message))
				   .setPositiveButton(getString(R.string.answer_yes), new DialogInterface.OnClickListener() {
					   @Override
					   public void onClick(DialogInterface d, int id) {
							if(RootController.requestRoot()) {
								enableRoot = true;
								d.dismiss();
							} else {
								MessageDialog.showDialog(
                                        getString(R.string.rootutils_root_access_failed_title),
                                        getString(R.string.rootutils_root_access_failed), staticContext);
							}
					   }
				   })
				  .setNegativeButton(getString(R.string.answer_no), new NegativeButtonCancel())
				   ;
				AppCompatDialog aD = adB.create();
				
				aD.show();
			}

        initThisActivity();
		
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		try {
			if(!mgo)onCreateOptionsMenu(null);
		} catch(Exception ex) {/* */}
	}
}
