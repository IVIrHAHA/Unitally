package com.example.unitally.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.unitally.MainActivity;
import com.example.unitally.R;
import com.example.unitally.app_modules.unit_tree_module.Calculator;
import com.example.unitally.app_modules.unit_tree_module.UnitTreeAdapter;
import com.example.unitally.app_modules.unit_tree_module.UnitTreeFragment;
import com.example.unitally.objects.Unit;
import com.example.unitally.objects.UnitWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Class intractability
 *
 * Tree Traversal
 *  A. UnitTreeManager.next(int index) or UnitTreeManager.next(Unit unit)
 *  B. UnitTreeManager.back()
 *
 * Branch Traversal
 *  A. UnitTreeManager.get(int index)
 *  B. UnitTreeManager.get(Unit unit)
 *  C. UnitTreeManager.add(Unit unit)
 *  D. UnitTreeManager.remove(int index)
 *  E. UnitTreeManager.remove(Unit unit)
 *
 *  Notify MainActivity when a Unit has been selected
 *
 *  This class acts much like a Pager object, or a wrapper for UnitTreeFragment
 */

// TODO: Need to fix lifecycle loading and saving
// TODO: 'Update' methods need to work more efficiently
// TODO: Update counts in AutoAdded units in real time
// TODO: Need to find a better way to assign worth to "User-Added" units

public class UnitTreeManager
                    implements Calculator.CalculationListener,
                                UnitTreeAdapter.UnitTreeListener {

    private static final String UNIT_TREE_FRAGMENT = "com.example.unitally.UnitTreeFragment";
    private static final int SAVE_IMMEDIATE = 0;
    private static final int SAVE_APPLY = 1;

    private static UnitTreeManager INSTANCE = null;
    private int mContainer;
    private FragmentManager mFragManager;
    private UnitChosenListener mUnitChosenListener;

    private static ListStateManager mListStateManager;

    // Tracks position split between user-added and auto-added units
    private int mMFPosition, mCurrentBranchPosition;

    private ArrayList<UnitWrapper> mCurrentBranch;
    private final ArrayList<UnitWrapper> MASTER_FIELD;
    private Stack<Unit> mBranchHeadStack;
    private UnitTreeAdapter mActiveAdapter;
    private Unit mCurrentBranchHead;

    private UnitTreeManager(Context context, UnitChosenListener listener) {
        mFragManager = ((MainActivity)context).getSupportFragmentManager();
        mUnitChosenListener = listener;

        MASTER_FIELD = new ArrayList<>();
        mCurrentBranch = MASTER_FIELD;

        mMFPosition = 0;
        mCurrentBranchPosition = 0;

        mBranchHeadStack = new Stack<>();
        mActiveAdapter = null;
        mCurrentBranchHead = null;

        mContainer = 0;
    }

    private UnitTreeManager(Context context, UnitChosenListener listener,
                            ArrayList<UnitWrapper> mf_list) {
        mFragManager = ((MainActivity)context).getSupportFragmentManager();
        mUnitChosenListener = listener;

        MASTER_FIELD = mf_list;
        mCurrentBranch = MASTER_FIELD;

        mMFPosition = mf_list.size();
        mCurrentBranchPosition = mMFPosition;

        mBranchHeadStack = new Stack<>();
        mActiveAdapter = null;
        mCurrentBranchHead = null;

        mContainer = 0;
    }

    public void setContainer(int container) {
        mContainer = container;
    }

    /**
     * Create singleton instance of the UnitTreeManager
     * or return the instance already created.
     *
     * @return Instance of UnitTreeManager
     */

    public static UnitTreeManager getInstance(Context context, boolean load) {
        // If being created for the first time, must come from MainActivity.
        // Otherwise return already created instance.
        if (INSTANCE == null) {
            // Ensure if INSTANCE is being created for the first time, correct listener is passed.
            if (context instanceof UnitChosenListener) {
                UnitChosenListener listener = (UnitChosenListener) context;

                mListStateManager = new ListStateManager(context);

                if (mListStateManager.isReady() && load) {
                    // Try and load. If load fails return a clean new List
                    INSTANCE = mListStateManager.load(context, listener);
                    INSTANCE.updateAll();
                } else {
                    INSTANCE = new UnitTreeManager(context, listener);
                }

            } else {
                throw new RuntimeException(context.toString()
                        + " must implement UnitChosenListener");
            }
        }

        return INSTANCE;
    }

/*------------------------------------------------------------------------------------------------*/
/*                              Adapter & Fragment Creation Process                               */
/*------------------------------------------------------------------------------------------------*/
    /**
     *  STEP 1: CALLED BY UnitTreeFragment
     *
     * Creates and notifies the UnitTreeManager of a new adapter. In addition receives the
     * new unit branch.
     *
     * This method is to be used when a new UnitTreeFragment is created.
     *
     * @param context Context to be used for the adapter
     * @param branch  Unit in which the branch will be built.
     * @return Adapter instance populated with Wrapped Units.
     */
    public static UnitTreeAdapter adapterInstance(Context context, Unit branch) {
        UnitTreeAdapter adapter = new UnitTreeAdapter(context);
        // Set the adapter creation process in motion
        Log.i(UnitallyValues.LIST_MANAGER_PROCESS, "LCC: NEW ADAPTER CREATED");
        INSTANCE.notifyNewAdapterCreated(adapter, branch);

        return adapter;
    }

    /**
     * STEP 2: CALLED BY static adapterInstance to create new adapter instance.
     *
     * Updates adapter instance and begins the branching process.
     * Needed for accessing non-static variable instances.
     *
     * @param newAdapter New adapter instance
     * @param branch     Unit containing the branching list
     */
    private void notifyNewAdapterCreated(UnitTreeAdapter newAdapter, Unit branch) {
        mActiveAdapter = null;
        mActiveAdapter = newAdapter;
        mActiveAdapter.setItemSelectionListener(this);
        branchInto(branch);
    }

    /**
     * STEP 3: CALLED WHEN ADAPTER INSTANCE HAS BEEN CREATED AND NOW NEEDS TO BE FILLED
     *
     * Saves the state of previous branch and loads the new branch into the UI.
     *
     * Only called when a new adapter has been created.
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
                // Only called when branching into and not when branching out with back().
                if(!mCurrentBranchHead.equals(branch))
                    mBranchHeadStack.push(mCurrentBranchHead);
            }
            // Triggers only when branching out of Master-Field.
            // Needed to appropriately return MF fragment when calling back().
            else {
                mBranchHeadStack.push(null);
            }

        // BRANCH INSTANCE
            mCurrentBranchHead = branch;
            mCurrentBranch = process(branch.getSubunits());
        }

        // Final step in the adapter creation process
        mActiveAdapter.setList(mCurrentBranch);
    }

    /**
     * STEP 4: CALLED WHEN NEW BRANCH NEEDS TO BE INSTANTIATED
     *
     * Process direct subunits of branching unit, by placing the direct subunits at the top
     * of the list and all auto-added units at the bottom.
     *
     * Takes in a list of unwrapped units, with unknown labels, and wraps them accordingly.
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
            processedUnits.add(UnitWrapper.wrapUnit(unprocessed
                    ,mCurrentBranchHead
                    ,label));
        }

        return processedUnits;
    }

    /**
     * Starts a UnitTreeFragment
     *
     * @param fragment Fragment created by either the "start()", "back()" or "next()" methods.
     */
    private void startFragment(UnitTreeFragment fragment) {
        if(mContainer != 0) {
            FragmentTransaction transaction = mFragManager.beginTransaction();

            if (mFragManager.findFragmentByTag(UNIT_TREE_FRAGMENT) != null) {
                transaction.replace(mContainer, fragment, UNIT_TREE_FRAGMENT).commit();
            } else {
                // Potential cause for error
                //(Patched by System.exit in MainActivity -> launchConfirmation())
                transaction.add(mContainer, fragment, UNIT_TREE_FRAGMENT).commit();
            }
        }
        else {
            throw new RuntimeException("Failed to set UnitTree container");
        }
    }

/*------------------------------------------------------------------------------------------------*/
/*                                       Tree Management                                          */
/*------------------------------------------------------------------------------------------------*/
    /**
     * Begins the UnitTree Module.
     *
     *  **Probably not necessary, but gives more control to external classes' life-cycles.
     */
    public void start() {
        UnitTreeFragment fragment = UnitTreeFragment.newInstance(null);
        startFragment(fragment);
    }

    /**
     * Reverts to previous branch.
     *
     *  **When reverting back to MasterField, mCurrentBranchHead == null and a
     *      a newInstance fragment is returned as expected with a null branch.
     *
     *      Exception occurs when trying to back beyond the MasterField.
     *
     * @return Fragment loaded with previous Branch Head. Null if Master-Field
     */
    public boolean back() {
        try {
            mCurrentBranchHead = mBranchHeadStack.pop();

            UnitTreeFragment fragment = UnitTreeFragment.newInstance(mCurrentBranchHead);
            startFragment(fragment);
            return true;

        } catch (Exception e) {
            mCurrentBranchHead = null;
            return false;
        }
    }

    /**
     * Traverses into the Subunits of a passed Unit
     *
     * @param unit new HeadUnit to be branched into
     * @return True if new branch is able to be processed
     */
    private boolean next(Unit unit) {
        if(!unit.isLeaf()) {
            UnitTreeFragment fragment = UnitTreeFragment.newInstance(unit);
            startFragment(fragment);
            return true;
        }
        else
            return false;
    }

    /**
     * Traverses into the Subunits of the Unit at the given index.
     *
     * @param i index of unit to be branched into
     * @return True if new branch is able to be processed
     */
    private boolean next(int i) {
        UnitWrapper newHead = mCurrentBranch.get(i);
        if(!newHead.peek().isLeaf()) {
            UnitTreeFragment fragment = UnitTreeFragment.newInstance(newHead.peek());
            startFragment(fragment);
            return true;
        }
        else
            return false;
    }

    /**
     * Add Unit to auto added section. Or update if already existing.
     *
     * @param unit Auto-Added Unit
     */
    private void autoAdd(Unit unit, Unit parent) {
        // Check auto-added section of the list
        // Check if already in list
        int checkIndex = mCurrentBranch.indexOf(UnitWrapper.forge(unit, UnitWrapper.AUTO_ADDED_LABEL));

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
        UnitWrapper wrappedUnit = UnitWrapper.wrapUnit(unit, null, UnitWrapper.MF_USER_ADDED_LABEL);

        MASTER_FIELD.add(mMFPosition, wrappedUnit);
        mActiveAdapter.setList(mCurrentBranch);
        ++mMFPosition;
        mCurrentBranchPosition = mMFPosition;

        return true;
    }

    private boolean addToTier(Unit unit) {
        UnitWrapper wrappedUnit = UnitWrapper.wrapUnit(unit, mCurrentBranchHead
                ,UnitWrapper.USER_ADDED_LABEL);
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

    public void updateAll() {
        for(UnitWrapper unit:mCurrentBranch) {
            update(unit);
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
                UnitWrapper.forge(unit, UnitWrapper.RETRIEVE_LABEL));

        if (index < mCurrentBranchPosition && index >= 0) {
            return mCurrentBranch.get(index);
        }
        return null;
    }

    /**
     * Removes a unit from the list
     *
     * @param rm_unit Wrapped unit to be removed.
     * @return True if successfully removed. False otherwise.
     */
    public boolean remove(UnitWrapper rm_unit) {
            // Remove Unit from Master_Field
            if (rm_unit.peek().getLabel() == UnitWrapper.MF_USER_ADDED_LABEL) {
                if (MASTER_FIELD.remove(rm_unit)) {
                    mMFPosition--;

                    for (int i = mMFPosition; i < MASTER_FIELD.size(); i++) {

                        if (MASTER_FIELD.get(i).remove(rm_unit.peek())) {
                            MASTER_FIELD.remove(i);
                            --i;
                        }
                    }

                    if(mCurrentBranch == MASTER_FIELD) {
                        updateAdapter();
                        return mActiveAdapter.removeItem(rm_unit);
                    }
                    else
                        return true;
                }
            }

            // Remove Unit from CurrentBranch
            else if(rm_unit.peek().getLabel() == UnitWrapper.USER_ADDED_LABEL) {

                if (mCurrentBranch.remove(rm_unit)) {
                    mCurrentBranchHead.removeSubunit(rm_unit.peek().getName());
                    mCurrentBranchPosition--;
                    return mActiveAdapter.removeItem(rm_unit);
                }
                else {
                    Unit rm_parent = rm_unit.getParent();

                    if(rm_parent != null) {
                        rm_parent.removeSubunit(rm_unit.peek().getName());
                        return true;
                    }
                }
            }
        return false;
    }

    public void saveList(int s) {
        mListStateManager.save(s);
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

    /**
     * Only behaves within the UTManager and the displaying of Units
     *
     * @param unit
     * @param direction
     */
    @Override
    public void OnItemSwiped(UnitWrapper unit, int direction) {
        if(direction == ItemTouchHelper.LEFT) {
            next(unit.peek());
        }
        else if(direction == ItemTouchHelper.RIGHT) {
            back();
        }
    }

    /**
     * Return to main to allow intractability with user.
     *
     * @param unit from chosen from the Adapter
     */
    @Override
    public void fromAdapterToStage(UnitWrapper unit) {
        mUnitChosenListener.UnitChosenForStage(unit);
    }

    /**
     * Responsible for saving and loading the state of the UnitTreeManager
     */
    private static class ListStateManager {
        private static final String MF_LIST_TAG = "com.example.unitally.masterlisttag";
        private SharedPreferences mPrefrences;

        ListStateManager(Context context) {
            mPrefrences = PreferenceManager.getDefaultSharedPreferences(context);
        }

        private void save(int s) {
            Log.i(UnitallyValues.LIFE_SAVE, "SAVING...");

            // STEP 1: STRIP WRAPPERS FROM MASTER LIST
            ArrayList<Unit> mf_list = extractMF();

            // STEP 2: CONVERT INTO JSON
            Gson gson = new Gson();
            String json = gson.toJson(mf_list);

            // STEP 3: PUT INTO SHARED PREFRENCES
            SharedPreferences.Editor editor = mPrefrences.edit();
            editor.putString(MF_LIST_TAG, json);

            // STEP 4: OFFICIALLY SAVE
            if(s == SAVE_IMMEDIATE)
                editor.commit();
            else if (s == SAVE_APPLY)
                editor.apply();
            else
                throw new RuntimeException("INVALID SAVE STATE PARAMETER");

            Log.i(UnitallyValues.LIFE_SAVE, "SAVED: " + json);
        }

        private ArrayList<Unit> extractMF() {
            ArrayList<Unit> units = new ArrayList<>();

            // Extract MASTER_FIELD units
            for(UnitWrapper parcel:INSTANCE.MASTER_FIELD) {
                if(parcel.getLabel() == UnitWrapper.MF_USER_ADDED_LABEL) {
                    Unit unit = parcel.peek();
                    units.add(unit);
                }
            }

            return units;
        }

        private UnitTreeManager load(Context context, UnitChosenListener listener) {
            Log.i(UnitallyValues.LIFE_LOAD, "ATTEMPTING TO LOAD...");

            ArrayList<Unit> units = loadFromSP();
            ArrayList<UnitWrapper> mf_list;

            mf_list = UnitWrapper.wrapUnits(units);

            return new UnitTreeManager(context,listener, mf_list);
        }

        private ArrayList<Unit> loadFromSP(){
            try {
                Log.i(UnitallyValues.LIFE_LOAD, "LOADING FROM SHARED_PREFRENCES");
                Gson gson = new Gson();
                String json = mPrefrences.getString(MF_LIST_TAG, "");
                Type type = new TypeToken<ArrayList<Unit>>() {}.getType();

                return gson.fromJson(json, type);
            } catch (Exception e) {
                Log.d(UnitallyValues.LIFE_LOAD, "FAILED WHILE ATTEMPTING TO LOAD DATA");
                return null;
            }
        }

        private void clearSP() {
            SharedPreferences.Editor editor = mPrefrences.edit();
            editor.clear();
            editor.apply();
        }

        private boolean isReady() {
            return mPrefrences.contains(MF_LIST_TAG);
        }
    }

    /**
     * Listener used to retrieve Unit out of the UnitTreeManager
     */
    public interface UnitChosenListener{
        void UnitChosenForStage(UnitWrapper parcel);
    }
}