package com.intel.ndg.chronos;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

public class FavoriteDrink {
    enum Drink {
        tea,
        chocolate,
        coffee
    }

    ArrayList<Drink> mFavDrinks;
    Context mContext;
    SharedPreferences mSharedPref;

    public FavoriteDrink(Context context) {
        mContext = context;
        mFavDrinks = new ArrayList<>();
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        restoreState();
    }

    public void addDrink(Drink aDrink) {
        if (!mFavDrinks.contains(aDrink))
            mFavDrinks.add(aDrink);
    }

    public void removeDrink(Drink aDrink) {
        if (mFavDrinks.contains(aDrink))
            mFavDrinks.remove(aDrink);
    }

    public ArrayList<Drink> getDrinks() {
        return mFavDrinks;
    }

    public void saveState() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        for (Drink s : mFavDrinks) {
            editor.putInt(s.name(), s.ordinal());
        }

        editor.commit();
    }

    public void restoreState() {
        for (Drink s : Drink.values()) {
            int i = mSharedPref.getInt(s.name(), -1);
            if (i > -1)
                mFavDrinks.add(s);
        }
    }
}
