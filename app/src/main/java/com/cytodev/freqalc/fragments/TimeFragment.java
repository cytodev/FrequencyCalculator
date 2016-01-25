package com.cytodev.freqalc.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.cytodev.freqalc.R;

/**
 * com.cytodev.freqalc.fragments "Frequency Calculator"
 * 2016/01/25 @ 11:46
 *
 * @author Roel Walraven <cytodev@gmail.com>
 */
public class TimeFragment extends CalculatorFragment {
    private static final String TAG = TimeFragment.class.getSimpleName();

    private View time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, "Called onCreateView");

        this.time = inflater.inflate(R.layout.layout_time, container, false);

        initUI();

        return time;
    }

    private void initUI() {
        final EditText tm  = (EditText) time.findViewById(R.id.freq_input_timeMinutes);
        final EditText ts  = (EditText) time.findViewById(R.id.freq_input_timeSeconds);
        final EditText tms = (EditText) time.findViewById(R.id.freq_input_timeMilis);
        final TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String identifier = "";
                if(!stop) {
                    try {
                        if(s.hashCode() == tm.getText().hashCode()) {
                            identifier = "tm";
                            freqalc.calculate(identifier, Double.parseDouble(tm.getText().toString()));
                        } else if(s.hashCode() == ts.getText().hashCode()) {
                            identifier = "ts";
                            freqalc.calculate(identifier, Double.parseDouble(ts.getText().toString()));
                        } else if(s.hashCode() == tms.getText().hashCode()) {
                            identifier = "tms";
                            freqalc.calculate(identifier, Double.parseDouble(tms.getText().toString()));
                        }
                    } catch(Exception e) {
                        freqalc.calculate("hz", 0.000);
                    }

                    updateVals(identifier);
                }
            }
        };

        tm.addTextChangedListener(tw);
        ts.addTextChangedListener(tw);
        tms.addTextChangedListener(tw);
    }

}
