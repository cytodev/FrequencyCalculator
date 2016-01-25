package com.cytodev.freqalc.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;

import com.cytodev.freqalc.R;
import com.cytodev.freqalc.logic.FrequencyCalculator;

/**
 * com.cytodev.freqalc.fragments "Frequency Calculator"
 * 2016/01/25 @ 11:18
 *
 * @author Roel Walraven <cytodev@gmail.com>
 */
public class CalculatorFragment extends Fragment {
    private static final String TAG = CalculatorFragment.class.getSimpleName();

    private Context context;

    public FrequencyCalculator freqalc;
    public boolean             stop;
    public boolean             average;
    public int                 decimals;
    public int                 averageTaps;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "Called onCreate");

        this.freqalc = new FrequencyCalculator();

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPrefs();
        freqalc.setDecimals(decimals);
    }

    private void getPrefs() {
        this.average     = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("pref_general_average", false);
        this.decimals    = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("pref_general_decimals", "3"));
        this.averageTaps = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("pref_general_averagenum", "4"));
    }

    public void updateVals(String identifier) {
        this.stop = true;

        if(!identifier.equals("hz") && getActivity().findViewById(R.id.freq_input_hertz) != null) {
            ((EditText) getActivity().findViewById(R.id.freq_input_hertz)).setText(Double.toString(freqalc.clipDecimals(freqalc.getFreq("hz"))));
        }
        if(!identifier.equals("bph") && getActivity().findViewById(R.id.freq_input_beatsPerHour) != null) {
            ((EditText) getActivity().findViewById(R.id.freq_input_beatsPerHour)).setText(Double.toString(freqalc.clipDecimals(freqalc.getFreq("bph"))));
        }
        if(!identifier.equals("bpm") && getActivity().findViewById(R.id.freq_input_beatsPerMinute) != null) {
            ((EditText) getActivity().findViewById(R.id.freq_input_beatsPerMinute)).setText(Double.toString(freqalc.clipDecimals(freqalc.getFreq("bpm"))));
        }
        if(!identifier.equals("bps") && getActivity().findViewById(R.id.freq_input_beatsPerSecond) != null) {
            ((EditText) getActivity().findViewById(R.id.freq_input_beatsPerSecond)).setText(Double.toString(freqalc.clipDecimals(freqalc.getFreq("bps"))));
        }
        if(!identifier.equals("tm") && getActivity().findViewById(R.id.freq_input_timeMinutes) != null) {
            ((EditText) getActivity().findViewById(R.id.freq_input_timeMinutes)).setText(Double.toString(freqalc.clipDecimals(freqalc.getFreq("tm"))));
        }
        if(!identifier.equals("ts") && getActivity().findViewById(R.id.freq_input_timeSeconds) != null) {
            ((EditText) getActivity().findViewById(R.id.freq_input_timeSeconds)).setText(Double.toString(freqalc.clipDecimals(freqalc.getFreq("ts"))));
        }
        if(!identifier.equals("tms") && getActivity().findViewById(R.id.freq_input_timeMilis) != null) {
            ((EditText) getActivity().findViewById(R.id.freq_input_timeMilis)).setText(Double.toString(freqalc.clipDecimals(freqalc.getFreq("tms"))));
        }

        this.stop = false;
    }

}
