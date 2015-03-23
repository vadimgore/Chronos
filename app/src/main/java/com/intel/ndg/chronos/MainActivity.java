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

    private Intent mBleshIntent;
    private Intent mUserProfileIntent;
    private Intent mCloudSettingsIntent;

    private static final String TAG = "Main Activity:";
    private static final int REQUEST_ENABLE_BT = 0;

    private UUID mConsumerID;
    private StyleConcierge mStyleConcierge;

    private static final String BACKEND_IP_ADDRESS = "http://52.10.19.66";
    private static final String BACKEND_PORT = "8080";
    private static final String AD_SERVER_IP_ADDRESS = "http://54.221.209.27";
    private static final String AD_SERVER_PORT = "4242";

    private static final String CONCIERGE_API = "/concierge";
    private static final String CONSUMER_API = "/consumer";
    private static final String PROFILE_API = "/profile";
    private static final String OFFERS_API = "/ad";

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
                mCloudSettingsIntent = new Intent(this, CloudSettingsActivity.class);
                mCloudSettingsIntent.putExtra("@string/ip_address", BACKEND_IP_ADDRESS);
                mCloudSettingsIntent.putExtra("@string/port", BACKEND_PORT);
                mCloudSettingsIntent.putExtra("@string/ad_server_ip_address", AD_SERVER_IP_ADDRESS);
                mCloudSettingsIntent.putExtra("@string/ad_server_port", AD_SERVER_PORT);
                startActivity(mCloudSettingsIntent);
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
        mBleshIntent = new Intent(this, Blesh.class);
        mBleshIntent.putExtra("APIUser", "intel");
        mBleshIntent.putExtra("APIKey", "RjKqGrNxEs");
        mBleshIntent.putExtra("integrationType", "M");
        mBleshIntent.putExtra("integrationId", "5033171751");
        mBleshIntent.putExtra("pushToken", "");
        mBleshIntent.putExtra("optionalKey", "5033171751");

        // Start the Blesh service using the bundle you have just created
        startService(mBleshIntent);
    }

    private void stopBlesh() {
        Log.i(TAG, "stopBlesh");
        stopService(mBleshIntent);
    }

    private void editProfile() {
        Log.i(TAG, "editProfile");
        mUserProfileIntent = new Intent(this, UserProfileActivity.class);
        mUserProfileIntent.putExtra("@string/consumer_id", mConsumerID.toString());
        mUserProfileIntent.putExtra("@string/ip_address", BACKEND_IP_ADDRESS);
        mUserProfileIntent.putExtra("@string/port", BACKEND_PORT);
        mUserProfileIntent.putExtra("@string/consumer_api", CONSUMER_API);
        startActivity(mUserProfileIntent);
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
                        .setAutoCancel(true)
                        .setSound(sound);

        // Add voice reply action to notification
        String replyLabel = getResources().getString(R.string.concierge_action_label);
        String[] replyChoices = getResources().getStringArray(R.array.concierge_choices);
        RemoteInput remoteInput = new RemoteInput.Builder("@string/extra_concierge")
                .setLabel(replyLabel)
                .setChoices(replyChoices)
                .build();

        // Create an intent for the concierge action
        Intent conciergeIntent = new Intent(this, BeaconNotificationHandler.class);
        conciergeIntent.putExtra("@string/consumer_id", mConsumerID.toString());
        conciergeIntent.putExtra("@string/concierge_id", mStyleConcierge.getID());
        conciergeIntent.putExtra("@string/ip_address", BACKEND_IP_ADDRESS);
        conciergeIntent.putExtra("@string/port", BACKEND_PORT);
        conciergeIntent.putExtra("@string/concierge_api", CONCIERGE_API);
        conciergeIntent.putExtra("@string/profile_api", PROFILE_API);
        PendingIntent conciergePendingIntent =
                PendingIntent.getActivity(this, 0, conciergeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the concierge action and add the remote input
        NotificationCompat.Action conciergeAction =
                new NotificationCompat.Action.Builder(R.drawable.concierge,
                        getString(R.string.concierge_action_label), conciergePendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        // Create an intent for editing profile
        Intent editProfileIntent = new Intent(this, UserProfileActivity.class);
        editProfileIntent.putExtra("@string/consumer_id", mConsumerID.toString());
        editProfileIntent.putExtra("@string/ip_address", BACKEND_IP_ADDRESS);
        editProfileIntent.putExtra("@string/port", BACKEND_PORT);
        editProfileIntent.putExtra("@string/consumer_api", CONSUMER_API);

        PendingIntent editProfilePendingIntent =
                PendingIntent.getActivity(this, 0, editProfileIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create edit profile action
        NotificationCompat.Action editProfileAction =
                new NotificationCompat.Action.Builder(R.drawable.profile,
                        getString(R.string.edit_profile_action_label), editProfilePendingIntent)
                        .build();

        // Create an intent for quick profile sharing
        Intent shareProfileIntent = new Intent(this, PersonalGuidanceActivity.class);
        shareProfileIntent.putExtra("@string/quick_share", "true");
        shareProfileIntent.putExtra("@string/consumer_id", mConsumerID.toString());
        shareProfileIntent.putExtra("@string/concierge_id", mStyleConcierge.getID());
        shareProfileIntent.putExtra("@string/ip_address", BACKEND_IP_ADDRESS);
        shareProfileIntent.putExtra("@string/port", BACKEND_PORT);
        shareProfileIntent.putExtra("@string/profile_api", PROFILE_API);

        PendingIntent shareProfilePendingIntent =
                PendingIntent.getActivity(this, 0, shareProfileIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create quick profile sharing action
        NotificationCompat.Action shareProfileAction =
                new NotificationCompat.Action.Builder(R.drawable.share,
                        getString(R.string.share_profile_action_label), shareProfilePendingIntent)
                        .build();

        // Create an intent for special offers
        Intent specialOffersIntent = new Intent(this, SpecialOffersActivity.class);
        specialOffersIntent.putExtra("@string/consumer_id", mConsumerID.toString());
        specialOffersIntent.putExtra("@string/concierge_id", mStyleConcierge.getID());
        specialOffersIntent.putExtra("@string/ad_server_ip_address", AD_SERVER_IP_ADDRESS);
        specialOffersIntent.putExtra("@string/ad_server_port", AD_SERVER_PORT);
        specialOffersIntent.putExtra("@string/ad_server_api", OFFERS_API);

        PendingIntent specialOffersPendingIntent =
                PendingIntent.getActivity(this, 0, specialOffersIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create quick profile sharing action
        NotificationCompat.Action specialOffersAction =
                new NotificationCompat.Action.Builder(R.drawable.special_offers,
                        getString(R.string.special_offers_action_label), specialOffersPendingIntent)
                        .build();

        // Create wearable extender for wearable specific actions
        NotificationCompat.WearableExtender wearExtender = new NotificationCompat.WearableExtender();
        wearExtender.addAction(conciergeAction);
        wearExtender.addAction(shareProfileAction);        
        wearExtender.addAction(editProfileAction);
        wearExtender.addAction(specialOffersAction);
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
