package com.example.unitally.objects;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.unitally.tools.UnitallyValues;

import java.util.ArrayList;
import java.util.List;

/**
 * Will have properties which communicate with UnitTreeAdapter on how to handle this Unit.
 */
public class UnitWrapper {
    public static final int USER_ADDED_LABEL = "Unit has been added by user".hashCode();
    public static final int MF_USER_ADDED_LABEL = "Unit has been added to master-field by user".hashCode();
    public static final int AUTO_ADDED_LABEL = "Unit has been auto-added".hashCode();


    private Unit mUnit;

    private UnitWrapper(Unit unit) {
        mUnit = unit;
    }

    // Eventually return a bundle
    public Unit unwrap() {
        return mUnit;
    }

    /**
     * Keep in mind that we also want to display where Unit results are coming from.
     * TODO: Add a way to implement ^^^
     *
     * @param unit
     * @return
     */
    public boolean merge(Unit unit) {
        if(mUnit.equals(unit)) {
            mUnit.setCount(mUnit.getCount() + unit.getCount());
            return true;
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return mUnit.getName();
    }

    @Override
    public boolean equals(Object obj) {
        Log.d(UnitallyValues.QUICK_CHECK, "CHECKING unit wrapper equals method");

        // Check in case unit is wrapped
        try {
            if(obj == null) {
                return false;
            }
            else if (obj.getClass() == getClass()) {
                Unit wrappedUnit = ((UnitWrapper) obj).unwrap();
                return mUnit.equals(wrappedUnit);
            }
            else
                return mUnit.equals(obj);

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return mUnit.hashCode();
    }

/*------------------------------------------------------------------------------------------------*/
/*                                   Wrapping Factory                                             */
/*------------------------------------------------------------------------------------------------*/

    /**
     * Wrap sections of the list
     *
     * @param list
     * @return
     */
    public static ArrayList<UnitWrapper> wrapUnits(List<Unit> list, int label) {
        ArrayList<UnitWrapper> wrappedUnits = new ArrayList<>();

        for(Unit unit:list) {
            // TODO: HANDLE UNIT WRAPPING
        }

        return wrappedUnits;
    }

    /**
     * Wrap individual Units3
     * @param unit
     * @param label
     * @return
     */
    public static UnitWrapper wrapUnit(Unit unit, int label) {
        UnitWrapper wrapper = new UnitWrapper(unit);

        switch (label) {
            // TODO: Add necessary properties to wrapper
        }

        return wrapper;
    }
}
