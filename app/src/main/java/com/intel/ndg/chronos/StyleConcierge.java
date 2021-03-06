package com.intel.ndg.chronos;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vadim on 2/24/2015.
 */
public class StyleConcierge {
    private final static String TAG = "StyleConcierge";

    private String mID;

    StyleConcierge(String profile) {

        try {
            JSONObject jsonObj = new JSONObject(new JSONObject(profile).getString("associate"));
            if (jsonObj != null) {
                mID = jsonObj.getString("uuid");
            }
        }
        catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }


    public String getID() {
        return mID;
    }
}
