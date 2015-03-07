package com.intel.ndg.chronos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Vadim on 2/20/2015.
 */
public class BeaconNotificationHandler extends Activity {

    // Key for the string that's delivered in the action's intent
    private static final String TAG = "NotificationHandler";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String userResponse = getUserResponse(this.getIntent());
        if (userResponse != null) {
            Log.i(TAG, userResponse);
            //Toast.makeText(getApplicationContext(), "You requested " + userResponse, Toast.LENGTH_LONG).show();

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

            userResponseHandler.putExtras(getIntent());
            startActivity(userResponseHandler);
        }
    }

    private String getUserResponse(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence("@string/extra_voice_reply").toString();
        }
        return null;
    }
}
