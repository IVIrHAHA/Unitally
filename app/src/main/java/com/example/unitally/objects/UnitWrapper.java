package com.example.unitally.objects;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.unitally.tools.Exceptions.FailedToWrapException;
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

    public Unit getParent() {
        if(mConstituents.keySet().size() != 1)
            return null;
        else
            return mConstituents.keySet().iterator().next();
    }

    private void setParent(Unit parent) {
        mConstituents.clear();
        // This should reference this Unit
        mConstituents.put(parent, mUnit);
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
    public static ArrayList<UnitWrapper> wrapUnits(ArrayList<Unit> list){
        ArrayList<UnitWrapper> wrappedUnits = new ArrayList<>();

        for(Unit unit:list) {
            wrappedUnits.add(wrapUnit(unit,null, RETRIEVE_LABEL));
        }

        return wrappedUnits;
    }

    /**
     * Only to be used as a mask to retrieve true reference in ListManager.
     *
     * @param unit
     * @param label
     * @return
     */
    public static UnitWrapper forge(Unit unit, int label) {
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
                wrapper = new UnitWrapper(unit, unitLabel,0);
            }
        }

        else {
            throw new RuntimeException("Error occurred while wrapping units");
        }

        return wrapper;
    }

    /**
     *
     * @param unit
     * @param parent
     * @param label
     * @return
     */
    public static UnitWrapper wrapUnit(Unit unit, Unit parent, int label) {
        UnitWrapper wrapper;

        // Enters only when an AA Unit is initially created
        if(label == AUTO_ADDED_LABEL) {
            // Ensure the count value stays at zero from start
            Unit temp = unit.copy();
            temp.setCount(0);
            wrapper = new UnitWrapper(temp, label, ++AAID);
            // pass the original unit in to record count value.
            wrapper.include(unit,parent);
        }

        // Enters only when UA Unit is initially created
        else if(label == USER_ADDED_LABEL) {
            unit.setLabel(USER_ADDED_LABEL);
            wrapper = new UnitWrapper(unit,label,++UAID);
            wrapper.setParent(parent);
        }

        // Enters only when a MF Unit is initially created
        else if(label == MF_USER_ADDED_LABEL) {
            unit.setLabel(label);
            wrapper = new UnitWrapper(unit, label, ++MFID);
        }

        // Enters when rebuilding a branch
        else if(label == RETRIEVE_LABEL) {
            int unitLabel = unit.getLabel();

            // Standard Unit
            if(unitLabel == 0) {
                // Used for default subunits
                wrapper = new UnitWrapper(unit, STANDARD_SUB_LABEL, ++AAID);
            }
            // UA unit
            else {
                wrapper = new UnitWrapper(unit,unitLabel, ++UAID);
            }
            wrapper.setParent(parent);
        }

        else {
            Log.d(UnitallyValues.BUGS, "Error identifying an element in "
                    + UnitWrapper.class.toString());
            wrapper = null;
        }

        return wrapper;
    }
}
