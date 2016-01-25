package com.cytodev.freqalc.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cytodev.freqalc.R;
import com.cytodev.freqalc.fragments.BeatsFragment;
import com.cytodev.freqalc.fragments.HertzFragment;
import com.cytodev.freqalc.fragments.TapFragment;
import com.cytodev.freqalc.fragments.TimeFragment;
import com.cytodev.themedactivity.ThemedActivity;

public class MainActivity extends ThemedActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    private Activity          thisActivity;
    private SharedPreferences preferences;
    private FragmentManager   manager;
    private String            themeName;
    private boolean           themeLight;
    private boolean           displayBeats;
    private boolean           displayTime;
    private boolean           displayTap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setDefaults();

        thisActivity = MainActivity.this;
        preferences  = PreferenceManager.getDefaultSharedPreferences(thisActivity);
        manager      = getFragmentManager();

        getPrefs();

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
            Intent settings = new Intent(thisActivity, PreferencesActivity.class);

            settings.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            thisActivity.startActivity(settings);
            thisActivity.finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getPrefs() {
        this.themeName    = preferences.getString("theme", "WhiteSmoke");
        this.themeLight   = !preferences.getBoolean("darkTheme", false);
        this.displayBeats = preferences.getBoolean("DisplayBeats", true);
        this.displayTime  = preferences.getBoolean("DisplayTime", true);
        this.displayTap   = preferences.getBoolean("DisplayTap", true);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if(getSupportActionBar() == null) {
            setSupportActionBar(toolbar);
        }
    }

    private void setupUserInterface() {
        FragmentTransaction fragTransaction = manager.beginTransaction();

        fragTransaction.replace(R.id.hertzView, new HertzFragment());

        if(displayBeats)
            fragTransaction.replace(R.id.beatsView, new BeatsFragment());

        if(displayTime)
            fragTransaction.replace(R.id.timeView, new TimeFragment());

        if(displayTap)
            fragTransaction.replace(R.id.tapView, new TapFragment());

        fragTransaction.commit();
    }

}
