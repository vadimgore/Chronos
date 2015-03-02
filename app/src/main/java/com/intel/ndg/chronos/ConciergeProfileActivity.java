package com.intel.ndg.chronos;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;


public class ConciergeProfileActivity extends ActionBarActivity {

    private static final String TAG = "ConciergeProfileActivity";

    TextView mHttpResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concierge_profile);

        mHttpResponse = (TextView) findViewById(R.id.http_response);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_concierge_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getConciergeProfile() {
        // Extract Concierge ID from Intent
        Intent intent = getIntent();
        String conciergeID = intent.getStringExtra("@string/extra_concierge_id");
        String httpURL = intent.getStringExtra("@string/ip_address") + ":" +
                intent.getStringExtra("@string/port") + intent.getStringExtra("@string/api");

        Log.i(TAG, "Concierge ID =" + conciergeID);

        String httpResponse = "";
        try {
            // Get full Concierge profile from iFashion
            httpResponse = new HttpGetter().execute(httpURL, "id=" + conciergeID).get();

        } catch (InterruptedException e) {
            // Handle exception
            Log.i(TAG, "InterruptedException:" + e.getMessage());
        } catch (ExecutionException e) {
            // Handle exception
            Log.i(TAG, "ExecutionException:" + e.getMessage());
        }

        Log.i(TAG, "HTTP Response =" + httpResponse);

        mHttpResponse.setText(httpResponse);
    }
}
