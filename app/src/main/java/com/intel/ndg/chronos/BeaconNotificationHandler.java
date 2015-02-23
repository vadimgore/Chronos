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
    private static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private static final String TAG = "NotificationHandler";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String response = getMessageText(this.getIntent());
        if (response != null) {
            Log.i(TAG, response);
            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            //finish();
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
}
