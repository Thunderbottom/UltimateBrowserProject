package io.github.UltimateBrowserProject.View;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Browser;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import org.xdevs23.debugUtils.Logging;

import io.github.UltimateBrowserProject.Activity.BrowserActivity;
import io.github.UltimateBrowserProject.Browser.AlbumController;
import io.github.UltimateBrowserProject.Browser.BrowserContainer;
import io.github.UltimateBrowserProject.R;
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

        int omniBgColor = BrowserActivity.omnibox.getSolidColor();
        float[] omniBgHSV = new float[3];
        Color.colorToHSV(omniBgColor, omniBgHSV);
        omniBgHSV[2] *= 0.48;
        int newSwitcherColor = Color.HSVToColor(omniBgHSV);

        this.setBackgroundColor(newSwitcherColor);


        View albumsView = BrowserActivity.currentAlbumController.getAlbumView();

        this.addView(albumsView);

        this.addView(BrowserActivity.switcherHeader);

        this.animate()
                .setDuration(animationDuration)
                .scaleY(
                        1
                )
                .translationY(0)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

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

        if(!isExpanded()) return;

        final TabSwitcher thisSwitcher = this;

        this.animate()
                .setDuration(animationDuration)
                .scaleY(
                        0
                )
                .translationY(this.getHeight() / 2)
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
