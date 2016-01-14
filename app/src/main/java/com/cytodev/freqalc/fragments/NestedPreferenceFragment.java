package com.cytodev.freqalc.fragments;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.cytodev.freqalc.R;
import com.cytodev.freqalc.activities.PreferencesActivity;

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
            if(getArguments().getInt("NAME") != R.string.nav_settings) {
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

        Preference.OnPreferenceClickListener nestedListener = new Preference
                .OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.v(TAG, "Called onPreferenceClick (nestedListener)");
                Log.d(TAG, "Clicked on " + preference.getKey());

                int instance = -1;

                switch(preference.getKey()) {
                    default:
                        break;
                }

                if(instance != -1) {
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.rootView, newInstance(instance, preference.getTitleRes()))
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .addToBackStack(preference.getKey())
                            .commit();
                }

                return true;
            }
        };

        changeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.v(TAG, "Called onSharedPreferenceChanged");

                switch(key) {
                    case "theme":
                    case "darkTheme":
                        PreferencesActivity prefs = (PreferencesActivity) getActivity();
                        prefs.finish();
                        startActivity(prefs.getIntent());
                        prefs.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                }
            }
        };

        Log.v(TAG, "Attaching listeners");

    }

}
