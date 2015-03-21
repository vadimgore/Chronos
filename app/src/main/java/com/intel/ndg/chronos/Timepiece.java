package com.intel.ndg.chronos;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Timepiece {

    enum Shape {
        ROUND,
        SQUARE
    }
    enum Type {
        CHRONOGRAPH,
        ANALOGWATCH
    }
    enum Strap {
        STEEL,
        LEATHER
    }

    enum Collection {
        NONE,
        CARRERA,
        AQUARACER,
        MONACO,
        FORMULA1
    }

    private Context mContext;
    private Shape mShape;
    private Type mType;
    private Strap mStrap;
    private Collection mCollection;
    private SharedPreferences mSharedPref;

    public Timepiece(Context context) {
        mContext = context;
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        restoreState();
    }

    public Collection getCollection() { return mCollection; }
    public Shape getShape() { return mShape; }
    public Type getType() { return mType; }
    public Strap getStrap() {
        return mStrap;
    }

    public void setCollection(Collection collection) {mCollection = collection;}
    public void setShape(Shape shape) {mShape = shape;}
    public void setType(Type type) {mType = type;}
    public void setStrap(Strap strap) {mStrap = strap;}

    public void saveState() {

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt("timepeice_collection", mCollection.ordinal());
        editor.putInt("timepeice_type", mType.ordinal());
        editor.putInt("timepeice_shape", mShape.ordinal());
        editor.putInt("timepeice_strap", mStrap.ordinal());

        editor.commit();
    }

    public void restoreState() {
        mCollection = Collection.values()[mSharedPref.getInt("timepeice_collection", 0)];
        mType = Type.values()[mSharedPref.getInt("timepeice_type", 0)];
        mShape = Shape.values()[mSharedPref.getInt("timepeice_shape", 0)];
        mStrap = Strap.values()[mSharedPref.getInt("timepeice_strap", 0)];
    }

}
