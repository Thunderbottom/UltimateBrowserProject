package io.github.UltimateBrowserProject.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import io.github.UltimateBrowserProject.Browser.AdBlock;
import io.github.UltimateBrowserProject.Database.RecordAction;
import io.github.UltimateBrowserProject.R;
import io.github.UltimateBrowserProject.Unit.BrowserUnit;
import io.github.UltimateBrowserProject.View.UltimateBrowserProjectToast;
import io.github.UltimateBrowserProject.View.WhitelistAdapter;

public class WhitelistActivity extends AppCompatActivity {
    private WhitelistAdapter adapter;
    private List<String> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whitelist);
        if (sp.getString(getString(R.string.sp_theme), "0").equals("1"))
            findViewById(R.id.whitelist_layout).setBackgroundColor(getResources().getColor(R.color.background_material_dark));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecordAction action = new RecordAction(this);
        action.open(false);
        list = action.listDomains();
        action.close();

        ListView listView = (ListView) findViewById(R.id.whitelist);
        listView.setEmptyView(findViewById(R.id.whitelist_empty));

        adapter = new WhitelistAdapter(this, R.layout.whitelist_item, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        final EditText editText = (EditText) findViewById(R.id.whitelist_edit);
        showSoftInput(editText);

        Button button = (Button) findViewById(R.id.whitelist_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String domain = editText.getText().toString().trim();
                if (domain.isEmpty())
                    UltimateBrowserProjectToast.show(WhitelistActivity.this, R.string.toast_input_empty);
                else if (!BrowserUnit.isURL(domain))
                    UltimateBrowserProjectToast.show(WhitelistActivity.this, R.string.toast_invalid_domain);
                else {
                    RecordAction action = new RecordAction(WhitelistActivity.this);
                    action.open(true);
                    if (action.checkDomain(domain)) {
                        UltimateBrowserProjectToast.show(WhitelistActivity.this, R.string.toast_domain_already_exists);
                    } else {
                        AdBlock adBlock = new AdBlock(WhitelistActivity.this);
                        adBlock.addDomain(domain.trim());
                        list.add(0, domain.trim());
                        adapter.notifyDataSetChanged();
                        UltimateBrowserProjectToast.show(WhitelistActivity.this, R.string.toast_add_whitelist_successful);
                    }
                    action.close();
                }
            }
        });
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setNavigationBarColor(getResources().getColor(R.color.gray_900));

    }

    @Override
    public void onPause() {
        hideSoftInput(findViewById(R.id.whitelist_edit));
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.whitelist_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.whitelist_menu_clear:
                AdBlock adBlock = new AdBlock(this);
                adBlock.clearDomains();
                list.clear();
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
        return true;
    }

    private void hideSoftInput(View view) {
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showSoftInput(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
