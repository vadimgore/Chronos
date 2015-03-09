package com.intel.ndg.chronos;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.blesh.sdk.classes.Blesh;
import com.blesh.sdk.classes.BleshInstance;
import com.blesh.sdk.models.BleshTemplateResult;

import java.util.UUID;


public class MainActivity extends ActionBarActivity {

    private Intent mBlesh;
    private Intent mUserProfile;
    private Intent mCloudSettings;

    private static final String TAG = "Main Activity:";
    private static final int REQUEST_ENABLE_BT = 0;

    private UUID mConsumerID;
    private StyleConcierge mStyleConcierge;

    private static final String IFASHION_IP_ADDRESS = "http://52.10.19.66";
    private static final String IFASHION_PORT = "8080";
    private static final String IFASHION_CONCIERGE_API = "/concierge";
    private static final String IFASHION_CONSUMER_API = "/consumer";
    private static final String IFASHION_PROFILE_API = "/profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createConsumerID();

        enableBluetooth();

        // Define a callback reference to be used by the Blesh service
        // in order to push the user's action results to your application
        BleshTemplateResult result = new BleshTemplateResult() {
            @Override
            public void bleshTemplateResultCallback(String actionType, String actionValue) {
                if (actionType!=null && actionValue!=null && !actionType.isEmpty() && !actionValue.isEmpty()) {
                    Log.i(TAG, "bleshTemplateResultCallback: action type:" + actionType + " value: " + actionValue);
                    // Check for the action type and value you want to use
                    // You may wish to load a web
                    //String msg = "bleshTemplateResultCallback: action type:" + actionType + " value: " + actionValue;
                    //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    if (actionType.equals("URL")) {
                        // ADD ACTION HERE
                        // build style concierge class and initiate with actionValue
                        mStyleConcierge = new StyleConcierge(actionValue);
                        buildNotification();
                    }
                }
                else {
                    Log.i(TAG, "bleshTemplateResultCallback: empty action type or value");
                    Toast.makeText(getApplicationContext(), "bleshTemplateResultCallback: empty action type or value", Toast.LENGTH_LONG).show();
                }
            }
        };

        // Register Blesh callback
        BleshInstance.sharedInstance().setTemplateResult(result);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Make sure Bluetooch is enabled
        enableBluetooth();
        // Start Blesh
        startBlesh();

        /// FOR DEBUGGING TO BYPASS BLESH
        //mStyleConcierge = new StyleConcierge("{\"associate\":{\"uuid\": \"12345\"}}");
        //buildNotification();

        // Finish activity after 10 sec of inactivity
        //delayedFinish(10);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stopBlesh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_profile:
                editProfile();
                return true;
            case R.id.action_cloud_settings:
                mCloudSettings = new Intent(this, CloudSettingsActivity.class);
                mCloudSettings.putExtra("@string/ip_address", IFASHION_IP_ADDRESS);
                mCloudSettings.putExtra("@string/port", IFASHION_PORT);
                startActivity(mCloudSettings);
                return true;
            case R.id.action_beacon:
                if (mStyleConcierge == null)
                    mStyleConcierge = new StyleConcierge("{\"associate\":{\"uuid\": \"12345\"}}");
                buildNotification();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createConsumerID() {
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String consumerID = sharedPref.getString("@string/consumer_id", "");
        if (consumerID.equals("")) {
            mConsumerID = UUID.randomUUID();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("@string/consumer_id", mConsumerID.toString());
            editor.commit();
        } else {
            mConsumerID = UUID.fromString(consumerID);
        }
    }

    /**
     * Ensures Bluetooth is available on the beacon and it is enabled. If not,
     * displays a dialog requesting user permission to enable Bluetooth.
     */
    private void enableBluetooth() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void startBlesh() {
        Log.i(TAG, "startBlesh");

        // Customer specific initialization parameters
        mBlesh = new Intent(this, Blesh.class);
        mBlesh.putExtra("APIUser", "intel");
        mBlesh.putExtra("APIKey", "RjKqGrNxEs");
        mBlesh.putExtra("integrationType", "M");
        mBlesh.putExtra("integrationId", "5033171751");
        mBlesh.putExtra("pushToken", "");
        mBlesh.putExtra("optionalKey", "5033171751");

        // Start the Blesh service using the bundle you have just created
        startService(mBlesh);
    }

    private void stopBlesh() {
        Log.i(TAG, "stopBlesh");
        stopService(mBlesh);
    }

    private void editProfile() {
        Log.i(TAG, "editProfile");
        mUserProfile = new Intent(this, UserProfileActivity.class);
        mUserProfile.putExtra("@string/consumer_id", mConsumerID.toString());
        mUserProfile.putExtra("@string/ip_address", IFASHION_IP_ADDRESS);
        mUserProfile.putExtra("@string/port", IFASHION_PORT);
        mUserProfile.putExtra("@string/consumer_api", IFASHION_CONSUMER_API);
        startActivity(mUserProfile);
    }

    private void buildNotification() {
        int notificationId = 001;

        // Build intent for notification content
        Intent notificationIntent = new Intent(this, BeaconNotificationHandler.class);

        PendingIntent notificationPendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        long[] vibPattern = {500, 500, 500, 500, 500};
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.chronos)
                        .setContentTitle(getString(R.string.title_notification))
                        .setContentText(getString(R.string.content_notification))
                        .setContentIntent(notificationPendingIntent)
                        .setVibrate(vibPattern)
                        .setSound(sound);

        // Add voice reply action to notification
        String replyLabel = getResources().getString(R.string.voice_reply_action_label);
        String[] replyChoices = getResources().getStringArray(R.array.voice_reply_choices);
        RemoteInput remoteInput = new RemoteInput.Builder("@string/extra_voice_reply")
                .setLabel(replyLabel)
                .setChoices(replyChoices)
                .build();

        // Create an intent for the voice reply action
        Intent replyIntent = new Intent(this, BeaconNotificationHandler.class);
        replyIntent.putExtra("@string/consumer_id", mConsumerID.toString());
        replyIntent.putExtra("@string/concierge_id", mStyleConcierge.getID());
        replyIntent.putExtra("@string/ip_address", IFASHION_IP_ADDRESS);
        replyIntent.putExtra("@string/port", IFASHION_PORT);
        replyIntent.putExtra("@string/concierge_api", IFASHION_CONCIERGE_API);
        replyIntent.putExtra("@string/profile_api", IFASHION_PROFILE_API);
        PendingIntent replyPendingIntent =
                PendingIntent.getActivity(this, 0, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the reply action and add the remote input
        NotificationCompat.Action replyAction =
                new NotificationCompat.Action.Builder(R.drawable.concierge,
                        getString(R.string.voice_reply_action_label), replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        // Create an intent for editing profile
        Intent editProfileIntent = new Intent(this, UserProfileActivity.class);
        editProfileIntent.putExtra("@string/consumer_id", mConsumerID.toString());
        editProfileIntent.putExtra("@string/ip_address", IFASHION_IP_ADDRESS);
        editProfileIntent.putExtra("@string/port", IFASHION_PORT);
        editProfileIntent.putExtra("@string/consumer_api", IFASHION_CONSUMER_API);

        PendingIntent editProfilePendingIntent =
                PendingIntent.getActivity(this, 0, editProfileIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create edit profile action
        NotificationCompat.Action editProfileAction =
                new NotificationCompat.Action.Builder(R.drawable.profile,
                        getString(R.string.edit_profile_action_label), editProfilePendingIntent)
                        .build();

        // Create wearable extender for wearable specific actions
        NotificationCompat.WearableExtender wearExtender = new NotificationCompat.WearableExtender();
        wearExtender.addAction(replyAction);
        wearExtender.addAction(editProfileAction);
        Bitmap bkGround = BitmapFactory.decodeResource(getResources(), R.drawable.ringbell);
        wearExtender.setBackground(bkGround);

        notificationBuilder.extend(wearExtender);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private void delayedFinish(int timeout) {
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                finish();
            }
        };

        handler.postDelayed(r, timeout*1000);
    }
}
