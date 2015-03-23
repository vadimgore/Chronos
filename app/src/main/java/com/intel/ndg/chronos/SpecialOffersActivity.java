package com.intel.ndg.chronos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;


public class SpecialOffersActivity extends ActionBarActivity {

    final static String TAG = "SpecialOffersActivity";
    TextView mNoOffers;
    LinearLayout mOffersLayout;
    TextView mOfferTitle;
    TextView mOfferDescription;
    ImageView mBrandAmbassadorImage;
    ImageView mProductAdImage;
    Button mAcceptButton;
    Button mViewCollection;
    WebView mWebView;

    String mCollection;
    String mGender;
    String mBudget;

    String mAdServerIP;
    String mAdServerPort;
    String mAdServerAPI;
    String mCollectionLink;

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mBitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mBitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mBitmap;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_offers);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNoOffers = (TextView) findViewById(R.id.no_offers_availble);
        mOffersLayout = (LinearLayout) findViewById(R.id.special_offers_layout);
        mOfferTitle = (TextView) findViewById(R.id.offer_title);
        mOfferDescription = (TextView) findViewById(R.id.offer_description);
        mBrandAmbassadorImage = (ImageView) findViewById(R.id.brand_ambassador);
        mProductAdImage = (ImageView) findViewById(R.id.ad);
        mAcceptButton = (Button) findViewById(R.id.accept_offer);
        mViewCollection = (Button) findViewById(R.id.view_collection);

        mWebView = (WebView) findViewById(R.id.webview);

        mAdServerIP = getIntent().getStringExtra("@string/ad_server_ip_address");
        mAdServerPort = getIntent().getStringExtra("@string/ad_server_port");
        mAdServerAPI = getIntent().getStringExtra("@string/ad_server_api");

        initAdProfile();
        String adServerResponse = getAd();
        try {
            JSONObject jsonResponse = new JSONObject(adServerResponse);
            JSONArray ads = new JSONArray(jsonResponse.getString("message"));
            JSONObject ad = ads.getJSONObject(0);
            String imageURL = ad.getString("imageURL");

            if (imageURL.equals("")) {
                mNoOffers.setVisibility(View.VISIBLE);
                return;
            }

            new DownloadImageTask(mProductAdImage).execute(imageURL);
            mOffersLayout.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            Log.i(TAG, "JSONException: " + e.getMessage());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_special_offers, menu);
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

    private void initAdProfile() {
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mGender = sharedPref.getString("@string/gender", "").toLowerCase();
        String[] collection_links;
        if (mGender.equals("male"))
            collection_links = getResources().getStringArray(R.array.male_collection_links);
        else
            collection_links = getResources().getStringArray(R.array.female_collection_links);

        mCollection = Timepiece.Collection.values()[
                sharedPref.getInt("celebritySpinner", 0)].name().toLowerCase();

        switch (mCollection) {
            case "carrera":
                mOfferTitle.setText(getResources().getStringArray(R.array.ad_titles)[0]);
                mOfferDescription.setText(getResources().getStringArray(R.array.ad_descriptions)[0]);
                mCollectionLink = collection_links[0];
                break;
            case "aquaracer":
                mOfferTitle.setText(getResources().getStringArray(R.array.ad_titles)[1]);
                mOfferDescription.setText(getResources().getStringArray(R.array.ad_descriptions)[1]);
                mCollectionLink = collection_links[1];
                break;
            case "formula1":
                mOfferTitle.setText(getResources().getStringArray(R.array.ad_titles)[2]);
                mOfferDescription.setText(getResources().getStringArray(R.array.ad_descriptions)[2]);
                mCollectionLink = collection_links[2];
                break;
            case "monaco":
                mOfferTitle.setText(getResources().getStringArray(R.array.ad_titles)[3]);
                mOfferDescription.setText(getResources().getStringArray(R.array.ad_descriptions)[3]);
                mCollectionLink = collection_links[3];
                break;
            default:
        }

        switch (sharedPref.getInt("seekBarBudget", -1)) {
            case 0:
                mBudget = "low";
                break;
            case 1:
            case 2:
                mBudget = "medium";
                break;
            case 3:
            case 4:
                mBudget = "high";
                break;
            default:
                mBudget = "";
        }

        mBrandAmbassadorImage.setImageResource(getResources().getIdentifier(
                mGender+"_"+mCollection, "drawable", getPackageName()));

    }

    private String getAd() {

        String httpURL = mAdServerIP + ":" + mAdServerPort + mAdServerAPI;
        String queryString = "?gender=" + mGender + "&collection=" + mCollection +
                "&budget=" + mBudget;
        String httpResponse = "";
        try {
            // Get full Concierge profile from iFashion
            httpResponse = new HttpGetter().execute(httpURL, queryString).get();

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


    public void onAcceptOffer(View view) {
        Log.i(TAG, "onAcceptOffer called");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Please take this offer to one of our Store Concierges")
                .setTitle("Special Offer");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onViewCollection(View view) {
        Log.i(TAG, "onViewCollection called");
        mWebView.setVisibility(View.VISIBLE);
        mWebView.loadUrl(mCollectionLink);
    }

}
