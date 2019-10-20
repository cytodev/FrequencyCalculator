package io.cytodev.freqcalc.logic;

import android.util.Log;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * io.cytodev.freqcalc.logic "Frequency Calculator"
 * 2016/01/25 @ 08:53
 *
 * @author Roel Walraven <mail@cytodev.io>
 *
 *     FrequencyCalculator built to calculate time and frequencies. Also comes with a tap function.
 */
public class FrequencyCalculator {
    private static final String TAG = FrequencyCalculator.class.getSimpleName();

    private double freq__hz = 0.000;
    private double freq_bph = 0.000;
    private double freq_bpm = 0.000;
    private double freq_bps = 0.000;
    private double freq__tm = 0.000;
    private double freq__ts = 0.000;
    private double freq_tms = 0.000;
    private double tap__tms = 0.000;
    private double tap_avrg = 0.000;
    private int    decimals = 3;
    private int    numTaps  = 0;

    /**
     * Gets the current frequency
     *
     * @param identifier the variable to get
     * @return double value of the variable
     */
    public double getFreq(String identifier) {
        Log.d(TAG, "Getting " + identifier);

        switch(identifier) {
            case "hz":
                return this.freq__hz;
            case "bph":
                return this.freq_bph;
            case "bpm":
                return this.freq_bpm;
            case "bps":
                return this.freq_bps;
            case "tm":
                return this.freq__tm;
            case "ts":
                return this.freq__ts;
            case "tms":
                return this.freq_tms;
            default:
                return 0.000;
        }
    }

    /**
     * Sets the current frequency
     *
     * @param identifier the variable to set
     * @param value the value to set it to
     */
    private void setFreq(String identifier, double value) {
        Log.d(TAG, "Setting " + identifier);

        switch(identifier) {
            case "hz":
                this.freq__hz = value;
                break;
            case "bph":
                this.freq_bph = value;
                break;
            case "bpm":
                this.freq_bpm = value;
                break;
            case "bps":
                this.freq_bps = value;
                break;
            case "tm":
                this.freq__tm = value;
                break;
            case "ts":
                this.freq__ts = value;
                break;
            case "tms":
                this.freq_tms = value;
                break;
            default:
                break;
        }
    }

    /**
     * Sets the decimals variable
     *
     * @param decimals int value from 0 to 2147483647
     */
    public void setDecimals(int decimals) {
        Log.d(TAG, String.format("Setting decimals to %d", decimals));

        this.decimals = decimals;
    }

    /**
     * Calculates all values instead of just one by the following calculateX methods
     *
     * @param identifier the known variable
     * @param value      the double value of the known variable
     */
    public void calculate(String identifier, double value) {
        switch(identifier) {
            case "hz":
                setFreq("hz",  value);
                setFreq("bps", getFreq("hz"));
                setFreq("bpm", calculateBeatsPerMinute(getFreq("bps"), 0.000));
                setFreq("bph", calculateBeatsPerHour(getFreq("bpm")));
                setFreq("tms", calculateMilliseconds(getFreq("hz"), 0.000));
                setFreq("ts",  calculateSeconds(getFreq("tms"), 0.000));
                setFreq("tm",  calculateMinutes(getFreq("ts")));
                break;
            case "bps":
                setFreq("bps", value);
                setFreq("hz",  getFreq("bps"));
                setFreq("bpm", calculateBeatsPerMinute(getFreq("bps"), 0.000));
                setFreq("bph", calculateBeatsPerHour(getFreq("bpm")));
                setFreq("tms", calculateMilliseconds(getFreq("hz"), 0.000));
                setFreq("ts",  calculateSeconds(getFreq("tms"), 0.000));
                setFreq("tm",  calculateMinutes(getFreq("ts")));
                break;
            case "bpm":
                setFreq("bpm", value);
                setFreq("hz",  calculateHertz(0.000, getFreq("bpm")));
                setFreq("bps", getFreq("hz"));
                setFreq("bph", calculateBeatsPerHour(getFreq("bpm")));
                setFreq("tms", calculateMilliseconds(getFreq("hz"), 0.000));
                setFreq("ts",  calculateSeconds(getFreq("tms"), 0.000));
                setFreq("tm",  calculateMinutes(getFreq("ts")));
                break;
            case "bph":
                setFreq("bph", value);
                setFreq("bpm", calculateBeatsPerMinute(0.000, getFreq("bph")));
                setFreq("hz",  calculateHertz(0.000, getFreq("bpm")));
                setFreq("bps", getFreq("hz"));
                setFreq("tms", calculateMilliseconds(getFreq("hz"), 0.000));
                setFreq("ts",  calculateSeconds(getFreq("tms"), 0.000));
                setFreq("tm",  calculateMinutes(getFreq("ts")));
                break;
            case "tms":
                setFreq("tms", value);
                setFreq("ts",  calculateSeconds(getFreq("tms"), 0.000));
                setFreq("tm",  calculateMinutes(getFreq("ts")));
                setFreq("hz",  calculateHertz(getFreq("tms"), 0.000));
                setFreq("bps", getFreq("hz"));
                setFreq("bpm", calculateBeatsPerMinute(getFreq("bps"), 0.000));
                setFreq("bph", calculateBeatsPerHour(getFreq("bpm")));
                break;
            case "ts":
                setFreq("ts",  value);
                setFreq("tm",  calculateMinutes(getFreq("ts")));
                setFreq("tms", calculateMilliseconds(0.000, getFreq("ts")));
                setFreq("hz",  calculateHertz(getFreq("tms"), 0.000));
                setFreq("bps", getFreq("hz"));
                setFreq("bpm", calculateBeatsPerMinute(getFreq("bps"), 0.000));
                setFreq("bph", calculateBeatsPerHour(getFreq("bpm")));
                break;
            case "tm":
                setFreq("tm",  value);
                setFreq("ts",  calculateSeconds(0.000, getFreq("tm")));
                setFreq("tms", calculateMilliseconds(0.000, getFreq("ts")));
                setFreq("hz",  calculateHertz(getFreq("tms"), 0.000));
                setFreq("bps", getFreq("hz"));
                setFreq("bpm", calculateBeatsPerMinute(getFreq("bps"), 0.000));
                setFreq("bph", calculateBeatsPerHour(getFreq("bpm")));
                break;
            default:
                Log.w(TAG, identifier+" is not a valid identifier");
                break;
        }
    }

    /**
     * Calculates the hz value based on either time in milliseconds or beats per minutes
     *
     * @param tms double time in milliseconds
     * @param bpm double beats per minutes
     * @return double hz
     */
    private double calculateHertz(double tms, double bpm) {
        if(tms != 0.000) {
            return 1000 / tms;
        } else if(bpm != 0.000) {
            return bpm / 60;
        } else {
            return 0.000;
        }
    }

    /**
     * Calculates the bpm value based on either beats per second or beats per hour
     *
     * @param bps double beats per second
     * @param bph double beats per hour
     * @return double bpm
     */
    private double calculateBeatsPerMinute(double bps, double bph) {
        if(bps != 0.000) {
            return bps * 60;
        } else if(bph != 0.000) {
            return bph / 60;
        } else {
            return 0.000;
        }
    }

    /**
     * Calculates the bph value based on beats per minute
     *
     * @param bpm double beats per minute
     * @return double bph
     */
    private double calculateBeatsPerHour(double bpm) {
        if(bpm != 0.000) {
            return bpm * 60;
        } else {
            return 0.000;
        }
    }

    /**
     * Calculates the tm value based on time in seconds
     *
     * @param ts double time in seconds
     * @return double tms
     */
    private double calculateMinutes(double ts) {
        if(ts != 0.000) {
            return ts / 60;
        } else {
            return 0.000;
        }
    }

    /**
     * Calculates the ts value based on either time in milliseconds or time in minutes
     *
     * @param tms double time in milliseconds
     * @param tm double time in minutes
     * @return double ts
     */
    private double calculateSeconds(double tms, double tm) {
        if(tms != 0.000) {
            return tms / 1000;
        } else if(tm != 0.000) {
            return tm * 60;
        } else {
            return 0.000;
        }
    }

    /**
     * Calculates the tms value based on either hertz or time in seconds
     *
     * @param hz double hertz
     * @param ts double time in seconds
     * @return double tms
     */
    private double calculateMilliseconds(double hz, double ts) {
        if(hz != 0.000) {
            return 1000 / hz;
        } else if(ts != 0.000) {
            return ts * 1000;
        } else {
            return 0.000;
        }
    }

    /**
     * Clips the decimals of a value to the number of decimals defined by setDecimals
     *
     * @param number double value to clip
     * @return double value with clipped decimal places
     */
    public String clipDecimals(double number) {
        char[] positions = new char[this.decimals];
        Arrays.fill(positions, '#');

        if(number != 0) {
            DecimalFormat df = new DecimalFormat("#." + new String(positions));
            df.setRoundingMode(RoundingMode.CEILING);
            return df.format(number);
        }

        return "0";
    }

    /**
     * Calculates an average between time in between taps. Works about as good as a bpm counter on a pioneer DJ controller... If you have a steady tap.
     *
     * @param average     set to true to take an average of taps instead of the distance between two taps
     * @param averageTaps an int value of the number of taps to take an average from
     * @param startTime   the time the first tap was placed as a Long
     * @param endTime     the time the last tap was places as a Long
     */
    public void tap(boolean average, int averageTaps, final Long startTime, final Long endTime) {
        if(startTime != null && endTime != null) {
            if(average) {
                if(this.numTaps == averageTaps) {
                    this.numTaps  = 0;
                    this.tap__tms = 0.000;
                    this.tap_avrg = 0.000;
                } else {
                    this.numTaps++;
                    this.freq_tms = endTime - startTime;
                    this.tap__tms += this.freq_tms;
                    this.tap_avrg = this.tap__tms / this.numTaps;
                }
            }

            if(this.tap_avrg != 0.000) {
                this.freq_tms = this.tap_avrg;
            } else {
                this.freq_tms = endTime - startTime;
            }

            this.freq__ts = this.calculateSeconds(this.freq_tms, 0.000);
            this.freq__tm = this.calculateMinutes(this.freq__ts);
            this.freq__hz = this.calculateHertz(this.freq_tms, 0.000);
            this.freq_bps = this.freq__hz;
            this.freq_bpm = this.calculateBeatsPerMinute(this.freq_bps, 0.000);
            this.freq_bph = this.calculateBeatsPerHour(this.freq_bpm);
        }
    }

    /**
     * Resets all but the decimal values to zero.
     */
    public void reset() {
        this.freq__hz = 0.000;
        this.freq_bph = 0.000;
        this.freq_bpm = 0.000;
        this.freq_bps = 0.000;
        this.freq__tm = 0.000;
        this.freq__ts = 0.000;
        this.freq_tms = 0.000;
        this.tap__tms = 0.000;
        this.tap_avrg = 0.000;
        this.numTaps  = 0;
    }

}
