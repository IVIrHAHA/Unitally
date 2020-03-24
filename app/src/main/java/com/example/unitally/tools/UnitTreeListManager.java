package com.example.unitally.tools;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.unitally.app_modules.unit_tree_module.Calculator;
import com.example.unitally.app_modules.unit_tree_module.UnitTreeAdapter;
import com.example.unitally.app_modules.unit_tree_module.UnitTreeFragment;
import com.example.unitally.objects.Unit;
import com.example.unitally.objects.UnitWrapper;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Receive orders from main
 * Provide Fragment with adapter
 * Provide Adapter with Tier data
 */
public class UnitTreeListManager implements Calculator.CalculationListener {

    private static UnitTreeListManager INSTANCE = null;

    // Tracks position split between user-added and auto-added units
    private int mMFPosition, mCurrentBranchPosition;

    private ArrayList<UnitWrapper> mCurrentBranch;
    private final ArrayList<UnitWrapper> MASTER_FIELD;
    private Stack<Unit> mBranchHeadStack;
    private UnitTreeAdapter mActiveAdapter;
    private UnitTreeAdapter.UnitTreeListener mItemSelectionListener;
    private Unit mCurrentBranchHead;

    private UnitTreeListManager(UnitTreeAdapter.UnitTreeListener listener,
                                ArrayList<UnitWrapper> mf_list) {

        if(mf_list != null)
            MASTER_FIELD = mf_list;
        else
            MASTER_FIELD = new ArrayList<>();

        mCurrentBranch = MASTER_FIELD;

        mMFPosition = 0;
        mCurrentBranchPosition = 0;

        mBranchHeadStack = new Stack<>();

        mActiveAdapter = null;
        mItemSelectionListener = listener;
        mCurrentBranchHead = null;
    }

    /**
     * Create singleton instance of the UnitTreeListManager
     *
     * @return UnitTreeListManager Singleton
     */
    public static UnitTreeListManager getInstance(UnitTreeAdapter.UnitTreeListener listener,
                                                  ArrayList<UnitWrapper> masterField) {
        // Loading
        if(INSTANCE == null && masterField != null) {
            INSTANCE = new UnitTreeListManager(listener, masterField);

            boolean flag = false;
            for(int i = 0; i<masterField.size() && !flag; i++) {
                UnitWrapper wrapper = masterField.get(i);

                if(wrapper.getLabel() == UnitWrapper.AUTO_ADDED_LABEL) {
                    INSTANCE.mMFPosition = i;
                    INSTANCE.mCurrentBranchPosition = INSTANCE.mMFPosition;
                    flag = true;
                }
                else {
                    INSTANCE.mMFPosition = i+1;
                    INSTANCE.mCurrentBranchPosition = INSTANCE.mMFPosition;
                }
            }

            Log.i(UnitallyValues.LIFE_LOAD, "RECONSTRUCTED LIST: pos. "
                    + INSTANCE.mMFPosition + ", size. " + INSTANCE.size());

            return INSTANCE;
        }
        // creating new
        else if(INSTANCE == null) {
            INSTANCE = new UnitTreeListManager(listener, null);
            Log.i(UnitallyValues.LIFE_START, "NEW LIST CREATED");
        }

        return INSTANCE;
    }

    public ArrayList<UnitWrapper> getList(){
        return MASTER_FIELD;
    }

    /**
     * Creates and notifies the UnitListManager of a new adapter. In addition receives the
     * new unit branch.
     *
     * @param context Context to be used for the adapter
     * @param branch  Unit in which the branch will be built.
     * @return Adapter instance
     */
    public static UnitTreeAdapter adapterInstance(Context context, Unit branch) {
        UnitTreeAdapter adapter = new UnitTreeAdapter(context);
        INSTANCE.notifyNewAdapterCreated(adapter, branch);
        return adapter;
    }

    /*------------------------------------------------------------------------------------------------*/
    /*                                       Tree Management                                          */
    /*------------------------------------------------------------------------------------------------*/

    /**
     * Updates adapter instance and begins the branching process.
     *
     * @param newAdapter New adapter instance
     * @param branch     Unit containing the branching list
     */
    private void notifyNewAdapterCreated(UnitTreeAdapter newAdapter, Unit branch) {
        mActiveAdapter = null;
        mActiveAdapter = newAdapter;
        mActiveAdapter.setItemSelectionListener(mItemSelectionListener);
        branchInto(branch);
    }

    /**
     * Saves the state of previous branch and loads the new branch into the UI.
     *
     * @param branch Unit in which the UI will branch into. Null if working with Master-Field
     */
    private void branchInto(Unit branch) {
        // MASTER FIELD INSTANCE (no previous branch instance to save)
        if(branch == null) {
            mCurrentBranch = MASTER_FIELD;
            mCurrentBranchHead = null;
            mCurrentBranchPosition = mMFPosition;
        }

        // SAVING PREVIOUS BRANCH INSTANCE
        else {
            if (mCurrentBranchHead != null) {
                // Only called when branching into and not when branching out with revert().
                if(!mCurrentBranchHead.equals(branch))
                    mBranchHeadStack.push(mCurrentBranchHead);
            }
            // Triggers only when branching out of Master-Field.
            // Needed to appropriately return MF fragment when calling revert().
            else {
                mBranchHeadStack.push(null);
            }

        // BRANCH INSTANCE
            mCurrentBranchHead = branch;
            mCurrentBranch = process(branch.getSubunits());
        }

        if (loadAdapter())
            Log.d(UnitallyValues.LIST_MANAGER_PROCESS, "Adapter has been loaded");
        else
            Log.d(UnitallyValues.LIST_MANAGER_PROCESS, "Something occurred while trying to load adapter");
    }

    /**
     * Process direct subunits of branching unit, by placing the direct subunits at the top
     * of the list and all auto-added units at the bottom.
     *
     * @param rawUnitList Direct Subunits of branching unit
     * @return Completed branched list
     */
    private ArrayList<UnitWrapper> process(ArrayList<Unit> rawUnitList) {
        ArrayList<UnitWrapper> processedUnits = new ArrayList<>();

        for(int i = 0; i<rawUnitList.size(); i++) {
            Unit unprocessed = rawUnitList.get(i);
            int label = unprocessed.getLabel();

            if(label == 0)
                label = UnitWrapper.RETRIEVE_LABEL;

            mCurrentBranchPosition=i+1;
            processedUnits.add(UnitWrapper.wrapUnit(unprocessed, label));
        }

        return processedUnits;
    }

    /**
     * Set adapter list all at once.
     *
     * @return True if the adapter was loaded successfully. False otherwise.
     */
    private boolean loadAdapter() {
        try {
            mActiveAdapter.setList(mCurrentBranch);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /*------------------------------------------------------------------------------------------------*/
    /*                                      Branch Management                                         */
    /*------------------------------------------------------------------------------------------------*/

    /**
     * Reverts to previous branch.
     *
     *  **When reverting back to MasterField, mCurrentBranchHead == null and a
     *      a newInstance fragment is returned as expected with a null branch.
     *
     *      Exception occurs when trying to revert beyond the MasterField.
     *
     * @return Fragment loaded with previous Branch Head. Null if Master-Field
     */
    public UnitTreeFragment revert() {
        try {
            mCurrentBranchHead = mBranchHeadStack.pop();

            return UnitTreeFragment.newInstance(mCurrentBranchHead);
        } catch (Exception e) {
            mCurrentBranchHead = null;
            return null;
        }
    }

    /**
     * Add Unit to auto added section. Or update if already existing.
     *
     * @param unit Auto-Added Unit
     */
    private void autoAdd(Unit unit, Unit parent) {
        // Check auto-added section of the list
        // Check if already in list
        int checkIndex = mCurrentBranch.indexOf(UnitWrapper.wrapUnit(unit, UnitWrapper.AUTO_ADDED_LABEL));

        // Include with exiting Units
        if (checkIndex >= mMFPosition && checkIndex != -1) {
            UnitWrapper wrappedUnit = mCurrentBranch.get(checkIndex);
            wrappedUnit.include(unit, parent);

            if(wrappedUnit.peek().getCount() == 0) {
                mCurrentBranch.remove(checkIndex);
            }
        }

        // If none were found, then add it to the bottom of the list.
        else {
            if(unit.getCount() != 0)
                mCurrentBranch.add(UnitWrapper.wrapUnit(unit, parent, UnitWrapper.AUTO_ADDED_LABEL));
        }
    }

    private void updateAdapter() {
        //TODO: If auto-added, update adapter as a batch
        // Only update auto-added section
        mActiveAdapter.setList(mCurrentBranch);
        mActiveAdapter.update();
    }

    /**
     * Listener method called by the Calculator when subunit calculations have been completed.
     * <p>
     * Calls the autoAdd method (adds units to the Auto-Added section of the list). When finished,
     * calls to have the adapter updated.
     *
     * @param calculatedUnits The list of calculated units. (does not include the parent)
     */
    @Override
    public void onCalculationFinished(ArrayList<Unit> calculatedUnits, Unit headUnit) {
        // Add/Update results portion of the list
        for (Unit unit : calculatedUnits) {
            autoAdd(unit, headUnit);
        }

        if (headUnit.getCount() == 0) {
            mActiveAdapter.updateAutoAdd(mCurrentBranchPosition);
        } else {
            // Update the display
            updateAdapter();
        }

        // Only adds units if count is not zero
        Log.i(UnitallyValues.CALC_PROCESS, "Finished Calculating " + headUnit.getName()
                +": Count " + headUnit.getCount() + " : *Subunits checked " + calculatedUnits.size());
    }

    /**
     * Add user-added unit to the master-field.
     *
     * @param unit User-Added unit
     * @return True if the unit has been successfully added. False otherwise.
     */
    private boolean addToMF(Unit unit) {
        UnitWrapper wrappedUnit = UnitWrapper.wrapUnit(unit, UnitWrapper.MF_USER_ADDED_LABEL);

        MASTER_FIELD.add(mMFPosition, wrappedUnit);
        mActiveAdapter.setList(mCurrentBranch);
        ++mMFPosition;
        mCurrentBranchPosition = mMFPosition;

        return true;
    }

    private boolean addToTier(Unit unit) {
        UnitWrapper wrappedUnit = UnitWrapper.wrapUnit(unit,UnitWrapper.USER_ADDED_LABEL);
        mCurrentBranchHead.addSubunit(unit);
        mCurrentBranch.add(0,wrappedUnit);
        ++mCurrentBranchPosition;
        mActiveAdapter.setList(mCurrentBranch);

        return true;
    }

    /**
     * Behaves differently depending on the current state of the tree.
     *
     *  case 1: Updating a Unit in MF
     *  case 2: Updating a subunit worth
     *
     * @param modifiedUnit
     */
    public void update(UnitWrapper modifiedUnit) {
        Unit unit = modifiedUnit.peek();

        Log.i(UnitallyValues.CALC_PROCESS, "Starting Calculation Process...");

        // UPDATING MF UNITS
        if(unit.getLabel() == UnitWrapper.MF_USER_ADDED_LABEL) {
            if(!unit.isLeaf()) {
                Log.i(UnitallyValues.CALC_PROCESS, "Calculating: " + unit.getName() + "...");
                new Calculator(this).execute(unit);
            }
        }

        // UPDATING USER-ADDED UNITS
        else if(unit.getLabel() == UnitWrapper.USER_ADDED_LABEL) {
            Unit tempUnit = mCurrentBranchHead.getSubunit(unit.getName());

            if(tempUnit != null)
                tempUnit.setWorth(unit.getCount());
        }
    }

    public boolean add(Unit unit) {
        // User added a root to master-field
        if (mCurrentBranch.equals(MASTER_FIELD)) {
            return addToMF(unit);
        }
        // Not in master-field, rather in a branched unit
        else {
            return addToTier(unit);
        }
    }

    public ArrayList<Unit> getActiveUnits() {
        ArrayList<Unit> activeUnits = new ArrayList<>();

        if(mCurrentBranchHead != null) {
            activeUnits.add(mCurrentBranchHead);
        }

        for (int i = 0; i <= mCurrentBranchPosition - 1; i++) {
            Unit unit = mCurrentBranch.get(i).peek();
            activeUnits.add(unit);
        }

        return activeUnits;
    }

    public UnitWrapper get(Unit unit) {
        int index = mCurrentBranch.indexOf(
                UnitWrapper.wrapUnit(unit, UnitWrapper.RETRIEVE_LABEL));

        if (index < mCurrentBranchPosition && index >= 0) {
            return mCurrentBranch.get(index);
        }
        return null;
    }

    // Delete Unit completely from list
    public boolean remove(@Nullable Object o) {
        if ((o != null ? o.getClass() : null) == UnitWrapper.class) {
            UnitWrapper rm_unit = (UnitWrapper) o;

            // Remove Unit from Master_Field
            if (mCurrentBranch == MASTER_FIELD) {
                if (mCurrentBranch.remove(rm_unit)) {
                    mMFPosition--;
                    mCurrentBranchPosition--;

                    for (int i = mMFPosition; i < mCurrentBranch.size(); i++) {

                        if (mCurrentBranch.get(i).remove(rm_unit.peek())) {
                            mCurrentBranch.remove(i);
                            --i;
                        }
                    }
                    updateAdapter();
                    return mActiveAdapter.removeItem(rm_unit);
                }
            }

            // Remove Unit from CurrentBranch
            else if (mCurrentBranch.remove(rm_unit)) {
                mCurrentBranchPosition--;
                return mActiveAdapter.removeItem(rm_unit);
            }
        }
        return false;
    }

    public int size() {
        return mCurrentBranch.size();
    }

    public boolean isEmpty() {
        return mCurrentBranch.isEmpty();
    }

    public boolean contains(@Nullable Object o) {
        return false;
    }

    public void clear() {
        mActiveAdapter.clear();
        mCurrentBranch.clear();
        mCurrentBranchPosition = 0;
        mMFPosition = 0;
    }
}