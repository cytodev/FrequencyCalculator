package com.cytodev.freqalc.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.cytodev.freqalc.R;
import com.cytodev.freqalc.fragments.NestedPreferenceFragment;
import com.cytodev.themedactivity.ThemedActivity;

/**
 * com.cytodev.freqalc.activities "Frequency Calculator"
 * 2016/01/14 @ 13:40
 *
 * @author Roel Walraven <cytodev@gmail.com>
 */
public class PreferencesActivity extends ThemedActivity {
    private final static String TAG = PreferencesActivity.class.getSimpleName();

    private Activity          thisActivity;
    private SharedPreferences preferences;
    private FragmentManager   manager;
    private String            themeName;
    private boolean           themeLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setDefaults();

        thisActivity = PreferencesActivity.this;
        preferences  = PreferenceManager.getDefaultSharedPreferences(thisActivity);
        manager      = getFragmentManager();
        themeName    = preferences.getString("theme", "WhiteSmoke");
        themeLight   = !preferences.getBoolean("darkTheme", false);

        if(!super.getCurrentThemeName().equals(themeName)) {
            super.setTheme(themeLight, themeName);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        setupToolbar();
        setupUserInterface();
    }

    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            back();
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
        manager.beginTransaction()
                .replace(R.id.rootView, NestedPreferenceFragment.newInstance(R.xml.prefs, R.string.action_settings))
                .commit();
    }

    private void back() {
        if(manager.getBackStackEntryCount() > 0) {
            manager.popBackStack();

//            if(getSupportActionBar() != null) {
//                if(PreferencesFragment.getSubTitle() == R.string.pref_thirdparty) {
//                    getSupportActionBar().setSubtitle(R.string.pref_about);
//                    PreferencesFragment.setSubTitle(R.string.pref_about);
//                } else {
//                    getSupportActionBar().setSubtitle(null);
//                }
//            }
        } else {
            Intent back = new Intent(PreferencesActivity.this, MainActivity.class);

            this.startActivity(back);
            this.finish();
        }
    }

}
