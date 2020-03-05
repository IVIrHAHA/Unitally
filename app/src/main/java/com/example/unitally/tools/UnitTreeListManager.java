package com.example.unitally.tools;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.unitally.app_modules.unit_tree_module.Calculator;
import com.example.unitally.app_modules.unit_tree_module.UnitTreeAdapter;
import com.example.unitally.objects.Unit;
import com.example.unitally.objects.UnitWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

// TODO: Make class generic

/**
 * Receive orders from main
 * Provide Fragment with adapter
 * Provide Adapter with Tier data
 */
public class UnitTreeListManager implements List<Unit>, Calculator.CalculationListener{

    private static UnitTreeListManager INSTANCE = null;

    // Tracks position split between user-added and auto-added units
    private int mUserAddedPosition, mCurrentBranchPosition;

    private ArrayList<UnitWrapper> mCurrentBranch;
    private final ArrayList<UnitWrapper> MASTER_FIELD;

    private Stack<ArrayList<UnitWrapper>> mListStack;
    private UnitTreeAdapter mActiveAdapter;

    private UnitTreeAdapter.OnItemToBeStaged mItemSelectionListener;

    private UnitTreeListManager(UnitTreeAdapter.OnItemToBeStaged listener) {
        MASTER_FIELD = new ArrayList<>();
        mCurrentBranch = MASTER_FIELD;

        mUserAddedPosition = 0;
        mCurrentBranchPosition = 0;

        mListStack = new Stack<>();

        mActiveAdapter = null;
        mItemSelectionListener = listener;
    }

    /**
     * Create singleton instance of the UnitTreeListManager
     *
     * @return UnitTreeListManager Singleton
     */
    public static UnitTreeListManager getInstance(UnitTreeAdapter.OnItemToBeStaged listener) {
        if(INSTANCE == null) {
            INSTANCE = new UnitTreeListManager(listener);
        }
        return INSTANCE;
    }

    /**
     * Creates and notifies the UnitListManager of a new adapter. In addition receives the
     * new unit branch.
     *
     * @param context Context to be used for the adapter
     * @param branch Unit in which the branch will be built.
     * @return Adapter instance
     */
    public static UnitTreeAdapter adapterInstance(Context context, Unit branch) {
        UnitTreeAdapter adapter = new UnitTreeAdapter(context);
        INSTANCE.notifyNewAdapterCreated(adapter, branch);
        return adapter;
    }

/*------------------------------------------------------------------------------------------------*/
/*                                 Branch Transition Management                                   */
/*------------------------------------------------------------------------------------------------*/

    /**
     * Updates adapter instance and begins the branching process.
     *
     * @param newAdapter New adapter instance
     * @param branch    Unit containing the branching list
     */
    private void notifyNewAdapterCreated(UnitTreeAdapter newAdapter, Unit branch) {
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
        // Dealing with master field
        if(branch == null) {
            mCurrentBranch = MASTER_FIELD;
        }

        // Dealing with branch
        else {
            mListStack.push(mCurrentBranch);
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
    private ArrayList<UnitWrapper> process(List<Unit> rawUnitList) {
        ArrayList<UnitWrapper> processedUnits = new ArrayList<>();

        //TODO: Add direct units to the top, add all others to the bottom

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
        } catch(Exception e) {
            return false;
        }
    }

/*------------------------------------------------------------------------------------------------*/
/*                                      Branch Management                                         */
/*------------------------------------------------------------------------------------------------*/

    /**
     * Add Unit to auto added section. This method is called after the Calculator has finished.
     *
     * @param unit Auto-Added Unit
     */
    private void autoAdd(Unit unit) {
        // Check auto-added section of the list
        // Check if already in list
        int checkIndex = mCurrentBranch.indexOf(UnitWrapper.wrapUnit(unit, UnitWrapper.AUTO_ADDED_LABEL));

        // Include with exiting Units
        if(checkIndex >= mUserAddedPosition && checkIndex != -1) {
            UnitWrapper wrappedUnit = mCurrentBranch.get(checkIndex);
            wrappedUnit.include(unit);
        }

        // TODO: Fix this, it's adding multiples
        //  ex. when adding time, it add multiple "time"s
        // If none were found, then add it to the bottom of the list.
        else {
            mCurrentBranch.add(UnitWrapper.wrapUnit(unit, UnitWrapper.AUTO_ADDED_LABEL));
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
     *
     * Calls the autoAdd method (adds units to the Auto-Added section of the list). When finished,
     * calls to have the adapter updated.
     *
     * @param calculatedUnits The list of calculated units. (does not include the parent)
     */
    @Override
    public void onCalculationFinished(ArrayList<Unit> calculatedUnits) {
        for(Unit unit:calculatedUnits) {
           autoAdd(unit);
        }
        updateAdapter();
    }

    /**
     * Add user-added unit to the master-field.
     *
     * @param unit User-Added unit
     * @return True if the unit has been successfully added. False otherwise.
     */
    private boolean addToMF(Unit unit) {
        UnitWrapper wrappedUnit = UnitWrapper.wrapUnit(unit, UnitWrapper.MF_USER_ADDED_LABEL);

        MASTER_FIELD.add(mUserAddedPosition, wrappedUnit);
        mActiveAdapter.setList(mCurrentBranch);
        mUserAddedPosition++;
        mCurrentBranchPosition = mUserAddedPosition;

        return true;
    }

    private boolean addToTier(Unit unit) {

        return false;
    }

    public void update(UnitWrapper modifiedUnit) {
        Unit unit = modifiedUnit.peek();

        // This will AutoAdd units
        if(!unit.isLeaf()) {
            new Calculator(this).execute(unit);
        }
        // Otherwise add itself to results section of list
        else {
            autoAdd(unit);
        }
    }

    public void add(List<Unit> list) {
        // TODO: Add all units at the same time
        // Calculation process may be a bit tricky
    }

    @Override
    public boolean add(Unit unit) {
        // User added a root to master-field
        if(mCurrentBranch.equals(MASTER_FIELD)) {
            return addToMF(unit);
        }
        // Not in master-field, rather in a branched unit
        else {
            return addToTier(unit);
        }
    }

    public ArrayList<Unit> getActiveUnits() {
     ArrayList<Unit> activeUnits = new ArrayList<>();

     for(int i = 0; i <= mCurrentBranchPosition-1; i++) {
         Unit unit = mCurrentBranch.get(i).peek();
         activeUnits.add(unit);
     }

     return activeUnits;
    }

    @Override
    public void add(int i, Unit unit) {
        // Do Nothing, this method will not be used
    }

    @Override
    public int size() {
        return mCurrentBranch.size();
    }

    @Override
    public boolean isEmpty() {
        return mCurrentBranch.isEmpty();
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return false;
    }

    @NonNull
    @Override
    public Iterator<Unit> iterator() {
        return null;
    }

    @Nullable
    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(@Nullable T[] ts) {
        return null;
    }


    // Delete Unit completely from list
    @Override
    public boolean remove(@Nullable Object o) {
        if ((o != null ? o.getClass() : null) == UnitWrapper.class) {
            UnitWrapper rm_unit = (UnitWrapper) o;

            if(mCurrentBranch.remove(rm_unit)) {
                return mActiveAdapter.removeItem(rm_unit);
            }
            // TODO: Remove Unit from AutoAdded units
        }
        return false;
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> collection) {
        return false;
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends Unit> collection) {
        return false;
    }

    @Override
    public boolean addAll(int i, @NonNull Collection<? extends Unit> collection) {
        return false;
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> collection) {
        return false;
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        return false;
    }

    @Override
    public void clear() {
        mActiveAdapter.clear();
        mCurrentBranch.clear();
        mCurrentBranchPosition = 0;
        mUserAddedPosition = 0;
    }

    @Override
    public Unit get(int i) {
        return null;
    }

    @Override
    public Unit set(int i, Unit unit) {
        return null;
    }

    @Override
    public Unit remove(int i) {
        return null;
    }

    @Override
    public int indexOf(@Nullable Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(@Nullable Object o) {
        return 0;
    }

    @NonNull
    @Override
    public ListIterator<Unit> listIterator() {
        return null;
    }

    @NonNull
    @Override
    public ListIterator<Unit> listIterator(int i) {
        return null;
    }

    @NonNull
    @Override
    public List<Unit> subList(int i, int i1) {
        return null;
    }
}
