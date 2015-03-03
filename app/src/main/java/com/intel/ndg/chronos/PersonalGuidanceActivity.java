package com.intel.ndg.chronos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.InputMismatchException;
import java.util.concurrent.ExecutionException;


public class PersonalGuidanceActivity extends ActionBarActivity {

    private static final String TAG = "ConciergeProfActivity";

    ImageView mConciergePhoto;
    ImageView mEnglish;
    ImageView mFrench;
    ImageView mSpanish;
    ImageView mGerman;
    TextView mConciergeName;
    TextView mConciergeTitle;
    TextView mConciergeSpecialties;

    JSONObject mJSONObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_guidance);

        mConciergePhoto = (ImageView) findViewById(R.id.concierge_photo);
        mConciergeName = (TextView) findViewById(R.id.concierge_name);
        mConciergeTitle = (TextView) findViewById(R.id.concierge_title);
        mConciergeSpecialties = (TextView) findViewById(R.id.concierge_specialties);

        mEnglish = (ImageView) findViewById(R.id.language_english);
        mFrench = (ImageView) findViewById(R.id.language_french);
        mSpanish = (ImageView) findViewById(R.id.language_spanish);
        mGerman = (ImageView) findViewById(R.id.language_german);

        String conciergeProfile = getConciergeProfile();
        try {
            mJSONObj = new JSONObject(conciergeProfile);
            mConciergeName.setText(mJSONObj.get("name").toString());
            mConciergeTitle.setText(mJSONObj.get("title").toString());
            mConciergeSpecialties.setText("Specializes in " + mJSONObj.get("specialty").toString());

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

        } catch (JSONException e) {
            Log.i(TAG, "onCreate: exception " + e.getMessage());
        }

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

        return httpResponse;
    }

    private Bitmap decodeBitmap(String encodedBitmap) {
        byte[] b = Base64.decode(encodedBitmap, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        return decodedBitmap;
    }
}
