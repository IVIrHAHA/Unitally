package com.example.unitally.Calculations;

import androidx.annotation.Nullable;

import com.example.unitally.objects.Unit;

public class ResultsUnitWrapper {
    private String mName;
    private double mCount;
    private boolean mSymbolBefore;
    private String mSymbol;

    public ResultsUnitWrapper(Unit unit) {
        mName = unit.getName();
        mSymbolBefore = unit.isSymbolBefore();
        mSymbol = unit.getSymbol();
        mCount = 0;
    }

    public void setCount(double count) {
        mCount = count;
    }

    public String getName() {
        return mName;
    }

    public double getCount() {
        return mCount;
    }

    public String getSymbol() {
        return mSymbol;
    }

    public String getCSstring() {
        return mSymbolBefore ? (mSymbol + " " + mCount) : (mCount + " " + mSymbol);
    }

    public boolean isSymbolBefore() {
        return mSymbolBefore;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof ResultsUnitWrapper) {
            ResultsUnitWrapper object = (ResultsUnitWrapper) obj;
            String objName = object.getName().toLowerCase();
            boolean s = objName.equals(mName.toLowerCase());

            return objName.equals(mName.toLowerCase());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return mName.hashCode();
    }
}
