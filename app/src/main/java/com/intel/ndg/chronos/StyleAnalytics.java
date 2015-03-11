package com.intel.ndg.chronos;

public class StyleAnalytics {

    enum Range {
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

    private Range mCoolness;
    private Range mVisibility;
    private Range mImpression;
    private Range mConvenience;
    private Range mBudget;
    private Usage mUsage;
    private WristSize mWristSize;
    private Timepiece mTimepiece;
    private FavoriteActivity mFavActivity;
    private FavoriteDrink mFavDrink;

    private int mStyleScore = 0;

    StyleAnalytics(int coolness, int visibility, int impression, int convenience,
                   int budget, int usage, int wristSize, Timepiece timepiece,
                   FavoriteActivity favActivity, FavoriteDrink favDrink) {

        mCoolness = Range.values()[coolness];
        mVisibility = Range.values()[visibility];
        mImpression = Range.values()[impression];
        mConvenience = Range.values()[convenience];
        mBudget = Range.values()[budget];
        mUsage = Usage.values()[usage];
        mWristSize = WristSize.values()[wristSize];
        mTimepiece = timepiece;
        mFavActivity = favActivity;
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
        String prodRec = "";
        prodRec += "Watch face: " + mTimepiece.getWatchface().getShape().name() + "\n";
        prodRec += "Watch type: " + mTimepiece.getWatchface().getType().name() + "\n";
        prodRec += "Strap material: " + mTimepiece.getStrapMaterial().name() + "\n";
        prodRec += "Usage: " + mUsage.name() + "\n";
        prodRec +="Wrist size: " + mWristSize.name() + "\n";

        return prodRec;
    }

    public String getFavActivities() {
        StringBuilder strBuilder = new StringBuilder();
        int i = 0;
        int size = mFavActivity.getActivities().size();
        for (FavoriteActivity.Activity s : mFavActivity.getActivities()) {
            strBuilder.append(s.name());
            if (i < size-1)
                strBuilder.append(", ");
        }

        return strBuilder.toString();
    }

    public String getFavDrinks() {
        StringBuilder strBuilder = new StringBuilder();
        int i = 0;
        int size = mFavDrink.getDrinks().size();
        for (FavoriteDrink.Drink d : mFavDrink.getDrinks()) {
            strBuilder.append(d.name());
            if (i < size-1)
                strBuilder.append(", ");
        }

        return strBuilder.toString();
    }
}
