package com.intel.ndg.chronos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vadim on 2/24/2015.
 */



public class StyleAnalytics {

    enum Factor {
        LOW,
        LOW_MEDIUM,
        MEDIUM,
        MEDIUM_HIGH,
        HIGH
    }

    enum WristSize {
        NARROW,
        AVERAGE,
        WIDE
    }

    enum Usage {
        DAILY,
        MODERATE,
        OCCASIONAL
    }

    private Factor mCoolness;
    private Factor mVisibility;
    private Factor mImpression;
    private Factor mConvenience;
    private Factor mBudget;
    private Usage mUsage;
    private WristSize mWristSize;
    private Timepiece mTimepiece;
    private FavoriteSport mFavSport;
    private FavoriteDrink mFavDrink;

    private int mStyleScore = 0;

    StyleAnalytics(int coolness, int visibility, int impression, int convenience,
                   int budget, int usage, int wristSize, Timepiece timepiece,
                   FavoriteSport favSport, FavoriteDrink favDrink) {

        mCoolness = Factor.values()[coolness];
        mVisibility = Factor.values()[visibility];
        mImpression = Factor.values()[impression];
        mConvenience = Factor.values()[convenience];
        mBudget = Factor.values()[budget];
        mUsage = Usage.values()[usage];
        mWristSize = WristSize.values()[wristSize];
        mTimepiece = timepiece;
        mFavSport = favSport;
        mFavDrink = favDrink;

        buildStyleScore();
    }

    private void buildStyleScore() {
        mStyleScore = mCoolness.ordinal() + mVisibility.ordinal() +
                mImpression.ordinal() + mConvenience.ordinal() + mUsage.ordinal();

    }

    public int getStyleScore() {
        return mStyleScore;
    }

    public int getBudgetScore() {
        return mBudget.ordinal();
    }

    public String getProductRecommendation() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("A ");
        strBuilder.append(mTimepiece.getWatchface().getShape().name());
        strBuilder.append(" face ");
        strBuilder.append(mTimepiece.getWatchface().getType().name());
        strBuilder.append(" for ");
        strBuilder.append(mUsage.name());
        strBuilder.append(" usage on ");
        strBuilder.append(mWristSize.name());
        strBuilder.append(" wrist. ");
        strBuilder.append("Preferred materials: ");
        int i = 0;
        int size = mTimepiece.getMaterials().size();
        for (Timepiece.Material m : mTimepiece.getMaterials()) {
            strBuilder.append(m.name());
            if (i < size-1)
                strBuilder.append(", ");
            else
                strBuilder.append(".");
            i++;
        }

        return strBuilder.toString();
    }

    public String getFavSports() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Customer's favorite sports are: ");
        int i = 0;
        int size = mFavSport.getSports().size();
        for (FavoriteSport.Sport s : mFavSport.getSports()) {
            strBuilder.append(s.name());
            if (i < size-1)
                strBuilder.append(", ");
            else
                strBuilder.append(".");
        }

        return strBuilder.toString();
    }

    public String getFavDrinks() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Customer's favorite drinks are: ");
        int i = 0;
        int size = mFavDrink.getDrinks().size();
        for (FavoriteDrink.Drink d : mFavDrink.getDrinks()) {
            strBuilder.append(d.name());
            if (i < size-1)
                strBuilder.append(", ");
            else
                strBuilder.append(".");
        }

        return strBuilder.toString();
    }

}
