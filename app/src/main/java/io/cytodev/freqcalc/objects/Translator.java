package io.cytodev.freqcalc.objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * io.cytodev.freqcalc.objects "FrequencyCalculator"
 * 2016/06/15 @ 17:18
 *
 * @author Roel Walraven <mail@cytodev.io>
 */
public class Translator {
    public String language;
    public String name;
    public String url;

    private Translator(JSONObject object) {
        try {
            this.language = object.getString("language");
            this.name     = object.getString("name");
            this.url      = object.getString("url");
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Translator> fromJson(JSONArray jsonObjects) {
        ArrayList<Translator> translators = new ArrayList<>();

        for(int i = 0; i < jsonObjects.length(); i++) {
            try {
                translators.add(new Translator(jsonObjects.getJSONObject(i)));
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }

        return translators;
    }
}
