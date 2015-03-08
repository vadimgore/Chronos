package com.intel.ndg.chronos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class PersonalGuidanceActivity extends ActionBarActivity {

    private static final String TAG = "PersonalGuidance";

    ImageView mConciergePhoto;
    ImageView mEnglish;
    ImageView mFrench;
    ImageView mSpanish;
    ImageView mGerman;

    TextView mHttpReqStatus;

    TextView mConciergeName;
    TextView mConciergeTitle;
    TextView mConciergeSpecialties;
    RatingBar mConciergeRating;
    TextView mConciergeReviews;

    LinearLayout mProfileSharingLayout;
    TextView mShareProfileReq;
    EditText mProfileSharingTimePicker;

    JSONObject mJSONObj;

    String miFashionIP;
    String miFashionPort;
    String miFashionAPI;
    String mConsumerID;
    String mConciergeID;

    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_guidance);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mConciergePhoto = (ImageView) findViewById(R.id.concierge_photo);
        mConciergeName = (TextView) findViewById(R.id.concierge_name);
        mConciergeTitle = (TextView) findViewById(R.id.concierge_title);
        mConciergeSpecialties = (TextView) findViewById(R.id.concierge_specialties);
        mShareProfileReq = (TextView) findViewById(R.id.share_profile_request);
        mConciergeRating = (RatingBar) findViewById(R.id.concierge_rating);
        mConciergeReviews = (TextView) findViewById(R.id.customer_reviews);
        mProfileSharingLayout = (LinearLayout) findViewById(R.id.profile_sharing_layout);

        mHttpReqStatus = (TextView) findViewById(R.id.http_request_status);

        mEnglish = (ImageView) findViewById(R.id.language_english);
        mFrench = (ImageView) findViewById(R.id.language_french);
        mSpanish = (ImageView) findViewById(R.id.language_spanish);
        mGerman = (ImageView) findViewById(R.id.language_german);

        mProfileSharingTimePicker = (EditText) findViewById(R.id.profile_sharing_time_picker);
        mProfileSharingTimePicker.setText("30");

        mConsumerID = getIntent().getExtras().getString("@string/consumer_id");
        mConciergeID = getIntent().getExtras().getString("@string/concierge_id");
        miFashionIP = getIntent().getExtras().getString("@string/ip_address");
        miFashionPort = getIntent().getExtras().getString("@string/port");
        miFashionAPI = getIntent().getExtras().getString("@string/profile_api");
    }

    @Override
    public void onStart() {
        super.onStart();

        mDialog = ProgressDialog.show(this, "Chronos",
                "Hang in there, while we connect you to the Style Concierge...", true);

        String conciergeProfile = getConciergeProfile();
        try {
            if (conciergeProfile.equals(""))
                throw new JSONException("Concierge profile not found");

            mJSONObj = new JSONObject(conciergeProfile);
            mConciergeName.setText(mJSONObj.get("name").toString());
            mConciergeTitle.setText(mJSONObj.get("title").toString());
            mConciergeSpecialties.setText("Specializes in " + mJSONObj.get("specialty").toString());
            mShareProfileReq.setText("Allow " + mJSONObj.get("name") + " to access Style Analytics?");

            JSONArray ratings = new JSONArray(mJSONObj.get("rating").toString());
            int numReviews = ratings.length();
            float rating = 0;
            if (numReviews > 0) {
                for (int i = 0; i < numReviews; i++) {
                    JSONObject r = ratings.getJSONObject(i);
                    rating += Float.parseFloat(r.get("rating").toString());
                }
                rating /= numReviews;
            }
            mConciergeRating.setRating(rating);
            mConciergeReviews.setText(numReviews + " consumer reviews");

            String encodedPhoto = mJSONObj.get("photo").toString();
            Bitmap conciergePhoto = decodeBitmap(encodedPhoto);
            mConciergePhoto.setImageBitmap(conciergePhoto);

            String languages = mJSONObj.get("languages").toString();
            if (languages.contains("English")) {
                mEnglish.setImageResource(R.drawable.english);
            }
            if (languages.contains("French")) {
                mFrench.setImageResource(R.drawable.french);
            }
            if (languages.contains("Spanish")) {
                mSpanish.setImageResource(R.drawable.spanish);
            }
            if (languages.contains("German")) {
                mGerman.setImageResource(R.drawable.german);
            }

            mConciergeRating.setVisibility(View.VISIBLE);
            mShareProfileReq.setVisibility(View.VISIBLE);
            mProfileSharingLayout.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            Log.i(TAG, "onStart: exception " + conciergeProfile);
            mHttpReqStatus.setText("No Style Concierges information is available at this time. " +
                    "We are sorry for the inconvenience!");
        }

        mDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_personal_guidance, menu);
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

    public String getConciergeProfile() {
        // Extract Concierge ID from Intent
        Intent intent = getIntent();
        String conciergeID = intent.getStringExtra("@string/concierge_id");
        String httpURL = intent.getStringExtra("@string/ip_address") + ":" +
                intent.getStringExtra("@string/port") + intent.getStringExtra("@string/concierge_api");

        Log.i(TAG, "Concierge ID = " + conciergeID);

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

        return httpResponse;
    }

    private Bitmap decodeBitmap(String encodedBitmap) {
        byte[] b = Base64.decode(encodedBitmap, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    public void onAllowProfileSharing(View view) {
        try {
            HttpPoster poster = new HttpPoster(getApplicationContext());
            String result = poster.execute(
                    miFashionIP + ":" + miFashionPort + miFashionAPI,
                    "consumer_id", mConsumerID,
                    "concierge_id", mConciergeID,
                    "profile_access", "true",
                    "access_time", mProfileSharingTimePicker.getText().toString()
            ).get();

            if (result.equalsIgnoreCase("not found")) {
                Log.i(TAG, "onAllowProfileSharing: consumer profile not found!");
                Toast.makeText(getApplicationContext(), "Please create your iFashion profile",
                        Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("profile shared successfully")) {
                // Start rate concierge activity
                Intent rateConcierge = new Intent(this, RateConciergeActivity.class);
                rateConcierge.putExtras(getIntent());
                startActivity(rateConcierge);
            }

        } catch (InterruptedException e) {
            Log.i(TAG, "onAllowProfileSharing:InterruptedException: " + e.getMessage());
        } catch (ExecutionException e) {
            Log.i(TAG, "onAllowProfileSharing:ExecutionException: " + e.getMessage());
        }
    }

    public void onDenyProfileSharing(View view) {
        try {
            HttpPoster poster = new HttpPoster(getApplicationContext());
            String result = poster.execute(
                    miFashionIP + ":" + miFashionPort + miFashionAPI,
                    "consumer_id", mConsumerID,
                    "concierge_id", mConciergeID,
                    "profile_access", "false"
            ).get();
            if (result.equalsIgnoreCase("not found")) {
                Log.i(TAG, "onDenyProfileSharing: consumer profile not found!");
                Toast.makeText(getApplicationContext(), "Please create your iFashion profile",
                        Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("profile shared successfully")) {
                // Start rate concierge activity
                Intent rateConcierge = new Intent(this, RateConciergeActivity.class);
                rateConcierge.putExtras(getIntent());
                startActivity(rateConcierge);
            }
        } catch (InterruptedException e) {
            Log.i(TAG, "onDenyProfileSharing:InterruptedException: " + e.getMessage());
        } catch (ExecutionException e) {
            Log.i(TAG, "onDenyProfileSharing:ExecutionException: " + e.getMessage());
        }
    }
}
