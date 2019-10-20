package io.cytodev.freqcalc.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.AppCompatImageView;

import com.crashlytics.android.Crashlytics;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Date;

import io.cytodev.freqcalc.R;
import io.cytodev.freqcalc.fragments.BeatsFragment;
import io.cytodev.freqcalc.fragments.HertzFragment;
import io.cytodev.freqcalc.fragments.TapFragment;
import io.cytodev.freqcalc.fragments.TimeFragment;
import io.cytodev.freqcalc.logic.FrequencyCalculator;
import io.cytodev.themedactivity.ThemedActivity;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends ThemedActivity {
    private static SharedPreferences preferences;

    private Activity        thisActivity;
    private FragmentManager manager;
    private String          themeName;
    private boolean         themeLight;
    private boolean         displayBeats;
    private boolean         displayTime;
    private boolean         displayTap;
    private boolean         reportCrashes;
    private int             decimals;

    public FrequencyCalculator freqcalc;
    public boolean             stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setDefaults();

        thisActivity = MainActivity.this;
        preferences  = PreferenceManager.getDefaultSharedPreferences(thisActivity);
        manager      = getSupportFragmentManager();

        getPrefs();

        if(!super.getCurrentThemeName().equals(themeName)) {
            super.setTheme(themeLight, themeName);
        }

        freqcalc = new FrequencyCalculator();
        freqcalc.setDecimals(decimals);

        super.onCreate(savedInstanceState);

        if(reportCrashes) {
            Fabric.with(this, new Crashlytics());
        }

        setContentView(R.layout.activity_main);
        setupToolbar();
        setupUserInterface();

        if(!getSharedPreferences("FreqCalcShared", 0).getBoolean("hasSeenThankYou", false)) {
            sayThankYou();
        }
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
        this.themeName     = preferences.getString("pref_appearance_theme", "WhiteSmoke");
        this.themeLight    = !preferences.getBoolean("pref_appearance_theme_dark", false);
        this.displayBeats  = preferences.getBoolean("pref_interface_beats", true);
        this.displayTime   = preferences.getBoolean("pref_interface_time", true);
        this.displayTap    = preferences.getBoolean("pref_interface_tap", true);
        this.reportCrashes = preferences.getBoolean("pref_privacy_crashreporting", true);
        this.decimals      = Integer.parseInt(preferences.getString("pref_general_decimals", "3"));
    }

    public static boolean getAverage() {
        return preferences.getBoolean("pref_general_average", false);
    }

    public static int getAverageTaps() {
        return Integer.parseInt(preferences.getString("pref_general_averagenum", "4"));
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

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

    private void sayThankYou() {
        long firstUse = getSharedPreferences("FreqCalcShared", 0).getLong("firstUse", 0);
        long today    = new Date().getTime();

        if(firstUse == 0) {
            getSharedPreferences("FreqCalcShared", 0).edit().putLong("firstUse", new Date().getTime()).apply();
            return;
        }

        if(firstUse < today - (long) 604800000) {
            new AlertDialog.Builder(thisActivity)
                    .setTitle(R.string.dialog_thankyou_title)
                    .setMessage(R.string.dialog_thankyou_message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);

                            try {
                                intent.setData(Uri.parse("market://details?id=io.cytodev.freqcalc"));

                                startActivity(intent);
                            } catch(ActivityNotFoundException anfe) {
                                intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=io.cytodev.freqcalc"));

                                startActivity(intent);
                            }
                        }
                    })
                    .setNegativeButton(R.string.dialog_thankyou_nothanks, null)
                    .show();

            getSharedPreferences("FreqCalcShared", 0).edit().putBoolean("hasSeenThankYou", true).apply();
        }
    }

    private void updateValue(String identifier, int inputId, int textId, int warningId) {
        AppCompatEditText  input   = findViewById(inputId);
        AppCompatTextView  text    = findViewById(textId);
        AppCompatImageView warning = findViewById(warningId);

        if(input == null)
            return;

        String value = freqcalc.clipDecimals(freqcalc.getFreq(identifier));

        int warningTextColor = getResources().getColor(R.color.warning);
        int normalTextColor  = getResources().getColor(isCurrentThemeLight() ? R.color.textColorPrimaryDark : R.color.textColorPrimaryLight);

        if(value.contains("!")) {
            value = value.replace("!", "");

            input.setTextColor(warningTextColor);
            input.setError(getResources().getString(R.string.freq_precision_warning), null);
            text.setTextColor(warningTextColor);
            warning.setVisibility(View.VISIBLE);
        } else {
            input.setTextColor(normalTextColor);
            input.setError(null, null);
            text.setTextColor(normalTextColor);
            warning.setVisibility(View.INVISIBLE);
        }

        input.setText(value);
    }

    public void updateValues(String identifier) {
        this.stop = true;

        if(!identifier.equals("hz"))
            updateValue("hz", R.id.freq_input_hertz, R.id.freq_hertz, R.id.freq_hertz_warning);

        if(!identifier.equals("bph"))
            updateValue("bph", R.id.freq_input_beatsPerHour, R.id.freq_beatsPerHour, R.id.freq_beatsPerHour_warning);

        if(!identifier.equals("bpm"))
            updateValue("bpm", R.id.freq_input_beatsPerMinute, R.id.freq_beatsPerMinute, R.id.freq_beatsPerMinute_warning);

        if(!identifier.equals("bps"))
            updateValue("bps", R.id.freq_input_beatsPerSecond, R.id.freq_beatsPerSecond, R.id.freq_beatsPerSecond_warning);

        if(!identifier.equals("tm"))
            updateValue("tm", R.id.freq_input_timeMinutes, R.id.freq_timeMinutes, R.id.freq_timeMinutes_warning);

        if(!identifier.equals("ts"))
            updateValue("ts", R.id.freq_input_timeSeconds, R.id.freq_timeSeconds, R.id.freq_timeSeconds_warning);

        if(!identifier.equals("tms"))
            updateValue("tms", R.id.freq_input_timeMilis, R.id.freq_timeMilis, R.id.freq_timeMilis_warning);

        this.stop = false;
    }

}
