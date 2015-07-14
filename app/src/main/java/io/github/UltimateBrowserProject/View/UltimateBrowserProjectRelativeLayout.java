package io.github.UltimateBrowserProject.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import io.github.UltimateBrowserProject.Browser.AlbumController;
import io.github.UltimateBrowserProject.Browser.BrowserController;
import io.github.UltimateBrowserProject.R;

public class UltimateBrowserProjectRelativeLayout extends RelativeLayout implements AlbumController {
    private Context context;
    private Album album;
    private int flag = 0;

    private BrowserController controller;
    public void setBrowserController(BrowserController controller) {
        this.controller = controller;
        this.album.setBrowserController(controller);
    }

    public UltimateBrowserProjectRelativeLayout(Context context) {
        this(context, null);
    }

    public UltimateBrowserProjectRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UltimateBrowserProjectRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.album = new Album(context, this, this.controller);
        initUI();
    }

    private void initUI() {
        album.setAlbumCover(null);
        album.setAlbumTitle(context.getString(R.string.album_untitled));
        album.setBrowserController(controller);
    }

    @Override
    public int getFlag() {
        return flag;
    }

    @Override
    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public View getAlbumView() {
        return album.getAlbumView();
    }

    @Override
    public void setAlbumCover(Bitmap bitmap) {
        album.setAlbumCover(bitmap);
    }

    @Override
    public String getAlbumTitle() {
        return album.getAlbumTitle();
    }

    @Override
    public void setAlbumTitle(String title) {
        album.setAlbumTitle(title);
    }

    @Override
    public String getUrl() { return null; }
    @Override
    public void setUrl(String url) {}
    @Override
    public void activate() {
        album.activate();
    }

    @Override
    public void deactivate() {
        album.deactivate();
    }
}
