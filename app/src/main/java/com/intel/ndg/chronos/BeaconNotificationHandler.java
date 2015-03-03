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
    private static final String TAG = "NotificationHandler";

    private StyleAnalytics mStyleAnalytics = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create style analytics
        mStyleAnalytics = createStyleAnalytics();

        String userResponse = getUserResponse(this.getIntent());
        if (userResponse != null) {
            Log.i(TAG, userResponse);
            //Toast.makeText(getApplicationContext(), "You requested " + userResponse, Toast.LENGTH_LONG).show();

            if (mStyleAnalytics != null) {
                Log.i(TAG, "User Style Score = " + mStyleAnalytics.getStyleScore());
                Log.i(TAG, "User Budget Score = " + mStyleAnalytics.getBudgetScore());
                Log.i(TAG, "Product Recommendation = " + mStyleAnalytics.getProductRecommendation());
                Log.i(TAG, "Favorite Sports = " + mStyleAnalytics.getFavSports());
                Log.i(TAG, "Favorite Drinks = " + mStyleAnalytics.getFavDrinks());
            }

            Intent userResponseHandler = null;
            if (userResponse.equalsIgnoreCase(getResources().getText(R.string.voice_reply_choice_1).toString())) {
                userResponseHandler = new Intent(this, ExploreOnMyOwnActivity.class);
            } else if (userResponse.equalsIgnoreCase(getResources().getText(R.string.voice_reply_choice_2).toString())) {
                userResponseHandler = new Intent(this, PersonalGuidanceActivity.class);
            } else {
                Toast.makeText(getApplicationContext(),
                        "We cannot handle this request at this time",
                        Toast.LENGTH_LONG).show();
            }

            onUserResponse(userResponseHandler);
        }
    }

    private String getUserResponse(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence("@string/extra_voice_reply").toString();
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

    public void onUserResponse(Intent intent) {

        if (intent.equals(null)) return;

        // Start Concierge Profile Activity

        intent.putExtra("@string/extra_concierge_id",
                getIntent().getStringExtra("@string/extra_concierge_id"));
        intent.putExtra("@string/ip_address",
                getIntent().getStringExtra("@string/ip_address"));
        intent.putExtra("@string/port",
                getIntent().getStringExtra("@string/port"));
        intent.putExtra("@string/api",
                getIntent().getStringExtra("@string/api"));

        if (mStyleAnalytics != null) {
            intent.putExtra("@string/extra_style_score", mStyleAnalytics.getStyleScore());
            intent.putExtra("@string/extra_budget_score", mStyleAnalytics.getBudgetScore());
            intent.putExtra("@string/extra_fav_sports", mStyleAnalytics.getFavSports());
            intent.putExtra("@string/extra_fav_drinks", mStyleAnalytics.getFavSports());
            intent.putExtra("@string/extra_prod_rec", mStyleAnalytics.getProductRecommendation());
        }

        startActivity(intent);
    }
}
