package com.example.unitally.Calculations;

import androidx.annotation.Nullable;

import com.example.unitally.objects.Unit;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ResultsUnitWrapper {
    private String mName;
    private double mCount;
    private boolean mSymbolBefore;
    private String mSymbol;
    private List<Unit> mSubunits;

    public ResultsUnitWrapper(Unit unit) {
        mName = unit.getName();
        mSymbolBefore = unit.isSymbolBefore();
        mSymbol = unit.getSymbol();
        mCount = 0;
        mSubunits =
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

    public List<ResultsUnitWrapper> getSubunits() {
        List<ResultsUnitWrapper> subunitsWrapperList = new ArrayList<>();

        for(Unit unit:mSubunits) {
            subunitsWrapperList.add(new ResultsUnitWrapper(unit));
        }

        return subunitsWrapperList;
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
