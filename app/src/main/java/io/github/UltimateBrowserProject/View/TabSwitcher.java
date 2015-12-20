package io.github.UltimateBrowserProject.View;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.provider.Browser;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import io.github.UltimateBrowserProject.Activity.BrowserActivity;
import io.github.UltimateBrowserProject.Browser.AlbumController;
import io.github.UltimateBrowserProject.Browser.BrowserContainer;
import io.github.UltimateBrowserProject.R;
import io.github.UltimateBrowserProject.Unit.TabStorage;
import io.github.UltimateBrowserProject.Unit.ViewUnit;

public class TabSwitcher extends RelativeLayout {

    public static final int
            DEFAULT_ANIMATION_DURATION  = 320,
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
        View albumsView = BrowserActivity.currentAlbumController.getAlbumView();

        this.addView(albumsView);

        this.addView(BrowserActivity.switcherHeader);

        this.animate()
                .setDuration(animationDuration)
                .translationY(
                        this.getHeight() * (BrowserActivity.anchor == 0 ? 1 : -1)
                );
        BrowserActivity.getContentFrame()
                .animate()
                .setDuration(DEFAULT_ANIMATION_DURATION)
                .translationY(ViewUnit.dp2px(BrowserActivity.getContext(),
                        DEFAULT_LAYOUT_HEIGHT)
                        * (BrowserActivity.anchor == 0 ? 1 : -1));
        setSwitcherState(SwitcherState.EXPANDED);
    }

    public void collapse() {
        this.animate()
                .setDuration(animationDuration)
                .translationY(
                        0
                );
        BrowserActivity.getContentFrame().animate().setDuration(TabSwitcher.DEFAULT_ANIMATION_DURATION).translationY(0);
        this.removeAllViews();

        setSwitcherState(SwitcherState.COLLAPSED);
    }

    public TabStorage getTabStorage() {
        return this.tabStorage;
    }

    public void setSwitcherState(SwitcherState state) {
        this.switcherState = state;
    }

    public SwitcherState getSwitcherState() { return switcherState; }

}
