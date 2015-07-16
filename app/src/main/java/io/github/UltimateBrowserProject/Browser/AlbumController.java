package io.github.UltimateBrowserProject.Browser;

import android.graphics.Bitmap;
import android.view.View;

public interface AlbumController {
    int getFlag();

    void setFlag(int flag);

    View getAlbumView();

    void setAlbumCover(Bitmap bitmap);

    String getAlbumTitle();

    void setAlbumTitle(String title);

    String getUrl();

    void setUrl(String url);

    void activate();

    void deactivate();
}