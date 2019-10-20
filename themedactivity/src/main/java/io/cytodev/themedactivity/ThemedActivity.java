package io.cytodev.themedactivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * io.cytodev.themedactivity "ThemedActivity"
 * 2016/01/13 @ 21:01
 *
 * @author Roel Walraven <cytodev@gmail.com>
 */
public class ThemedActivity extends AppCompatActivity {
    private final static String TAG = ThemedActivity.class.getSimpleName();

    private final Map<String, Integer> lightThemes = new HashMap<>();
    private final Map<String, Integer> darkThemes  = new HashMap<>();

    private Activity             thisActivity        = null;
    private int                  currentTheme        = -1;
    private boolean              currentThemeIsLight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        thisActivity = ThemedActivity.this;
    }

    /**
     * Inserts the default themes from ThemedActivity's resources
     */
    protected void setDefaults() {
        Log.v(TAG, "Setting up default themes");

        addTheme(true,  "WhiteSmoke",      R.style.Theme_Cyto_Light_WhiteSmoke);
        addTheme(true,  "DodgerBlue",      R.style.Theme_Cyto_Light_DodgerBlue);
        addTheme(true,  "SpringBud",       R.style.Theme_Cyto_Light_SpringBud);
        addTheme(true,  "ElectricPurple",  R.style.Theme_Cyto_Light_ElectricPurple);
        addTheme(true,  "OrangePeel",      R.style.Theme_Cyto_Light_OrangePeel);
        addTheme(true,  "HollywoodCerise", R.style.Theme_Cyto_Light_HollywoodCerise);
        addTheme(true,  "SpringGreen",     R.style.Theme_Cyto_Light_SpringGreen);
        addTheme(false, "WhiteSmoke",      R.style.Theme_Cyto_Dark_WhiteSmoke);
        addTheme(false, "DodgerBlue",      R.style.Theme_Cyto_Dark_DodgerBlue);
        addTheme(false, "SpringBud",       R.style.Theme_Cyto_Dark_SpringBud);
        addTheme(false, "ElectricPurple",  R.style.Theme_Cyto_Dark_ElectricPurple);
        addTheme(false, "OrangePeel",      R.style.Theme_Cyto_Dark_OrangePeel);
        addTheme(false, "HollywoodCerise", R.style.Theme_Cyto_Dark_HollywoodCerise);
        addTheme(false, "SpringGreen",     R.style.Theme_Cyto_Dark_SpringGreen);
    }

    /**
     * Sets the current theme to the theme stored with themeName
     *
     * setting light to true will apply the light theme, else a dark theme will be used
     *
     * @param light     boolean
     * @param themeName String
     */
    protected void setTheme(boolean light, String themeName) {
        Log.d(TAG, "Setting theme to \""+((light) ? "Light" : "Dark")+themeName+"\"");

        currentTheme        = getTheme(light, themeName);
        currentThemeIsLight = light;

        super.setTheme(currentTheme);
    }

    /**
     * Gets the currently applied theme's resid
     *
     * @return resource id of the theme
     */
    private int getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Returns whether or not the current applied theme is a light theme or not
     *
     * @return true if the current theme is light, false if it's not
     */
    private boolean isCurrentThemeLight() {
        return currentThemeIsLight;
    }

    /**
     * gets the currently applied theme's name
     *
     * @return name of the theme
     */
    protected String getCurrentThemeName() {
        return getTheme(isCurrentThemeLight(), getCurrentTheme());
    }

    /**
     * Adds a theme to the themes map
     *
     * @param light      place theme in the light themes map, if set to false it will be placed in the dark theme map instead
     * @param themeName  name of the theme
     * @param themeResID resource id of the theme
     */
    private void addTheme(boolean light, String themeName, int themeResID) {
        Log.d(TAG, "Adding new theme \""+((light) ? "Light." : "Dark.")+themeName+"\"");

        if(light) {
            lightThemes.put(themeName, themeResID);
        } else {
            darkThemes.put(themeName, themeResID);
        }
    }

    /**
     * Gets the resid of theme with name themeName
     *
     * @param light     get theme from lightThemes, if set to false this method will return a theme from darkThemes
     * @param themeName the name of the theme
     * @return the resource id of the theme
     */
    private int getTheme(boolean light, String themeName) {
        Log.d(TAG, "Getting resid of \""+((light) ? "Light." : "Dark.")+themeName+"\"");

        return (light) ? lightThemes.get(themeName) : darkThemes.get(themeName);
    }

    /**
     * Gets the name of theme with resid ThemeResID
     *
     * @param light      search for a light theme. If set to false, the search will look for a dark theme
     * @param themeResID the resource id of the theme
     * @return the name of the theme
     */
    private String getTheme(boolean light, int themeResID) {
        Log.v(TAG, String.format("Searching for %s theme with resid %d", (light) ? "light" : "dark", themeResID));

        Iterator<Map.Entry<String, Integer>> data = (light) ? lightThemes.entrySet().iterator() : darkThemes.entrySet().iterator();

        while(data.hasNext()) {
            Map.Entry<String, Integer> theme = data.next();

            if(theme.getValue().equals(themeResID)) {
                Log.v(TAG, "Found theme!");
                return theme.getKey();
            }
        }

        Log.w(TAG, String.format("No %s theme with resid %d found!", (light) ? "light" : "dark", themeResID));

        return "";
    }

    /**
     * Returns all themes stored in the map
     *
     * @param light return light themes, if set to false this method will return dark themes instead
     * @return the theme map
     */
    public Map<String, Integer> getThemes(boolean light) {
        Log.v(TAG, "Getting all " + ((light) ? "light" : "dark") + " themes");

        return (light) ? lightThemes : darkThemes;
    }

    /**
     * Returns the perceived darkness of a color
     *
     * @param color integer value of a color
     * @return darkness value as double
     */
    private double calculateDarkness(int color) {
        int c = (int) Long.parseLong(Integer.toString(color), 16);

        int r = (c >> 16) & 0xFF;
        int g = (c >>  8) & 0xFF;
        int b = (c)       & 0xFF;

        return 1 - (0.299 * Color.red(r)
                 +  0.587 * Color.green(g)
                 +  0.114 * Color.blue(b)
                   )
                 / 255;
    }

    /**
     * The following table is a reference of the default themes darkness values:
     *
     * <table summary="default themes darkness values">
     *   <tr>
     *       <th>theme</th>
     *       <th>darkness value</th>
     *   </tr>
     *   <tr>
     *       <td>WhiteSmoke</td>
     *       <td>0.9065647058823529</td>
     *   </tr>
     *   <tr>
     *       <td>DodgerBlue</td>
     *       <td>0.9407647058823530</td>
     *   </tr>
     *   <tr>
     *       <td>SpringBud</td>
     *       <td>0.8953882352941176</td>
     *   </tr>
     *   <tr>
     *       <td>ElectricPurple</td>
     *       <td>0.9407647058823530</td>
     *   </tr>
     *   <tr>
     *       <td>OrangePeel</td>
     *       <td>0.8953882352941176</td>
     *   </tr>
     *   <tr>
     *       <td>HollywoodCerise</td>
     *       <td>0.9228823529411765</td>
     *   </tr>
     *   <tr>
     *       <td>SpringGreen</td>
     *       <td>0.9228823529411765</td>
     *   </tr>
     *</table>
     * @return the overall darkness value of the theme (based on colorPrimary and colorPrimaryDark)
     */
    public double getThemeDarkness() {
        TypedValue colorPrimary     = new TypedValue();
        TypedValue colorPrimaryDark = new TypedValue();

        thisActivity.getTheme().resolveAttribute(R.attr.colorPrimary, colorPrimary, true);
        thisActivity.getTheme().resolveAttribute(R.attr.colorPrimaryDark, colorPrimaryDark, true);

        double colorPrimaryDarkness     = calculateDarkness(colorPrimary.data);
        double colorPrimaryDarkDarkness = calculateDarkness(colorPrimaryDark.data);

        double darkness = ((colorPrimaryDarkness + colorPrimaryDarkDarkness) / 2); // pretty rad variable.

        Log.d(TAG, String.format("colorPrimary darkness value: %s", colorPrimaryDarkness));
        Log.d(TAG, String.format("colorPrimaryDark darkness value: %s", colorPrimaryDarkDarkness));
        Log.d(TAG, String.format("overall darkness value: %s", darkness));

        return darkness;
    }

}
