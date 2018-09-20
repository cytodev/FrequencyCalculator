package io.cytodev.freqcalc.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

import io.cytodev.freqcalc.R;
import io.cytodev.freqcalc.adapters.TranslatorsAdapter;
import io.cytodev.freqcalc.objects.Translator;
import io.cytodev.themedactivity.ThemedActivity;

/**
 * io.cytodev.freqcalc.activities "FrequencyCalculator"
 * 2016/06/15 @ 15:42
 *
 * @author Roel Walraven <cytodev@gmail.com>
 */
public class TranslationsActivity extends ThemedActivity {
    private Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setDefaults();

        thisActivity = TranslationsActivity.this;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(thisActivity);
        String            themeName   = preferences.getString("pref_appearance_theme", "WhiteSmoke");
        boolean           themeLight  = !preferences.getBoolean("pref_appearance_theme_dark", false);

        if(!super.getCurrentThemeName().equals(themeName)) {
            super.setTheme(themeLight, themeName);
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_translations);
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
        Button    help            = (Button) findViewById(R.id.pref_about_translations_help);
        ListView  translatorsList = (ListView) findViewById(R.id.translators);
        JSONArray translatorsJSON = null;

        if(help != null) {
            help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/CytoDev/FrequencyCalculator/tree/translations"));

                    thisActivity.startActivity(browserIntent);
                }
            });
        }

        try {
            InputStream ins = getResources().openRawResource(R.raw.translators);
            byte[]      b   = new byte[ins.available()];

            ins.read(b);

            translatorsJSON = new JSONObject(new String(b)).getJSONArray("translators");
        } catch(Exception e) {
            e.printStackTrace();
        }

        if(translatorsJSON != null && translatorsList != null)
            translatorsList.setAdapter(new TranslatorsAdapter(thisActivity, Translator.fromJson(translatorsJSON)));
    }

    private void back() {
        thisActivity.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

}
