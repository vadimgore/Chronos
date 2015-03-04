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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

public class UserProfileActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "User Profile Activity:";
    private Timepiece mTimepiece;
    private FavoriteSport mFavSport;
    private FavoriteDrink mFavDrink;

    private SeekBar mSeekBarGoals1;
    private SeekBar mSeekBarGoals2;
    private SeekBar mSeekBarStyle1;
    private SeekBar mSeekBarStyle2;
    private SeekBar mSeekBarBudget;
    private SeekBar mSeekBarUsage;
    private SeekBar mSeekBarWrist;
    private Spinner mWatchfaceShapeSpinner;
    private Spinner mWatchfaceTypeSpinner;

    private String mConsumerID;
    private String miFashionIP;
    private String miFashionPort;
    private String miFashionAPI;

    private StyleAnalytics mStyleAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.i(TAG, "Start User Profile Activity");

        mConsumerID = getIntent().getExtras().getString("@string/consumer_id");
        miFashionIP = getIntent().getExtras().getString("@string/ip_address");
        miFashionPort = getIntent().getExtras().getString("@string/port");
        miFashionAPI = getIntent().getExtras().getString("@string/consumer_api");

        mSeekBarGoals1 = (SeekBar) findViewById(R.id.seekBar1);
        mSeekBarGoals2 = (SeekBar) findViewById(R.id.seekBar2);
        mSeekBarStyle1 = (SeekBar) findViewById(R.id.seekBar3);
        mSeekBarStyle2 = (SeekBar) findViewById(R.id.seekBar4);
        mSeekBarBudget = (SeekBar) findViewById(R.id.seekBar5);
        mSeekBarUsage = (SeekBar) findViewById(R.id.seekBar6);
        mSeekBarWrist = (SeekBar) findViewById(R.id.seekBar7);

        mWatchfaceShapeSpinner = (Spinner) findViewById(R.id.watchface_shape_spinner);
        mWatchfaceShapeSpinner.setOnItemSelectedListener(this);

        mWatchfaceTypeSpinner = (Spinner) findViewById(R.id.watchface_type_spinner);
        mWatchfaceTypeSpinner.setOnItemSelectedListener(this);

        mTimepiece = new Timepiece(getApplicationContext());
        mFavSport = new FavoriteSport(getApplicationContext());
        mFavDrink = new FavoriteDrink(getApplicationContext());

        restoreProfile();

        // Create style analytics
        mStyleAnalytics = createStyleAnalytics();
        if (mStyleAnalytics != null) {
            Log.i(TAG, "User Style Score = " + mStyleAnalytics.getStyleScore());
            Log.i(TAG, "User Budget Score = " + mStyleAnalytics.getBudgetScore());
            Log.i(TAG, "Product Recommendation = " + mStyleAnalytics.getProductRecommendation());
            Log.i(TAG, "Favorite Sports = " + mStyleAnalytics.getFavSports());
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
            case R.id.upload_profile:
                sendToBackend();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onMaterialsCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_Gold:
                if (checked)
                    mTimepiece.addMaterial(Timepiece.Material.Gold);
                else
                    mTimepiece.removeMaterial(Timepiece.Material.Gold);
                break;
            case R.id.checkbox_Titan:
                if (checked)
                    mTimepiece.addMaterial(Timepiece.Material.Titan);
                else
                    mTimepiece.removeMaterial(Timepiece.Material.Titan);
                break;
            case R.id.checkbox_Diamonds:
                if (checked)
                    mTimepiece.addMaterial(Timepiece.Material.Diamonds);
                else
                    mTimepiece.removeMaterial(Timepiece.Material.Diamonds);
                break;
            case R.id.checkbox_Leather:
                if (checked)
                    mTimepiece.addMaterial(Timepiece.Material.Leather);
                else
                    mTimepiece.removeMaterial(Timepiece.Material.Leather);
                break;
            default:
                break;
        }
    }

    public void onFavSportsCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_Football:
                if (checked)
                    mFavSport.addSport(FavoriteSport.Sport.Football);
                else
                    mFavSport.removeSport(FavoriteSport.Sport.Football);
                break;
            case R.id.checkbox_Basketball:
                if (checked)
                    mFavSport.addSport(FavoriteSport.Sport.Basketball);
                else
                    mFavSport.removeSport(FavoriteSport.Sport.Basketball);
                break;
            case R.id.checkbox_Golf:
                if (checked)
                    mFavSport.addSport(FavoriteSport.Sport.Golf);
                else
                    mFavSport.removeSport(FavoriteSport.Sport.Golf);
                break;
            case R.id.checkbox_Formula_1:
                if (checked)
                    mFavSport.addSport(FavoriteSport.Sport.Formula_1);
                else
                    mFavSport.removeSport(FavoriteSport.Sport.Formula_1);
                break;
            case R.id.checkbox_Watersports:
                if (checked)
                    mFavSport.addSport(FavoriteSport.Sport.Watersports);
                else
                    mFavSport.removeSport(FavoriteSport.Sport.Watersports);
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

        editor.putString("consumer_id", mConsumerID);

        editor.putInt("seekBarGoals1", mSeekBarGoals1.getProgress());
        editor.putInt("seekBarGoals2", mSeekBarGoals2.getProgress());
        editor.putInt("seekBarStyle1", mSeekBarStyle1.getProgress());
        editor.putInt("seekBarStyle2", mSeekBarStyle2.getProgress());
        editor.putInt("seekBarBudget", mSeekBarBudget.getProgress());
        editor.putInt("seekBarUsage", mSeekBarUsage.getProgress());
        editor.putInt("seekBarWrist", mSeekBarWrist.getProgress());

        editor.commit();

        mTimepiece.saveState();
        mFavSport.saveState();
        mFavDrink.saveState();

        Toast.makeText(getApplicationContext(), "Profile saved!", Toast.LENGTH_LONG).show();
    }

    private void sendToBackend() {
        HttpPoster poster = new HttpPoster(getApplicationContext());
        poster.execute(
                miFashionIP + ":" + miFashionPort + miFashionAPI,
                "consumer_id", mConsumerID,
                "style_score", String.valueOf(mStyleAnalytics.getStyleScore()),
                "budget_score", String.valueOf(mStyleAnalytics.getBudgetScore()),
                "fav_sports", String.valueOf(mStyleAnalytics.getFavSports()),
                "fav_drinks", String.valueOf(mStyleAnalytics.getFavDrinks()),
                "prod_rec", String.valueOf(mStyleAnalytics.getProductRecommendation())
        );
    }

    private StyleAnalytics createStyleAnalytics() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!sharedPref.contains("seekBarGoals1"))
            // No shared preferences exist
            return null;

        int [] seekBarState = getSeekBarState();
        Timepiece timepiece = new Timepiece(getApplicationContext());
        FavoriteSport favSport = new FavoriteSport(getApplicationContext());
        FavoriteDrink favDrink = new FavoriteDrink(getApplicationContext());

        return new StyleAnalytics(
                seekBarState[0],
                seekBarState[1],
                seekBarState[2],
                seekBarState[3],
                seekBarState[4],
                seekBarState[5],
                seekBarState[6],
                timepiece,
                favSport,
                favDrink );
    }

    private int[] getSeekBarState() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int [] seekBarState = {
                sharedPref.getInt("seekBarGoals1", 0),
                sharedPref.getInt("seekBarGoals2", 0),
                sharedPref.getInt("seekBarStyle1", 0),
                sharedPref.getInt("seekBarStyle2", 0),
                sharedPref.getInt("seekBarBudget", 0),
                sharedPref.getInt("seekBarUsage", 0),
                sharedPref.getInt("seekBarWrist", 0),
        };

        return seekBarState;
    }

    private void restoreProfile() {
        // Restore seek bars state
        int [] seekBarState = getSeekBarState();
        mSeekBarGoals1.setProgress(seekBarState[0]);
        mSeekBarGoals2.setProgress(seekBarState[1]);
        mSeekBarStyle1.setProgress(seekBarState[2]);
        mSeekBarStyle2.setProgress(seekBarState[3]);
        mSeekBarBudget.setProgress(seekBarState[4]);
        mSeekBarUsage.setProgress(seekBarState[5]);
        mSeekBarWrist.setProgress(seekBarState[6]);

        // Restore Timepiece materials
        if (mTimepiece.getMaterials() != null) {
            for (Timepiece.Material m : mTimepiece.getMaterials()) {
                int id = getResources().getIdentifier("checkbox_" + m.name(), "id", getPackageName());
                CheckBox checkBox = (CheckBox) findViewById(id);
                if (checkBox != null)
                    checkBox.setChecked(true);
            }
        }

        // Restore Timepiece style
        Spinner watchFaceShapeSpinner = (Spinner) findViewById(R.id.watchface_shape_spinner);
        watchFaceShapeSpinner.setSelection(mTimepiece.getWatchface().getShape().ordinal());
        Spinner watchFaceTypeSpinner = (Spinner) findViewById(R.id.watchface_type_spinner);
        watchFaceTypeSpinner.setSelection(mTimepiece.getWatchface().getType().ordinal());

        // Restore Favorite Sport state
        if (mFavSport.getSports() != null) {
            for (FavoriteSport.Sport s : mFavSport.getSports()) {
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
        if (parent.getId() == mWatchfaceShapeSpinner.getId()) {
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
