package com.example.unitally.objects;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.unitally.tools.UnitallyValues;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class UnitWrapper implements Serializable {
    private Unit mUnit;
    private final int LABEL, ID;

    private Hashtable<Unit, Unit> mConstituents;

    private UnitWrapper(Unit unit, int label, int id) {
        mUnit = unit;
        ID = id;
        LABEL = label;
        mConstituents = new Hashtable<>();
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

    public boolean remove(Unit key) {
        Unit removed = mConstituents.remove(key);

        if(removed != null) {
            mUnit.increment_decrement(-removed.getCount());

            return mUnit.getCount() == 0;
        }
        return false;
    }

    /**
     *
     * @param unit
     * @param parent    Will be used as the key
     * @return
     */
    public boolean include(Unit unit, Unit parent) {
        if(mUnit.equals(unit)) {
            // Check if already in table
            if(mConstituents.containsKey(parent)) {
                Unit temp = mConstituents.get(parent);

                // Make sure Unit exists
                if(temp != null) {
                    int count_diff = unit.getCount()-temp.getCount();
                    temp.setCount(unit.getCount());

                    mUnit.increment_decrement(count_diff);
                    return true;
                }
                else {
                    throw new RuntimeException(UnitWrapper.class.toString()
                            + " NULL OBJECT IN TABLE");
                }
            }

            // New results
            else {
                mConstituents.put(parent, unit);
                mUnit.increment_decrement(unit.getCount());
                return true;
            }
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
        return (mUnit.hashCode()/31) + getId() * 31;
    }

/*------------------------------------------------------------------------------------------------*/
/*                                   Wrapping Factory                                             */
/*------------------------------------------------------------------------------------------------*/
    public static final int USER_ADDED_LABEL = "Unit has been added by user".hashCode();
    public static final int MF_USER_ADDED_LABEL = "Unit has been added to master-field by user".hashCode();
    public static final int AUTO_ADDED_LABEL = "Unit has been auto-added".hashCode();
    public static final int RETRIEVE_LABEL = "Get Label from unit hash".hashCode();
    public static final int STANDARD_SUB_LABEL = "Standard subunit".hashCode();

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
            wrappedUnits.add(wrapUnit(unit, label));
        }

        //

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
            unit.setLabel(label);
            wrapper = new UnitWrapper(unit, label, ++AAID);
        }

        else if(label == MF_USER_ADDED_LABEL) {
            unit.setLabel(label);
            wrapper = new UnitWrapper(unit, label, ++MFID);
        }

        else if(label == USER_ADDED_LABEL) {
            unit.setLabel(label);
            wrapper = new UnitWrapper(unit, label, ++UAID);
        }

        else if(label == RETRIEVE_LABEL) {
            int unitLabel = unit.getLabel();

            if(unitLabel == 0) {
                // Used for default subunits
                wrapper = new UnitWrapper(unit, STANDARD_SUB_LABEL, ++AAID);
            }
            else {
                // Returned when staging
                Log.d(UnitallyValues.QUICK_CHECK, "Returning for staging: " + unit.getName());
                wrapper = new UnitWrapper(unit, unitLabel,0);
            }
        }

        else {
            throw new RuntimeException("Error occurred while wrapping units");
        }

        return wrapper;
    }

    /**
     * Used when auto adding a unit. This method ensures the wrapped unit
     * does not have a count value set at start.
     *
     *      * The reason being is to maintain absolute control over where
     *          values are coming from. Hence, having a parent passed.
     *
     * @param unit
     * @param parent
     * @param label
     * @return
     */
    public static UnitWrapper wrapUnit(Unit unit, Unit parent, int label) {
        UnitWrapper wrapper;

        if(label == AUTO_ADDED_LABEL) {
            // Ensure the count value stays at zero from start
            Unit temp = unit.copy();
            temp.setCount(0);
            wrapper = new UnitWrapper(temp, label, ++AAID);
            // pass the original unit in to record count value.
            wrapper.include(unit,parent);
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
