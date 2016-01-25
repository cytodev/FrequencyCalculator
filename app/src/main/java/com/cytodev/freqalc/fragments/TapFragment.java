package com.cytodev.freqalc.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.cytodev.freqalc.R;

/**
 * com.cytodev.freqalc.fragments "Frequency Calculator"
 * 2016/01/25 @ 12:53
 *
 * @author Roel Walraven <cytodev@gmail.com>
 */
public class TapFragment extends CalculatorFragment {
    private static final String TAG = TapFragment.class.getSimpleName();

    private View tap;
    private boolean clickSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, "Called onCreateView");

        this.tap = inflater.inflate(R.layout.layout_tap, container, false);

        initUI();

        return tap;
    }

    private void initUI() {
        final Button tb = (Button) tap.findViewById(R.id.freq_input_tap);
        final View.OnClickListener tc = new View.OnClickListener() {
            Long endTime = null;
            Long startTime = null;

            @Override
            public void onClick(View v) {
                if(clickSwitch) {
                    endTime = System.currentTimeMillis();
                    freqalc.tap(average, averageTaps, startTime, endTime);
                    updateVals("");
                    startTime = System.currentTimeMillis();
                } else {
                    startTime = System.currentTimeMillis();
                    clickSwitch = true;
                }
            }
        };
        final View.OnLongClickListener tlc = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                freqalc.reset();
                clickSwitch = false;
                updateVals("");
                Vibrator vb = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(50);
                Toast.makeText(getActivity(), R.string.freq_input_tap_cleared, Toast.LENGTH_SHORT).show();
                return false;
            }
        };

        tb.setOnClickListener(tc);

        if(average && averageTaps == -1) {
            tb.setOnLongClickListener(tlc);
        }
    }

}