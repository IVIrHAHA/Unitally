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
    private int mUserAddedPosition;

    private ArrayList<UnitWrapper> mCurrentBranch;
    private final ArrayList<UnitWrapper> MASTER_FIELD;

    private Stack<ArrayList<UnitWrapper>> mListStack;
    private UnitTreeAdapter mActiveAdapter;

    private UnitTreeListManager() {
        MASTER_FIELD = new ArrayList<>();
        mCurrentBranch = MASTER_FIELD;

        mUserAddedPosition = 0;
        mListStack = new Stack<>();

        mActiveAdapter = null;
    }

    /**
     * Create singleton instance of the UnitTreeListManager
     *
     * @return UnitTreeListManager Singleton
     */
    public static UnitTreeListManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new UnitTreeListManager();
        }
        return INSTANCE;
    }

    /**
     * Creates and notifies the UnitListManager of a new adapter. In addition receives the
     * new unit branch.
     *
     * @param context
     * @param branch Unit in which the branch will be built.
     * @return
     */
    public static UnitTreeAdapter adapterInstance(Context context, Unit branch) {
        UnitTreeAdapter adapter = new UnitTreeAdapter(context);
        INSTANCE.notifyNewAdapterCreated(adapter, branch);
        return adapter;
    }

    private void notifyNewAdapterCreated(UnitTreeAdapter newAdapter, Unit branch) {
        mActiveAdapter = newAdapter;
        branchInto(branch);
    }

    private void branchInto(Unit branch) {
        // Dealing with master field
        if(branch == null) {
            mCurrentBranch = MASTER_FIELD;
        }

        // Dealing with branch
        else {
            mListStack.push(mCurrentBranch);
            mCurrentBranch = process(branch.getSubunits());

            if (loadAdapter())
                Log.d(UnitallyValues.LIFE_CYCLE_CHECKS, "Adapter has been loaded");
            else
                Log.d(UnitallyValues.LIFE_CYCLE_CHECKS, "Something occurred while trying to load adapter");
        }
    }

    private boolean loadAdapter() {
        mActiveAdapter.setList(mCurrentBranch);
        return false;
    }

    private ArrayList<UnitWrapper> process(List<Unit> rawUnitList) {
        ArrayList<UnitWrapper> processedUnits = new ArrayList<>();

        //TODO: Add direct units to the top, add all others to the bottom

        return processedUnits;
    }

    public void add(List<Unit> list) {

    }

    /**
     * Add Unit to auto added section
     * @param unit
     */
    @SuppressWarnings("SuspiciousMethodCalls")
    private void autoAdd(Unit unit) {
        // TODO: ADD AUTO ADDED DETAILS
        // Check if already in list
//        int checkIndex = mTierList.indexOf(unit);
//
//        if(checkIndex >= mUserAddedPosition && checkIndex != -1) {
//            UnitWrapper wrappedUnit = mTierList.get(checkIndex);
//            wrappedUnit.merge(unit);
//        }
//        else
//            mTierList.add(mUserAddedPosition, new UnitWrapper(unit));

//        updateAdapter();
    }

    private void updateAdapter() {
 //       mActiveAdapter.setList(mTierList);
    }

    @Override
    public void onCalculationFinished(ArrayList<Unit> calculatedUnits) {
        for(Unit unit:calculatedUnits) {
           autoAdd(unit);
        }
    }

    private boolean addToMF(Unit unit) {
        if(MASTER_FIELD.add(UnitWrapper.wrapUnit(unit, UnitWrapper.MF_USER_ADDED_LABEL))) {
            ++mUserAddedPosition;

            // This will AutoAdd units
            if(!unit.isLeaf()) {
                new Calculator(this).execute(unit.getSubunits());
            }
            // Otherwise add itself to results section of list
            else {
                autoAdd(unit);
            }
            return true;
        }
        return false;
    }

    private boolean addToTier(Unit unit) {

        return false;
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


    @Override
    public boolean remove(@Nullable Object o) {
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
