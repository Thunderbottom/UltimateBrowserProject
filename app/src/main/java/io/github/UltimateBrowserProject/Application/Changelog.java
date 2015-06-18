package io.github.UltimateBrowserProject.Application;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.XmlRes;
import android.support.annotation.StringRes;
import android.text.format.DateFormat;
import android.util.Log;
import android.webkit.WebView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Changelog {
    private static final String RELEASE_TAG = "release";
    private static final String CHANGE_TAG = "change";
    private static final String VERSION_CODE_ATTR = "versioncode";
    private static final String VERSION_ATTR = "version";
    private static final String RELEASE_DATE_ATTR = "releasedate";
    private static final String RELEASE_SUMMARY_ATTR = "summary";
    private static final String CHANGE_TYPE_ATTR = "type";
    private static final String CHANGE_TYPE_BUG_VALUE = "bug";
    private static final String CHANGE_TYPE_NEW_VALUE = "new";
    private static final String CHANGE_TYPE_IMPROVEMENT_VALUE = "improvement";

    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public enum ChangeType {EMPTY, BUG, NEW, IMPROVEMENT}

    private final Context mContext;
    private final int mChangelogResourceId;

    /*  Formats */
    private String mTitle = null;
    private String mReleasePrefix = "Release: ";
    private Integer mReleaseColor = null;
    private Integer mReleaseDateColor = null;
    private Style mStyle = new StyleList();

    /* Button */
    private String mButtonText = null;
    private DialogInterface.OnClickListener mOnClickListener = null;

    public Changelog(@NonNull Context context, @XmlRes int changelogResourceId) {
        this.mContext = context;
        this.mChangelogResourceId = changelogResourceId;
    }

    private String getStyles() {
        StringBuilder sb = new StringBuilder("");
        sb.append("<style type='text/css'>");
        sb.append(".h1 {font-size: 12pt; font-weight: 700;").append(mReleaseColor == null ? "" : "color: #" + String.format("%06X", mReleaseColor).toUpperCase()).append(";}");
        sb.append(".releasedate {font-size: 9pt;").append(mReleaseDateColor == null ? "" : "color: #" + String.format("%06X", mReleaseDateColor).toUpperCase()).append(";}");
        sb.append(".summary {font-size: 9pt; display: block; clear: left;}");

        mStyle.generateStyles(sb);

        sb.append("</style>");
        return sb.toString();
    }

    private String getHtml(Integer minVersion) throws Exception {
        StringBuilder sb = new StringBuilder("<html><head>");
        Resources resources = mContext.getPackageManager().getResourcesForApplication(mContext.getPackageName());
        XmlResourceParser xmlResourceParser = resources.getXml(mChangelogResourceId);
        boolean hasReleases = false;

        sb.append(getStyles());

        sb.append("</head><body>");

        int eventType = xmlResourceParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if ((eventType == XmlPullParser.START_TAG) && (xmlResourceParser.getName().equals(RELEASE_TAG))) {
                int versionCode = Integer.parseInt(xmlResourceParser.getAttributeValue(null, VERSION_CODE_ATTR));
                if ((minVersion == null) || (versionCode >= minVersion)) {
                    addRelease(sb, xmlResourceParser);
                    hasReleases = true;
                }
            }
            eventType = xmlResourceParser.next();
        }
        sb.append("</body></html>");

        return hasReleases ? sb.toString() : null;
    }

    private void addRelease(StringBuilder sb, XmlPullParser xmlResourceParser) throws XmlPullParserException, IOException {
        sb.append("<div><span class='h1'>").append(mReleasePrefix).append(xmlResourceParser.getAttributeValue(null, VERSION_ATTR)).append("</span>");

        if (xmlResourceParser.getAttributeValue(null, RELEASE_DATE_ATTR) != null) {
            String dateString = xmlResourceParser.getAttributeValue(null, RELEASE_DATE_ATTR);
            try {
                Date date = mDateFormat.parse(dateString);
                java.text.DateFormat localDateFormat = DateFormat.getDateFormat(mContext);
                dateString = localDateFormat.format(date);
            } catch (ParseException e) {
            }

            if(dateString != null && dateString.length() > 0) {
                sb.append("<span class='releasedate'>&nbsp;&nbsp;").append(dateString).append("</span>");
            }
        }
        sb.append("</div>");

        if (xmlResourceParser.getAttributeValue(null, RELEASE_SUMMARY_ATTR) != null) {
            sb.append("<span class='summary'>").append(xmlResourceParser.getAttributeValue(null, RELEASE_SUMMARY_ATTR)).append("</span>");
        }

        mStyle.renderStartReleaseChanges(sb);
        int eventType = xmlResourceParser.getEventType();
        while ((eventType != XmlPullParser.END_TAG) || (xmlResourceParser.getName().equals(CHANGE_TAG))) {
            if ((eventType == XmlPullParser.START_TAG) && (xmlResourceParser.getName().equals(CHANGE_TAG))) {
                ChangeType changeType = ChangeType.EMPTY;

                if (xmlResourceParser.getAttributeValue(null, CHANGE_TYPE_ATTR) != null) {
                    String type = xmlResourceParser.getAttributeValue(null, CHANGE_TYPE_ATTR);
                    switch (type) {
                        case CHANGE_TYPE_BUG_VALUE:
                            changeType = ChangeType.BUG;
                            break;
                        case CHANGE_TYPE_NEW_VALUE:
                            changeType = ChangeType.NEW;
                            break;
                        case CHANGE_TYPE_IMPROVEMENT_VALUE:
                            changeType = ChangeType.IMPROVEMENT;
                            break;
                    }
                }

                xmlResourceParser.next();

                /*String change = xmlResourceParser.getText();
                change = change.replace("[b]", "<b>").replace("[/b]", "</b>").replace("[i]", "<i>").replace("[/i]", "</i>").replace("[u]", "<u>").replace("[/u]", "</u>").replace("[s]", "<s>").replace("[/s]", "</s>");
                */

                StringBuilder change = new StringBuilder(xmlResourceParser.getText());
                List<String> tags = Arrays.asList("[b]", "[/b]", "[i]", "[/i]", "[u]", "[/u]", "[s]", "[/s]", "[/color]");
                List<String> htmlTags = Arrays.asList("<b>", "</b>", "<i>", "</i>", "<u>", "</u>", "<s>", "</s>", "</font>");

                for(int i = 0, count = tags.size(); i < count; i++) {
                    String tag = tags.get(i);
                    String htmlTag = htmlTags.get(i);
                    int pos = change.indexOf(tag);
                    while(pos >= 0) {
                        change.replace(pos, pos + tag.length(), htmlTag);
                        pos = change.indexOf(tag);
                    }
                }

                String colorTag = "[color";
                int start = change.indexOf(colorTag);
                while(start >= 0) {
                    int end = change.indexOf("]", start + 1);
                    if(end > start) {
                        change.setCharAt(end, '>');
                        change.replace(start, start+1, "<font ");
                    }
                    start = change.indexOf(colorTag);
                }

                mStyle.renderChange(sb, change.toString(), changeType);
            }
            eventType = xmlResourceParser.next();
        }
        mStyle.renderEndReleaseChanges(sb);
    }

    public boolean show() {
        return show(null);
    }

    public boolean showWhatsNew() {
        try {
            int lastVersion = PreferenceManager.getDefaultSharedPreferences(mContext).getInt(this.getClass().getName(), 0);
            int currentVersion = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;

            if(currentVersion > lastVersion) {
                PreferenceManager.getDefaultSharedPreferences(mContext).edit().putInt(this.getClass().getName(), currentVersion).commit();
                return show(lastVersion + 1);
            }

        } catch(Exception e) {
            Log.e(this.getClass().getName(), e.getMessage() + "", e);
        }

        return false;
    }

    private boolean show(Integer minVersion) {
        CharSequence popupTitle;

        try {
            if(mTitle == null) {
                PackageManager pm = mContext.getPackageManager();
                ApplicationInfo ai = pm.getApplicationInfo(mContext.getPackageName(), 0);
                PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), 0);
                popupTitle = ai.loadLabel(pm) + " v" + pi.versionName;
            } else {
                popupTitle = mTitle;
            }

            WebView webView = new WebView(mContext);

            String html = getHtml(minVersion);
            if(html != null && html.length() > 0) {
                webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                        .setTitle(popupTitle)
                        .setView(webView)
                        .setPositiveButton(android.R.string.ok, null);

                if (mButtonText != null && mOnClickListener != null) {
                    builder.setNeutralButton(mButtonText, mOnClickListener);
                }

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        } catch(Exception e) {
            Log.e(this.getClass().getName(), e.getMessage() + "", e);
        }
        return false;
    }

    public static abstract class Style {
        protected abstract void renderStartReleaseChanges(StringBuilder sb);
        protected abstract void renderEndReleaseChanges(StringBuilder sb);
        protected abstract void renderChange(StringBuilder sb, String change, ChangeType changeType);
        protected abstract void generateStyles(StringBuilder sb);
    }

    public static class StyleList extends Style {
        private boolean mNumbered = false;
        private Integer mBugBackgroundColor = null;
        private Integer mNewBackgroundColor = null;
        private Integer mImprovementBackgroundColor = null;
        private Integer mBugColor = null;
        private Integer mNewColor = null;
        private Integer mImprovementColor = null;

        public StyleList() {
            this(false);
        }

        public StyleList(boolean numbered) {
            this.mNumbered = numbered;
        }

        public StyleList(boolean numbered, Integer bugBackgroundColor, Integer newBackgroundColor, Integer improvementBackgroundColor, Integer bugColor, Integer newColor, Integer improvementColor) {
            this.mNumbered = numbered;
            this.mBugBackgroundColor = bugBackgroundColor;
            this.mNewBackgroundColor = newBackgroundColor;
            this.mImprovementBackgroundColor = improvementBackgroundColor;
            this.mBugColor = bugColor;
            this.mNewColor = newColor;
            this.mImprovementColor = improvementColor;
        }

        public StyleList setBugBackgroundColor(Integer bugBackgroundColor) {
            this.mBugBackgroundColor = bugBackgroundColor;
            return this;
        }

        public StyleList setNewBackgroundColor(Integer newBackgroundColor) {
            this.mNewBackgroundColor = newBackgroundColor;
            return this;
        }

        public StyleList semImprovementBackgroundColor(Integer improvementBackgroundColor) {
            this.mImprovementBackgroundColor = improvementBackgroundColor;
            return this;
        }

        public StyleList setBugColor(Integer bugColor) {
            this.mBugColor = bugColor;
            return this;
        }

        public StyleList setNewColor(Integer newColor) {
            this.mNewColor = newColor;
            return this;
        }

        public StyleList setImprovementColor(Integer improvementColor) {
            this.mImprovementColor = improvementColor;
            return this;
        }

        protected void renderStartReleaseChanges(StringBuilder sb){
            sb.append(mNumbered ? "<ol>" : "<ul>");
        }
        protected void renderEndReleaseChanges(StringBuilder sb){
            sb.append(mNumbered ? "</ol>" : "</ul>");
        }


        protected void renderChange(StringBuilder sb, String change, ChangeType changeType){
            sb.append("<li")
                    .append(changeType == ChangeType.EMPTY ? "" : changeType == ChangeType.BUG ? " class='bug'" : changeType == ChangeType.NEW ? " class='new'" : " class='improvement'")
                    .append("><div>")
                    .append(change).append("</div></li>");
        }
        protected void generateStyles(StringBuilder sb) {
            sb.append("ol, ul {padding-left: 30px;}");
            sb.append("li {margin-left: 0px; font-size: 9pt;}");

            if(mBugBackgroundColor != null || mBugColor != null) {
                sb.append("li.bug div {")
                        .append(mBugBackgroundColor == null ? "" : "background-color: #" + String.format("%06X", mBugBackgroundColor).toUpperCase() + "; ")
                        .append(mBugColor == null ? "" : "color: #" + String.format("%06X", mBugColor).toUpperCase() + "; ")
                        .append("}");
            }
            if(mNewBackgroundColor != null || mNewColor != null) {
                sb.append("li.new div {")
                        .append(mNewBackgroundColor == null ? "" : "background-color: #" + String.format("%06X", mNewBackgroundColor).toUpperCase() + "; ")
                        .append(mNewColor == null ? "" : "color: #" + String.format("%06X", mNewColor).toUpperCase() + "; ")
                        .append("}");
            }
            if(mImprovementBackgroundColor != null || mImprovementColor != null) {
                sb.append("li.improvement div {")
                        .append(mImprovementBackgroundColor == null ? "" : "background-color: #" + String.format("%06X", mImprovementBackgroundColor).toUpperCase() + "; ")
                        .append(mImprovementColor == null ? "" : "color: #" + String.format("%06X", mImprovementColor).toUpperCase() + "; ")
                        .append("}");
            }
        }
    }

    public static class StyleCharacters extends Style {
        private final String mCharBug;
        private final String mCharNew;
        private final String mCharImprovement;

        public StyleCharacters(String charBug, String charNew, String charImprovement) {
            this.mCharBug = charBug;
            this.mCharNew = charNew;
            this.mCharImprovement = charImprovement;
        }

        protected void renderStartReleaseChanges(StringBuilder sb) {
            sb.append("<div class='table'>");
        }
        protected void renderEndReleaseChanges(StringBuilder sb) {
            sb.append("</div>");
        }
        protected void renderChange(StringBuilder sb, String change, ChangeType changeType) {
            sb.append("<div class='tr'>")
                    .append("<div class='td empty'>")
                    .append(changeType == ChangeType.EMPTY ? "&nbsp;" : changeType == ChangeType.BUG ? mCharBug : changeType == ChangeType.NEW ? mCharNew : mCharImprovement)
                    .append("</div><div class='td'>")
                    .append(change)
                    .append("</div><div style='clear:both;'></div></div>");
        }
        protected void generateStyles(StringBuilder sb) {
            sb.append(".table {width: 100%; padding-top: 10pt; padding-bottom: 10pt;}");
            sb.append(".table .td {float: left; font-size: 9pt; width: 95%;}");
            sb.append(".table .empty {width: 5%; font-size: 9pt;}");
        }
    }

    public static class Builder {
        private final Context mContext;
        private final int mChangelogResourceId;

        private String mTitle = null;
        private String mReleasePrefix = null;
        private Style mStyle = null;

        private Integer mReleaseColor = null;
        private Integer mReleaseDateColor = null;

        private String mButtonText = null;
        private DialogInterface.OnClickListener mOnClickListener = null;

        public Builder(@NonNull Context context, @XmlRes int changelogResourceId) {
            this.mContext = context;
            this.mChangelogResourceId = changelogResourceId;
        }

        public Builder setTitle(String title) {
            this.mTitle = title;
            return this;
        }

        public Builder setTitle(@StringRes int titleResourceId) {
            setTitle(mContext.getString(titleResourceId));
            return this;
        }

        public Builder setReleaseColor(Integer releaseColor) {
            this.mReleaseColor = releaseColor;
            return this;
        }

        public Builder setReleaseDateColor(Integer releaseDateColor) {
            this.mReleaseDateColor = releaseDateColor;
            return this;
        }

        public Builder setReleasePrefix(String releasePrefix) {
            this.mReleasePrefix = releasePrefix;
            return this;
        }

        public Builder setReleasePrefix(@StringRes int releasePrefixResourceId) {
            setReleasePrefix(mContext.getString(releasePrefixResourceId));
            return this;
        }

        public Builder setStyle(Style style) {
            this.mStyle = style;
            return this;
        }

        public void setButton(@StringRes int buttonTextResId, @NonNull DialogInterface.OnClickListener onClickListener) {
            setButton(mContext.getString(buttonTextResId), onClickListener);
        }

        public void setButton(@NonNull String buttonText, @NonNull DialogInterface.OnClickListener onClickListener) {
            this.mButtonText = buttonText;
            this.mOnClickListener = onClickListener;
        }

        public Changelog create() {
            Changelog changelog = new Changelog(mContext, mChangelogResourceId);

            if(mTitle != null) {
                changelog.mTitle = mTitle;
            }

            if(mReleaseColor != null) {
                changelog.mReleaseColor = mReleaseColor;
            }
            if(mReleasePrefix != null) {
                changelog.mReleasePrefix = mReleasePrefix;
            }

            if(mReleaseDateColor != null) {
                changelog.mReleaseDateColor = mReleaseDateColor;
            }

            if(mStyle != null) {
                changelog.mStyle = mStyle;
            }
            if(mButtonText != null && mOnClickListener != null) {
                changelog.mButtonText = mButtonText;
                changelog.mOnClickListener = mOnClickListener;
            }

            return changelog;
        }
    }

}