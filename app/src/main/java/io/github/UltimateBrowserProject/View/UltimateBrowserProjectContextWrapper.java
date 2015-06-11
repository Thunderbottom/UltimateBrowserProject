package io.github.UltimateBrowserProject.View;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import io.github.UltimateBrowserProject.R;

public class UltimateBrowserProjectContextWrapper extends ContextWrapper {
    private Context context;

    public UltimateBrowserProjectContextWrapper(Context context) {
        super(context);
        this.context = context;
        this.context.setTheme(R.style.BrowserActivityTheme);
    }

    @Override
    public Resources.Theme getTheme() {
        return context.getTheme();
    }
}
