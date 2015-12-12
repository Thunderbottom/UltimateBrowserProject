package io.github.UltimateBrowserProject.Activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.askerov.dynamicgrid.DynamicGridView;
import org.xdevs23.config.ConfigUtils;
import org.xdevs23.debugUtils.Logging;
import org.xdevs23.debugUtils.StackTraceParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.github.UltimateBrowserProject.Application.Changelog;
import io.github.UltimateBrowserProject.Browser.AdBlock;
import io.github.UltimateBrowserProject.Browser.AlbumController;
import io.github.UltimateBrowserProject.Browser.BrowserContainer;
import io.github.UltimateBrowserProject.Browser.BrowserController;
import io.github.UltimateBrowserProject.Database.Record;
import io.github.UltimateBrowserProject.Database.RecordAction;
import io.github.UltimateBrowserProject.Print.PrintDialogActivity;
import io.github.UltimateBrowserProject.R;
import io.github.UltimateBrowserProject.Service.ClearService;
import io.github.UltimateBrowserProject.Service.HolderService;
import io.github.UltimateBrowserProject.Task.ScreenshotTask;
import io.github.UltimateBrowserProject.Unit.BrowserUnit;
import io.github.UltimateBrowserProject.Unit.IntentUnit;
import io.github.UltimateBrowserProject.Unit.ViewUnit;
import io.github.UltimateBrowserProject.View.CompleteAdapter;
import io.github.UltimateBrowserProject.View.DialogAdapter;
import io.github.UltimateBrowserProject.View.FullscreenHolder;
import io.github.UltimateBrowserProject.View.GridAdapter;
import io.github.UltimateBrowserProject.View.GridItem;
import io.github.UltimateBrowserProject.View.RecordAdapter;
import io.github.UltimateBrowserProject.View.SwipeToBoundListener;
import io.github.UltimateBrowserProject.View.SwitcherPanel;
import io.github.UltimateBrowserProject.View.UltimateBrowserProjectRelativeLayout;
import io.github.UltimateBrowserProject.View.UltimateBrowserProjectToast;
import io.github.UltimateBrowserProject.View.UltimateBrowserProjectWebView;

public class BrowserActivity extends Activity implements BrowserController {
    private static final int DOUBLE_TAPS_QUIT_DEFAULT = 1800;

    private SwitcherPanel switcherPanel;
    private boolean fullscreen;
    public static int anchor;
    private float dimen156dp, dimen144dp, dimen117dp, dimen108dp, dimen48dp;

    private HorizontalScrollView switcherScroller;
    private LinearLayout switcherContainer;
    private ImageButton switcherSetting,
                        switcherBookmarks,
                        switcherHistory,
                        switcherAdd,
                        switcherPrint;

    public static RelativeLayout omnibox = null;
    private AutoCompleteTextView inputBox;
    private ImageButton omniboxBookmark,
                        omniboxRefresh,
                        omniboxForward,
                        omniboxOverflow;
    private ProgressBar progressBar;

    private RelativeLayout searchPanel;
    private EditText searchBox;
    private ImageButton searchUp,
                        searchDown,
                        searchCancel;

    private Button relayoutOK;
    private static CoordinatorLayout contentFrame = null;

    private static UltimateBrowserProjectWebView ubpWebView = null;

    private Handler mHandler;

    public  static boolean firstLaunch = false;

    private static String TAG = "UltimateBrowserProject";

    private static void logt(String msg) {
        Log.d(TAG, msg);
    }
    private static void logd(String msg) {
        Logging.logd(msg);
    }

    private class VideoCompletionListener implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            onHideCustomView();
        }
    }

    private FullscreenHolder fullscreenHolder;
    private View customView;
    private VideoView videoView;
    private int originalOrientation;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private ValueCallback<Uri> uploadMsg = null;
    private ValueCallback<Uri[]> filePathCallback = null;

    public static RelativeLayout webViewPlaceHolder = null;

    private static boolean quit = false;
    private boolean create = true, restore;
    private int shortAnimTime = 0,
               mediumAnimTime = 0,
                 longAnimTime = 0;
    private AlbumController currentAlbumController = null;

    private static Context staticContext = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            filePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
    }

    public void prepareFirstLaunch() {
        Logging.logd("\nPreparing first launch\n");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String lang,
                langS = Resources.getSystem().getConfiguration().locale.getLanguage();
        Logging.logd("Language found: " + langS);
        switch( BrowserLanguages.valueOf(langS) ) {
            case de: lang = BrowserUnit.INTRODUCTION_DE;    break;
            case en: lang = BrowserUnit.INTRODUCTION_EN;    break;
            default: lang = BrowserUnit.INTRODUCTION_EN;    break;
        }
        addAlbum("Introduction", BrowserUnit.BASE_URL + lang, true, null);
        showAlbum(BrowserContainer.get(0), false, false, false);
        sp.edit().putBoolean(getString(R.string.sp_first), false).apply();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        logd("DEBUG ENABLED");
        if(ConfigUtils.isDebuggable()) {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    StackTraceParser.logStackTrace(ex);
                }
            });
        }

        super.onCreate(savedInstanceState);

        staticContext = this.getApplicationContext();

        logd("Initializing...");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager.TaskDescription description = new ActivityManager.TaskDescription(
                    getString(R.string.app_name),
                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher),
                    getResources().getColor(R.color.background_dark)
            );
            setTaskDescription(description);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setNavigationBarColor(getResources().getColor(R.color.gray_900));

        logd("Loading preferences...");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        fullscreen = sp.getBoolean(getString(R.string.sp_fullscreen), false);
        firstLaunch = sp.getBoolean(getString(R.string.sp_first), true);
        setFullscreen(fullscreen);
        restore = sp.getBoolean(getString(R.string.sp_restore_tabs), true);
        anchor = Integer.valueOf(sp.getString(getString(R.string.sp_anchor), "1"));

        logd("Appliying layout...");
        setContentView(R.layout.main_top);

        logd("Checking for update");
        new Changelog(this, R.xml.changelog).showWhatsNew();
        mHandler = new Handler();
        checkUpdate.start();


        shortAnimTime  = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mediumAnimTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        longAnimTime   = getResources().getInteger(android.R.integer.config_longAnimTime);
        switcherPanel  = (SwitcherPanel) findViewById(R.id.switcher_panel);
        switcherPanel.setStatusListener(new SwitcherPanel.StatusListener() {
            @Override
            public void onFling() {
            }

            @Override
            public void onExpanded() {
            }

            @Override
            public void onCollapsed() {
                inputBox.clearFocus();
            }
        });


        dimen156dp = getResources().getDimensionPixelSize(R.dimen.layout_width_156dp);
        dimen144dp = getResources().getDimensionPixelSize(R.dimen.layout_width_144dp);
        dimen117dp = getResources().getDimensionPixelSize(R.dimen.layout_height_117dp);
        dimen108dp = getResources().getDimensionPixelSize(R.dimen.layout_height_108dp);
        dimen48dp  = getResources().getDimensionPixelOffset(R.dimen.layout_height_48dp);

        logd("Initializing switcher view...");
        initSwitcherView();
        logd("Initializing omnibox...");
        initOmnibox();
        logd("Initializing search panel...");
        initSearchPanel();
        relayoutOK = (Button) findViewById(R.id.main_relayout_ok);
        contentFrame = (CoordinatorLayout) findViewById(R.id.main_content);


        new AdBlock(this); // For AdBlock cold boot
        dispatchIntent(getIntent());

        logd("Starting browser init");
        if(firstLaunch) prepareFirstLaunch();
        else {
            if (restore) openSavedTabs();
            else pinAlbums(null);
        }


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) hideKeyboard();

        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        logd("New intent!");
        setIntent(intent);
    }

    @Override
    public void onResume() {
        logd("Resuming...");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        fullscreen = sp.getBoolean(getString(R.string.sp_fullscreen), false);
        IntentUnit.setContext(this);
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(snackbarReceiver,
                new IntentFilter(SNACKBAR_BROADCAST_ACTION_NAME));

        if (create) return;


        setFullscreen(fullscreen);
        dispatchIntent(getIntent());

        if (IntentUnit.isDBChange()) {
            updateBookmarks();
            updateAutoComplete();
            IntentUnit.setDBChange(false);
        }

        if (IntentUnit.isSPChange()) {
            for (AlbumController controller : BrowserContainer.list()) {
                if (controller instanceof UltimateBrowserProjectWebView) {
                    ((UltimateBrowserProjectWebView) controller).initPreferences();
                }
            }

            IntentUnit.setSPChange(false);
        }
    }

    public enum BrowserLanguages {
        en,
        de
    }

    private void dispatchIntent(Intent intent) {
        logd("Dispatching intent...");
        Intent toHolderService = new Intent(this, HolderService.class);
        IntentUnit.setClear(false);
        stopService(toHolderService);
        String action = intent.getAction();

        if (intent != null && intent.hasExtra(IntentUnit.OPEN)) { // From HolderActivity's menu
            pinAlbums(intent.getStringExtra(IntentUnit.OPEN));
        } else if (intent != null && action != null && Intent.ACTION_WEB_SEARCH.equals(action)){ // From ActionMode and some others
            pinAlbums(intent.getStringExtra(SearchManager.QUERY));
        } else if (intent != null && filePathCallback != null) {
            filePathCallback = null;
        } else if (intent != null && Intent.ACTION_VIEW.equals(action)) {
            String filePath;
            Uri uri = intent.getData();
            Log.d("", "Uri is " + uri);
            if (uri != null && "content".equals(uri.getScheme())) {
                Cursor cursor = this.getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
                cursor.moveToFirst();       // may produce NullPointerException
                filePath = cursor.getString(0);
                cursor.close();
            } else {
                filePath = uri.getPath();
            }
            filePath = "file://" + filePath;
            Log.d("", "Path is " + filePath);
            updateAlbum(filePath);
        } else {

        }
    }


    @Override
    public void onPause() {
        logd("Pausing...");
        Intent toHolderService = new Intent(this, HolderService.class);
        IntentUnit.setClear(false);
        stopService(toHolderService);
        saveOpenTabs();

        create = false;
        inputBox.clearFocus();
        if (currentAlbumController != null && currentAlbumController instanceof UltimateBrowserProjectRelativeLayout) {
            UltimateBrowserProjectRelativeLayout layout = (UltimateBrowserProjectRelativeLayout) currentAlbumController;
            if (layout.getFlag() == BrowserUnit.FLAG_HOME) {
                DynamicGridView gridView = (DynamicGridView) layout.findViewById(R.id.home_grid);
                if (gridView.isEditMode()) {
                    gridView.stopEditMode();
                    relayoutOK.setVisibility(View.GONE);
                    omnibox.setVisibility(View.VISIBLE);
                    initHomeGrid(layout, true);
                }
            }
        }

        IntentUnit.setContext(this);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(snackbarReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        logd("Destroying...");
    //    ViewServer.get(this).removeWindow(this);
        Intent toHolderService = new Intent(this, HolderService.class);
        IntentUnit.setClear(true);
        stopService(toHolderService);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean exit = true;
        if (sp.getBoolean(getString(R.string.sp_clear_quit), false)) {
            Intent toClearService = new Intent(this, ClearService.class);
            startService(toClearService);
            exit = false;
        }

        BrowserContainer.clear();
        IntentUnit.setContext(null);
        super.onDestroy();
        if (exit) {
            android.os.Process.killProcess(android.os.Process.myPid()); // recommended, use System.exit(0) as fallback
            System.exit(0); // For remove all WebView thread
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        logd("Config changed!");
        if (currentAlbumController != null && currentAlbumController instanceof UltimateBrowserProjectRelativeLayout) {
            UltimateBrowserProjectRelativeLayout layout = (UltimateBrowserProjectRelativeLayout) currentAlbumController;
            if (layout.getFlag() == BrowserUnit.FLAG_HOME) {
                DynamicGridView gridView = (DynamicGridView) layout.findViewById(R.id.home_grid);
                if (gridView.isEditMode()) {
                    gridView.stopEditMode();
                    relayoutOK.setVisibility(View.GONE);
                    omnibox   .setVisibility(View.VISIBLE);
                }
            }
        }

        hideSoftInput(inputBox);
        hideSearchPanel();
        if (switcherPanel.getStatus() != SwitcherPanel.Status.EXPANDED)
            switcherPanel.expanded();

        super.onConfigurationChanged(newConfig);

        float coverHeight = ViewUnit.getWindowHeight(this) - ViewUnit.getStatusBarHeight(this) - dimen108dp - dimen48dp;
        switcherPanel.setCoverHeight(coverHeight);
        switcherPanel.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                switcherPanel.fixKeyBoardShowing(switcherPanel.getHeight());
                switcherPanel.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        if (currentAlbumController != null && currentAlbumController instanceof UltimateBrowserProjectRelativeLayout) {
            UltimateBrowserProjectRelativeLayout layout = (UltimateBrowserProjectRelativeLayout) currentAlbumController;
            if (layout.getFlag() == BrowserUnit.FLAG_HOME) {
                initHomeGrid(layout, true);
                omnibox.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        logd("Key down event");
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            // When video fullscreen, just control the sound
            return !(fullscreenHolder != null || customView != null || videoView != null) && onKeyCodeVolumeUp();
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // When video fullscreen, just control the sound
            return !(fullscreenHolder != null || customView != null || videoView != null) && onKeyCodeVolumeDown();
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            return showOverflow();
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            // When video fullscreen, first close it
            if (fullscreenHolder != null || customView != null || videoView != null)
                return onHideCustomView();

            return onKeyCodeBack(true);
        }

        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        logd("Key up event");
        // When video fullscreen, just control the sound
        if (fullscreenHolder != null || customView != null || videoView != null)
            return false;

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            int vc = Integer.valueOf(sp.getString(getString(R.string.sp_volume), "1"));
            if (vc != 2) {
                return true;
            }
        }

        return false;
    }

    private void initSwitcherView() {
        switcherScroller  = (HorizontalScrollView)  findViewById(R.id.switcher_scroller);
        switcherContainer = (LinearLayout)          findViewById(R.id.switcher_container);
        switcherSetting   = (ImageButton)           findViewById(R.id.switcher_setting);
        switcherBookmarks = (ImageButton)           findViewById(R.id.switcher_bookmarks);
        switcherHistory   = (ImageButton)           findViewById(R.id.switcher_history);
        switcherAdd       = (ImageButton)           findViewById(R.id.switcher_add);
        switcherPrint     = (ImageButton)           findViewById(R.id.switcher_print);

        switcherSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BrowserActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        switcherBookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlbum(BrowserUnit.FLAG_BOOKMARKS);
            }
        });

        switcherPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentAlbumController != null && currentAlbumController instanceof UltimateBrowserProjectRelativeLayout) {
                    UltimateBrowserProjectRelativeLayout layout = (UltimateBrowserProjectRelativeLayout) currentAlbumController;
                    if(layout.getFlag()== BrowserUnit.FLAG_HOME) {
                        Toast.makeText(BrowserActivity.this,
                                "Nothing to Print here!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else if(currentAlbumController != null && currentAlbumController instanceof UltimateBrowserProjectWebView) {
                        UltimateBrowserProjectWebView UltimateBrowserProjectWebView = (UltimateBrowserProjectWebView) currentAlbumController;
                        String title = UltimateBrowserProjectWebView.getTitle();
                        String url = UltimateBrowserProjectWebView.getUrl();
                        final String targetCache = url;
                        BrowserUnit.downloadCache(BrowserActivity.this, targetCache, targetCache, URLConnection.guessContentTypeFromName(targetCache));
                        if (!isNetworkAvailable()) {
                            Toast.makeText(BrowserActivity.this,
                                    "Network connection not available, Please try later",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/cache");
                            Intent printIntent = new Intent(BrowserActivity.this, PrintDialogActivity.class);
                            printIntent.setDataAndType(Uri.fromFile(file), "text/html");
                            printIntent.putExtra("title", title);
                            startActivity(printIntent);
                        }
                    }
                }


        });
        switcherHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlbum(BrowserUnit.FLAG_HISTORY);
            }
        });
        switcherAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlbum(BrowserUnit.FLAG_HOME);
            }
        });

    }

    public static Context getContext() {
        return staticContext;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.e("Network Testing", "***Available***");
            return true;
        }
        Log.e("Network Testing", "***Not Available***");
        return false;
    }

    public static void removeWebViewPlaceholder() {
        contentFrame.removeView(webViewPlaceHolder);
    }

    private void initOmnibox() {
        omnibox         = (RelativeLayout) findViewById(R.id.main_omnibox);
        inputBox        = (AutoCompleteTextView) findViewById(R.id.main_omnibox_input);
        omniboxBookmark = (ImageButton) findViewById(R.id.main_omnibox_bookmark);
        omniboxRefresh  = (ImageButton) findViewById(R.id.main_omnibox_refresh);
        omniboxForward  = (ImageButton) findViewById(R.id.main_omnibox_forward);
        omniboxOverflow = (ImageButton) findViewById(R.id.main_omnibox_overflow);
        progressBar     = (ProgressBar) findViewById(R.id.main_progress_bar);


        inputBox.setOnTouchListener(new SwipeToBoundListener(omnibox, new SwipeToBoundListener.BoundCallback() {
            private KeyListener keyListener = inputBox.getKeyListener();

            @Override
            public boolean canSwipe() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(BrowserActivity.this);
                boolean ob = sp.getBoolean(getString(R.string.sp_omnibox_control), true);
                return !switcherPanel.isKeyBoardShowing() && ob;
            }

            @Override
            public void onSwipe() {
                inputBox.setKeyListener(null);
                inputBox.setFocusable(false);
                inputBox.setFocusableInTouchMode(false);
                inputBox.clearFocus();
            }

            @Override
            public void onBound(boolean canSwitch, boolean left) {
                inputBox.setKeyListener(keyListener);
                inputBox.setFocusable(true);
                inputBox.setFocusableInTouchMode(true);
                inputBox.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_URI);
                inputBox.clearFocus();

                if (canSwitch) {
                    AlbumController controller = nextAlbumController(left);
                    showAlbum(controller, false, false, true);
                    UltimateBrowserProjectToast.show(BrowserActivity.this, controller.getAlbumTitle());
                }
            }
        }));

        inputBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (currentAlbumController == null) { // || !(actionId == EditorInfo.IME_ACTION_DONE)) {
                    return false;
                }

                String query = inputBox.getText().toString().trim();
                if (query.isEmpty()) {
                    UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_input_empty);
                    return true;
                }

                updateAlbum(query);
                hideSoftInput(inputBox);
                return false;
            }
        });
        logd("Updating bookmarks...");
        updateBookmarks();
        logd("Updating autocomplete...");
        updateAutoComplete();

        omniboxBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!prepareRecord()) {
                    UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_add_bookmark_failed);
                    return;
                }

                UltimateBrowserProjectWebView UltimateBrowserProjectWebView = (UltimateBrowserProjectWebView) currentAlbumController;
                String title = UltimateBrowserProjectWebView.getTitle();
                String url = UltimateBrowserProjectWebView.getUrl();

                RecordAction action = new RecordAction(BrowserActivity.this);
                action.open(true);
                if (action.checkBookmark(url)) {
                    action.deleteBookmark(url);
                    UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_delete_bookmark_successful);
                } else {
                    action.addBookmark(new Record(title, url, System.currentTimeMillis()));
                    UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_add_bookmark_successful);
                }
                action.close();

                updateBookmarks();
                updateAutoComplete();
            }
        });

        omniboxRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentAlbumController == null) {
                    UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_refresh_failed);
                    return;
                }

                if (currentAlbumController instanceof UltimateBrowserProjectWebView) {
                    UltimateBrowserProjectWebView UltimateBrowserProjectWebView = (UltimateBrowserProjectWebView) currentAlbumController;
                    if (UltimateBrowserProjectWebView.isLoadFinish()) {
                        UltimateBrowserProjectWebView.reload();
                    } else {
                        UltimateBrowserProjectWebView.stopLoading();
                    }
                } else if (currentAlbumController instanceof UltimateBrowserProjectRelativeLayout) {
                    final UltimateBrowserProjectRelativeLayout layout = (UltimateBrowserProjectRelativeLayout) currentAlbumController;
                    if (layout.getFlag() == BrowserUnit.FLAG_HOME) {
                        initHomeGrid(layout, true);
                        omnibox.setVisibility(View.VISIBLE);
                        return;
                    }
                    initBHList(layout, true);
                } else {
                    UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_refresh_failed);
                }
            }
        });

        omniboxOverflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverflow();
            }
        });

        omniboxForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentAlbumController == null) {
                    UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_cannot_forward);
                    return;
                }
                if (currentAlbumController instanceof UltimateBrowserProjectWebView) {
                    UltimateBrowserProjectWebView ultimateBrowserProjectWebView = (UltimateBrowserProjectWebView) currentAlbumController;
                    if (ultimateBrowserProjectWebView.canGoForward()) {
                        ultimateBrowserProjectWebView.goForward();
                    } else if (currentAlbumController instanceof UltimateBrowserProjectRelativeLayout) {
                        final UltimateBrowserProjectRelativeLayout layout = (UltimateBrowserProjectRelativeLayout) currentAlbumController;
                        if (layout.getFlag() == BrowserUnit.FLAG_HOME) {
                            initHomeGrid(layout, true);
                            omnibox.setVisibility(View.VISIBLE);
                            return;
                        }
                        initBHList(layout, true);
                    } else UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_cannot_forward);
                }
            }
        });

    }

    /* This Thread checks for Updates in the Background */
    private Thread checkUpdate = new Thread() {
        public void run() {
            try {
                URL updateURL = new URL("https://raw.githubusercontent.com/balzathor/UltimateBrowserProject/master/Update.txt");
                BufferedReader in = new BufferedReader(new InputStreamReader(updateURL.openStream()));
                String str;
                while ((str = in.readLine()) != null) {
                    // str is one line of text; readLine() strips the newline character(s)
                /* Get current Version Number */
                    PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    int curVersion = packageInfo.versionCode;
                    int newVersion = Integer.valueOf(str);

                /* Is a higher version than the current already out? */
                    if (newVersion > curVersion) {
                    /* Post a Handler for the UI to pick up and open the Dialog */
                        mHandler.post(showUpdate);
                    }

                }
                in.close();
            } catch (Exception e) { /* Do nothing */ }
        }

    };

    /* This Runnable creates a Dialog and asks the user to open the Market */
    private Runnable showUpdate = new Runnable() {
        public void run() {
            new AlertDialog.Builder(BrowserActivity.this)
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle("Update Available")
                    .setMessage("An update for the latest version is available!\n\nOpen Update page and download?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            /* User clicked OK so do some stuff */
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/balzathor/UltimateBrowserProject/releases/download/latest/UltimateBrowserProject.apk"));
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            /* User clicked Cancel */
                        }
                    })
                    .show();
        }
    };

    private void initHomeGrid(final UltimateBrowserProjectRelativeLayout layout, boolean update) {
        if (update)
            updateProgress(BrowserUnit.PROGRESS_MIN);


        RecordAction action = new RecordAction(this);
        action.open(false);
        final List<GridItem> gridList = action.listGrid();
        action.close();

        DynamicGridView gridView = (DynamicGridView) layout.findViewById(R.id.home_grid);
        TextView aboutBlank = (TextView) layout.findViewById(R.id.home_about_blank);
        gridView.setEmptyView(aboutBlank);

        final GridAdapter gridAdapter;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
             gridAdapter = new GridAdapter(this, gridList, 3);
        else gridAdapter = new GridAdapter(this, gridList, 2);

        gridView.setAdapter(gridAdapter);
        gridAdapter.notifyDataSetChanged();

        /* Wait for gridAdapter.notifyDataSetChanged() */
        if (update) {
            gridView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    layout.setAlbumCover(ViewUnit.capture(layout, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
                    updateProgress(BrowserUnit.PROGRESS_MAX);
                }
            }, shortAnimTime);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateAlbum(gridList.get(position).getURL());
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showGridMenu(gridList.get(position));
                return true;
            }
        });
    }

    private void initBHList(final UltimateBrowserProjectRelativeLayout layout, boolean update) {
        if (update)
            updateProgress(BrowserUnit.PROGRESS_MIN);

        RecordAction action = new RecordAction(BrowserActivity.this);
        action.open(false);
        final List<Record> list;
        if (layout.getFlag() == BrowserUnit.FLAG_BOOKMARKS) {
            list = action.listBookmarks();
            Collections.sort(list, new Comparator<Record>() {
                @Override
                public int compare(Record first, Record second) {
                    return first.getTitle().compareTo(second.getTitle());
                }
            });
        } else if (layout.getFlag() == BrowserUnit.FLAG_HISTORY)
             list = action.listHistory();
        else list = new ArrayList<>();

        action.close();

        ListView listView = (ListView) layout.findViewById(R.id.record_list);
        TextView textView = (TextView) layout.findViewById(R.id.record_list_empty);
        listView.setEmptyView(textView);

        final RecordAdapter adapter = new RecordAdapter(BrowserActivity.this, R.layout.record_item, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        /* Wait for adapter.notifyDataSetChanged() */
        if (update) {
            listView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    layout.setAlbumCover(ViewUnit.capture(layout, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
                    updateProgress(BrowserUnit.PROGRESS_MAX);
                }
            }, shortAnimTime);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateAlbum(list.get(position).getURL());
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showListMenu(adapter, list, position);
                return true;
            }
        });
    }

    private void initSearchPanel() {
        searchPanel  = (RelativeLayout) findViewById(R.id.main_search_panel);
        searchBox    = (EditText)       findViewById(R.id.main_search_box);
        searchUp     = (ImageButton)    findViewById(R.id.main_search_up);
        searchDown   = (ImageButton)    findViewById(R.id.main_search_down);
        searchCancel = (ImageButton)    findViewById(R.id.main_search_cancel);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (currentAlbumController != null && currentAlbumController instanceof UltimateBrowserProjectWebView) {
                    ((UltimateBrowserProjectWebView) currentAlbumController).findAllAsync(s.toString());
                }
            }
        });

        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != EditorInfo.IME_ACTION_DONE) {
                    return false;
                }

                if (searchBox.getText().toString().isEmpty()) {
                    UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_input_empty);
                    return true;
                }
                return false;
            }
        });

        searchUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchBox.getText().toString();
                if (query.isEmpty()) {
                    UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_input_empty);
                    return;
                }

                hideSoftInput(searchBox);
                if (currentAlbumController instanceof UltimateBrowserProjectWebView)
                    ((UltimateBrowserProjectWebView) currentAlbumController).findNext(false);

            }
        });

        searchDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchBox.getText().toString();
                if (query.isEmpty()) {
                    UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_input_empty);
                    return;
                }

                hideSoftInput(searchBox);
                if (currentAlbumController instanceof UltimateBrowserProjectWebView)
                    ((UltimateBrowserProjectWebView) currentAlbumController).findNext(true);

            }
        });

        searchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSearchPanel();
            }
        });
    }

    private synchronized void addAlbum(int flag) {
        logd("Adding album...");
        final AlbumController holder;
        if (flag == BrowserUnit.FLAG_BOOKMARKS) {
            UltimateBrowserProjectRelativeLayout layout = (UltimateBrowserProjectRelativeLayout)
                    getLayoutInflater().inflate(R.layout.record_list, null, false);

            layout.setBrowserController(this);
            layout.setFlag(BrowserUnit.FLAG_BOOKMARKS);
            layout.setAlbumCover(ViewUnit.capture(layout, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
            layout.setAlbumTitle(getString(R.string.album_title_bookmarks));
            holder = layout;
            initBHList(layout, false);
        } else if (flag == BrowserUnit.FLAG_HISTORY) {
            UltimateBrowserProjectRelativeLayout layout = (UltimateBrowserProjectRelativeLayout)
                    getLayoutInflater().inflate(R.layout.record_list, null, false);

            layout.setBrowserController(this);
            layout.setFlag(BrowserUnit.FLAG_HISTORY);
            layout.setAlbumCover(ViewUnit.capture(layout, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
            layout.setAlbumTitle(getString(R.string.album_title_history));
            holder = layout;
            initBHList(layout, false);
        } else if (flag == BrowserUnit.FLAG_HOME) {
            UltimateBrowserProjectRelativeLayout layout = (UltimateBrowserProjectRelativeLayout)
                    getLayoutInflater().inflate(R.layout.home, null, false);

            layout.setBrowserController(this);
            layout.setFlag(BrowserUnit.FLAG_HOME);
            layout.setAlbumCover(ViewUnit.capture(layout, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
            layout.setAlbumTitle(getString(R.string.album_title_home));
            holder = layout;
            initHomeGrid(layout, true);
            omnibox.setVisibility(View.VISIBLE);
        } else return;


        final View albumView = holder.getAlbumView();
        albumView.setVisibility(View.INVISIBLE);

        BrowserContainer.add(holder);
        switcherContainer.addView(albumView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.album_slide_in_up);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
                albumView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showAlbum(holder, false, true, true);
            }
        });
        albumView.startAnimation(animation);
    }

    private synchronized void addAlbum(String title, final String url, final boolean foreground, final Message resultMsg) {
        logd("Adding album... (2)");
        final UltimateBrowserProjectWebView webView = new UltimateBrowserProjectWebView(this);
        webView.setBrowserController(this);
        webView.setFlag(BrowserUnit.FLAG_UltimateBrowserProject);
        webView.setAlbumCover(ViewUnit.capture(webView, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
        webView.setAlbumTitle(title);
        setBound(webView);
        webView.setUrl(url);

        final View albumView = webView.getAlbumView();
        if (currentAlbumController != null && (currentAlbumController instanceof UltimateBrowserProjectWebView) && resultMsg != null) {
            int index = BrowserContainer.indexOf(currentAlbumController) + 1;
            BrowserContainer.add(webView, index);
            switcherContainer.addView(albumView, index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        } else {
            BrowserContainer.add(webView);
            switcherContainer.addView(albumView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        if (!foreground) {
            setBound(webView);
            webView.loadUrl(url);
            webView.deactivate();

            albumView.setVisibility(View.VISIBLE);
            if (currentAlbumController != null)
                switcherScroller.smoothScrollTo(currentAlbumController.getAlbumView().getLeft(), 0);

            return;
        }

        albumView.setVisibility(View.INVISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.album_slide_in_up);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationRepeat(Animation animation) {
            }


            @Override
            public void onAnimationStart(Animation animation) {
                albumView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showAlbum(webView, false, true, false);

                if (url != null && !url.isEmpty()) {
                    webView.loadUrl(url);
                } else if (resultMsg != null) {
                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                    transport.setWebView(webView);
                    resultMsg.sendToTarget();
                }
            }
        });
        albumView.startAnimation(animation);
    }


    public synchronized void pinAlbums(String url) {
        logd("Pinning ablums...");
        hideSoftInput(inputBox);
        hideSearchPanel();
        switcherContainer.removeAllViews();

        for (AlbumController controller : BrowserContainer.list()) {
            if (controller instanceof UltimateBrowserProjectWebView) {
                ((UltimateBrowserProjectWebView) controller).setBrowserController(this);
            } else if (controller instanceof UltimateBrowserProjectRelativeLayout) {
                ((UltimateBrowserProjectRelativeLayout) controller).setBrowserController(this);
            }
            switcherContainer.addView(controller.getAlbumView(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            controller.getAlbumView().setVisibility(View.VISIBLE);
            controller.deactivate();
        }

        if (BrowserContainer.size() < 1 && url == null) {
            addAlbum(BrowserUnit.FLAG_HOME);
        } else if (BrowserContainer.size() >= 1 && url == null) {
            if (currentAlbumController != null) {
                currentAlbumController.activate();
                return;
            }

            int index = BrowserContainer.size() - 1;
            currentAlbumController = BrowserContainer.get(index);
            contentFrame.removeAllViews();
            contentFrame.addView((View) currentAlbumController);
            currentAlbumController.activate();

            updateOmnibox();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    switcherScroller.smoothScrollTo(currentAlbumController.getAlbumView().getLeft(), 0);
                    currentAlbumController.setAlbumCover(ViewUnit.capture(((View) currentAlbumController), dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
                }
            }, shortAnimTime);
        } else { // When url != null
            UltimateBrowserProjectWebView webView = new UltimateBrowserProjectWebView(this);
            webView.setBrowserController(this);
            webView.setFlag(BrowserUnit.FLAG_UltimateBrowserProject);
            webView.setAlbumCover(ViewUnit.capture(webView, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
            webView.setAlbumTitle(getString(R.string.album_untitled));
            setBound(webView);
            ubpWebView = webView;
            contentFrame.addView(webView);
            webView.loadUrl(url);

            BrowserContainer.add(webView);
            final View albumView = webView.getAlbumView();
            albumView.setVisibility(View.VISIBLE);
            switcherContainer.addView(albumView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            contentFrame.removeAllViews();


            if (currentAlbumController != null)
                currentAlbumController.deactivate();

            currentAlbumController = webView;
            currentAlbumController.activate();

            updateOmnibox();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    switcherScroller.smoothScrollTo(currentAlbumController.getAlbumView().getLeft(), 0);
                    currentAlbumController.setAlbumCover(ViewUnit.capture(((View) currentAlbumController), dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
                }
            }, shortAnimTime);
        }
    }

    private void setBound(View view) {
        ViewUnit.bound(this, view, fullscreen);
    }

    @Override
    public synchronized void showAlbum(AlbumController controller, boolean anim, final boolean expand, final boolean capture) {
        logd("Showing album...");
        if (controller == null || controller == currentAlbumController) {
            if (ViewCompat.isAttachedToWindow(switcherPanel)) switcherPanel.expanded();
            return;
        }

        if (currentAlbumController != null && anim) {
            currentAlbumController.deactivate();
            final View rv = (View) currentAlbumController;
            final View av = (View) controller;

            Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.album_fade_out);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationStart(Animation animation) {
                    contentFrame.removeAllViews();
                    contentFrame.addView(av);
                    setBound(av);
                }
            });
            rv.startAnimation(fadeOut);
        } else {
            if (currentAlbumController != null) {
                currentAlbumController.deactivate();
            }
            contentFrame.removeView((View) controller);
            contentFrame.addView((View) controller);
            setBound((View) controller);
        }

        currentAlbumController = controller;
        currentAlbumController.activate();
        switcherScroller.smoothScrollTo(currentAlbumController.getAlbumView().getLeft(), 0);
        updateOmnibox();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (expand) {
                    switcherPanel.expanded();
                }

                if (capture) {
                    currentAlbumController.setAlbumCover(ViewUnit.capture(((View) currentAlbumController), dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
                }
            }
        }, shortAnimTime);
        ViewParent parent = omnibox.getParent();
        if (parent != contentFrame) {
            ((ViewGroup) parent).removeView(omnibox);
            contentFrame.addView(omnibox);

            logd("Applying omnibox layout options...");
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) omnibox.getLayoutParams();

            p.height = ViewUnit.getOmniboxHeight(getApplicationContext());

            if(anchor == 0)
                p.setMargins(0, 0, 0, ViewUnit.getAdjustedWindowHeight(getApplicationContext()) - p.height);
            else
                p.setMargins(0, ViewUnit.getAdjustedWindowHeight(getApplicationContext()) - p.height, 0, 0);

            logd("Setting layout params...");
            omnibox.setLayoutParams(p);
        }
        omnibox.bringToFront();
    }

    private synchronized void updateAlbum() {
        logd("Updating album...");
        if (currentAlbumController == null)
            return;


        UltimateBrowserProjectRelativeLayout layout = (UltimateBrowserProjectRelativeLayout) getLayoutInflater().inflate(R.layout.home, null, false);
        layout.setBrowserController(this);
        layout.setFlag(BrowserUnit.FLAG_HOME);
        layout.setAlbumCover(ViewUnit.capture(layout, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
        layout.setAlbumTitle(getString(R.string.album_title_home));
        initHomeGrid(layout, true);

        int index = switcherContainer.indexOfChild(currentAlbumController.getAlbumView());
        currentAlbumController.deactivate();
        switcherContainer.removeView(currentAlbumController.getAlbumView());
        contentFrame.removeView(layout);

        switcherContainer.addView(layout.getAlbumView(), index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        contentFrame.addView(layout);
        BrowserContainer.set(layout, index);
        BrowserContainer.set(layout, index);
        currentAlbumController = layout;
        updateOmnibox();
        omnibox.setVisibility(View.VISIBLE);
        omnibox.bringToFront();
    }

    private synchronized void updateAlbum(String url) {
        logd("Updating album...(2)");
        if (currentAlbumController == null)
            return;


        if (currentAlbumController instanceof UltimateBrowserProjectWebView) {
            ((UltimateBrowserProjectWebView) currentAlbumController).loadUrl(url);
            updateOmnibox();
        } else if (currentAlbumController instanceof UltimateBrowserProjectRelativeLayout) {
            UltimateBrowserProjectWebView webView = new UltimateBrowserProjectWebView(this);
            webView.setBrowserController(this);
            webView.setFlag(BrowserUnit.FLAG_UltimateBrowserProject);
            webView.setAlbumCover(ViewUnit.capture(webView, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
            webView.setAlbumTitle(getString(R.string.album_untitled));
            setBound(webView);

            int index = switcherContainer.indexOfChild(currentAlbumController.getAlbumView());
            currentAlbumController.deactivate();
            switcherContainer.removeView(currentAlbumController.getAlbumView());
            contentFrame.removeView(webView);

            switcherContainer.addView(webView.getAlbumView(), index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            contentFrame.addView(webView);
            BrowserContainer.set(webView, index);
            currentAlbumController = webView;
            webView.activate();

            webView.loadUrl(url);
            updateOmnibox();
        } else {
            UltimateBrowserProjectToast.show(this, R.string.toast_load_error);
        }
        omnibox.bringToFront();
    }

    @Override
    public synchronized void removeAlbum(AlbumController controller) {
        logd("Removing album...");
        if (currentAlbumController == null || BrowserContainer.size() <= 1) {
            switcherContainer.removeView(controller.getAlbumView());
            BrowserContainer.remove(controller);
            addAlbum(BrowserUnit.FLAG_HOME);
            return;
        }
        BrowserContainer.get(BrowserContainer.indexOf(controller)).getUrl();
        if (controller != currentAlbumController) {
            switcherContainer.removeView(controller.getAlbumView());
            BrowserContainer.remove(controller);
        } else {
            switcherContainer.removeView(controller.getAlbumView());
            int index = BrowserContainer.indexOf(controller);
            BrowserContainer.remove(controller);
            if (index >= BrowserContainer.size()) {
                index = BrowserContainer.size() - 1;
            }
            showAlbum(BrowserContainer.get(index), false, false, false);
        }
    }

    @Override
    public void updateAutoComplete() {
        RecordAction action = new RecordAction(this);
        action.open(false);
        List<Record> list = action.listBookmarks();
        list.addAll(action.listHistory());
        action.close();

        final CompleteAdapter adapter = new CompleteAdapter(this, R.layout.complete_item, list);
        inputBox.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            inputBox.setDropDownVerticalOffset(getResources().getDimensionPixelOffset(R.dimen.layout_height_6dp));
        }
        inputBox.setDropDownWidth(ViewUnit.getWindowWidth(this));
        inputBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = ((TextView) view.findViewById(R.id.complete_item_url)).getText().toString();
                inputBox.setText(Html.fromHtml(BrowserUnit.urlWrapper(url)), EditText.BufferType.SPANNABLE);
                inputBox.setSelection(url.length());
                updateAlbum(url);
                hideSoftInput(inputBox);
            }
        });
    }

    @Override
    public void updateBookmarks() {
        if (currentAlbumController == null || !(currentAlbumController instanceof UltimateBrowserProjectWebView)) {
            omniboxBookmark.setImageDrawable(ViewUnit.getDrawable(this, R.drawable.bookmark_selector_dark));
            return;
        }

        RecordAction action = new RecordAction(this);
        action.open(false);
        String url = ((UltimateBrowserProjectWebView) currentAlbumController).getUrl();
        if (action.checkBookmark(url)) {
            omniboxBookmark.setImageDrawable(ViewUnit.getDrawable(this, R.drawable.bookmark_selector_blue));
        } else {
            omniboxBookmark.setImageDrawable(ViewUnit.getDrawable(this, R.drawable.bookmark_selector_dark));
        }
        action.close();
    }

    @Override
    public void updateInputBox(String query) {
        if (query != null)
            inputBox.setText(Html.fromHtml(BrowserUnit.urlWrapper(query)), EditText.BufferType.SPANNABLE);
        else
            inputBox.setText(null);

        inputBox.clearFocus();
    }

    private void updateOmnibox() {
        logd("Updating omnibox");

        if (currentAlbumController == null)
            return;

        if (currentAlbumController instanceof UltimateBrowserProjectRelativeLayout) {
            updateProgress(BrowserUnit.PROGRESS_MAX);
            updateBookmarks();
            updateInputBox(null);
        } else if (currentAlbumController instanceof UltimateBrowserProjectWebView) {
            UltimateBrowserProjectWebView UltimateBrowserProjectWebView = (UltimateBrowserProjectWebView) currentAlbumController;
            updateProgress(UltimateBrowserProjectWebView.getProgress());
            updateBookmarks();
            if (UltimateBrowserProjectWebView.getUrl() == null && UltimateBrowserProjectWebView.getOriginalUrl() == null) {
                updateInputBox(null);
            } else if (UltimateBrowserProjectWebView.getUrl() != null) {
                updateInputBox(UltimateBrowserProjectWebView.getUrl());
            } else {
                updateInputBox(UltimateBrowserProjectWebView.getOriginalUrl());
            }
        }
    }

    @Override
    public synchronized void updateProgress(int progress) {
        if (progress > progressBar.getProgress()) {
            ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", progress);
            animator.setDuration(shortAnimTime);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.start();
        } else if (progress < progressBar.getProgress()) {
            ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", 0, progress);
            animator.setDuration(shortAnimTime);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.start();
        }

        updateBookmarks();
        if (progress < BrowserUnit.PROGRESS_MAX) {
            updateRefresh(true);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            updateRefresh(false);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void updateRefresh(boolean running) {
        if (running)
            omniboxRefresh.setImageDrawable(ViewUnit.getDrawable(this, R.drawable.cl_selector_dark));
        else
            omniboxRefresh.setImageDrawable(ViewUnit.getDrawable(this, R.drawable.refresh_selector));

    }

    @Override
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        // Because Activity launchMode is singleInstance,
        // so we can not get result from onActivityResult when Android 4.X,
        // what a pity
        //
        // this.uploadMsg = uploadMsg;
        // Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // intent.addCategory(Intent.CATEGORY_OPENABLE);
        // intent.setType("*/*");
        // startActivityForResult(Intent.createChooser(intent, getString(R.string.main_file_chooser)), IntentUnit.REQUEST_FILE_16);
        uploadMsg.onReceiveValue(null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        FrameLayout layout = (FrameLayout) getLayoutInflater().inflate(R.layout.dialog_desc, null, false);
        TextView textView = (TextView) layout.findViewById(R.id.dialog_desc);
        textView.setText(R.string.dialog_content_upload);

        builder.setView(layout);
        builder.create().show();
    }

    @Override
    public void showFileChooser(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.filePathCallback = filePathCallback;

            try {
                Intent intent = fileChooserParams.createIntent();
                startActivityForResult(intent, IntentUnit.REQUEST_FILE_21);
            } catch (Exception e) {
                UltimateBrowserProjectToast.show(this, R.string.toast_open_file_manager_failed);
            }
        }
    }

    @Override
    public void onCreateView(WebView view, final Message resultMsg) {
        logd("[]onCreateView");
        if (resultMsg == null)
            return;

        switcherPanel.collapsed();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addAlbum(getString(R.string.album_untitled), null, true, resultMsg);
            }
        }, shortAnimTime);
    }

    @Override
    public boolean onShowCustomView(View view, int requestedOrientation, WebChromeClient.CustomViewCallback callback) {
        return onShowCustomView(view, callback);
    }

    @Override
    public boolean onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        logd("[]onShowCustomView");
        if (view == null)
            return false;

        if (customView != null && callback != null) {
            callback.onCustomViewHidden();
            return false;
        }

        customView = view;
        originalOrientation = getRequestedOrientation();

        fullscreenHolder = new FullscreenHolder(this);
        fullscreenHolder.addView(
                customView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ));

        FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
        decorView.addView(
                fullscreenHolder,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ));

        customView.setKeepScreenOn(true);
        ((View) currentAlbumController).setVisibility(View.GONE);
        setCustomFullscreen(true);

        if (view instanceof FrameLayout) {
            if (((FrameLayout) view).getFocusedChild() instanceof VideoView) {
                videoView = (VideoView) ((FrameLayout) view).getFocusedChild();
                videoView.setOnErrorListener(new VideoCompletionListener());
                videoView.setOnCompletionListener(new VideoCompletionListener());
            }
        }
        customViewCallback = callback;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Auto landscape when video shows

        return true;
    }

    @Override
    public boolean onHideCustomView() {
        logd("[]onHideCustomView");
        if (customView == null || customViewCallback == null || currentAlbumController == null)
            return false;


        FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
        if (decorView != null)
            decorView.removeView(fullscreenHolder);


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            try {
                customViewCallback.onCustomViewHidden();
            } catch (Throwable t) {
                /* */
            }
        }

        customView.setKeepScreenOn(false);
        ((View) currentAlbumController).setVisibility(View.VISIBLE);
        setCustomFullscreen(false);

        fullscreenHolder = null;
        customView = null;
        if (videoView != null) {
            videoView.setOnErrorListener(null);
            videoView.setOnCompletionListener(null);
            videoView = null;
        }
        setRequestedOrientation(originalOrientation);

        return true;
    }

    @Override
    public void onLongPress(String url) {
        WebView.HitTestResult result;
        if (!(currentAlbumController instanceof UltimateBrowserProjectWebView))
            return;

        result = ((UltimateBrowserProjectWebView) currentAlbumController).getHitTestResult();

        final List<String> list = new ArrayList<>();
        list.add(getString(R.string.main_menu_new_tab));
        list.add(getString(R.string.main_menu_copy_link));
        list.add(getString(R.string.main_menu_save));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        FrameLayout layout = (FrameLayout) getLayoutInflater().inflate(R.layout.dialog_list, null, false);
        builder.setView(layout);

        ListView listView = (ListView) layout.findViewById(R.id.dialog_list);
        DialogAdapter adapter = new DialogAdapter(this, R.layout.dialog_text_item, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        final AlertDialog dialog = builder.create();
        if (url != null || (result != null && result.getExtra() != null)) {
            if (url == null) {
                url = result.getExtra();
            }
            dialog.show();
        }

        final String target = url;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = list.get(position);
                if (s.equals(getString(R.string.main_menu_new_tab))) { // New tab
                    addAlbum(getString(R.string.album_untitled), target, false, null);
                    UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_new_tab_successful);
                } else if (s.equals(getString(R.string.main_menu_copy_link))) { // Copy link
                    BrowserUnit.copyURL(BrowserActivity.this, target);
                } else if (s.equals(getString(R.string.main_menu_save))) {  // Save link
                    BrowserUnit.download(BrowserActivity.this, target, target, URLConnection.guessContentTypeFromName(target));
                }

                dialog.hide();
                dialog.dismiss();
            }
        });
    }

    private boolean onKeyCodeVolumeUp() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int vc = Integer.valueOf(sp.getString(getString(R.string.sp_volume), "1"));

        if (vc == 0) { // Switch tabs
            if (switcherPanel.isKeyBoardShowing()) {
                return true;
            }
            AlbumController controller = nextAlbumController(false);
            showAlbum(controller, false, false, true);
            UltimateBrowserProjectToast.show(this, controller.getAlbumTitle());

            return true;
        } else if (vc == 1 && currentAlbumController instanceof UltimateBrowserProjectWebView) { // Scroll webpage
            UltimateBrowserProjectWebView UltimateBrowserProjectWebView = (UltimateBrowserProjectWebView) currentAlbumController;
            int height = UltimateBrowserProjectWebView.getMeasuredHeight();
            int scrollY = UltimateBrowserProjectWebView.getScrollY();
            int distance = Math.min(height, scrollY);

            ObjectAnimator anim = ObjectAnimator.ofInt(UltimateBrowserProjectWebView, "scrollY", scrollY, scrollY - distance);
            anim.setDuration(mediumAnimTime);
            anim.start();
            return true;
        }

        return false;
    }

    private boolean onKeyCodeVolumeDown() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int vc = Integer.valueOf(sp.getString(getString(R.string.sp_volume), "1"));

        if (vc == 0) { // Switch tabs
            if (switcherPanel.isKeyBoardShowing()) {
                return true;
            }
            AlbumController controller = nextAlbumController(true);
            showAlbum(controller, false, false, true);
            UltimateBrowserProjectToast.show(this, controller.getAlbumTitle());

            return true;
        } else if (vc == 1 && currentAlbumController instanceof UltimateBrowserProjectWebView) {
            UltimateBrowserProjectWebView UltimateBrowserProjectWebView = (UltimateBrowserProjectWebView) currentAlbumController;
            int height = UltimateBrowserProjectWebView.getMeasuredHeight();
            int scrollY = UltimateBrowserProjectWebView.getScrollY();
            int surplus = (int) (UltimateBrowserProjectWebView.getContentHeight() * ViewUnit.getDensity(this) - height - scrollY);
            int distance = Math.min(height, surplus);

            ObjectAnimator anim = ObjectAnimator.ofInt(UltimateBrowserProjectWebView, "scrollY", scrollY, scrollY + distance);
            anim.setDuration(mediumAnimTime);
            anim.start();

            return true;
        }

        return false;
    }

    private boolean onKeyCodeBack(boolean douQ) {
        hideSoftInput(inputBox);
        if (switcherPanel.getStatus() != SwitcherPanel.Status.EXPANDED)
            switcherPanel.expanded();
        else if (currentAlbumController == null)
            finish();
        else if (currentAlbumController instanceof UltimateBrowserProjectWebView) {
            UltimateBrowserProjectWebView UltimateBrowserProjectWebView = (UltimateBrowserProjectWebView) currentAlbumController;
            if (UltimateBrowserProjectWebView.canGoBack())
                UltimateBrowserProjectWebView.goBack();
            else updateAlbum();

        } else if (currentAlbumController instanceof UltimateBrowserProjectRelativeLayout) {
            switch (currentAlbumController.getFlag()) {
                case BrowserUnit.FLAG_BOOKMARKS:    updateAlbum();      break;
                case BrowserUnit.FLAG_HISTORY:      updateAlbum();      break;
                case BrowserUnit.FLAG_HOME: if (douQ)doubleTapsQuit();  break;
                default: finish();                                      break;
            }
        } else           finish();


        return true;
    }

    private void doubleTapsQuit() {
        final Timer timer = new Timer();
        if (!quit) {
            quit = true;
            UltimateBrowserProjectToast.show(this, R.string.toast_double_taps_quit);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    quit = false;
                    timer.cancel();
                }
            }, DOUBLE_TAPS_QUIT_DEFAULT);
        } else {
            timer.cancel();
            finish();
        }
    }

    private void hideSoftInput(View view) {
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showSoftInput(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideSearchPanel() {
        hideSoftInput(searchBox);
        searchBox.setText("");
        searchPanel.setVisibility(View.GONE);
        omnibox.setVisibility(View.VISIBLE);
    }

    private void showSearchPanel() {
        omnibox.setVisibility(View.GONE);
        searchPanel.setVisibility(View.VISIBLE);
        showSoftInput(searchBox);
    }

    private boolean showOverflow() {
        logd("Showing overflow...");
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        FrameLayout layout = (FrameLayout) getLayoutInflater().inflate(R.layout.dialog_list, null, false);
        builder.setView(layout);

        final String[] array = getResources().getStringArray(R.array.main_overflow);
        final List<String> stringList = new ArrayList<>();
        stringList.addAll(Arrays.asList(array));
        if (currentAlbumController != null && currentAlbumController instanceof UltimateBrowserProjectRelativeLayout) {
            stringList.remove(array[0]); // Go to top
            stringList.remove(array[1]); // Add to home
            stringList.remove(array[2]); // Find in page
            stringList.remove(array[3]); // Screenshot
            stringList.remove(array[4]); // Readability
            stringList.remove(array[5]); // Share
            stringList.remove(array[6]); // Save Link As
            stringList.remove(array[7]); // Create Shortcut

            UltimateBrowserProjectRelativeLayout UltimateBrowserProjectRelativeLayout = (UltimateBrowserProjectRelativeLayout) currentAlbumController;
            if (UltimateBrowserProjectRelativeLayout.getFlag() != BrowserUnit.FLAG_HOME) {
                stringList.remove(array[8]); // Relayout
            }
        } else if (currentAlbumController != null && currentAlbumController instanceof UltimateBrowserProjectWebView) {
            if (!sp.getBoolean(getString(R.string.sp_readability), false)) {
                stringList.remove(array[4]); // Readability
            }
            stringList.remove(array[8]); // Relayout
        }

        ListView listView = (ListView) layout.findViewById(R.id.dialog_list);
        DialogAdapter dialogAdapter = new DialogAdapter(this, R.layout.dialog_text_item, stringList);
        listView.setAdapter(dialogAdapter);
        dialogAdapter.notifyDataSetChanged();

        final AlertDialog dialog = builder.create();
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String s = stringList.get(position);
                if (s.equals(array[0])) { // Go to top
                    UltimateBrowserProjectWebView UltimateBrowserProjectWebView = (UltimateBrowserProjectWebView) currentAlbumController;
                    ObjectAnimator anim = ObjectAnimator.ofInt(UltimateBrowserProjectWebView, "scrollY", UltimateBrowserProjectWebView.getScrollY(), 0);
                    anim.setDuration(mediumAnimTime);
                    anim.start();
                } else if (s.equals(array[1])) { // Add to home
                    UltimateBrowserProjectWebView UltimateBrowserProjectWebView = (UltimateBrowserProjectWebView) currentAlbumController;
                    RecordAction action = new RecordAction(BrowserActivity.this);
                    action.open(true);
                    if (action.checkGridItem(UltimateBrowserProjectWebView.getUrl())) {
                        UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_already_exist_in_home);
                    } else {
                        String title = UltimateBrowserProjectWebView.getTitle().trim();
                        String url = UltimateBrowserProjectWebView.getUrl().trim();
                        Bitmap bitmap = ViewUnit.capture(UltimateBrowserProjectWebView, dimen156dp, dimen117dp, false, Bitmap.Config.ARGB_8888);
                        String filename = System.currentTimeMillis() + BrowserUnit.SUFFIX_PNG;
                        int ordinal = action.listGrid().size();
                        GridItem item = new GridItem(title, url, filename, ordinal);

                        if (BrowserUnit.bitmap2File(BrowserActivity.this, bitmap, filename) && action.addGridItem(item)) {
                            UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_add_to_home_successful);
                        } else {
                            UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_add_to_home_failed);
                        }
                    }
                    action.close();
                } else if (s.equals(array[2])) { // Find in page
                    hideSoftInput(inputBox);
                    showSearchPanel();
                } else if (s.equals(array[3])) { // Screenshot
                    UltimateBrowserProjectWebView UltimateBrowserProjectWebView = (UltimateBrowserProjectWebView) currentAlbumController;
                    new ScreenshotTask(BrowserActivity.this, UltimateBrowserProjectWebView).execute();
                } else if (s.equals(array[4])) { // Readability
                    String token = sp.getString(getString(R.string.sp_readability_token), null);
                    if (token == null || token.trim().isEmpty()) {
                        UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_token_empty);
                    } else {
                        UltimateBrowserProjectWebView UltimateBrowserProjectWebView = (UltimateBrowserProjectWebView) currentAlbumController;
                        Intent intent = new Intent(BrowserActivity.this, ReadabilityActivity.class);
                        intent.putExtra(IntentUnit.URL, UltimateBrowserProjectWebView.getUrl());
                        startActivity(intent);
                    }
                } else if (s.equals(array[5])) { // Share
                    if (!prepareRecord()) {
                        UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_share_failed);
                    } else {
                        UltimateBrowserProjectWebView UltimateBrowserProjectWebView = (UltimateBrowserProjectWebView) currentAlbumController;
                        IntentUnit.share(BrowserActivity.this, UltimateBrowserProjectWebView.getTitle(), UltimateBrowserProjectWebView.getUrl());
                    }
                } else if (s.equals(array[6])) { // Save link as
                    UltimateBrowserProjectWebView UltimateBrowserProjectWebView = (UltimateBrowserProjectWebView) currentAlbumController;
                    BrowserUnit.download(BrowserActivity.this, UltimateBrowserProjectWebView.getUrl(), UltimateBrowserProjectWebView.getUrl(), URLConnection.guessContentTypeFromName(UltimateBrowserProjectWebView.getUrl()));
                } else if (s.equals(array[7])) { // Create Shortcut
                    UltimateBrowserProjectWebView ultimateBrowserProjectWebView = (UltimateBrowserProjectWebView) currentAlbumController;
                    String title = ultimateBrowserProjectWebView.getTitle();
                    String url = ultimateBrowserProjectWebView.getUrl();
                    Intent shortcutIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    Toast.makeText(getApplicationContext(), "Shortcut successfully created",
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent();
                    intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
                    intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
                    intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                            Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.ic_launcher));
                    intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                    getApplicationContext().sendBroadcast(intent);
                } else if (s.equals(array[8])) { // Relayout
                    UltimateBrowserProjectRelativeLayout UltimateBrowserProjectRelativeLayout = (UltimateBrowserProjectRelativeLayout) currentAlbumController;
                    final DynamicGridView gridView = (DynamicGridView) UltimateBrowserProjectRelativeLayout.findViewById(R.id.home_grid);
                    final List<GridItem> gridList = ((GridAdapter) gridView.getAdapter()).getList();

                    omnibox.setVisibility(View.GONE);
                    relayoutOK.setVisibility(View.VISIBLE);

                    relayoutOK.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                relayoutOK.setTextColor(getResources().getColor(R.color.blue_500));
                            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                                relayoutOK.setTextColor(getResources().getColor(R.color.white));
                            }

                            return false;
                        }
                    });

                    relayoutOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gridView.stopEditMode();
                            relayoutOK.setVisibility(View.GONE);
                            omnibox.setVisibility(View.VISIBLE);

                            RecordAction action = new RecordAction(BrowserActivity.this);
                            action.open(true);
                            action.clearGrid();
                            for (GridItem item : gridList) {
                                action.addGridItem(item);
                            }
                            action.close();
                            UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_relayout_successful);
                        }
                    });

                    gridView.setOnDragListener(new DynamicGridView.OnDragListener() {
                        private GridItem dragItem;

                        @Override
                        public void onDragStarted(int position) {
                            dragItem = gridList.get(position);
                        }

                        @Override
                        public void onDragPositionsChanged(int oldPosition, int newPosition) {
                            if (oldPosition < newPosition) {
                                for (int i = newPosition; i > oldPosition; i--) {
                                    GridItem item = gridList.get(i);
                                    item.setOrdinal(i - 1);
                                }
                            } else if (oldPosition > newPosition) {
                                for (int i = newPosition; i < oldPosition; i++) {
                                    GridItem item = gridList.get(i);
                                    item.setOrdinal(i + 1);
                                }
                            }
                            dragItem.setOrdinal(newPosition);

                            Collections.sort(gridList, new Comparator<GridItem>() {
                                @Override
                                public int compare(GridItem first, GridItem second) {
                                    if (first.getOrdinal() < second.getOrdinal()) {
                                        return -1;
                                    } else if (first.getOrdinal() > second.getOrdinal()) {
                                        return 1;
                                    } else {
                                        return 0;
                                    }
                                }
                            });
                        }
                    });
                    gridView.startEditMode();
                } else if (s.equals(array[9])) { // Quit
                    finish();
                }

                dialog.hide();
                dialog.dismiss();
            }
        });

        return true;
    }

    private void showGridMenu(final GridItem gridItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        FrameLayout layout = (FrameLayout) getLayoutInflater().inflate(R.layout.dialog_list, null, false);
        builder.setView(layout);

        final String[] array = getResources().getStringArray(R.array.list_menu);
        final List<String> stringList = new ArrayList<>();
        stringList.addAll(Arrays.asList(array));
        stringList.remove(array[1]); // Copy link
        stringList.remove(array[2]); // Share

        ListView listView = (ListView) layout.findViewById(R.id.dialog_list);
        DialogAdapter dialogAdapter = new DialogAdapter(this, R.layout.dialog_text_item, stringList);
        listView.setAdapter(dialogAdapter);
        dialogAdapter.notifyDataSetChanged();

        final AlertDialog dialog = builder.create();
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = stringList.get(position);
                if (s.equals(array[0])) { // New tab
                    addAlbum(getString(R.string.album_untitled), gridItem.getURL(), false, null);
                    UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_new_tab_successful);
                } else if (s.equals(array[3])) { // Edit
                    showEditDialog(gridItem);
                } else if (s.equals(array[4])) { // Delete
                    RecordAction action = new RecordAction(BrowserActivity.this);
                    action.open(true);
                    action.deleteGridItem(gridItem);
                    action.close();
                    BrowserActivity.this.deleteFile(gridItem.getFilename());

                    initHomeGrid((UltimateBrowserProjectRelativeLayout) currentAlbumController, true);
                    UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_delete_successful);
                }

                dialog.hide();
                dialog.dismiss();
            }
        });
    }

    private void showListMenu(final RecordAdapter recordAdapter, final List<Record> recordList, final int location) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        FrameLayout layout = (FrameLayout) getLayoutInflater().inflate(R.layout.dialog_list, null, false);
        builder.setView(layout);

        final String[] array = getResources().getStringArray(R.array.list_menu);
        final List<String> stringList = new ArrayList<>();
        stringList.addAll(Arrays.asList(array));
        if (currentAlbumController.getFlag() != BrowserUnit.FLAG_BOOKMARKS) {
            stringList.remove(array[3]);
        }

        ListView listView = (ListView) layout.findViewById(R.id.dialog_list);
        DialogAdapter dialogAdapter = new DialogAdapter(this, R.layout.dialog_text_item, stringList);
        listView.setAdapter(dialogAdapter);
        dialogAdapter.notifyDataSetChanged();

        final AlertDialog dialog = builder.create();
        dialog.show();

        final Record record = recordList.get(location);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = stringList.get(position);
                if (s.equals(array[0])) { // New tab
                    addAlbum(getString(R.string.album_untitled), record.getURL(), false, null);
                    UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_new_tab_successful);
                } else if (s.equals(array[1])) { // Copy link
                    BrowserUnit.copyURL(BrowserActivity.this, record.getURL());
                } else if (s.equals(array[2])) { // Share
                    IntentUnit.share(BrowserActivity.this, record.getTitle(), record.getURL());
                } else if (s.equals(array[3])) { // Edit
                    showEditDialog(recordAdapter, recordList, location);
                } else if (s.equals(array[4])) { // Delete
                    RecordAction action = new RecordAction(BrowserActivity.this);
                    action.open(true);
                    if (currentAlbumController.getFlag() == BrowserUnit.FLAG_BOOKMARKS) {
                        action.deleteBookmark(record);
                    } else if (currentAlbumController.getFlag() == BrowserUnit.FLAG_HISTORY) {
                        action.deleteHistory(record);
                    }
                    action.close();

                    recordList.remove(location);
                    recordAdapter.notifyDataSetChanged();

                    updateBookmarks();
                    updateAutoComplete();

                    UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_delete_successful);
                }

                dialog.hide();
                dialog.dismiss();
            }
        });
    }

    private void showEditDialog(final GridItem gridItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        FrameLayout layout = (FrameLayout) getLayoutInflater().inflate(R.layout.dialog_edit, null, false);
        builder.setView(layout);

        final AlertDialog dialog = builder.create();
        dialog.show();

        final EditText editText = (EditText) layout.findViewById(R.id.dialog_edit);
        editText.setHint(R.string.dialog_title_hint);
        editText.setText(gridItem.getTitle());
        editText.setSelection(gridItem.getTitle().length());
        hideSoftInput(inputBox);
        showSoftInput(editText);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != EditorInfo.IME_ACTION_DONE) {
                    return false;
                }

                String text = editText.getText().toString().trim();
                if (text.isEmpty()) {
                    UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_input_empty);
                    return true;
                }

                RecordAction action = new RecordAction(BrowserActivity.this);
                action.open(true);
                gridItem.setTitle(text);
                action.updateGridItem(gridItem);
                action.close();

                hideSoftInput(editText);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.hide();
                        dialog.dismiss();
                    }
                }, longAnimTime);
                return false;
            }
        });
    }

    private void showEditDialog(final RecordAdapter recordAdapter, List<Record> recordList, int location) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        FrameLayout layout = (FrameLayout) getLayoutInflater().inflate(R.layout.dialog_edit, null, false);
        builder.setView(layout);

        final AlertDialog dialog = builder.create();
        dialog.show();

        final Record record = recordList.get(location);
        final EditText editText = (EditText) layout.findViewById(R.id.dialog_edit);
        editText.setHint(R.string.dialog_title_hint);
        editText.setText(record.getTitle());
        editText.setSelection(record.getTitle().length());
        hideSoftInput(inputBox);
        showSoftInput(editText);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != EditorInfo.IME_ACTION_DONE) {
                    return false;
                }

                String text = editText.getText().toString().trim();
                if (text.isEmpty()) {
                    UltimateBrowserProjectToast.show(BrowserActivity.this, R.string.toast_input_empty);
                    return true;
                }

                RecordAction action = new RecordAction(BrowserActivity.this);
                action.open(true);
                record.setTitle(text);
                action.updateBookmark(record);
                action.close();

                recordAdapter.notifyDataSetChanged();
                hideSoftInput(editText);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.hide();
                        dialog.dismiss();
                    }
                }, longAnimTime);
                return false;
            }
        });
    }

    private boolean prepareRecord() {
        if (currentAlbumController == null || !(currentAlbumController instanceof UltimateBrowserProjectWebView)) {
            return false;
        }

        UltimateBrowserProjectWebView webView = (UltimateBrowserProjectWebView) currentAlbumController;
        String title = webView.getTitle();
        String url = webView.getUrl();
        if (title == null
                || title.isEmpty()
                || url == null
                || url.isEmpty()
                || url.startsWith(BrowserUnit.URL_SCHEME_ABOUT)
                || url.startsWith(BrowserUnit.URL_SCHEME_MAIL_TO)
                || url.startsWith(BrowserUnit.URL_SCHEME_INTENT)) {
            return false;
        }
        return true;
    }

    private void setCustomFullscreen(boolean fullscreen) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        /*
         * Can not use View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION,
         * so we can not hide NavigationBar :(
         */
        int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;

        if (fullscreen)
            layoutParams.flags |= bits;
        else {
            layoutParams.flags &= ~bits;
            if (customView != null)
                customView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            else
                contentFrame.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
        getWindow().setAttributes(layoutParams);
    }

    private AlbumController nextAlbumController(boolean next) {
        if (BrowserContainer.size() <= 1)
            return currentAlbumController;


        List<AlbumController> list = BrowserContainer.list();
        int index = list.indexOf(currentAlbumController);
        if (next) {
            index++;
            if (index >= list.size()) index = 0;
        } else {
            index--;
            if (index < 0) index = list.size() - 1;

        }
        return list.get(index);
    }

    private void setFullscreen(boolean fullscreen) {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        if (fullscreen) {
            attrs.flags |=  WindowManager.LayoutParams.FLAG_FULLSCREEN;
        } else {
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        getWindow().setAttributes(attrs);
    }

    public static final String EXTRA_SNACKBAR_TITLE = "EXTRA_SNACKBAR_TITLE";
    public static final String EXTRA_SNACKBAR_ACTION_TITLE = "EXTRA_SNACKBAR_ACTION_TITLE";
    public static final String EXTRA_SNACKBAR_ACTION_INTENT_URI = "EXTRA_SNACKBAR_ACTION_INTENT_URI";
    public static final String SNACKBAR_BROADCAST_ACTION_NAME = "snackbar";
    private BroadcastReceiver snackbarReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            final Snackbar snackbar = Snackbar.make(contentFrame, intent.getStringExtra(EXTRA_SNACKBAR_TITLE), Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.WHITE);
            final View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.gray_900));
            TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            final String actionTitle = intent.getStringExtra(EXTRA_SNACKBAR_ACTION_TITLE);
            if (actionTitle != null) {
                snackbar.setAction(actionTitle, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String intentUri = intent.getStringExtra(EXTRA_SNACKBAR_ACTION_INTENT_URI);
                        try {
                            final Intent launchIntent = Intent.parseUri(intentUri, 0);
                            startActivity(launchIntent);
                        } catch (Exception ignored) {
                            ignored.printStackTrace();
                        }
                    }
                });
            }
            snackbar.show();
        }
    };

    private void saveOpenTabs() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        String url = currentAlbumController.getUrl();
        String urls = "";
        for (int i = 0; i < BrowserContainer.size(); i++)
            urls += BrowserContainer.get(i).getUrl() + "||&&SEPARATOR&&||";

        editor.putString("SAVED_URLS", urls);
        editor.putString("OPENED_TAB", url);
        editor.apply();
    }

    private void openSavedTabs() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String urls = sp.getString("SAVED_URLS", "");
        String opened_url = sp.getString("OPENED_TAB", "");
        String[] array;
        int tabPos = 0;
        try {
            array = urls.split("\\|\\|\\&\\&SEPARATOR\\&\\&\\|\\|");
            create = false;
        } catch (NullPointerException e) {
            pinAlbums(null);
            return;
        }
        for (int i = 0; i < array.length; i++) {
            String url = array[i];
            if (!url.equals("null")) { // Do not open blank tabs
                if (url.equals(opened_url)) {
                    tabPos = i;
                }
                addAlbum(url, url, false, null);
            }
        }
        try { // If only blank tabs were open, just open home page
            showAlbum(BrowserContainer.get(tabPos), false, false, false);
        } catch (IndexOutOfBoundsException e) {
            pinAlbums(null);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
    }

}