package io.github.UltimateBrowserProject.Unit;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebIconDatabase;
import android.webkit.WebViewDatabase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import io.github.UltimateBrowserProject.Browser.AdBlock;
import io.github.UltimateBrowserProject.Database.Record;
import io.github.UltimateBrowserProject.Database.RecordAction;
import io.github.UltimateBrowserProject.R;
import io.github.UltimateBrowserProject.View.UltimateBrowserProjectToast;

public class BrowserUnit {
    public static final int PROGRESS_MAX = 100;
    public static final int PROGRESS_MIN = 0;
    public static final String SUFFIX_HTML = ".html";
    public static final String SUFFIX_PNG = ".png";
    public static final String SUFFIX_TXT = ".txt";

    public static final int FLAG_BOOKMARKS = 0x100;
    public static final int FLAG_HISTORY = 0x101;
    public static final int FLAG_HOME = 0x102;
    public static final int FLAG_UltimateBrowserProject = 0x103;

    public static final String MIME_TYPE_TEXT_HTML = "text/html";
    public static final String MIME_TYPE_TEXT_PLAIN = "text/plain";
    public static final String MIME_TYPE_IMAGE = "image/*";

    public static final String BASE_URL = "file:///android_asset/";
    public static final String BOOKMARK_TYPE = "<DT><A HREF=\"{url}\" ADD_DATE=\"{time}\">{title}</A>";
    public static final String BOOKMARK_TITLE = "{title}";
    public static final String BOOKMARK_URL = "{url}";
    public static final String BOOKMARK_TIME = "{time}";
    public static final String INTRODUCTION_EN = "UltimateBrowserProject_introduction_en.html";
    public static final String INTRODUCTION_DE = "UltimateBrowserProject_introduction_de.html";

    public static final String SEARCH_ENGINE_GOOGLE = "https://www.google.com/search?q=";
    public static final String SEARCH_ENGINE_DUCKDUCKGO = "https://duckduckgo.com/?q=";
    public static final String SEARCH_ENGINE_STARTPAGE = "https://startpage.com/do/search?query=";
    public static final String SEARCH_ENGINE_BING = "http://www.bing.com/search?q=";
    public static final String SEARCH_ENGINE_BAIDU = "http://www.baidu.com/s?wd=";

    // Chrome desktop 41.0.2228.0
    public static final String UA_DESKTOP = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
    public static final String URL_ENCODING = "UTF-8";
    public static final String URL_ABOUT_BLANK = "about:blank";
    public static final String URL_SCHEME_ABOUT = "about:";
    public static final String URL_SCHEME_MAIL_TO = "mailto:";
    public static final String URL_SCHEME_FILE = "file://";
    public static final String URL_SCHEME_FTP = "ftp://";
    public static final String URL_SCHEME_HTTP = "http://";
    public static final String URL_SCHEME_HTTPS = "https://";
    public static final String URL_SCHEME_INTENT = "intent://";

    public static final String URL_PREFIX_GOOGLE_PLAY = "www.google.com/url?q=";
    public static final String URL_SUFFIX_GOOGLE_PLAY = "&sa";
    public static final String URL_PREFIX_GOOGLE_PLUS = "plus.url.google.com/url?q=";
    public static final String URL_SUFFIX_GOOGLE_PLUS = "&rct";


    public static boolean isURL(String url) {
        if (url == null) {
            return false;
        }

        url = url.toLowerCase(Locale.getDefault());
        if (url.startsWith(URL_ABOUT_BLANK)
                || url.startsWith(URL_SCHEME_MAIL_TO)
                || url.startsWith(URL_SCHEME_FILE)) {
            return true;
        }

        String regex = "^((ftp|http|https|intent)?://)"                      // support scheme
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" // ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}"                            // IP形式的URL -> 199.194.52.184
                + "|"                                                        // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*"                                  // 域名 -> www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\."                    // 二级域名
                + "[a-z]{2,6})"                                              // first level domain -> .com or .museum
                + "(:[0-9]{1,4})?"                                           // 端口 -> :80
                + "((/?)|"                                                   // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(url).matches();
    }

    public static String queryWrapper(Context context, String query) {
        // Use prefix and suffix to process some special links
        String temp = query.toLowerCase(Locale.getDefault());
        if (temp.contains(URL_PREFIX_GOOGLE_PLAY) && temp.contains(URL_SUFFIX_GOOGLE_PLAY)) {
            int start = temp.indexOf(URL_PREFIX_GOOGLE_PLAY) + URL_PREFIX_GOOGLE_PLAY.length();
            int end = temp.indexOf(URL_SUFFIX_GOOGLE_PLAY);
            query = query.substring(start, end);
        } else if (temp.contains(URL_PREFIX_GOOGLE_PLUS) && temp.contains(URL_SUFFIX_GOOGLE_PLUS)) {
            int start = temp.indexOf(URL_PREFIX_GOOGLE_PLUS) + URL_PREFIX_GOOGLE_PLUS.length();
            int end = temp.indexOf(URL_SUFFIX_GOOGLE_PLUS);
            query = query.substring(start, end);
        }

        if (isURL(query)) {
            if (query.startsWith(URL_SCHEME_ABOUT) || query.startsWith(URL_SCHEME_MAIL_TO)) {
                return query;
            }

            if (!query.contains("://")) {
                query = URL_SCHEME_HTTP + query;
            }

            return query;
        }

        try {
            query = URLEncoder.encode(query, URL_ENCODING);
        } catch (UnsupportedEncodingException u) {}

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String custom = sp.getString(context.getString(R.string.sp_search_engine_custom), SEARCH_ENGINE_GOOGLE);
        final int i = Integer.valueOf(sp.getString(context.getString(R.string.sp_search_engine), "0"));
        switch (i) {
            case 0:
                return SEARCH_ENGINE_GOOGLE + query;
            case 1:
                return SEARCH_ENGINE_DUCKDUCKGO + query;
            case 2:
                return SEARCH_ENGINE_STARTPAGE + query;
            case 3:
                return SEARCH_ENGINE_BING + query;
            case 4:
                return SEARCH_ENGINE_BAIDU + query;
            case 5:
                return custom + query;
            default:
                return SEARCH_ENGINE_GOOGLE + query;
        }
    }

    public static String urlWrapper(String url) {
        if (url == null) {
            return null;
        }

        String green500 = "<font color='#4CAF50'>{content}</font>";
        String gray500 = "<font color='#9E9E9E'>{content}</font>";

        if (url.startsWith(BrowserUnit.URL_SCHEME_HTTPS)) {
            String scheme = green500.replace("{content}", BrowserUnit.URL_SCHEME_HTTPS);
            url = scheme + url.substring(8);
        } else if (url.startsWith(BrowserUnit.URL_SCHEME_HTTP)) {
            String scheme = gray500.replace("{content}", BrowserUnit.URL_SCHEME_HTTP);
            url = scheme + url.substring(7);
        }

        return url;
    }

    public static boolean bitmap2File(Context context, Bitmap bitmap, String filename) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static Bitmap file2Bitmap(Context context, String filename) {
        try {
            FileInputStream fileInputStream = context.openFileInput(filename);
            return BitmapFactory.decodeStream(fileInputStream);
        } catch (Exception e) {
            return null;
        }
    }

    public static void copyURL(Context context, String url) {
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText(null, url.trim());
        manager.setPrimaryClip(data);
        UltimateBrowserProjectToast.show(context, R.string.toast_copy_successful);
    }

    public static void download(Context context, String url, String contentDisposition, String mimeType) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String filename = URLUtil.guessFileName(url, contentDisposition, mimeType); // Maybe unexpected filename.

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(filename);
        request.setMimeType(mimeType);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        UltimateBrowserProjectToast.show(context, R.string.toast_start_download);
    }

    public static void downloadCache(Context context, String url, String contentDisposition, String mimeType) {
        DownloadManager.Request requestCache = new DownloadManager.Request(Uri.parse(url));
        String filename = URLUtil.guessFileName(url, contentDisposition, mimeType); // Maybe unexpected filename.

                requestCache.allowScanningByMediaScanner();
        requestCache.setTitle(filename);
        requestCache.setMimeType(mimeType);
        requestCache.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "cache");

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(requestCache);
    }

    public static String screenshot(Context context, Bitmap bitmap, String name) {
        if (bitmap == null) {
            return null;
        }

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (name == null || name.trim().isEmpty()) {
            name = String.valueOf(System.currentTimeMillis());
        }
        name = name.trim();

        int count = 0;
        File file = new File(dir, name + SUFFIX_PNG);
        while (file.exists()) {
            count++;
            file = new File(dir, name + "." + count + SUFFIX_PNG);
        }

        try {
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            return file.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    public static String exportBookmarks(Context context) {
        RecordAction action = new RecordAction(context);
        action.open(false);
        List<Record> list = action.listBookmarks();
        action.close();

        String filename = context.getString(R.string.bookmarks_filename);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename + SUFFIX_HTML);
        int count = 0;
        while (file.exists()) {
            count++;
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename + "." + count + SUFFIX_HTML);
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            for (Record record : list) {
                String type = BOOKMARK_TYPE;
                type = type.replace(BOOKMARK_TITLE, record.getTitle());
                type = type.replace(BOOKMARK_URL, record.getURL());
                type = type.replace(BOOKMARK_TIME, String.valueOf(record.getTime()));
                writer.write(type);
                writer.newLine();
            }
            writer.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    public static String exportWhitelist(Context context) {
        RecordAction action = new RecordAction(context);
        action.open(false);
        List<String> list = action.listDomains();
        action.close();

        String filename = context.getString(R.string.whitelist_filename);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename + SUFFIX_TXT);
        int count = 0;
        while (file.exists()) {
            count++;
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename + "." + count + SUFFIX_TXT);
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            for (String domain : list) {
                writer.write(domain);
                writer.newLine();
            }
            writer.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    public static int importBookmarks(Context context, File file) {
        if (file == null) {
            return -1;
        }
        List<Record> list = new ArrayList<>();

        try {
            RecordAction action = new RecordAction(context);
            action.open(true);

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!((line.startsWith("<dt><a ") && line.endsWith("</a>")) || (line.startsWith("<DT><A ") && line.endsWith("</A>")))) {
                    continue;
                }
                String title = getBookmarkTitle(line);
                String url = getBookmarkURL(line);
                if (title.trim().isEmpty() || url.trim().isEmpty()) {
                    continue;
                }

                Record record = new Record();
                record.setTitle(title);
                record.setURL(url);
                record.setTime(System.currentTimeMillis());
                if (!action.checkBookmark(record)) {
                    list.add(record);
                }
            }
            reader.close();

            Collections.sort(list, new Comparator<Record>() {
                @Override
                public int compare(Record first, Record second) {
                    return first.getTitle().compareTo(second.getTitle());
                }
            });

            for (Record record : list) {
                action.addBookmark(record);
            }
            action.close();
        } catch (Exception e) {}

        return list.size();
    }

    public static int importWhitelist(Context context, File file) {
        if (file == null) {
            return -1;
        }

        AdBlock adBlock = new AdBlock(context);
        int count = 0;

        try {
            RecordAction action = new RecordAction(context);
            action.open(true);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine().trim()) != null) {
                if (!action.checkDomain(line)) {
                    adBlock.addDomain(line);
                    count++;
                }
            }
            reader.close();
            action.close();
        } catch (Exception e) {}

        return count;
    }

    public static void clearBookmarks(Context context) {
        RecordAction action = new RecordAction(context);
        action.open(true);
        action.clearBookmarks();
        action.close();
    }

    public static boolean clearCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }

            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    // CookieManager.removeAllCookies() must be called on a thread with a running Looper.
    public static void clearCookie(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.flush();
            cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                @Override
                public void onReceiveValue(Boolean value) {}
            });
        } else {
            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
        }
    }

    public static void clearFormData(Context context) {
        WebViewDatabase.getInstance(context).clearFormData();
    }

    public static void clearHistory(Context context) {
        RecordAction action = new RecordAction(context);
        action.open(true);
        action.clearHistory();
        action.close();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            WebIconDatabase.getInstance().removeAllIcons();
        }
    }

    public static void clearPasswords(Context context) {
        WebViewDatabase.getInstance(context).clearHttpAuthUsernamePassword();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            WebViewDatabase.getInstance(context).clearUsernamePassword();
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }

        return dir != null && dir.delete();
    }
    private static String getBookmarkTitle(String line) {
        line = line.substring(0, line.length() - 4); // Remove last </a>
        int index = line.lastIndexOf(">");
        return line.substring(index + 1, line.length());
    }
    private static String getBookmarkURL(String line) {
        for (String string : line.split(" +")) {
            if (string.startsWith("href=\"") || string.startsWith("HREF=\"")) {
                return string.substring(6, string.length() - 1); // Remove href=\" and \"
            }
        }
        return "";
    }
}
