package com.intel.ndg.chronos;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class StyleAnalytics {

    private static String TAG = "StyleAnalytics";

    enum Range {
        LOW,
        LOW_MEDIUM,
        MEDIUM,
        MEDIUM_HIGH,
        HIGH
    }

    enum WristSize {
        NARROW,
        AVERAGE,
        WIDE
    }

    private Range mCoolness;
    private Range mVisibility;
    private Range mImpression;
    private Range mConvenience;
    private Range mBudget;
    private WristSize mWristSize;
    private Timepiece mTimepiece;

    private int mStyleScore = 0;

    StyleAnalytics(int coolness, int visibility, int impression, int convenience,
                   int budget, int wristSize, Timepiece timepiece) {

        mCoolness = Range.values()[coolness];
        mVisibility = Range.values()[visibility];
        mImpression = Range.values()[impression];
        mConvenience = Range.values()[convenience];
        mBudget = Range.values()[budget];
        mWristSize = WristSize.values()[wristSize];
        mTimepiece = timepiece;

        buildStyleScore();
    }

    private void buildStyleScore() {
        mStyleScore = mCoolness.ordinal() + mVisibility.ordinal() +
                mImpression.ordinal() + mConvenience.ordinal();

    }

    public int getStyleScore() {
        return mStyleScore;
    }

    public int getBudgetScore() {
        return mBudget.ordinal();
    }

    public String getProductRecommendation() {
        JSONObject jsonProdRec = new JSONObject();
        try {
            jsonProdRec.put("collection", mTimepiece.getCollection().name());
            jsonProdRec.put("shape", mTimepiece.getShape().name());
            jsonProdRec.put("type", mTimepiece.getType().name());
            jsonProdRec.put("strap", mTimepiece.getStrap().name());
            jsonProdRec.put("wrist", mWristSize.name());

        } catch (JSONException e) {
            Log.i(TAG, "JSONException: " + e.getMessage());
        }

        return jsonProdRec.toString();
    }
}
