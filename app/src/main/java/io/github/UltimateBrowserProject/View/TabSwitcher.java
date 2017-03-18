package io.github.UltimateBrowserProject.View;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.xdevs23.debugUtils.Logging;

import io.github.UltimateBrowserProject.Activity.BrowserActivity;
import io.github.UltimateBrowserProject.Unit.TabStorage;
import io.github.UltimateBrowserProject.Unit.ViewUnit;

public class TabSwitcher extends RelativeLayout {

    public static final int
            SHORTER_ANIMATION_DURATION  = 240,
            DEFAULT_ANIMATION_DURATION  = 320,
            LONGER_ANIMATION_DURATION   = 480,

            DEFAULT_LAYOUT_HEIGHT       = 200
            ;


    public enum SwitcherState {
        EXPANDED,
        COLLAPSED
    }

    private SwitcherState switcherState = SwitcherState.COLLAPSED;

    private int animationDuration = DEFAULT_ANIMATION_DURATION;

    private TabStorage tabStorage;



    private void initTabStorage() {
        tabStorage = new TabStorage();
    }

    private void initLayout() {
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
        );

        p.height = (int) ViewUnit.dp2px(BrowserActivity.getContext(), DEFAULT_LAYOUT_HEIGHT);

        if(BrowserActivity.anchor == 0) p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        else                            p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        this.setLayoutParams(p);
        this.setMinimumHeight(p.height);
    }

    private void init() {
        initTabStorage();
        initLayout();
    }

    public TabSwitcher(Context context) {
        super(context);
        init();
    }

    public TabSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TabSwitcher(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initTabStorage();
    }

    public void setAnimationDuration(int animationDuration) {
        this.animationDuration = animationDuration;
    }

    public void expand() {
        Logging.logd("Expanding TabSwitcher");

        if(!isCollapsed()) return;
        if(BrowserActivity.isKeyboardShowing) return;

        this.removeAllViews();

        try { ((ViewGroup)BrowserActivity.getSwitcherContainer().getParent())
                .removeView(BrowserActivity.getSwitcherContainer()); } catch(Exception ex) {}

        ColorDrawable omniBgDrawable = (ColorDrawable) BrowserActivity.omnibox.getBackground();
        int omniBgColor = omniBgDrawable.getColor();
        float[] omniBgHSV = new float[3];
        Color.colorToHSV(omniBgColor, omniBgHSV);
        omniBgHSV[2] *= 0.48;
        int newSwitcherColor = Color.HSVToColor(omniBgHSV);

        this.setBackgroundColor(newSwitcherColor);


        LinearLayout albumsViewLayout = BrowserActivity.getSwitcherContainer();

        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        p.setMargins(
                (int) ViewUnit.dp2px(BrowserActivity.getContext(),  8),
                (int) ViewUnit.dp2px(BrowserActivity.getContext(), 48),
                (int) ViewUnit.dp2px(BrowserActivity.getContext(),  8),
                (int) ViewUnit.dp2px(BrowserActivity.getContext(),  2)
        );

        albumsViewLayout.setLayoutParams(p);

        addView(BrowserActivity.getSwitcherContainer());

        addView(BrowserActivity.switcherHeader);

        bringToFront();

        animate()
                .setDuration(animationDuration)
                .alpha(1f)
                .scaleY(
                        1
                )
                .translationY(0)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        bringToFront();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setSwitcherState(SwitcherState.EXPANDED);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
        BrowserActivity.getContentFrame()
                .animate()
                .setDuration(DEFAULT_ANIMATION_DURATION)
                .translationY(ViewUnit.dp2px(BrowserActivity.getContext(),
                        DEFAULT_LAYOUT_HEIGHT)
                        * (BrowserActivity.anchor == 0 ? 1 : -1));

    }

    public void collapse() {
        Logging.logd("Collapsing TabSwitcher");

        if( (!isExpanded()) ) return;

        final TabSwitcher thisSwitcher = this;

        this.animate()
                .setDuration(animationDuration)
                .scaleY(
                        0
                )
                .translationY(this.getHeight() / (BrowserActivity.anchor == 0 ? -2 : 2))
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thisSwitcher.removeAllViews();
                        setSwitcherState(SwitcherState.COLLAPSED);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

        BrowserActivity.getContentFrame().animate().setDuration(TabSwitcher.DEFAULT_ANIMATION_DURATION).translationY(0);

    }

    public boolean isCollapsed() {
        return (getSwitcherState() == SwitcherState.COLLAPSED);
    }

    public boolean isExpanded() {
        return (getSwitcherState() == SwitcherState.EXPANDED);
    }

    public TabStorage getTabStorage() {
        return this.tabStorage;
    }

    public void setSwitcherState(SwitcherState state) {
        this.switcherState = state;
    }

    public SwitcherState getSwitcherState() { return switcherState; }

}
