package io.cytodev.freqcalc.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import io.cytodev.freqcalc.R;
import io.cytodev.freqcalc.activities.MainActivity;

/**
 * io.cytodev.freqcalc.fragments "Frequency Calculator"
 * 2016/01/25 @ 11:46
 *
 * @author Roel Walraven <cytodev@gmail.com>
 */
public class BeatsFragment extends Fragment {
    private static final String TAG = BeatsFragment.class.getSimpleName();

    private View beats;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, "Called onCreateView");

        this.beats = inflater.inflate(R.layout.layout_beats, container, false);

        initUI();

        return beats;
    }

    private void initUI() {
        final EditText    bph = (EditText) beats.findViewById(R.id.freq_input_beatsPerHour);
        final EditText    bpm = (EditText) beats.findViewById(R.id.freq_input_beatsPerMinute);
        final EditText    bps = (EditText) beats.findViewById(R.id.freq_input_beatsPerSecond);
        final TextWatcher tw  = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String identifier = "";
                if(!((MainActivity) getActivity()).stop) {
                    try {
                        if(s.hashCode() == bph.getText().hashCode()) {
                            identifier = "bph";
                            ((MainActivity) getActivity()).freqcalc.calculate(identifier, Double.parseDouble(bph.getText().toString()));
                        } else if(s.hashCode() == bpm.getText().hashCode()) {
                            identifier = "bpm";
                            ((MainActivity) getActivity()).freqcalc.calculate(identifier, Double.parseDouble(bpm.getText().toString()));
                        } else if(s.hashCode() == bps.getText().hashCode()) {
                            identifier = "bps";
                            ((MainActivity) getActivity()).freqcalc.calculate(identifier, Double.parseDouble(bps.getText().toString()));
                        }
                    } catch(Exception e) {
                        ((MainActivity) getActivity()).freqcalc.calculate("hz", 0.000);
                    }

                    ((MainActivity) getActivity()).updateVals(identifier);
                }
            }
        };

        bph.addTextChangedListener(tw);
        bpm.addTextChangedListener(tw);
        bps.addTextChangedListener(tw);
    }

}
