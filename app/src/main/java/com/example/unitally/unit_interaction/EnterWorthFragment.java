package com.example.unitally.unit_interaction;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.unitally.R;
import com.example.unitally.objects.Unit;
import com.google.android.material.textfield.TextInputEditText;

public class EnterWorthFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String UNIT_PARAM = "com.example.unitally.UNIT";
    private static final String CODE_PARAM = "com.example.unitally.CODE";

    private static final String WORTH_ERROR = "Please enter a valid number";

    // Used by meta classes
    static final int FROM_RU_CODE = 1;
    static final int FROM_SUE_CODE = 2;

    private Unit mUnit;
    private int mReturnCode;
    private TextInputEditText mWorthInput;
    private OnFragmentInteractionListener mListener;

    public EnterWorthFragment() {
        // Required empty public constructor
    }

    static EnterWorthFragment newInstance(Unit unit, int returnCode) {
        EnterWorthFragment fragment = new EnterWorthFragment();
        Bundle args = new Bundle();
        args.putSerializable(UNIT_PARAM, unit);
        args.putInt(CODE_PARAM, returnCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            mUnit = (Unit) getArguments().getSerializable(UNIT_PARAM);
            mReturnCode = getArguments().getInt(CODE_PARAM);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_enter_worth, container, false);

        mWorthInput = v.findViewById(R.id.enter_worth_text_input);
        Button ok_button = v.findViewById(R.id.enter_worth_button_ok);
        Button cancel_button = v.findViewById(R.id.enter_worth_button_cancel);

        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okButtonPressed();
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               cancelButtonPressed();
            }
        });

        return v;
    }

    private void okButtonPressed() {
        if (mListener != null) {
            if(mWorthInput.getText() != null) {
                String worthText = mWorthInput.getText().toString();

                try {
                    double worthValue = Double.parseDouble(worthText);

                    if(worthValue > 0) {
                        mUnit.setWorth(worthValue);
                        mListener.onEnterWorthInteraction(mUnit, mReturnCode);
                        getActivity().getSupportFragmentManager().beginTransaction().detach(this).commit();
                    }
                    else {
                        Toast.makeText(getContext(), "Value must be greater than zero", Toast.LENGTH_SHORT).show();
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(),WORTH_ERROR,Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void cancelButtonPressed() {
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).commit();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onUnitRetrievalInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onEnterWorthInteraction(Unit unit, int returnCode);
    }
}
