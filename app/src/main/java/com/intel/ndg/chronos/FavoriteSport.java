package com.intel.ndg.chronos;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

/**
 * Created by Vadim on 2/23/2015.
 */
public class FavoriteSport {
    enum Sport {
        Football,
        Basketball,
        Golf,
        Formula_1,
        Watersports
    }

    ArrayList<Sport> mFavSports;
    Context mContext;
    SharedPreferences mSharedPref;

    public FavoriteSport(Context context) {
        mContext = context;
        mFavSports = new ArrayList<>();
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        restoreState();
    }

    public void addSport(Sport aSport) {
        if (!mFavSports.contains(aSport))
            mFavSports.add(aSport);
    }

    public void removeSport(Sport aSport) {
        if (mFavSports.contains(aSport))
            mFavSports.remove(aSport);
    }

    public ArrayList<Sport> getSports() {
        return mFavSports;
    }

    public void saveState() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        for (Sport s : mFavSports) {
            editor.putInt(s.name(), s.ordinal());
        }

        editor.commit();
    }

    public void restoreState() {
        for (Sport s : Sport.values()) {
            int i = mSharedPref.getInt(s.name(), -1);
            if (i > -1)
                mFavSports.add(s);
        }
    }
}
