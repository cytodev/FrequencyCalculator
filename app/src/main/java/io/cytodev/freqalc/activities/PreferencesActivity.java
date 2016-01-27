package io.cytodev.freqalc.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import io.cytodev.freqalc.R;
import io.cytodev.freqalc.fragments.NestedPreferenceFragment;
import io.cytodev.themedactivity.ThemedActivity;

/**
 * io.cytodev.freqalc.activities "Frequency Calculator"
 * 2016/01/14 @ 13:40
 *
 * @author Roel Walraven <cytodev@gmail.com>
 */
public class PreferencesActivity extends ThemedActivity {
    private Activity        thisActivity;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setDefaults();

        thisActivity = PreferencesActivity.this;
        manager      = getFragmentManager();

        SharedPreferences preferences  = PreferenceManager.getDefaultSharedPreferences(thisActivity);
        String            themeName    = preferences.getString("pref_appearance_theme", "WhiteSmoke");
        boolean           themeLight   = !preferences.getBoolean("pref_appearance_theme_dark", false);

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupUserInterface() {
        manager.beginTransaction()
                .replace(R.id.rootView, NestedPreferenceFragment.newInstance(R.xml.prefs, R.string.action_settings))
                .commit();

        if(getIntent().getExtras() != null) {
            int preferenceFile = getIntent().getExtras().getInt("pref");
            int preferenceName = getIntent().getExtras().getInt("name");

            // zero seems to work... Not at all tested on enough devices.
            manager.beginTransaction()
                    .setCustomAnimations(0, 0, R.animator.push_right_in, R.animator.push_right_out)
                    .replace(R.id.rootView, NestedPreferenceFragment.newInstance(preferenceFile, preferenceName))
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void back() {
        if(manager.getBackStackEntryCount() > 0) {
            manager.popBackStack();

            if(getSupportActionBar() != null) {
                getSupportActionBar().setSubtitle(null);
            }
        } else {
            Intent back = new Intent(thisActivity, MainActivity.class);

            back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            thisActivity.startActivity(back);
            thisActivity.finish();
            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        }
    }

}
