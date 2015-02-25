package com.intel.ndg.chronos;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
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


public class MainActivity extends ActionBarActivity {

    private Intent mBlesh = null;
    private Intent mUserProfile = null;
    private static final String TAG = "Main Activity:";
    private static final int REQUEST_ENABLE_BT = 0;
    private StyleConcierge mStyleConcierge;

    // Key for the string that's delivered in the action's intent
    private static final String EXTRA_VOICE_REPLY = "extra_voice_reply";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Finish activity after 10 sec of inactivity
        //delayedFinish(10);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopBlesh();
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
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        startActivity(mUserProfile);
    }

    private void buildNotification() {
        int notificationId = 001;

        // Build intent for notification content
        Intent notificationIntent = new Intent(this, BeaconNotificationHandler.class);
        //viewIntent.putExtra(EXTRA_EVENT_ID, eventId);
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
        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
                .setLabel(replyLabel)
                .setChoices(replyChoices)
                .build();

        // Create an intent for the voice reply action
        Intent replyIntent = new Intent(this, BeaconNotificationHandler.class);
        PendingIntent replyPendingIntent =
                PendingIntent.getActivity(this, 0, replyIntent, 0);

        // Create the reply action and add the remote input
        NotificationCompat.Action replyAction =
                new NotificationCompat.Action.Builder(R.drawable.concierge,
                        getString(R.string.voice_reply_action_label), replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        // Create an intent for editing profile
        Intent editProfileIntent = new Intent(this, UserProfileActivity.class);
        PendingIntent editProfilePendingIntent =
                PendingIntent.getActivity(this, 0, editProfileIntent, 0);

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
