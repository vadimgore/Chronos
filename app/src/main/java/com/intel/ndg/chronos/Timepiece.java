package com.intel.ndg.chronos;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;


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

    enum Material {
        Gold,
        Titan,
        Diamonds,
        Leather
    }

    ArrayList<Material> mMaterials;
    Watchface mFace;
    Context mContext;
    SharedPreferences mSharedPref;

    public Timepiece(Context aContext) {
        mContext = aContext;
        mMaterials = new ArrayList<>();
        mFace = new Watchface(mContext);
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        restoreState();
    }

    public ArrayList<Material> getMaterials() {
        return mMaterials;
    }

    public void addMaterial(Material m) {
        if (!mMaterials.contains(m))
            mMaterials.add(m);
    }

    public void removeMaterial(Material m) {
        if (mMaterials.contains(m))
            mMaterials.remove(m);
    }

    public void setWatchface(Watchface aFace) {
        mFace = aFace;
    }

    public Watchface getWatchface() {
        return mFace;
    }

    public void saveState() {

        mFace.saveState();
        SharedPreferences.Editor editor = mSharedPref.edit();
        for (Material m : mMaterials) {
            editor.putInt(m.name(), m.ordinal());
        }

        editor.commit();
    }

    public void restoreState() {
        mFace.restoreState();
        for (Material m : Material.values()) {
            int i = mSharedPref.getInt(m.name(), -1);
            if (i > -1)
                mMaterials.add(m);
        }
    }
}
