package io.github.UltimateBrowserProject.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.UltimateBrowserProject.Browser.AlbumController;
import io.github.UltimateBrowserProject.Browser.BrowserController;
import io.github.UltimateBrowserProject.R;

public class Album {

    private Context context;

    private View albumView;
    public View getAlbumView() {
        return albumView;
    }

    private ImageView albumCover;
    public void setAlbumCover(Bitmap bitmap) {
        albumCover.setImageBitmap(bitmap);
    }

    private TextView albumTitle;
    public String getAlbumTitle() {
        return albumTitle.getText().toString();
    }
    public void setAlbumTitle(String title) {
        albumTitle.setText(title);
    }

    private AlbumController albumController;
    public void setAlbumController(AlbumController albumController) {
        this.albumController = albumController;
    }

    private String url;
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    private BrowserController browserController;
    public void setBrowserController(BrowserController browserController) {
        this.browserController = browserController;
    }

    public Album(Context context, AlbumController albumController, BrowserController browserController) {
        this.context = context;
        this.albumController = albumController;
        this.browserController = browserController;
        initUI();
    }

    private void initUI() {
        albumView = LayoutInflater.from(context).inflate(R.layout.album, null, false);

        albumView.setOnTouchListener(new SwipeToDismissListener(
                albumView,
                null,
                new SwipeToDismissListener.DismissCallback() {
                    @Override
                    public boolean canDismiss(Object token) {
                        return true;
                    }

                    @Override
                    public void onDismiss(View view, Object token) {
                        browserController.removeAlbum(albumController);
                    }
                }
        ));

        albumView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browserController.showAlbum(albumController, false, false, false);
            }
        });

        albumView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                UltimateBrowserProjectToast.show(context, albumTitle.getText().toString());
                return true;
            }
        });

        albumCover = (ImageView) albumView.findViewById(R.id.album_cover);
        albumTitle = (TextView) albumView.findViewById(R.id.album_title);
        albumTitle.setText(context.getString(R.string.album_untitled));
    }

    public void activate() {
        albumView.setBackgroundResource(R.drawable.album_shape_blue);
    }

    public void deactivate() {
        albumView.setBackgroundResource(R.drawable.album_shape_dark);
    }

}