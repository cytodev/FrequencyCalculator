package com.cytodev.freqalc.logic;

import android.util.Log;

/**
 * com.cytodev.freqalc.logic "Frequency Calculator"
 * 2016/01/25 @ 08:53
 *
 * @author Roel Walraven <cytodev@gmail.com>
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
                return this.freq__hz; // hertz
            case "bph":
                return this.freq_bph; // beats per hour
            case "bpm":
                return this.freq_bpm; // beats per minute
            case "bps":
                return this.freq_bps; // beats per second (aka hertz)
            case "tm":
                return this.freq__tm; // time in minutes
            case "ts":
                return this.freq__ts; // time in seconds
            case "tms":
                return this.freq_tms; // time in miliseconds
            default:
                return 0.000; // nothing
        }
    }

    /**
     * Sets the current frequency
     *
     * @param identifier the variable to set
     * @param value the value to set it to
     */
    public void setFreq(String identifier, double value) {
        Log.d(TAG, "Setting " + identifier);

        switch(identifier) {
            case "hz":
                this.freq__hz = value;
                break; // hertz
            case "bph":
                this.freq_bph = value;
                break; // beats per hour
            case "bpm":
                this.freq_bpm = value;
                break; // beats per minute
            case "bps":
                this.freq_bps = value;
                break; // beats per second (aka hertz)
            case "tm":
                this.freq__tm = value;
                break; // time in minutes
            case "ts":
                this.freq__ts = value;
                break; // time in seconds
            case "tms":
                this.freq_tms = value;
                break; // time in miliseconds
            default:
                break; // nothing
        }
    }

    /**
     * Sets the decimals variable
     *
     * @param decimals int value from 0 to 2147483647
     */
    public void setDecimals(int decimals) {
        Log.d(TAG, "Setting decimals to " + Integer.toString(decimals));

        this.decimals = decimals;
    }

    /**
     * Calculates the hz value based on either time in milliseconds or beats per minutes
     *
     * @param tms double time in milliseconds
     * @param bpm double beats per minutes
     * @return double hz
     */
    public double calculateHertz(double tms, double bpm) {
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
    public double calculateBeatsPerMinute(double bps, double bph) {
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
    public double calculateBeatsPerHour(double bpm) {
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
    public double calculateMinutes(double ts) {
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
    public double calculateSeconds(double tms, double tm) {
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
    public double calculateMiliseconds(double hz, double ts) {
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
    public double clipDecimals(double number) {
        double power = Math.pow(10, this.decimals);
        return (double) Math.round(number * power) / power;
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
