package com.intel.ndg.chronos;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;


public class UserProfileActivity extends ActionBarActivity {
    private static final String TAG = "User Profile Activity:";
    SeekBar mSeekBar1;
    SeekBar mSeekBar2;
    SeekBar mSeekBar3;
    SeekBar mSeekBar4;
    SeekBar mSeekBar5;
    SeekBar mSeekBar6;
    SeekBar mSeekBar7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.i(TAG, "Start User Profile Activity");
        mSeekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        mSeekBar2 = (SeekBar) findViewById(R.id.seekBar2);
        mSeekBar3 = (SeekBar) findViewById(R.id.seekBar3);
        mSeekBar4 = (SeekBar) findViewById(R.id.seekBar4);
        mSeekBar5 = (SeekBar) findViewById(R.id.seekBar5);
        mSeekBar6 = (SeekBar) findViewById(R.id.seekBar6);
        mSeekBar7 = (SeekBar) findViewById(R.id.seekBar7);

        restoreProfile();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Handle presses on the action bar items
        switch (id) {
            case R.id.save_profile:
                saveProfile();
                return true;
            case R.id.action_settings:
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveProfile() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.putInt("seekBar1", mSeekBar1.getProgress());
        editor.putInt("seekBar2", mSeekBar2.getProgress());
        editor.putInt("seekBar3", mSeekBar3.getProgress());
        editor.putInt("seekBar4", mSeekBar4.getProgress());
        editor.putInt("seekBar5", mSeekBar5.getProgress());
        editor.putInt("seekBar6", mSeekBar6.getProgress());
        editor.putInt("seekBar7", mSeekBar7.getProgress());
        editor.commit();

        Toast.makeText(getApplicationContext(), "Profile saved!", Toast.LENGTH_LONG).show();
    }

    private int[] getProfile() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int [] profile = {
                sharedPref.getInt("seekBar1", 0),
                sharedPref.getInt("seekBar2", 0),
                sharedPref.getInt("seekBar3", 0),
                sharedPref.getInt("seekBar4", 0),
                sharedPref.getInt("seekBar5", 0),
                sharedPref.getInt("seekBar6", 0),
                sharedPref.getInt("seekBar7", 0),
        };

        return profile;
    }

    private void restoreProfile() {
        int [] profile = getProfile();
        mSeekBar1.setProgress(profile[0]);
        mSeekBar2.setProgress(profile[1]);
        mSeekBar3.setProgress(profile[2]);
        mSeekBar4.setProgress(profile[3]);
        mSeekBar5.setProgress(profile[4]);
        mSeekBar6.setProgress(profile[5]);
        mSeekBar7.setProgress(profile[6]);
    }

}
