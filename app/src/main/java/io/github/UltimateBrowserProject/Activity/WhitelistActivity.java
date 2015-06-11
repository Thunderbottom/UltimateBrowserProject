package io.github.UltimateBrowserProject.Activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import io.github.UltimateBrowserProject.Browser.AdBlock;
import io.github.UltimateBrowserProject.Database.RecordAction;
import io.github.UltimateBrowserProject.R;
import io.github.UltimateBrowserProject.Unit.BrowserUnit;
import io.github.UltimateBrowserProject.View.UltimateBrowserProjectToast;
import io.github.UltimateBrowserProject.View.WhitelistAdapter;

import java.util.List;

public class WhitelistActivity extends Activity {
    private WhitelistAdapter adapter;
    private List<String> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.gray_900));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whitelist);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        RecordAction action = new RecordAction(this);
        action.open(false);
        list = action.listDomains();
        action.close();

        ListView listView = (ListView) findViewById(R.id.whitelist);
        listView.setEmptyView(findViewById(R.id.whitelist_empty));

        adapter = new WhitelistAdapter(this, R.layout.whitelist_item, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        final EditText editText = (EditText) findViewById(R.id.whilelist_edit);
        Button button = (Button) findViewById(R.id.whilelist_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String domain = editText.getText().toString().trim();
                if (domain.isEmpty()) {
                    UltimateBrowserProjectToast.show(WhitelistActivity.this, R.string.toast_input_empty);
                } else if (!BrowserUnit.isURL(domain)) {
                    UltimateBrowserProjectToast.show(WhitelistActivity.this, R.string.toast_invalid_domain);
                } else {
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
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.whilelist_menu, menu);
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
}
