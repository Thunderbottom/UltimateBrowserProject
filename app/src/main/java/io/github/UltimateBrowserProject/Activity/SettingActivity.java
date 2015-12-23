package io.github.UltimateBrowserProject.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

import io.github.UltimateBrowserProject.Fragment.SettingFragment;
import io.github.UltimateBrowserProject.R;
import io.github.UltimateBrowserProject.Task.ImportBookmarksTask;
import io.github.UltimateBrowserProject.Task.ImportWhitelistTask;
import io.github.UltimateBrowserProject.Unit.IntentUnit;
import io.github.UltimateBrowserProject.View.UltimateBrowserProjectToast;

public class SettingActivity extends AppCompatActivity {
    private SettingFragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getString(getString(R.string.sp_theme), "0").equals("0"))
            this.setTheme(R.style.SettingActivityTheme);
        else
            this.setTheme(R.style.SettingActivityThemeDark);

        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragment = new SettingFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setNavigationBarColor(getResources().getColor(R.color.gray_900));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                IntentUnit.setDBChange(fragment.isDBChange());
                IntentUnit.setSPChange(fragment.isSPChange());
                finish();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            IntentUnit.setDBChange(fragment.isDBChange());
            IntentUnit.setSPChange(fragment.isSPChange());
            finish();
            return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentUnit.REQUEST_BOOKMARKS) {
            if (resultCode != Activity.RESULT_OK || data == null || data.getData() == null)
                UltimateBrowserProjectToast.show(this, R.string.toast_import_bookmarks_failed);
            else {
                File file = new File(data.getData().getPath());
                new ImportBookmarksTask(fragment, file).execute();
            }
        } else if (requestCode == IntentUnit.REQUEST_WHITELIST) {
            if (resultCode != Activity.RESULT_OK || data == null || data.getData() == null)
                UltimateBrowserProjectToast.show(this, R.string.toast_import_whitelist_failed);
            else {
                File file = new File(data.getData().getPath());
                new ImportWhitelistTask(fragment, file).execute();
            }
        } else if (requestCode == IntentUnit.REQUEST_CLEAR) {
            if (resultCode == Activity.RESULT_OK && data != null && data.hasExtra(ClearActivity.DB_CHANGE))
                fragment.setDBChange(data.getBooleanExtra(ClearActivity.DB_CHANGE, false));
        }
    }
}
