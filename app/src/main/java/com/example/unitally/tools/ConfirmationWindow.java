package com.example.unitally.tools;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unitally.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link onFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConfirmationWindow#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfirmationWindow extends Fragment {
    private static final String PROMPT_PARAM = "com.example.unitally.PROMPT_PARAMETER";
    private static final String TOAST_PARAM = "com.example.unitally.TOAST_PARAM";

    private String mPrompt;
    private String mToast;

    private onFragmentInteractionListener mListener;

    public ConfirmationWindow() {
        // Required empty public constructor
    }

    public static ConfirmationWindow newInstance(@NonNull String prompt, String toast) {
        ConfirmationWindow fragment = new ConfirmationWindow();
        Bundle args = new Bundle();
        args.putString(PROMPT_PARAM, prompt);
        args.putString(TOAST_PARAM,toast);
        fragment.setArguments(args);
        return fragment;
    }

    public static ConfirmationWindow newInstance() {
        return new ConfirmationWindow();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPrompt = getArguments().getString(PROMPT_PARAM);
            mToast = getArguments().getString(TOAST_PARAM);
        }
        else {
            mPrompt = UnitallyValues.DEFAULT_CONFRIMATION_PROMPT;
            mToast = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.confirmation_fragment, container, false);

        TextView tv = v.findViewById(R.id.confirmation_prompt);
        Button confirm_button = v.findViewById(R.id.confirmation_confirm_button);
        Button cancel_button = v.findViewById(R.id.confirmation_cancel_button);

        tv.setText(mPrompt);

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmOnClick();
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelOnClick();
            }
        });

        return v;
    }


    public void confirmOnClick() {
        if (mListener != null) {
            mListener.onConfirmationInteractionListener(true);
        }
    }

    public void cancelOnClick() {
        if (mListener != null) {
            mListener.onConfirmationInteractionListener(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onFragmentInteractionListener) {
            mListener = (onFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mPrompt = null;

        if(mToast != null) {
            Toast.makeText(getActivity(), mToast, Toast.LENGTH_LONG).show();
            mToast = null;
        }
    }


    public interface onFragmentInteractionListener {
        void onConfirmationInteractionListener(boolean confirmation_status);
    }
}
