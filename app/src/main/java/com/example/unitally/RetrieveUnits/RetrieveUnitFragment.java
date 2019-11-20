package com.example.unitally.RetrieveUnits;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.unitally.DividerItemDecoration;
import com.example.unitally.R;
import com.example.unitally.UnitInterPlay.UnitInterPlayActivity;
import com.example.unitally.room.UnitObjectViewModel;
import com.example.unitally.objects.Unit;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class RetrieveUnitFragment extends Fragment
                                implements SearchView.OnQueryTextListener{

    public static final String TAG= RetrieveUnitFragment.class.getName();

// Intent IDs
    // Getting the unit out
    public static final String RETRIEVE_UNIT ="com.example.UnitCounterV2.RetrieveUnit";
    // Whether selection is multiple or singular
    private static final String MULTI_CHOICE = "com.example.UnitCounterV2.MultiChoice";
    // Any Units not to be included in selection
    private static final String BANISHED_UNITS = "com.example.UnitCounterV2.BanishedUnits";
    // Quarantined Unit
    private static final String QUARANTINED_UNIT = "com.example.UnitCounterV2.Quarantine";

// Ease-of-use variables
    private final String SELECTIONID = "mySelectionID";
    private final int MIN_QUERYLENGTH = 2;

// Variable Resources
    private RetrieveUnitAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private List<Unit> mUnitList;
    private ActionMode mActionMode;
    private MenuItem mSelectionCount;
    private SelectionTracker<String> mSelectionTracker;
    private Unit mQuarantinedUnit;

    // UI variables
    private ViewFlipper mFabFlipper;
    private FloatingActionButton mReturnUnitsButton, mStartCreateUnitButton;
    private TextView mContextualTV;

    // Contextual variables
    private OnFragmentInteractionListener mListener;
    private boolean mMultiSelect;
    private List<Unit> mBanishedUnits;

    private RetrieveUnitFragment() {}

    public static RetrieveUnitFragment newInstance(ArrayList<Unit> banishedUnits, boolean multi_choice) {
        RetrieveUnitFragment fragment = new RetrieveUnitFragment();
        Bundle args = new Bundle();
        args.putBoolean(MULTI_CHOICE,multi_choice);
        args.putSerializable(BANISHED_UNITS, banishedUnits);
        fragment.setArguments(args);

        return fragment;
    }

    // Remove any Units containing this unit
    public static RetrieveUnitFragment newInstance(Unit quarantinedUnit, boolean multi_choice) {
        RetrieveUnitFragment fragment = new RetrieveUnitFragment();
        Bundle args = new Bundle();
        args.putBoolean(MULTI_CHOICE,multi_choice);
        args.putSerializable(QUARANTINED_UNIT, quarantinedUnit);
        fragment.setArguments(args);

        return fragment;
    }

    public static RetrieveUnitFragment newInstance(boolean multi_choice) {
        RetrieveUnitFragment fragment = new RetrieveUnitFragment();
        Bundle args = new Bundle();
        args.putBoolean(MULTI_CHOICE,multi_choice);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            mBanishedUnits = (ArrayList<Unit>) getArguments().getSerializable(BANISHED_UNITS);
            mMultiSelect = getArguments().getBoolean(MULTI_CHOICE);
            mQuarantinedUnit = (Unit) getArguments().getSerializable(QUARANTINED_UNIT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_retrieve_unit,container,false);

        mFabFlipper = v.findViewById(R.id.ru_fab_flipper);
        mReturnUnitsButton = v.findViewById(R.id.retrieve_unit_check_fab);
        mStartCreateUnitButton = v.findViewById(R.id.retrieve_unit_create_fab);
        mContextualTV = v.findViewById(R.id.ru_context_hint);

        // FAB onCLick Listeners
        mReturnUnitsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReturnUnitsOnClick(v);
            }
        });

        mStartCreateUnitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCreateUnit(v);
            }
        });

        // Instantiating Toolbar
        Toolbar toolbar = v.findViewById(R.id.retrieve_unit_toolbar);
        toolbar.inflateMenu(R.menu.unit_filter);

        Menu menu = toolbar.getMenu();
        MenuItem menuItem = menu.findItem(R.id.search_view);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        // Getting count view
        mSelectionCount = menu.findItem(R.id.item_count);

        // Instantiating RecyclerView
        mRecyclerView = v.findViewById(R.id.addUnit_recyclerview);
        mAdapter = new RetrieveUnitAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Divider
        RecyclerView.ItemDecoration decoration =
                new DividerItemDecoration(getContext(),R.drawable.divider);

        mRecyclerView.addItemDecoration(decoration);

        // Loading Units and Populating RecyclerView
        UnitObjectViewModel viewModel = ViewModelProviders.of(this).get(UnitObjectViewModel.class);
        viewModel.getAllUnits().observe(this, new Observer<List<Unit>>() {
            @Override
            public void onChanged(List<Unit> units) {
                createDetails(units);
            }
        });

        return v;
    }

    private void createDetails(List<Unit> units) {
    // Banishing Units
        // Banishing a list of units
        if(mBanishedUnits != null && !mBanishedUnits.isEmpty()) {
            mContextualTV.setText("elements already in use have been removed");
            mContextualTV.setVisibility(View.VISIBLE);
            mUnitList = new ArrayList<>();
            for (Unit suspect : units) {
                if (!mBanishedUnits.contains(suspect)) {

                    mUnitList.add(suspect);
                }
            }
        }
        // Quarantine any Units containing mQuarantinedUnit
        else if(mQuarantinedUnit != null) {
            mContextualTV.setText("elements containing " + mQuarantinedUnit.getName() + " have been removed");
            mContextualTV.setVisibility(View.VISIBLE);
            mUnitList = new ArrayList<>();
            for(Unit suspect : units) {
                if(!suspect.getAllSubunits().contains(mQuarantinedUnit)) {
                    mUnitList.add(suspect);
                }
            }
        }
        else {
            mContextualTV.setVisibility(View.GONE);
            mUnitList = units;
        }
        mAdapter.setUnitList(mUnitList);

        // SelectionTracker Resources
        mSelectionTracker = createSelectorTracker(mUnitList,mMultiSelect);
        mAdapter.setSelectionTracker(mSelectionTracker);

        // ActionMode Utilities
        mSelectionTracker.addObserver(new SelectionTracker.SelectionObserver<String>() {
            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();

                fabDisplay(mSelectionTracker.hasSelection());

                if(mMultiSelect) {
                    if (mSelectionTracker.hasSelection() && mActionMode == null) {
                        mActionMode = ((AppCompatActivity)getActivity()).startSupportActionMode(
                                new RetrieveUnitActionMode(getContext(),
                                        mSelectionTracker));
                        mSelectionCount.setTitle("Selected: " + mSelectionTracker.getSelection().size());
                    } else if (!mSelectionTracker.hasSelection() && mActionMode != null) {
                        mSelectionCount.setTitle("");
                        mActionMode.finish();
                        mActionMode = null;
                    } else if (!mSelectionTracker.hasSelection()) {
                        mSelectionCount.setTitle("");
                    } else {
                        mSelectionCount.setTitle("Selected: " + mSelectionTracker.getSelection().size());
                    }
                }
            }
        });
    }

    private void fabDisplay(boolean isSelected) {

        // Show check mark fab
        if(isSelected) {
            if(mFabFlipper.getCurrentView() != mReturnUnitsButton){
                mFabFlipper.showNext();
            }
        }
        // Show create unit fab
        else {
            if(mFabFlipper.getCurrentView() != mStartCreateUnitButton) {
                mFabFlipper.showNext();
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

/*------------------------------------------------------------------------------------------------*/
//                                          OnClick Methods                                       //
/*------------------------------------------------------------------------------------------------*/
    public void ReturnUnitsOnClick(View view) {
        ArrayList<Unit> selectedUnits = new ArrayList<>();

        for (String unitname : mSelectionTracker.getSelection()) {
            for (Unit unit : mUnitList) {
                if (unit.getName().equals(unitname)) {
                    selectedUnits.add(unit);
                    getActivity().getSupportFragmentManager().beginTransaction().detach(this).commit();
                }
            }
        }

        if(mListener != null) {
            mListener.onFragmentInteraction(selectedUnits);
        }
    }

    public void onClickCreateUnit(View view) {
        Intent createNewUnitIntention = new Intent(getContext(), UnitInterPlayActivity.class);
        createNewUnitIntention.putExtra(UnitInterPlayActivity.EDIT_UNIT,false);
        startActivity(createNewUnitIntention);
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).commit();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(List<Unit> selectedUnits);
    }

/*------------------------------------------------------------------------------------------------*/
//                                          Filter Methods                                        //
/*------------------------------------------------------------------------------------------------*/
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if(query.length() >= MIN_QUERYLENGTH) {
            List<Unit> filteredList = filter(query, mUnitList);
            mAdapter.replaceAll(filteredList);
        }
        else {
            mAdapter.replaceAll(mUnitList);
        }
        mRecyclerView.scrollToPosition(0);
        return true;
    }

    private List<Unit> filter(String query, List<Unit> list) {
        String lowerCaseQuery = query.toLowerCase();
        List<Unit> filteredList = new ArrayList<>();

        for(Unit testUnit:list) {
            String unitName = testUnit.getName().toLowerCase();
            if(unitName.contains(lowerCaseQuery)) {
                filteredList.add(testUnit);
            }
        }
        return filteredList;
    }

//------------------------------------------------------------------------------------------------//
/*                                      Selection Methods                                         */
//------------------------------------------------------------------------------------------------//
    private SelectionTracker<String> createSelectorTracker(List<Unit> staticList,
                                                           boolean multiSelect) {
        SelectionTracker<String> selectionTracker;

        // Multiple-Choice
        if(multiSelect) {
            selectionTracker = new SelectionTracker.Builder<>(
                    SELECTIONID,
                    mRecyclerView,
                    new UnitKeyProvider(ItemKeyProvider.SCOPE_CACHED,
                                        staticList,
                                        mAdapter.getList()),
                    new UnitItemLookup(mRecyclerView),
                    StorageStrategy.createStringStorage()
            ).build();
        }
        // Single-Choice
        else {
            selectionTracker = new SelectionTracker.Builder<>(
                    SELECTIONID,
                    mRecyclerView,
                    new UnitKeyProvider(ItemKeyProvider.SCOPE_CACHED,
                                        staticList,
                                        mAdapter.getList()),
                    new UnitItemLookup(mRecyclerView),
                    StorageStrategy.createStringStorage())
                    .withSelectionPredicate(SelectionPredicates.
                    <String>createSelectSingleAnything())
                    .build();
        }
        return selectionTracker;
    }

/*------------------------------------------------------------------------------------------------*/
//                                      Selection Details                                         //
/*------------------------------------------------------------------------------------------------*/
    private static class UnitKeyProvider extends ItemKeyProvider<String> {
        private List<Unit> mSelectionList;
        private SortedList<Unit> mAdapterList;

        UnitKeyProvider(int scope, List<Unit> staticList, SortedList<Unit> dynamicList) {
            super(scope);
            mAdapterList = dynamicList;
            mSelectionList = staticList;
        }

        @Nullable
        @Override
        public String getKey(int position) {
            return mSelectionList.get(position).getName();
        }

        @Override
        public int getPosition(@NonNull String key){
            int position = 0;
            for(int i = 0; i<mAdapterList.size(); i++) {
                if(mAdapterList.get(i).getName().equals(key)) {
                    return i;
                }
            }
            return position;
        }
    }

    public class UnitItemLookup extends ItemDetailsLookup<String> {
        private RecyclerView mRecyclerView;

        UnitItemLookup(RecyclerView recyclerView) {
            this.mRecyclerView = recyclerView;
        }

        @Nullable
        @Override
        public ItemDetails<String> getItemDetails(@NonNull MotionEvent e) {
            View view = mRecyclerView.findChildViewUnder(e.getX(),e.getY());
            if(view != null) {
                RecyclerView.ViewHolder viewHolder = mRecyclerView.getChildViewHolder(view);
                if(viewHolder instanceof RetrieveUnitAdapter.UnitViewHolder) {
                    return ((RetrieveUnitAdapter.UnitViewHolder) viewHolder).getItemDetails();
                }
            }
            return null;
        }
    }
}
