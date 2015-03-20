package com.intel.ndg.chronos;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

public class UserProfileActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "User Profile Activity:";
    private Timepiece mTimepiece;
    private FavoriteActivity mFavActivity;
    private FavoriteDrink mFavDrink;

    private SeekBar mSeekBarAge;
    private SeekBar mSeekBarGoals1;
    private SeekBar mSeekBarGoals2;
    private SeekBar mSeekBarStyle1;
    private SeekBar mSeekBarStyle2;
    private SeekBar mSeekBarBudget;
    private SeekBar mSeekBarUsage;
    private SeekBar mSeekBarWrist;
    private Spinner mLanguageSpinner;
    private Spinner mWatchfaceShapeSpinner;
    private Spinner mWatchfaceTypeSpinner;
    private RadioButton mStrapLeather;
    private RadioButton mStrapSteel;
    private RadioButton mGenderMale;
    private RadioButton mGenderFemale;

    private String mConsumerID;
    private String mBackendIP;
    private String mBackendPort;
    private String mBackendAPI;
    private String mGender;
    private Integer mLanguage;

    private StyleAnalytics mStyleAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.i(TAG, "Start User Profile Activity");

        mConsumerID = getIntent().getExtras().getString("@string/consumer_id");
        mBackendIP = getIntent().getExtras().getString("@string/ip_address");
        mBackendPort = getIntent().getExtras().getString("@string/port");
        mBackendAPI = getIntent().getExtras().getString("@string/consumer_api");

        mSeekBarAge = (SeekBar) findViewById(R.id.seekBarAge);
        mSeekBarGoals1 = (SeekBar) findViewById(R.id.seekBar1);
        mSeekBarGoals2 = (SeekBar) findViewById(R.id.seekBar2);
        mSeekBarStyle1 = (SeekBar) findViewById(R.id.seekBar3);
        mSeekBarStyle2 = (SeekBar) findViewById(R.id.seekBar4);
        mSeekBarBudget = (SeekBar) findViewById(R.id.seekBar5);
        mSeekBarUsage = (SeekBar) findViewById(R.id.seekBar6);
        mSeekBarWrist = (SeekBar) findViewById(R.id.seekBar7);

        mLanguageSpinner = (Spinner) findViewById(R.id.preferred_language_spinner);
        mLanguageSpinner.setOnItemSelectedListener(this);

        mWatchfaceShapeSpinner = (Spinner) findViewById(R.id.watchface_shape_spinner);
        mWatchfaceShapeSpinner.setOnItemSelectedListener(this);

        mWatchfaceTypeSpinner = (Spinner) findViewById(R.id.watchface_type_spinner);
        mWatchfaceTypeSpinner.setOnItemSelectedListener(this);

        mGenderMale = (RadioButton) findViewById(R.id.male);
        mGenderFemale = (RadioButton) findViewById(R.id.female);
        mStrapLeather = (RadioButton) findViewById(R.id.leather);
        mStrapSteel = (RadioButton) findViewById(R.id.steel);

        mTimepiece = new Timepiece(getApplicationContext());
        mFavActivity = new FavoriteActivity(getApplicationContext());
        mFavDrink = new FavoriteDrink(getApplicationContext());

        restoreProfile();

        // Create style analytics
        mStyleAnalytics = createStyleAnalytics();
        if (mStyleAnalytics != null) {
            Log.i(TAG, "User Style Score = " + mStyleAnalytics.getStyleScore());
            Log.i(TAG, "User Budget Score = " + mStyleAnalytics.getBudgetScore());
            Log.i(TAG, "Product Recommendation = " + mStyleAnalytics.getProductRecommendation());
            Log.i(TAG, "Favorite Activities = " + mStyleAnalytics.getFavActivities());
            Log.i(TAG, "Favorite Drinks = " + mStyleAnalytics.getFavDrinks());
        }
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
                break;
            case R.id.upload_profile:
                sendToBackend();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onGenderRadioButtonClicked(View view) {
        // Is the view now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.male:
                if (checked)
                    mGender = "male";
                break;
            case R.id.female:
                if (checked)
                    mGender = "female";
                break;
            default:
                break;
        }
    }

    public void onMaterialsRadioButtonClicked(View view) {
        // Is the view now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.leather:
                if (checked)
                    mTimepiece.setStrapMaterial(Timepiece.StrapMaterial.Leather);
                break;
            case R.id.steel:
                if (checked)
                    mTimepiece.setStrapMaterial(Timepiece.StrapMaterial.Steel);
                break;
            default:
                break;
        }
    }

    public void onFavActivitiesCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_Football:
                if (checked)
                    mFavActivity.addActivity(FavoriteActivity.Activity.Football);
                else
                    mFavActivity.removeActivity(FavoriteActivity.Activity.Football);
                break;
            case R.id.checkbox_Basketball:
                if (checked)
                    mFavActivity.addActivity(FavoriteActivity.Activity.Basketball);
                else
                    mFavActivity.removeActivity(FavoriteActivity.Activity.Basketball);
                break;
            case R.id.checkbox_Golf:
                if (checked)
                    mFavActivity.addActivity(FavoriteActivity.Activity.Golf);
                else
                    mFavActivity.removeActivity(FavoriteActivity.Activity.Golf);
                break;
            case R.id.checkbox_Formula_1:
                if (checked)
                    mFavActivity.addActivity(FavoriteActivity.Activity.Formula_1);
                else
                    mFavActivity.removeActivity(FavoriteActivity.Activity.Formula_1);
                break;
            case R.id.checkbox_Diving:
                if (checked)
                    mFavActivity.addActivity(FavoriteActivity.Activity.Diving);
                else
                    mFavActivity.removeActivity(FavoriteActivity.Activity.Diving);
                break;
            default:
                break;
        }
    }

    public void onFavDrinkCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_Tea:
                if (checked)
                    mFavDrink.addDrink(FavoriteDrink.Drink.Tea);
                else
                    mFavDrink.removeDrink(FavoriteDrink.Drink.Tea);
                break;
            case R.id.checkbox_Coffee:
                if (checked)
                    mFavDrink.addDrink(FavoriteDrink.Drink.Coffee);
                else
                    mFavDrink.removeDrink(FavoriteDrink.Drink.Coffee);
                break;
            case R.id.checkbox_HotChocolate:
                if (checked)
                    mFavDrink.addDrink(FavoriteDrink.Drink.HotChocolate);
                else
                    mFavDrink.removeDrink(FavoriteDrink.Drink.HotChocolate);
                break;
            default:
                break;
        }
    }

    private void saveProfile() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();

        editor.putString("@string/consumer_id", mConsumerID);
        editor.putString("@string/gender", mGender);
        editor.putInt("@string/language", mLanguage);

        editor.putInt("seekBarAge", mSeekBarAge.getProgress());
        editor.putInt("seekBarGoals1", mSeekBarGoals1.getProgress());
        editor.putInt("seekBarGoals2", mSeekBarGoals2.getProgress());
        editor.putInt("seekBarStyle1", mSeekBarStyle1.getProgress());
        editor.putInt("seekBarStyle2", mSeekBarStyle2.getProgress());
        editor.putInt("seekBarBudget", mSeekBarBudget.getProgress());
        editor.putInt("seekBarUsage", mSeekBarUsage.getProgress());
        editor.putInt("seekBarWrist", mSeekBarWrist.getProgress());

        editor.commit();

        mTimepiece.saveState();
        mFavActivity.saveState();
        mFavDrink.saveState();

        // Rebuild style analytics
        mStyleAnalytics = createStyleAnalytics();

        Toast.makeText(getApplicationContext(), "Profile saved!", Toast.LENGTH_LONG).show();
    }

    private void sendToBackend() {

        if (mStyleAnalytics == null) {
            // Save profile before uploading
            saveProfile();
        }

        HttpPoster poster = new HttpPoster(this, "We will be back momentarily");
        poster.execute(
                mBackendIP + ":" + mBackendPort + mBackendAPI,
                "consumer_id", mConsumerID,
                "gender", mGender,
                "language", mLanguageSpinner.getItemAtPosition(mLanguage).toString(),
                "age_group", String.valueOf(mSeekBarAge.getProgress()),
                "style_score", String.valueOf(mStyleAnalytics.getStyleScore()),
                "budget_score", String.valueOf(mStyleAnalytics.getBudgetScore()),
                "fav_activities", String.valueOf(mStyleAnalytics.getFavActivities()),
                "fav_drinks", String.valueOf(mStyleAnalytics.getFavDrinks()),
                "prod_rec", String.valueOf(mStyleAnalytics.getProductRecommendation())
        );
    }

    private StyleAnalytics createStyleAnalytics() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!sharedPref.contains("seekBarGoals1"))
            // No shared preferences exist
            return null;

        Timepiece timepiece = new Timepiece(getApplicationContext());
        FavoriteActivity favActivity = new FavoriteActivity(getApplicationContext());
        FavoriteDrink favDrink = new FavoriteDrink(getApplicationContext());

        return new StyleAnalytics(
                mSeekBarGoals1.getProgress(),
                mSeekBarGoals2.getProgress(),
                mSeekBarStyle1.getProgress(),
                mSeekBarStyle2.getProgress(),
                mSeekBarBudget.getProgress(),
                mSeekBarUsage.getProgress(),
                mSeekBarWrist.getProgress(),
                timepiece,
                favActivity,
                favDrink );
    }

    private void restoreProfile() {
        // Restore state from shared settings
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mGender = sharedPref.getString("@string/gender", "");
        mLanguage = sharedPref.getInt("@string/language", 0);
        mLanguageSpinner.setSelection(mLanguage);

        mSeekBarAge.setProgress(sharedPref.getInt("seekBarAge", 0));
        mSeekBarGoals1.setProgress(sharedPref.getInt("seekBarGoals1", 0));
        mSeekBarGoals2.setProgress(sharedPref.getInt("seekBarGoals2", 0));
        mSeekBarStyle1.setProgress(sharedPref.getInt("seekBarStyle1", 0));
        mSeekBarStyle2.setProgress(sharedPref.getInt("seekBarStyle2", 0));
        mSeekBarBudget.setProgress(sharedPref.getInt("seekBarBudget", 0));
        mSeekBarUsage.setProgress(sharedPref.getInt("seekBarUsage", 0));
        mSeekBarWrist.setProgress(sharedPref.getInt("seekBarWrist", 0));

        // Restore Timepiece style
        Spinner watchFaceShapeSpinner = (Spinner) findViewById(R.id.watchface_shape_spinner);
        watchFaceShapeSpinner.setSelection(mTimepiece.getWatchface().getShape().ordinal());
        Spinner watchFaceTypeSpinner = (Spinner) findViewById(R.id.watchface_type_spinner);
        watchFaceTypeSpinner.setSelection(mTimepiece.getWatchface().getType().ordinal());

        // Restore gender
        switch (mGender) {
            case "male":
                mGenderMale.setChecked(true);
                break;
            case "female":
                mGenderFemale.setChecked(true);
                break;
            default:
                break;
        }

        // Restore strap material
        switch (mTimepiece.getStrapMaterial()) {
            case Leather:
                mStrapLeather.setChecked(true);
                break;
            case Steel:
                mStrapSteel.setChecked(true);
                break;
            default:
                break;
        }

        // Restore Favorite Activity state
        if (mFavActivity.getActivities() != null) {
            for (FavoriteActivity.Activity s : mFavActivity.getActivities()) {
                int id = getResources().getIdentifier("checkbox_" + s.name(), "id", getPackageName());
                CheckBox checkBox = (CheckBox) findViewById(id);
                if (checkBox != null)
                    checkBox.setChecked(true);
            }
        }

        // Restore Favorite Drink state
        if (mFavDrink.getDrinks() != null) {
            for (FavoriteDrink.Drink s : mFavDrink.getDrinks()) {
                int id = getResources().getIdentifier("checkbox_" + s.name(), "id", getPackageName());
                CheckBox checkBox = (CheckBox) findViewById(id);
                if (checkBox != null)
                    checkBox.setChecked(true);
            }
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.i("Spinner:onItemSelected", parent.getItemAtPosition(position).toString());
        if (parent.getId() == mLanguageSpinner.getId()) {
            mLanguage = mLanguageSpinner.getSelectedItemPosition();
        }
        else if (parent.getId() == mWatchfaceShapeSpinner.getId()) {
            mTimepiece.getWatchface().setShape(Watchface.Shape.values()[position]);
        } else if (parent.getId() == mWatchfaceTypeSpinner.getId()) {
            mTimepiece.getWatchface().setType(Watchface.Type.values()[position]);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.i("Spinner:onNothingSelected", " called");
    }
}
