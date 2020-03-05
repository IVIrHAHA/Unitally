package com.example.unitally.objects;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.unitally.tools.UnitallyValues;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UnitWrapper implements Serializable {
    private Unit mUnit;
    private final int LABEL, ID;

    private UnitWrapper(Unit unit, int label, int id) {
        mUnit = unit;

        ID = id;
        LABEL = label;
    }

    /**
     * Unwrap old unit and reconfigure
     *
     * @param unit
     */
    public void update(Unit unit) {

    }

    public Unit peek() {
        return mUnit;
    }

    public int getLabel() {
        return LABEL;
    }

    public int getId() {
        return ID;
    }

    /**
     * Keep in mind that we also want to display where Unit results are coming from.
     * TODO: Add a way to implement ^^^
     *
     * @param unit
     * @return
     */
    public boolean include(Unit unit) {
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
        // Check in case unit is wrapped
        if(obj == null) {
            return false;
        }
        else if (obj.getClass() == getClass()) {
            UnitWrapper wrappedUnit = ((UnitWrapper) obj);

            return wrappedUnit.getLabel() == LABEL && mUnit.equals(wrappedUnit.peek());
        }

        return false;
    }

    @Override
    public int hashCode() {
        // TODO: 31 * i == (i << 5) - i  <---- Investigate this

        return (mUnit.getName().hashCode());
    }

/*------------------------------------------------------------------------------------------------*/
/*                                   Wrapping Factory                                             */
/*------------------------------------------------------------------------------------------------*/
    public static final int USER_ADDED_LABEL = "Unit has been added by user".hashCode();
    public static final int MF_USER_ADDED_LABEL = "Unit has been added to master-field by user".hashCode();
    public static final int AUTO_ADDED_LABEL = "Unit has been auto-added".hashCode();

    private static int  UAID = 1000,
                        MFID = 2000,
                        AAID = 3000;
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
     * Wrap individual Units
     * @param unit
     * @param label
     * @return
     */
    public static UnitWrapper wrapUnit(Unit unit, int label) {
        UnitWrapper wrapper;

        if(label == AUTO_ADDED_LABEL) {
            wrapper = new UnitWrapper(unit, label, ++AAID);
        }

        else if(label == MF_USER_ADDED_LABEL) {
            wrapper = new UnitWrapper(unit, label, ++MFID);
        }

        else if(label == USER_ADDED_LABEL) {
            wrapper = new UnitWrapper(unit, label, ++UAID);
        }

        else {
            Log.d(UnitallyValues.BUGS, "Error identifying an element in "
                                                + UnitWrapper.class.toString());
            wrapper = null;
        }

        return wrapper;
    }

    public static UnitWrapper forge(Unit unit) {
        return new UnitWrapper(unit, 0, 0);
    }
}
