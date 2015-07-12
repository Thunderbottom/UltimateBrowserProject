package io.github.UltimateBrowserProject.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import io.github.UltimateBrowserProject.Fragment.ClearFragment;
import io.github.UltimateBrowserProject.R;
import io.github.UltimateBrowserProject.Unit.BrowserUnit;
import io.github.UltimateBrowserProject.View.UltimateBrowserProjectToast;

public class ClearActivity extends AppCompatActivity {
    public static final String DB_CHANGE = "DB_CHANGE";
    private boolean dbChange = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.gray_900));
        }
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new ClearFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.clear_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra(DB_CHANGE, dbChange);
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case R.id.clear_menu_done_all:
                clear();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        Intent intent = new Intent();
        intent.putExtra(DB_CHANGE, dbChange);
        setResult(Activity.RESULT_OK, intent);
        finish();
        return true;
    }

    private void clear() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean clearBookmarks = sp.getBoolean(getString(R.string.sp_clear_bookmarks), false);
        boolean clearCache = sp.getBoolean(getString(R.string.sp_clear_cache), true);
        boolean clearCookie = sp.getBoolean(getString(R.string.sp_clear_cookie), false);
        boolean clearFormData = sp.getBoolean(getString(R.string.sp_clear_form_data), false);
        boolean clearHistory = sp.getBoolean(getString(R.string.sp_clear_history), true);
        boolean clearPasswords = sp.getBoolean(getString(R.string.sp_clear_passwords), false);

        ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.toast_wait_a_minute));
        dialog.show();

        if (clearBookmarks) {
            BrowserUnit.clearBookmarks(this);
        }
        if (clearCache) {
            BrowserUnit.clearCache(this);
        }
        if (clearCookie) {
            BrowserUnit.clearCookie(this);
        }
        if (clearFormData) {
            BrowserUnit.clearFormData(this);
        }
        if (clearHistory) {
            BrowserUnit.clearHistory(this);
        }
        if (clearPasswords) {
            BrowserUnit.clearPasswords(this);
        }

        dialog.hide();
        dialog.dismiss();

        dbChange = true;
        UltimateBrowserProjectToast.show(this, R.string.toast_clear_successful);
    }
}
