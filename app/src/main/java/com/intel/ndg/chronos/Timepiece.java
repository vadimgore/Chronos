package com.intel.ndg.chronos;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;


class Watchface {
    enum Shape {
        ROUND,
        SQUARE
    }
    enum Type {
        CHRONOGRAPH,
        ANALOGWATCH
    }
    private Shape mShape;
    private Type mType;
    Context mContext;
    SharedPreferences mSharedPref;

    public Watchface(Context aContext) {
        mContext = aContext;
        mShape = Shape.ROUND;
        mType = Type.CHRONOGRAPH;
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public Watchface(Shape aShape, Type aType) {
        mShape = aShape;
        mType = aType;
    }

    public Shape getShape() { return mShape; }
    public Type getType() { return mType; }

    public void setShape(Shape aShape) {
        mShape = aShape;
    }

    public void setType(Type aType) {
        mType = aType;
    }

    public void saveState() {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt("watchface_shape", mShape.ordinal());
        editor.putInt("watchface_type", mType.ordinal());

        editor.commit();
    }

    public void restoreState() {
        int i = mSharedPref.getInt("watchface_shape", -1);
        if (i > -1)
            mShape = Shape.values()[i];

        i = mSharedPref.getInt("watchface_type", -1);
        if (i > -1)
            mType = Type.values()[i];
    }
}

/**
 * Created by vgore on 2/23/2015.
 */
public class Timepiece {

    enum StrapMaterial {
        Steel,
        Leather
    }

    ArrayList<String> mProperties;
    StrapMaterial mStrapMaterial;
    Watchface mFace;
    Context mContext;
    SharedPreferences mSharedPref;

    public Timepiece(Context aContext) {
        mContext = aContext;
        mProperties = new ArrayList<>();
        mStrapMaterial = StrapMaterial.Leather;
        mFace = new Watchface(mContext);
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        restoreState();
    }

    public ArrayList<String> getProperties() {
        return mProperties;
    }

    public void addProperty(String s) {
        if (!mProperties.contains(s))
            mProperties.add(s);
    }

    public void removeProperty(String s) {
        if (mProperties.contains(s))
            mProperties.remove(s);
    }

    public void setWatchface(Watchface aFace) {
        mFace = aFace;
    }

    public void setStrapMaterial(StrapMaterial aMaterial) {
        mStrapMaterial = aMaterial;
    }

    public Watchface getWatchface() {
        return mFace;
    }

    public StrapMaterial getStrapMaterial() {
        return mStrapMaterial;
    }

    public void saveState() {

        mFace.saveState();
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt("StrapMaterial", mStrapMaterial.ordinal());
        StringBuilder strBuilder = new StringBuilder();
        for (String s : mProperties) {
            strBuilder.append(s);
            strBuilder.append(",");
        }
        editor.putString("Properties", strBuilder.toString());
        editor.commit();
    }

    public void restoreState() {
        mFace.restoreState();
        mStrapMaterial = StrapMaterial.values()[mSharedPref.getInt("StrapMaterial", 0)];
        String props = mSharedPref.getString("Properties", "");
        mProperties.addAll(Arrays.asList(props.split("\\s*,\\s*")));
    }
}
