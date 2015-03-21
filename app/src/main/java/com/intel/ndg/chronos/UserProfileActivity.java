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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

public class UserProfileActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "User Profile Activity:";
    private Timepiece mTimepiece;
    private FavoriteDrink mFavDrink;

    private SeekBar mSeekBarAge;
    private SeekBar mSeekBarAspirations1;
    private SeekBar mSeekBarAspirations2;
    private SeekBar mSeekBarStyle1;
    private SeekBar mSeekBarStyle2;
    private SeekBar mSeekBarBudget;
    private SeekBar mSeekBarWrist;
    private Spinner mLanguageSpinner;
    private Spinner mCelebritySpinner;
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
        mSeekBarAspirations1 = (SeekBar) findViewById(R.id.seekBarAspirations1);
        mSeekBarAspirations2 = (SeekBar) findViewById(R.id.seekBarAspirations2);
        mSeekBarStyle1 = (SeekBar) findViewById(R.id.seekBarStyle1);
        mSeekBarStyle2 = (SeekBar) findViewById(R.id.seekBarStyle2);
        mSeekBarBudget = (SeekBar) findViewById(R.id.seekBarBudget);
        mSeekBarWrist = (SeekBar) findViewById(R.id.seekBarWristSize);

        mLanguageSpinner = (Spinner) findViewById(R.id.preferred_language_spinner);
        mLanguageSpinner.setOnItemSelectedListener(this);

        mCelebritySpinner  = (Spinner) findViewById(R.id.celebrity_spinner);
        mCelebritySpinner.setOnItemSelectedListener(this);

        mWatchfaceShapeSpinner = (Spinner) findViewById(R.id.watchface_shape_spinner);
        mWatchfaceShapeSpinner.setOnItemSelectedListener(this);

        mWatchfaceTypeSpinner = (Spinner) findViewById(R.id.watchface_type_spinner);
        mWatchfaceTypeSpinner.setOnItemSelectedListener(this);

        mGenderMale = (RadioButton) findViewById(R.id.male);
        mGenderFemale = (RadioButton) findViewById(R.id.female);
        mStrapLeather = (RadioButton) findViewById(R.id.leather);
        mStrapSteel = (RadioButton) findViewById(R.id.steel);

        mTimepiece = new Timepiece(getApplicationContext());
        mFavDrink = new FavoriteDrink(getApplicationContext());

        restoreProfile();

        // Create style analytics
        mStyleAnalytics = createStyleAnalytics();
        if (mStyleAnalytics != null) {
            Log.i(TAG, "User Style Score = " + mStyleAnalytics.getStyleScore());
            Log.i(TAG, "User Budget Score = " + mStyleAnalytics.getBudgetScore());
            Log.i(TAG, "Product Recommendation = " + mStyleAnalytics.getProductRecommendation());
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
                    mCelebritySpinner.setAdapter(ArrayAdapter.createFromResource(this,
                            R.array.male_celebrity_choices, android.R.layout.simple_spinner_item));
                break;
            case R.id.female:
                if (checked)
                    mGender = "female";
                    mCelebritySpinner.setAdapter(ArrayAdapter.createFromResource(this,
                            R.array.female_celebrity_choices, android.R.layout.simple_spinner_item));
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
                    mTimepiece.setStrap(Timepiece.Strap.LEATHER);
                break;
            case R.id.steel:
                if (checked)
                    mTimepiece.setStrap(Timepiece.Strap.STEEL);
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
            case R.id.checkbox_black_tea:
                if (checked)
                    mFavDrink.addDrink(FavoriteDrink.Drink.black_tea);
                else
                    mFavDrink.removeDrink(FavoriteDrink.Drink.black_tea);
                break;
            case R.id.checkbox_green_tea:
                if (checked)
                    mFavDrink.addDrink(FavoriteDrink.Drink.green_tea);
                else
                    mFavDrink.removeDrink(FavoriteDrink.Drink.green_tea);
                break;
            case R.id.checkbox_espresso:
                if (checked)
                    mFavDrink.addDrink(FavoriteDrink.Drink.espresso);
                else
                    mFavDrink.removeDrink(FavoriteDrink.Drink.espresso);
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
        editor.putInt("seekBarBudget", mSeekBarBudget.getProgress());       
        editor.putInt("seekBarAspirations1", mSeekBarAspirations1.getProgress());
        editor.putInt("seekBarAspirations2", mSeekBarAspirations2.getProgress());
        editor.putInt("seekBarStyle1", mSeekBarStyle1.getProgress());
        editor.putInt("seekBarStyle2", mSeekBarStyle2.getProgress());
        
        editor.putInt("seekBarWrist", mSeekBarWrist.getProgress());
        editor.putInt("celebritySpinner", mCelebritySpinner.getSelectedItemPosition());
                
        editor.commit();

        mTimepiece.saveState();
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
                "fav_drinks", mFavDrink.getDrinks().toString(),
                "prod_rec", String.valueOf(mStyleAnalytics.getProductRecommendation())
        );
    }

    private StyleAnalytics createStyleAnalytics() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!sharedPref.contains("seekBarAspirations1"))
            // No shared preferences exist
            return null;

        return new StyleAnalytics(
                mSeekBarAspirations1.getProgress(),
                mSeekBarAspirations2.getProgress(),
                mSeekBarStyle1.getProgress(),
                mSeekBarStyle2.getProgress(),
                mSeekBarBudget.getProgress(),
                mSeekBarWrist.getProgress(),
                mTimepiece);
    }

    private void restoreProfile() {
        // Restore state from shared settings
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mGender = sharedPref.getString("@string/gender", "");
        mLanguage = sharedPref.getInt("@string/language", 0);
        mLanguageSpinner.setSelection(mLanguage);

        mSeekBarAge.setProgress(sharedPref.getInt("seekBarAge", 0));
        mSeekBarAspirations1.setProgress(sharedPref.getInt("seekBarAspirations1", 0));
        mSeekBarAspirations2.setProgress(sharedPref.getInt("seekBarAspirations2", 0));
        mSeekBarStyle1.setProgress(sharedPref.getInt("seekBarStyle1", 0));
        mSeekBarStyle2.setProgress(sharedPref.getInt("seekBarStyle2", 0));
        mSeekBarBudget.setProgress(sharedPref.getInt("seekBarBudget", 0));
        mSeekBarWrist.setProgress(sharedPref.getInt("seekBarWrist", 0));

        // Restore Timepiece style
        Spinner watchFaceShapeSpinner = (Spinner) findViewById(R.id.watchface_shape_spinner);
        watchFaceShapeSpinner.setSelection(mTimepiece.getShape().ordinal());
        Spinner watchFaceTypeSpinner = (Spinner) findViewById(R.id.watchface_type_spinner);
        watchFaceTypeSpinner.setSelection(mTimepiece.getType().ordinal());

        // Restore gender
        switch (mGender) {
            case "male":
                mGenderMale.setChecked(true);
                if (mCelebritySpinner.getCount() == 0)
                    mCelebritySpinner.setAdapter(ArrayAdapter.createFromResource(this,
                            R.array.male_celebrity_choices, android.R.layout.simple_spinner_item));
                break;
            case "female":
                mGenderFemale.setChecked(true);
                if (mCelebritySpinner.getCount() == 0)
                    mCelebritySpinner.setAdapter(ArrayAdapter.createFromResource(this,
                            R.array.female_celebrity_choices, android.R.layout.simple_spinner_item));
                break;
            default:
                break;
        }

        mCelebritySpinner.setSelection(sharedPref.getInt("celebritySpinner", 0));

        // Restore strap material
        switch (mTimepiece.getStrap()) {
            case LEATHER:
                mStrapLeather.setChecked(true);
                break;
            case STEEL:
                mStrapSteel.setChecked(true);
                break;
            default:
                break;
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
        } else if (parent.getId() == mCelebritySpinner.getId()) {
            mTimepiece.setCollection(Timepiece.Collection.values()[position]);
        } else if (parent.getId() == mWatchfaceShapeSpinner.getId()) {
            mTimepiece.setShape(Timepiece.Shape.values()[position]);
        } else if (parent.getId() == mWatchfaceTypeSpinner.getId()) {
            mTimepiece.setType(Timepiece.Type.values()[position]);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.i("Spinner:onNothingSelected", " called");
    }
}
