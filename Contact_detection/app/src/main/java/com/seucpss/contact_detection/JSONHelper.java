package com.seucpss.contact_detection;

import org.json.JSONArray;
import org.json.JSONException;

public class JSONHelper {
    static public JSONArray joinJSONArray(JSONArray array1, JSONArray array2) {
        JSONArray finalJSONArray = array1;
        for (int i = 0; i < array2.length(); i++) {
            try {
                finalJSONArray.put(array2.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return finalJSONArray;
    }
}
