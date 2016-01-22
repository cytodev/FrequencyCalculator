package com.cytodev.freqalc.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.Html;
import android.util.Log;

import com.cytodev.freqalc.R;
import com.cytodev.freqalc.activities.CytoActivity;
import com.cytodev.freqalc.activities.PreferencesActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * com.cytodev.freqalc.fragments "Frequency Calculator"
 * 2016/01/14 @ 13:42
 *
 * @author Roel Walraven <cytodev@gmail.com>
 */
public class NestedPreferenceFragment extends PreferenceFragment {
    private final static String TAG = NestedPreferenceFragment.class.getSimpleName();

    private SharedPreferences.OnSharedPreferenceChangeListener changeListener;
    private Context context;

    private static int subTitle = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "Called onCreate");

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(getArguments().getInt("KEY"));
        NestedPreferenceFragment.subTitle = getArguments().getInt("NAME");

        if(((PreferencesActivity) getActivity()).getSupportActionBar() != null) {
            if(getArguments().getInt("NAME") != R.string.action_settings) {
                ((PreferencesActivity) getActivity()).getSupportActionBar().setSubtitle(subTitle);
            }
        }

        setupListeners();
    }

    @Override
    public void onPause() {
        Log.v(TAG, "Called onPause");

        super.onPause();
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(changeListener);
    }

    @Override
    public void onResume() {
        Log.v(TAG, "Called onResume");

        super.onResume();
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(changeListener);
    }

    @Override
    public void onAttach(Context context) {
        Log.v(TAG, "Called onAttach");

        super.onAttach(context);
        this.context = context;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(Activity activity) {
        Log.v(TAG, "Called onAttach");
        Log.w(TAG, "onAttach(Activity activity) is deprecated");

        super.onAttach(activity);
        this.context = activity;
    }

    public static NestedPreferenceFragment newInstance(int key, int name) {
        Log.v(TAG, "Creating new instance");

        NestedPreferenceFragment fragment = new NestedPreferenceFragment();
        Bundle args = new Bundle();

        args.putInt("KEY", key);
        args.putInt("NAME", name);
        fragment.setArguments(args);

        return fragment;
    }

    public static int getSubTitle() {
        Log.v(TAG, "Querying subTitle");

        return NestedPreferenceFragment.subTitle;
    }

    public static void setSubTitle(int subTitle) {
        Log.v(TAG, "Applying subTitle");

        NestedPreferenceFragment.subTitle = subTitle;
    }

    private void attachClickListener(String key, Preference.OnPreferenceClickListener listener) {
        Log.d(TAG, "Attaching listener to " + key);

        if(findPreference(key) == null)
            return;

        findPreference(key).setOnPreferenceClickListener(listener);
    }

    private void setupListeners() {
        Log.v(TAG, "Setting up listeners");

        final Context c = this.context;

        changeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.v(TAG, "Called onSharedPreferenceChanged");

                switch(key) {
                    case "theme":
                    case "darkTheme":
                        PreferencesActivity prefs = (PreferencesActivity) getActivity();
                        Bundle bundle = new Bundle();
                        Intent restart = prefs.getIntent();

                        bundle.putInt("pref", R.xml.prefs_appearance);
                        bundle.putInt("name", R.string.pref_cat_appearance);
                        restart.putExtras(bundle);
                        prefs.finish();
                        startActivity(restart);
                        prefs.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                }
            }
        };

        final Preference.OnPreferenceClickListener nestedListener = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.v(TAG, "Called onPreferenceClick (nestedListener)");
                Log.d(TAG, "Clicked on " + preference.getKey());

                int instance = -1;

                switch(preference.getKey()) {
                    case "pref_cat_about":
                        instance = R.xml.prefs_about;
                        break;
                    case "pref_cat_appearance":
                        instance = R.xml.prefs_appearance;
                        break;
                    default:
                        break;
                }

                if(instance != -1) {
                    getFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.animator.push_left_in, R.animator.push_left_out, R.animator.push_right_in, R.animator.push_right_out)
                            .replace(R.id.rootView, newInstance(instance, preference.getTitleRes()))
                            .addToBackStack(preference.getKey())
                            .commit();
                }

                return true;
            }
        };
        final Preference.OnPreferenceClickListener cytoLauncher = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                PreferencesActivity prefs = (PreferencesActivity) getActivity();
                Intent cytoLauncher = new Intent(prefs, CytoActivity.class);
                prefs.startActivity(cytoLauncher);
                return true;
            }
        };
        final Preference.OnPreferenceClickListener licenseLauncher = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.v(TAG, "Called onPreferenceClick (dialogLauncher)");
                Log.d(TAG, "Clicked on " + preference.getKey());

                AlertDialog.Builder licenseDialog = new AlertDialog.Builder(c);
                licenseDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                try {
                    Resources res = getResources();
                    InputStream ins;

                    switch(preference.getKey()) {
                        case "pref_about_license":
                            licenseDialog.setTitle(R.string.pref_about_license);
                            ins = res.openRawResource(R.raw.freqalc);
                            break;
                        default:
                            throw new FileNotFoundException();
                    }

                    byte[] b = new byte[ins.available()];
                    ins.read(b);
                    licenseDialog.setMessage(Html.fromHtml(new String(b)));
                } catch(Exception e) {
                    e.printStackTrace();
                    licenseDialog.setMessage(e.getLocalizedMessage());
                } finally {
                    licenseDialog.show();
                }

                return true;
            }
        };

        Log.v(TAG, "Attaching listeners");
        attachClickListener("pref_cat_about", nestedListener);
        attachClickListener("pref_about_developer", cytoLauncher);
        attachClickListener("pref_about_license", licenseLauncher);
        attachClickListener("pref_cat_appearance", nestedListener);
    }

}
