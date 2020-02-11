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

package com.example.unitally;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.unitally.activities.TickerView;
import com.example.unitally.objects.Unit;
import com.example.unitally.tools.StageController;
import com.example.unitally.tools.UnitallyValues;

public class StageFragment extends Fragment implements StageController.OnSwipeListener {
    public static final String STAGE_UNIT = "com.example.unitally.UnitStage";
    public static final int NON_EXIT    = 0, // Used when another fragment is opened before this one closes.
                            LEFT_EXIT   = 1,
                            DOWN_EXIT   = 2,
                            RIGHT_EXIT  = 3,
                            UP_EXIT     = 4;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM = "com.example.unitally.UnitForStaging";

    private Unit mStagedUnit;
    private TickerView mTickerView;
    private TextView mCountTextView;

    private OnItemExitListener mListener;
    private StageController mScrollHelper;

    public StageFragment() {
        // Required empty public constructor
    }

    public static StageFragment newInstance(Unit stageUnit) {
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
            mStagedUnit = (Unit) getArguments().getSerializable(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stage, container, false);

        mTickerView = view.findViewById(R.id.stage_ticker);
        mTickerView.setUnit(mStagedUnit);

        mScrollHelper = new StageController(getContext(), mTickerView, this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemExitListener) {
            mListener = (OnItemExitListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
            case StageController.UP:        finish();
                                            break;

            case StageController.RIGHT:     finish();
                                            break;

            case StageController.DOWN:      finish();
                                            break;

            case StageController.LEFT:      finish();
                                            break;

            default: //Log.d(UnitallyValues.QUICK_CHECK, "Nothing performed yet");
                    break;
        }
    }

    public interface OnItemExitListener {
        void OnItemExit(Unit unit, int exitInstance);
    }
}
