package com.example.unitally.Settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unitally.R;
import com.google.android.material.textfield.TextInputEditText;

public class GetUserNumber extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private String mDisplayText;
    private TextInputEditText mTiet;

    private OnGetUserNumberInteractionListener mListener;

    public GetUserNumber() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static GetUserNumber newInstance(@NonNull String displayText) {
        GetUserNumber fragment = new GetUserNumber();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, displayText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDisplayText = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_get_user_number, container, false);

        TextView titleTextView = v.findViewById(R.id.get_user_number_title);
        titleTextView.setText(mDisplayText);

        mTiet = v.findViewById(R.id.get_user_number_tiet);

        Button ok_button = v.findViewById(R.id.get_user_number_button_ok);
        Button cancel_button = v.findViewById(R.id.get_user_number_button_cancel);



        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okButtonPressed();
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                cancelButtonPressed();
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void okButtonPressed() {
        if (mListener != null) {
            if(!TextUtils.isEmpty(mTiet.getText())) {
                String line = mTiet.getText().toString();

                try {
                    double number = Double.parseDouble(line);
                    mListener.onFragmentInteraction(number);
                    getActivity().getSupportFragmentManager().beginTransaction().detach(this).commit();

                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(),"please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void cancelButtonPressed() {
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).commit();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGetUserNumberInteractionListener) {
            mListener = (OnGetUserNumberInteractionListener) context;
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

    public interface OnGetUserNumberInteractionListener {
        void onFragmentInteraction(double userNumber);
    }
}
