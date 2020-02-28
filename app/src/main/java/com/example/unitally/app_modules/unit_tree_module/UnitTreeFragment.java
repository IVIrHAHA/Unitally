package com.example.unitally.app_modules.unit_tree_module;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.unitally.R;
import com.example.unitally.objects.Unit;
import com.example.unitally.tools.UnitTreeListManager;

import java.util.ArrayList;
import java.util.List;

public class UnitTreeFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARENT_UNIT = "com.example.unitally.parent_unit";

    private Unit mRootUnit;

    private OnUnitSelection mListener;

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
            mRootUnit = (Unit) getArguments().getSerializable(ARG_PARENT_UNIT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.results_numerical_module, container, false);

        RecyclerView rv = view.findViewById(R.id.numerical_rv);

        UnitTreeAdapter adapter = UnitTreeListManager.adapterInstance(getContext(), mRootUnit);

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        //substantiateNumericalList();

        return view;
    }

    // TODO: Calculate list
//    private void substantiateNumericalList() {
//        mAdapter.setList(mUnitList);
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUnitSelection) {
            mListener = (OnUnitSelection) context;
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

    public interface OnUnitSelection {
        void onUnitSelected(Unit selectedUnit);
    }
}