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
import android.widget.EditText;

import com.cytodev.freqalc.R;
import com.cytodev.freqalc.fragments.BeatsFragment;
import com.cytodev.freqalc.fragments.HertzFragment;
import com.cytodev.freqalc.fragments.TapFragment;
import com.cytodev.freqalc.fragments.TimeFragment;
import com.cytodev.freqalc.logic.FrequencyCalculator;
import com.cytodev.themedactivity.ThemedActivity;

public class MainActivity extends ThemedActivity {
    private Activity          thisActivity;
    private SharedPreferences preferences;
    private FragmentManager   manager;
    private String            themeName;
    private boolean           themeLight;
    private boolean           displayBeats;
    private boolean           displayTime;
    private boolean           displayTap;

    public FrequencyCalculator freqalc;
    public int                 decimals;
    public int                 averageTaps;
    public boolean             average;
    public boolean             stop;

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

        freqalc = new FrequencyCalculator();
        freqalc.setDecimals(decimals);

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
        this.themeName    = preferences.getString("pref_appearance_theme", "WhiteSmoke");
        this.themeLight   = !preferences.getBoolean("pref_appearance_theme_dark", false);
        this.displayBeats = preferences.getBoolean("pref_interface_beats", true);
        this.displayTime  = preferences.getBoolean("pref_interface_time", true);
        this.displayTap   = preferences.getBoolean("pref_interface_tap", true);
        this.average      = preferences.getBoolean("pref_general_average", false);
        this.decimals     = Integer.parseInt(preferences.getString("pref_general_decimals", "3"));
        this.averageTaps  = Integer.parseInt(preferences.getString("pref_general_averagenum", "4"));
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

    public void updateVals(String identifier) {
        this.stop = true;

        if(!identifier.equals("hz") && findViewById(R.id.freq_input_hertz) != null) {
            ((EditText) findViewById(R.id.freq_input_hertz)).setText(Double.toString(freqalc.clipDecimals(freqalc.getFreq("hz"))));
        }
        if(!identifier.equals("bph") && findViewById(R.id.freq_input_beatsPerHour) != null) {
            ((EditText) findViewById(R.id.freq_input_beatsPerHour)).setText(Double.toString(freqalc.clipDecimals(freqalc.getFreq("bph"))));
        }
        if(!identifier.equals("bpm") && findViewById(R.id.freq_input_beatsPerMinute) != null) {
            ((EditText) findViewById(R.id.freq_input_beatsPerMinute)).setText(Double.toString(freqalc.clipDecimals(freqalc.getFreq("bpm"))));
        }
        if(!identifier.equals("bps") && findViewById(R.id.freq_input_beatsPerSecond) != null) {
            ((EditText) findViewById(R.id.freq_input_beatsPerSecond)).setText(Double.toString(freqalc.clipDecimals(freqalc.getFreq("bps"))));
        }
        if(!identifier.equals("tm") && findViewById(R.id.freq_input_timeMinutes) != null) {
            ((EditText) findViewById(R.id.freq_input_timeMinutes)).setText(Double.toString(freqalc.clipDecimals(freqalc.getFreq("tm"))));
        }
        if(!identifier.equals("ts") && findViewById(R.id.freq_input_timeSeconds) != null) {
            ((EditText) findViewById(R.id.freq_input_timeSeconds)).setText(Double.toString(freqalc.clipDecimals(freqalc.getFreq("ts"))));
        }
        if(!identifier.equals("tms") && findViewById(R.id.freq_input_timeMilis) != null) {
            ((EditText) findViewById(R.id.freq_input_timeMilis)).setText(Double.toString(freqalc.clipDecimals(freqalc.getFreq("tms"))));
        }

        this.stop = false;
    }

}
