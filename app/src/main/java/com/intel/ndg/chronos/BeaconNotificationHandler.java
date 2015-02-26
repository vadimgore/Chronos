package com.intel.ndg.chronos;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.RemoteInput;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Vadim on 2/20/2015.
 */
public class BeaconNotificationHandler extends Activity {

    // Key for the string that's delivered in the action's intent
    private static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private static final String TAG = "NotificationHandler";

    StyleAnalytics mStyleAnalytics = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create style analytics
        mStyleAnalytics = createStyleAnalytics();

        String response = getMessageText(this.getIntent());
        if (response != null && mStyleAnalytics != null) {
            Log.i(TAG, response);
            Log.i(TAG, "User Style Score = " + mStyleAnalytics.getStyleScore());
            Log.i(TAG, "User Budget Score = " + mStyleAnalytics.getBudgetScore());
            Log.i(TAG, "Product Recommendation = " + mStyleAnalytics.getProductRecommendation());
            Log.i(TAG, "Favorite Sports = " + mStyleAnalytics.getFavSports());
            Log.i(TAG, "Favorite Drinks = " + mStyleAnalytics.getFavDrinks());

            //finish();
            Toast.makeText(getApplicationContext(), "You requested " + response, Toast.LENGTH_LONG).show();

            if (response.equalsIgnoreCase(getResources().getText(R.string.voice_reply_choice_1).toString())) {
                Toast.makeText(getApplicationContext(),
                        "Please don't hesistate to contact one of our Style Concierges",
                        Toast.LENGTH_LONG).show();
            } else if (response.equalsIgnoreCase(getResources().getText(R.string.voice_reply_choice_2).toString())) {
                Toast.makeText(getApplicationContext(),
                        "One of our Style Concierges will be with you momentarily",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "We cannot handle this request at this time",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
    /**
     * Obtain the intent that started this activity by calling
     * Activity.getIntent() and pass it into this method to
     * get the associated voice input string.
     */

    private String getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(EXTRA_VOICE_REPLY).toString();
        }
        return null;
    }

    private StyleAnalytics createStyleAnalytics() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!sharedPref.contains("seekBarGoals1"))
                // No shared preferences exist
                return null;

        int [] seekBarState = {
                sharedPref.getInt("seekBarGoals1", 0),
                sharedPref.getInt("seekBarGoals2", 0),
                sharedPref.getInt("seekBarStyle1", 0),
                sharedPref.getInt("seekBarStyle2", 0),
                sharedPref.getInt("seekBarBudget", 0),
                sharedPref.getInt("seekBarUsage", 0),
                sharedPref.getInt("seekBarWrist", 0),
        };

        Timepiece timepiece = new Timepiece(getApplicationContext());
        FavoriteSport favSport = new FavoriteSport(getApplicationContext());
        FavoriteDrink favDrink = new FavoriteDrink(getApplicationContext());

        return new StyleAnalytics(
                        seekBarState[0],
                        seekBarState[1],
                        seekBarState[2],
                        seekBarState[3],
                        seekBarState[4],
                        seekBarState[5],
                        seekBarState[6],
                        timepiece,
                        favSport,
                        favDrink );
    }
}
