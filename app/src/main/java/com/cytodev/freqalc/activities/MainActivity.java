package com.cytodev.freqalc.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cytodev.freqalc.R;
import com.cytodev.themedactivity.ThemedActivity;

public class MainActivity extends ThemedActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    private Activity          thisActivity;
    private SharedPreferences preferences;
    private FragmentManager   manager;
    private String            themeName;
    private boolean           themeLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setDefaults();

        thisActivity = MainActivity.this;
        preferences  = PreferenceManager.getDefaultSharedPreferences(thisActivity);
        manager      = getFragmentManager();
        themeName    = preferences.getString("theme", "WhiteSmoke");
        themeLight   = !preferences.getBoolean("darkTheme", false);

        if(!super.getCurrentThemeName().equals(themeName)) {
            super.setTheme(themeLight, themeName);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        setupUserInterface();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_settings) {
            Intent settings = new Intent(MainActivity.this, PreferencesActivity.class);
            MainActivity.this.startActivity(settings);
            MainActivity.this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if(getSupportActionBar() == null) {
            setSupportActionBar(toolbar);
        }
    }

    private void setupUserInterface() {

    }

}
