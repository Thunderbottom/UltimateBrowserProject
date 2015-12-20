package io.github.UltimateBrowserProject.View;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import io.github.UltimateBrowserProject.Activity.BrowserActivity;
import io.github.UltimateBrowserProject.Browser.AlbumController;
import io.github.UltimateBrowserProject.Browser.BrowserContainer;
import io.github.UltimateBrowserProject.Unit.TabStorage;

public class TabSwitcher extends RelativeLayout {

    public static final int DEFAULT_ANIMATION_DURATION = 320;


    public enum SwitcherState {
        EXPANDED,
        COLLAPSED
    }

    private SwitcherState switcherState = SwitcherState.COLLAPSED;

    private int animationDuration = DEFAULT_ANIMATION_DURATION;

    // Not used yet but maybe better later?
    private TabStorage tabStorage;


    private void initTabStorage() {
        tabStorage = new TabStorage();
    }


    public TabSwitcher(Context context) {
        super(context);
        initTabStorage();
    }

    public TabSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTabStorage();
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
        for ( int i = 0; i < BrowserContainer.size(); i++ ) {
            this.addView((View)BrowserContainer.get(i));
        }

        this.animate()
                .setDuration(animationDuration)
                .translationY(
                        this.getHeight() * (BrowserActivity.anchor == 0 ? 1 : -1)
                );
        setSwitcherState(SwitcherState.EXPANDED);
    }

    public void collapse() {
        this.animate()
                .setDuration(animationDuration)
                .translationY(
                        0
                );

        this.removeAllViews();

        setSwitcherState(SwitcherState.COLLAPSED);
    }

    public void setSwitcherState(SwitcherState state) {
        this.switcherState = state;
    }

}
