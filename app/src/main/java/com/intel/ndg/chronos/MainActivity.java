package com.intel.ndg.chronos;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
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
                if (!actionType.isEmpty() && !actionValue.isEmpty()) {
                    Log.i(TAG, "bleshTemplateResultCallback: action type:" + actionType + " value: " + actionValue);
                    // Check for the action type and value you want to use
                    // You may wish to load a web
                    //String msg = "bleshTemplateResultCallback: action type:" + actionType + " value: " + actionValue;
                    //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    if (actionType.equals("URL")) {
                        // ADD ACTION HERE
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
        startBlesh();
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
}
