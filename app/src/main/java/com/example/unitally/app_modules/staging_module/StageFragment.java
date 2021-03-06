/**
 * Handles Unit lifecycle and determines what the user wants to do with the unit,
 * by using swipes. Depending on which exit method is used(swipe to right, down, left, up)
 * different outcomes will occur.
 *
 * Key points.
 *  - Handle Lifecycle
 *  - Handle Exit notifications
 *
 *  Need to implement
 *      - Swiping
 */

package com.example.unitally.app_modules.staging_module;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.unitally.R;
import com.example.unitally.activities.TickerView;
import com.example.unitally.objects.Unit;
import com.example.unitally.objects.UnitWrapper;
import com.example.unitally.tools.StageController;

public class StageFragment extends Fragment implements StageController.OnSwipeListener {
    public static final String STAGE_UNIT = "com.example.unitally.UnitStage";
    public static final int NON_EXIT    = 0, // Used when another fragment is opened before this one closes.
                            LEFT_EXIT   = 1,
                            DOWN_EXIT   = 2,
                            RIGHT_EXIT  = 3,
                            UP_EXIT     = 4;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM = "com.example.unitally.UnitForStaging";

    private UnitWrapper mStagedUnit;
    private TickerView mTickerView;
    private TextView mCountTextView;

    private OnItemExitListener mListener;
    private StageController mScrollHelper;

    public StageFragment() {
        // Required empty public constructor
    }

    public static StageFragment newInstance(UnitWrapper stageUnit) {
        StageFragment fragment = new StageFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM, stageUnit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            UnitWrapper wrappedUnit = (UnitWrapper) getArguments().getSerializable(ARG_PARAM);
            if(wrappedUnit != null)
                mStagedUnit = wrappedUnit;
            else
                throw new RuntimeException("Cannot stage a null object");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stage, container, false);

        mTickerView = view.findViewById(R.id.stage_ticker);
        mTickerView.setUnit(mStagedUnit.peek());

        mScrollHelper = new StageController(getContext(), mTickerView, this);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnItemExitListener) {
            mListener = (OnItemExitListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnItemExitListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void finish() {
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).commit();
    }

    @Override
    public void onSwipe(int direction) {
        switch (direction) {
            // Save changes
            case StageController.UP:        mListener.OnStageExit(mStagedUnit, UP_EXIT);
                                            finish();
                                            break;

            // Hide parent unit, save subunit calculations
            case StageController.RIGHT:     mListener.OnStageExit(mStagedUnit, RIGHT_EXIT);
                                            finish();
                                            break;

            // Cancel any changes
            case StageController.DOWN:      mListener.OnStageExit(mStagedUnit, DOWN_EXIT);
                                            finish();
                                            break;

            // Remove Unit and any subsequent units from calculations
            case StageController.LEFT:      mListener.OnStageExit(mStagedUnit, LEFT_EXIT);
                                            finish();
                                            break;

            default: //Log.d(UnitallyValues.QUICK_CHECK, "Nothing performed yet");
                    break;
        }
    }

    public interface OnItemExitListener {
        void OnStageExit(UnitWrapper unit, int exitInstance);
    }
}
