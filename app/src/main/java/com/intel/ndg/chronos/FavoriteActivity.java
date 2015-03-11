package com.intel.ndg.chronos;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

public class FavoriteActivity {
    enum Activity {
        Football,
        Basketball,
        Golf,
        Formula_1,
        Diving
    }

    ArrayList<Activity> mFavActivities;
    Context mContext;
    SharedPreferences mSharedPref;

    public FavoriteActivity(Context context) {
        mContext = context;
        mFavActivities = new ArrayList<>();
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        restoreState();
    }

    public void addActivity(Activity aActivity) {
        if (!mFavActivities.contains(aActivity))
            mFavActivities.add(aActivity);
    }

    public void removeActivity(Activity aActivity) {
        if (mFavActivities.contains(aActivity))
            mFavActivities.remove(aActivity);
    }

    public ArrayList<Activity> getActivities() {
        return mFavActivities;
    }

    public void saveState() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        for (Activity s : mFavActivities) {
            editor.putInt(s.name(), s.ordinal());
        }

        editor.commit();
    }

    public void restoreState() {
        for (Activity s : Activity.values()) {
            int i = mSharedPref.getInt(s.name(), -1);
            if (i > -1)
                mFavActivities.add(s);
        }
    }
}
