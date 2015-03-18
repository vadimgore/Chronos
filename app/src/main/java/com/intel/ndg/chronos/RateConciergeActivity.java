package com.intel.ndg.chronos;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;


public class RateConciergeActivity extends ActionBarActivity {

    private static final String TAG = "RateConciergeActivity";

    private String mRateConciergeAPI = "/concierge_rating";

    private String miFashionIP;
    private String miFashionPort;
    private String mConsumerID;
    private String mConciergeID;

    RatingBar mRatingBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_concierge);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mConsumerID = getIntent().getExtras().getString("@string/consumer_id");
        mConciergeID = getIntent().getExtras().getString("@string/concierge_id");
        miFashionIP = getIntent().getExtras().getString("@string/ip_address");
        miFashionPort = getIntent().getExtras().getString("@string/port");

        mRatingBar = (RatingBar) findViewById(R.id.rating_bar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rate_concierge, menu);
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

    public void onRatingSubmit(View view) {
        float rating = mRatingBar.getRating();
        Log.i(TAG, "Concierge rating is " + rating + " stars");

        try {
            HttpPoster poster = new HttpPoster(this, "We will be back momentarily...");

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm");

            String result = poster.execute(
                    miFashionIP + ":" + miFashionPort + mRateConciergeAPI,
                    "consumer_id", mConsumerID,
                    "concierge_id", mConciergeID,
                    "rating", String.valueOf(rating),
                    "date", df.format(cal.getTime())
            ).get();

            Toast.makeText(getApplicationContext(), "Concierge's rating is posted successfully!",
                    Toast.LENGTH_LONG).show();

        } catch (InterruptedException e) {
            Log.i(TAG, "onDenyProfileSharing:InterruptedException: " + e.getMessage());
        } catch (ExecutionException e) {
            Log.i(TAG, "onDenyProfileSharing:ExecutionException: " + e.getMessage());
        }
    }
}
