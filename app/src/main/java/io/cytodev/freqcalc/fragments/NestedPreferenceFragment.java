package io.cytodev.freqcalc.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

import io.cytodev.freqcalc.R;
import io.cytodev.freqcalc.activities.CytoActivity;
import io.cytodev.freqcalc.activities.PreferencesActivity;
import io.cytodev.freqcalc.activities.TranslationsActivity;

/**
 * io.cytodev.freqcalc.fragments "Frequency Calculator"
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
                    case "pref_appearance_theme":
                        changeTheme(false, false);
                    case "pref_appearance_theme_dark":
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
                    case "pref_appearance_icon":
                        changeTheme(false, true);
                        break;
                    case "pref_general_averagenum":
                        if(sharedPreferences.getString(key, "4").equals("-1")) {
                            Toast.makeText(context, R.string.pref_general_averagenum_unlimitedHelp, Toast.LENGTH_SHORT).show();
                        }
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
                    case "pref_cat_general":
                        instance = R.xml.prefs_general;
                        break;
                    case "pref_cat_interface":
                        instance = R.xml.prefs_interface;
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
                            ins = res.openRawResource(R.raw.freqcalc);
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
                    AlertDialog dialog = licenseDialog.create();
                    dialog.show();
                    ((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                }

                return true;
            }
        };
        final Preference.OnPreferenceClickListener translationsLauncher = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference){
                PreferencesActivity prefs = (PreferencesActivity) getActivity();
                Intent translationsLauncher = new Intent(prefs, TranslationsActivity.class);
                translationsLauncher.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                prefs.startActivity(translationsLauncher);
                prefs.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                return true;
            }
        };

        Log.v(TAG, "Attaching listeners");
        attachClickListener("pref_cat_about", nestedListener);
        attachClickListener("pref_about_developer", cytoLauncher);
        attachClickListener("pref_about_license", licenseLauncher);
        attachClickListener("pref_about_translations", translationsLauncher);
        attachClickListener("pref_cat_appearance", nestedListener);
        attachClickListener("pref_cat_general", nestedListener);
        attachClickListener("pref_cat_interface", nestedListener);
    }

    private void changeTheme(boolean revert, boolean iconChange) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if(revert) {
            context.getPackageManager().setComponentEnabledSetting(new ComponentName("io.cytodev.freqcalc", "io.cytodev.freqcalc.activities.MainActivity.WhiteSmoke"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            context.getPackageManager().setComponentEnabledSetting(new ComponentName("io.cytodev.freqcalc", "io.cytodev.freqcalc.activities.MainActivity.DodgerBlue"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            context.getPackageManager().setComponentEnabledSetting(new ComponentName("io.cytodev.freqcalc", "io.cytodev.freqcalc.activities.MainActivity.SpringBud"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            context.getPackageManager().setComponentEnabledSetting(new ComponentName("io.cytodev.freqcalc", "io.cytodev.freqcalc.activities.MainActivity.ElectricPurple"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            context.getPackageManager().setComponentEnabledSetting(new ComponentName("io.cytodev.freqcalc", "io.cytodev.freqcalc.activities.MainActivity.OrangePeel"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            context.getPackageManager().setComponentEnabledSetting(new ComponentName("io.cytodev.freqcalc", "io.cytodev.freqcalc.activities.MainActivity.HollywoodCerise"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            context.getPackageManager().setComponentEnabledSetting(new ComponentName("io.cytodev.freqcalc", "io.cytodev.freqcalc.activities.MainActivity.SpringGreen"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            context.getPackageManager().setComponentEnabledSetting(new ComponentName("io.cytodev.freqcalc", "io.cytodev.freqcalc.activities.MainActivity.WhiteSmoke"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            return;
        }

        if(prefs.getBoolean("pref_appearance_icon", false)) {
            context.getPackageManager().setComponentEnabledSetting(new ComponentName("io.cytodev.freqcalc", "io.cytodev.freqcalc.activities.MainActivity.WhiteSmoke"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            context.getPackageManager().setComponentEnabledSetting(new ComponentName("io.cytodev.freqcalc", "io.cytodev.freqcalc.activities.MainActivity.DodgerBlue"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            context.getPackageManager().setComponentEnabledSetting(new ComponentName("io.cytodev.freqcalc", "io.cytodev.freqcalc.activities.MainActivity.SpringBud"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            context.getPackageManager().setComponentEnabledSetting(new ComponentName("io.cytodev.freqcalc", "io.cytodev.freqcalc.activities.MainActivity.ElectricPurple"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            context.getPackageManager().setComponentEnabledSetting(new ComponentName("io.cytodev.freqcalc", "io.cytodev.freqcalc.activities.MainActivity.OrangePeel"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            context.getPackageManager().setComponentEnabledSetting(new ComponentName("io.cytodev.freqcalc", "io.cytodev.freqcalc.activities.MainActivity.HollywoodCerise"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            context.getPackageManager().setComponentEnabledSetting(new ComponentName("io.cytodev.freqcalc", "io.cytodev.freqcalc.activities.MainActivity.SpringGreen"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            context.getPackageManager().setComponentEnabledSetting(new ComponentName("io.cytodev.freqcalc", "io.cytodev.freqcalc.activities.MainActivity." + prefs.getString("pref_appearance_theme", "WhiteSmoke")), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        } else {
            if(iconChange) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.dialog_icon_title)
                        .setMessage(R.string.dialog_icon_message)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                changeTheme(true, false);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        }
    }

}
