package io.github.UltimateBrowserProject.Unit;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import org.xdevs23.debugUtils.Logging;

public class ViewUnit {

    public static boolean respectAdjustHeight = true,
                          disableAdjusting    = true;


    public static void bound(Context context, View view, boolean isFullScreen) { // Don't need this?!
        if(!disableAdjusting) {
            int windowWidth = getWindowWidth(context);
            int windowHeight = getWindowHeight(context);
            int adjustHeight = 0;
            if (!isFullScreen && respectAdjustHeight)
                adjustHeight += getStatusBarHeight(context);


            int widthSpec = View.MeasureSpec.makeMeasureSpec(windowWidth, View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(windowHeight - adjustHeight, View.MeasureSpec.EXACTLY);
            int heightC = windowHeight - adjustHeight;

            Logging.logt(String.format(
                    "Window height: %s; Window width: %s; "
                            + "Adjust height: %s; status bar height: %s;"
                            + "widthSpec: %s; heightSpec: %s",
                    windowHeight, windowWidth,
                    adjustHeight, getStatusBarHeight(context),
                    widthSpec, heightSpec
            ));

            view.measure(widthSpec, heightSpec);
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }
    }

    public static Bitmap capture(View view, float width, float height, boolean scroll, Bitmap.Config config) {
        if (!view .isDrawingCacheEnabled())
             view.setDrawingCacheEnabled(true);


        Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, config);
        bitmap.eraseColor(Color.WHITE);

        Canvas canvas = new Canvas(bitmap);
        int left = view.getLeft();
        int top = view.getTop();
        if (scroll) {
            left = view.getScrollX();
            top = view.getScrollY();
        }

        int status = canvas.save();
        canvas.translate(-left, -top);

        float scale = width / view.getWidth();
        canvas.scale(scale, scale, left, top);

        view.draw(canvas);
        canvas.restoreToCount(status);

        Paint alphaPaint = new Paint();
        alphaPaint.setColor(Color.TRANSPARENT);

        canvas.drawRect(0f, 0f, 1f, height, alphaPaint);
        canvas.drawRect(width - 1f, 0f, width, height, alphaPaint);
        canvas.drawRect(0f, 0f, width, 1f, alphaPaint);
        canvas.drawRect(0f, height - 1f, width, height, alphaPaint);
        canvas.setBitmap(null);

        return bitmap;
    }

    public static float dp2px(Context context, float dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static Drawable getDrawable(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return context.getResources().getDrawable(id, null);
        else
            return context.getResources().getDrawable(id);

    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");

        return ( (resourceId > 0)  ? resources.getDimensionPixelSize(resourceId)
                                   : 0);

    }

    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");

        return ( (resourceId > 0)  ? resources.getDimensionPixelSize(resourceId)
                : 0);

    }

    public static int getWindowHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getWindowWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getAdjustedWindowHeight(Context context) {
        return getWindowHeight(context) - getStatusBarHeight(context);
    }

    public static int getAdjustedWindowHeight(Context context, boolean statusbar, boolean navbar) {
        return getWindowHeight(context)
                - (statusbar ? getStatusBarHeight    (context) : 0)
                - (navbar    ? getNavigationBarHeight(context) : 0);
    }

    public static void setElevation(View view, float elevation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setElevation(elevation);
        }
    }

    public static int densityPixelsToPixels(Context context, int dp) {
        return ((int)getDensity(context) * dp);
    }

    public static int getOmniboxHeight(Context context) {
        return (int)dp2px(context, 48);
    }

    public static int goh(Context c) {
        return getOmniboxHeight(c);
    }

}