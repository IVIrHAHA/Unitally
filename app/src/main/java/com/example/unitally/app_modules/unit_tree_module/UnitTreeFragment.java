package com.example.unitally.app_modules.unit_tree_module;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.unitally.DragSwipeHelper;
import com.example.unitally.R;
import com.example.unitally.app_modules.NextTierCallback;
import com.example.unitally.objects.Unit;

import java.util.ArrayList;
import java.util.List;

public class UnitTreeFragment extends Fragment
implements NextTierCallback{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARENT_UNIT = "com.example.unitally.parent_unit";

    private ArrayList<Unit> mUnitList;

    private CalculationMacroAdapter mAdapter;
    private OnUnitTreeInteraction mListener;

    public UnitTreeFragment() {
        // Required empty public constructor
    }

    /**
     * Pass a parent unit to display all child units. To display Master-Field,
     * pass null.
     *
     * @param parent Parent Unit or Null.
     * @return  Fragment.
     */
    public static UnitTreeFragment newInstance(Unit parent) {
        UnitTreeFragment fragment = new UnitTreeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARENT_UNIT, parent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Unit parent = (Unit) getArguments().getSerializable(ARG_PARENT_UNIT);

            if(parent != null)
                mUnitList = parent.getSubunits();

            else
                mUnitList = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.results_numerical_module, container, false);

        RecyclerView rv = view.findViewById(R.id.numerical_rv);

        mAdapter = new CalculationMacroAdapter(this.getContext());

        rv.setAdapter(mAdapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        substantiateNumericalList();

        return view;
    }

    // TODO: Calculate list
    private void substantiateNumericalList() {
        mAdapter.setList(mUnitList);
    }

    public ArrayList<Unit> getUnitTreeTier() {
        return mUnitList;
    }

    public void appendToTier(Unit unit) {
        mAdapter.add(unit);
    }

    public void appendToTier(List<Unit> units) {
        // TODO: ALLOW TO ADD IN BATCHES
        for(Unit unit:units) {
            mAdapter.add(unit);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUnitTreeInteraction) {
            mListener = (OnUnitTreeInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onUnitTreeFragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void OnNextTierReached(List<Unit> NextTierList, List<Unit> PreviousTierList) {

    }

    public interface OnUnitTreeInteraction {
        void onUnitTreeInteraction(List<Unit> currentTier, Unit unitBranch);
    }
}
