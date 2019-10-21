package io.cytodev.freqcalc.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import io.cytodev.freqcalc.R;
import io.cytodev.freqcalc.activities.MainActivity;

/**
 * io.cytodev.freqcalc.fragments "Frequency Calculator"
 * 2016/01/25 @ 12:53
 *
 * @author Roel Walraven <mail@cytodev.io>
 */
public class TapFragment extends Fragment {
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
        final MainActivity mainActivity = (MainActivity) getActivity();
        final Button       tb           = tap.findViewById(R.id.freq_input_tap);

        final View.OnClickListener tc = new View.OnClickListener() {
            Long endTime   = null;
            Long startTime = null;

            @Override
            public void onClick(View v) {
                if(clickSwitch) {
                    endTime   = System.currentTimeMillis();

                    if (mainActivity != null) {
                        mainActivity.freqcalc.tap(MainActivity.getAverage(), MainActivity.getAverageTaps(), startTime, endTime);
                        mainActivity.updateValues("");
                    }

                    startTime = System.currentTimeMillis();
                } else {
                    startTime   = System.currentTimeMillis();
                    clickSwitch = true;
                }
            }
        };
        final View.OnLongClickListener tlc = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mainActivity != null)
                    mainActivity.freqcalc.reset();

                clickSwitch = false;

                if(mainActivity != null) {
                    mainActivity.updateValues("");

                    Vibrator vb = (Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE);

                    if (vb != null)
                        vb.vibrate(50);
                }

                Toast.makeText(getActivity(), R.string.freq_input_tap_cleared, Toast.LENGTH_SHORT).show();

                return false;
            }
        };

        tb.setOnClickListener(tc);

        if(MainActivity.getAverage() && MainActivity.getAverageTaps() == -1) {
            tb.setOnLongClickListener(tlc);
        }
    }

}
