package io.github.UltimateBrowserProject.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

import org.xdevs23.debugUtils.Logging;
import org.xdevs23.debugUtils.StackTraceParser;

import io.github.UltimateBrowserProject.Activity.BrowserActivity;
import io.github.UltimateBrowserProject.R;
import io.github.UltimateBrowserProject.Unit.ViewUnit;

public class SwitcherPanel extends ViewGroup {
    private View switcherView;

    private RelativeLayout omnibox, mainView, rootView;

    private float dimen108dp = 0f, dimen48dp = 0f;

    private boolean keyBoardShowing = false;
    public boolean isKeyBoardShowing() {
        return keyBoardShowing;
    }
    public void fixKeyBoardShowing(int height) {
        keyBoardShowing = getMeasuredHeight() < height;
    }

    /* switcherView's position */
    public enum Anchor {
        TOP,
        BOTTOM
    }
    private static final Anchor ANCHOR_DEFAULT = Anchor.TOP;
    private Anchor anchor = ANCHOR_DEFAULT;

    /* switcherPanel's status */
    public enum Status {
        EXPANDED,
        COLLAPSED,
        FLING
    }
    private static final Status STATUS_DEFAULT = Status.COLLAPSED;
    private Status status = STATUS_DEFAULT;
    public Status getStatus() {
        return status;
    }

    public interface StatusListener {
        void onExpanded();
        void onCollapsed();
        void onFling();
    }

    private StatusListener statusListener;
    public void setStatusListener(StatusListener statusListener) {
        this.statusListener = statusListener;
    }

    public void setRootView(View view) {
        rootView = (RelativeLayout) view;
    }

    public void setRootView(RelativeLayout view) {
        rootView = view;
    }

    public SwitcherPanel(Context context) {
        this(context, null);
    }

    public SwitcherPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitcherPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        anchor = Anchor.values()[BrowserActivity.anchor];

        setWillNotDraw(false);

        dimen108dp = getResources().getDimensionPixelSize(R.dimen.layout_height_108dp);
        dimen48dp  = getResources().getDimensionPixelOffset(R.dimen.layout_height_48dp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.setMinimumHeight((int) dimen108dp);

        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) this.getLayoutParams();

        if(BrowserActivity.anchor == 1) {
            p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else {
            p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        }

        p.height = (int) dimen108dp;

        this.setLayoutParams(p);

        if(rootView == null) return;

        switcherView = (RelativeLayout) rootView.findViewById(R.id.switcher_view);
        mainView     = (RelativeLayout) rootView.findViewById(R.id.main_view);
        omnibox      = (RelativeLayout) rootView.findViewById(R.id.main_omnibox);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(widthSize, heightSize);

        keyBoardShowing = heightSize < getHeight();

    }

    @Override
    protected void onLayout(boolean change, int l, int t, int r, int b) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();


        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();

            int top = paddingTop;

            int height = child.getMeasuredHeight();
            int bottom = top + height;
            int left = paddingLeft;
            int right = left + child.getMeasuredWidth();

            child.layout(left, top, right, bottom);
        }

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    public void expanded() {
        if(rootView == null) return;
        Logging.logd("Expanding switcher panel");
        switcherView = rootView.findViewById(R.id.switcher_view);
        mainView = (RelativeLayout) rootView.findViewById(R.id.main_view);
        omnibox  = (RelativeLayout) rootView.findViewById(R.id.main_omnibox);
        expand();
    }

    public void collapsed() {
        Logging.logd("Collapsing switcher panel");
        switcherView.setEnabled(true);
        collapse();
    }

    public void expand() {
        switcherView.animate()
                .setDuration(TabSwitcher.DEFAULT_ANIMATION_DURATION)
                .translationY(dimen108dp
                        * (BrowserActivity.anchor == 0 ? 1 : -1));
        BrowserActivity.getContentFrame()
                .animate()
                .setDuration(TabSwitcher.DEFAULT_ANIMATION_DURATION)
                .translationY(dimen108dp
                        * (BrowserActivity.anchor == 0 ? 1 : -1));
        status = Status.EXPANDED;
    }
    public void collapse() {
        switcherView.animate().setDuration(TabSwitcher.DEFAULT_ANIMATION_DURATION).translationY(0);
        BrowserActivity.getContentFrame().animate().setDuration(TabSwitcher.DEFAULT_ANIMATION_DURATION).translationY(0);
        status = Status.COLLAPSED;
    }

}
