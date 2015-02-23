package com.intel.ndg.chronos;

import android.graphics.Color;

import java.util.ArrayList;


class Watchface {
    enum Shape {
        ROUND,
        SQUARE
    }
    enum Type {
        WATCH,
        CHRONOGRAPH,
        DOUBLECHRONOGRAPH
    }
    private Shape mShape;
    private Type mType;
    private int mColor;

    public Watchface() {
        mShape = Shape.ROUND;
        mType = Type.CHRONOGRAPH;
        mColor = Color.BLUE;
    }

    public Watchface(Shape aShape, Type aType, int aColor) {
        mShape = aShape;
        mType = aType;
        mColor = aColor;
    }

    public Shape getShape() { return mShape; }
    public Type getType() { return mType; }
    public int getColor() { return mColor; }

    public void setShape(Shape aShape) {
        mShape = aShape;
    }

    public void setmType(Type aType) {
        mType = aType;
    }

    public void setmColor(int aColor) {
        mColor = aColor;
    }
}

/**
 * Created by vgore on 2/23/2015.
 */
public class Timepiece {
    ArrayList<String> mMaterials;
    Watchface mFace;

    public Timepiece() {
        mMaterials = new ArrayList<String>();
        mFace = new Watchface();
    }

    public void addMaterial(String name) {
        mMaterials.add(name);
    }

    public void removeMaterial(String name) {
        mMaterials.remove(name);
    }

    public void setWatchface(Watchface aFace) {
        mFace = aFace;
    }

    public Watchface getWatchface() {
        return mFace;
    }

}
