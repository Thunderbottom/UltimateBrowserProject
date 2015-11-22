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
import android.widget.EditText;

import com.rey.material.widget.Button;

import io.github.UltimateBrowserProject.R;
import io.github.UltimateBrowserProject.View.UltimateBrowserProjectToast;

public class TokenActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.token);

        if (sp.getString(getString(R.string.sp_theme), "0").equals("1"))
            findViewById(R.id.token_layout).setBackgroundColor(getResources().getColor(R.color.background_material_dark));


        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch(NullPointerException e) { /* */ }

        final EditText tokenEdit = (EditText) findViewById(R.id.token_edit);
        Button tokenAdd = (Button) findViewById(R.id.token_add);

        String token = sp.getString(getString(R.string.sp_readability_token), "");
        tokenEdit.setText(token);
        tokenEdit.setSelection(token.length());
        showSoftInput(tokenEdit);

        tokenAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tokenEdit.getText().toString().trim().isEmpty()) {
                    UltimateBrowserProjectToast.show(TokenActivity.this, R.string.toast_input_empty);
                } else {
                    sp.edit().putString(
                            getString(R.string.sp_readability_token),
                            tokenEdit.getText().toString().trim()
                    ).apply();
                    UltimateBrowserProjectToast.show(TokenActivity.this, R.string.toast_add_token_successful);
                }
            }
        });
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setNavigationBarColor(getResources().getColor(R.color.gray_900));

    }

    @Override
    public void onPause() {
        hideSoftInput(findViewById(R.id.token_edit));
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.token_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
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
