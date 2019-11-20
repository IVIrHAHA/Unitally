package com.example.unitally;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.unitally.objects.Unit;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SubunitEditFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SubunitEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubunitEditFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    public static final int REMOVE_UNIT = 1;
    public static final int EDIT_UNIT = 2;

    private Unit mUnit;

    private TextView mTitle;
    private TextView mRemove;
    private TextView mEdit;

    private OnFragmentInteractionListener mListener;

    public SubunitEditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param unit Parameter 1.
     * @return A new instance of fragment SubunitEditFragment.
     */
    public static SubunitEditFragment newInstance(Unit unit) {
        SubunitEditFragment fragment = new SubunitEditFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, unit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUnit = (Unit) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_subunit_edit, container, false);
        mTitle = v.findViewById(R.id.subunit_edit_title);
        mRemove = v.findViewById(R.id.subunit_edit_remove);
        mEdit = v.findViewById(R.id.subunit_edit_edit);

        mTitle.setText(mUnit.getName());

        mRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRemovePressed();
            }
        });

        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditPressed();
            }
        });

        return v;
    }

    public void onRemovePressed() {
        if (mListener != null) {
            mListener.onSubunitEditInteraction(mUnit, REMOVE_UNIT);
        }
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).commit();
    }

    public void onEditPressed() {
        if (mListener != null) {
            mListener.onSubunitEditInteraction(mUnit, EDIT_UNIT);
        }
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    public interface OnFragmentInteractionListener {
        void onSubunitEditInteraction(Unit unit, int resultCode);
    }
}
