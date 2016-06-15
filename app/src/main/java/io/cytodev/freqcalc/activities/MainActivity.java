package io.cytodev.freqcalc.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;

import java.util.Date;

import io.cytodev.freqcalc.R;
import io.cytodev.freqcalc.fragments.BeatsFragment;
import io.cytodev.freqcalc.fragments.HertzFragment;
import io.cytodev.freqcalc.fragments.TapFragment;
import io.cytodev.freqcalc.fragments.TimeFragment;
import io.cytodev.freqcalc.logic.DateChecker;
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
    private int             decimals;

    public FrequencyCalculator freqcalc;
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

        freqcalc = new FrequencyCalculator();
        freqcalc.setDecimals(decimals);

        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        setupToolbar();
        setupUserInterface();

        if(!getSharedPreferences("FreqCalcShared", 0).getBoolean("hasSeenThankyou", false)) {
            sayThankyou();
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
        this.themeName    = preferences.getString("pref_appearance_theme", "WhiteSmoke");
        this.themeLight   = !preferences.getBoolean("pref_appearance_theme_dark", false);
        this.displayBeats = preferences.getBoolean("pref_interface_beats", true);
        this.displayTime  = preferences.getBoolean("pref_interface_time", true);
        this.displayTap   = preferences.getBoolean("pref_interface_tap", true);
        this.decimals     = Integer.parseInt(preferences.getString("pref_general_decimals", "3"));
    }

    public static boolean getAverage() {
        return preferences.getBoolean("pref_general_average", false);
    }

    public static int getAverageTaps() {
        return Integer.parseInt(preferences.getString("pref_general_averagenum", "4"));
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

    private void sayThankyou() {
        Date date = DateChecker.getInstallTime(getPackageManager(), getApplication().getPackageName());

        if(date != null) {
            long today = new Date().getTime();
            long installTime = date.getTime();

            if(installTime + ((long) 604800) < today) {
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
                        .setNegativeButton(R.string.dialog_thankyou_fuckoff, null)
                        .show();

                getSharedPreferences("FreqCalcShared", 0).edit().putBoolean("hasSeenThankyou", true).commit();
            }
        }
    }

    public void updateVals(String identifier) {
        this.stop = true;

        if(!identifier.equals("hz") && findViewById(R.id.freq_input_hertz) != null) {
            ((EditText) findViewById(R.id.freq_input_hertz)).setText(freqcalc.clipDecimals(freqcalc.getFreq("hz")));
        }
        if(!identifier.equals("bph") && findViewById(R.id.freq_input_beatsPerHour) != null) {
            ((EditText) findViewById(R.id.freq_input_beatsPerHour)).setText(freqcalc.clipDecimals(freqcalc.getFreq("bph")));
        }
        if(!identifier.equals("bpm") && findViewById(R.id.freq_input_beatsPerMinute) != null) {
            ((EditText) findViewById(R.id.freq_input_beatsPerMinute)).setText(freqcalc.clipDecimals(freqcalc.getFreq("bpm")));
        }
        if(!identifier.equals("bps") && findViewById(R.id.freq_input_beatsPerSecond) != null) {
            ((EditText) findViewById(R.id.freq_input_beatsPerSecond)).setText(freqcalc.clipDecimals(freqcalc.getFreq("bps")));
        }
        if(!identifier.equals("tm") && findViewById(R.id.freq_input_timeMinutes) != null) {
            ((EditText) findViewById(R.id.freq_input_timeMinutes)).setText(freqcalc.clipDecimals(freqcalc.getFreq("tm")));
        }
        if(!identifier.equals("ts") && findViewById(R.id.freq_input_timeSeconds) != null) {
            ((EditText) findViewById(R.id.freq_input_timeSeconds)).setText(freqcalc.clipDecimals(freqcalc.getFreq("ts")));
        }
        if(!identifier.equals("tms") && findViewById(R.id.freq_input_timeMilis) != null) {
            ((EditText) findViewById(R.id.freq_input_timeMilis)).setText(freqcalc.clipDecimals(freqcalc.getFreq("tms")));
        }

        this.stop = false;
    }

}
