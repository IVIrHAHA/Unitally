package com.example.unitally.unit_interaction;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unitally.R;
import com.example.unitally.objects.Unit;
import com.google.android.material.textfield.TextInputEditText;

public class SubunitEditFragment extends Fragment {
    private static final String UNIT_PARAM = "com.example.unitally.unit_params";
    private static final String REASON_PARAM = "com.example.unitally.unit_reason";
    public static final int REMOVE_REASON = 1,
                            EDIT_SUBUNITS = 2,
                            ENTER_WORTH = 3;

    private static final String WORTH_ERROR = "Please enter a valid number";

    private Unit mUnit;
    private int mReason;

    private TextInputEditText mAmountTiet, mSymbolTiet;
    private ImageView mSymbolBtn;
    private boolean mRemoveSymbol;

    private OnFragmentInteractionListener mListener;

    public SubunitEditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param unit Subunit to be modified.
     * @return A new instance of fragment SubunitEditFragment.
     */
    public static SubunitEditFragment newInstance(Unit unit, int reason) {
        SubunitEditFragment fragment = new SubunitEditFragment();
        Bundle args = new Bundle();
        args.putSerializable(UNIT_PARAM, unit);
        args.putInt(REASON_PARAM, reason);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUnit = (Unit) getArguments().getSerializable(UNIT_PARAM);
            mReason = getArguments().getInt(REASON_PARAM);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.subunit_edit_fragment, container, false);

        // Set Unit name as Title
        TextView title_tv = v.findViewById(R.id.subunit_edit_title);
        title_tv.setText(mUnit.getName());

        // Set Remove view and remove onClick method
        TextView remove_tv = v.findViewById(R.id.subunit_edit_remove);
        remove_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRemovePressed();
            }
        });

        // Set Amount hint
        mAmountTiet = v.findViewById(R.id.sue_amount_tiet);
        mAmountTiet.setHint(String.valueOf(mUnit.getWorth()));

        mAmountTiet.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return hideKeyboard(mAmountTiet);
                }
                return false;
            }
        });

        // Set Symbol components
        mSymbolTiet = v.findViewById(R.id.sue_symbol_tiet);
        mSymbolTiet.setHint(mUnit.getSymbol());

        // Handling of "Enter" key press
        mSymbolTiet.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return hideKeyboard(mSymbolTiet);
                }
                return false;
            }
        });

        mRemoveSymbol = false;
        mSymbolBtn = v.findViewById(R.id.sue_cancel_symbol_button);
        mSymbolBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRemoveSymbol = true;
                mSymbolTiet.setHint("");
                mSymbolTiet.setText("");
            }
        });

        if(mSymbolTiet.getText() != null)
            enableSymbolButton();

        else
            disableSymbolButton();

        // Set Buttons
        Button saveBtn = v.findViewById(R.id.sue_save_button);
        final Button cancelBtn = v.findViewById(R.id.sue_cancel_button);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSavePressed();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(cancelBtn);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return v;
    }

    /**
     * Boilerplate to hide keyboard
     *
     * @param view The view which initiates keyboard removal.
     * @return True if successful, false otherwise.
     */
    private boolean hideKeyboard(View view) {
        if (getActivity() != null) {
            UnitInterPlayActivity
                    .hideKeyboardFrom(getActivity().getApplicationContext(), view);
            return true;
        }
        return false;
    }

    /**
     * Enables a button for symbol removal, if user desires.
     * Enables only when the symbol field is not empty.
     */
    private void enableSymbolButton() {
        mSymbolBtn.setVisibility(View.VISIBLE);
        mSymbolBtn.setClickable(true);
    }

    /**
     * Disables a button for symbol removal.
     * Disables only when the symbol field is empty.
     */
    private void disableSymbolButton() {
        mSymbolBtn.setVisibility(View.INVISIBLE);
        mSymbolBtn.setClickable(false);
    }

    /**
     * Removal button has been pressed. Subunit removal will occur in parent activity.
     */
    private void onRemovePressed() {
        if (mListener != null) {
            mListener.onSubunitEditInteraction(mUnit, mReason);
        }
        getActivity().getSupportFragmentManager().popBackStack();
    }

    /**
     * Save changes to the subunit
     */
    private void onSavePressed() {
        if (mListener != null) {
            // Set Worth
            if (mAmountTiet.getText() != null) {
                String worthText = mAmountTiet.getText().toString();

                // Process new input
                if (worthText.length() != 0) { // If only hint is being shown, then getText() will
                                                // return an empty string
                    try {
                        double worthValue = Double.parseDouble(worthText);
                        mUnit.setWorth(worthValue);

                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), WORTH_ERROR, Toast.LENGTH_LONG).show();
                    }
                }
            }

            // Set Symbol
            if(mSymbolTiet.getText() != null && !mRemoveSymbol) {
                String symbolText = mSymbolTiet.getText().toString();

                // Process new input
                if(symbolText.length() != 0) {
                    mUnit.setSymbol(symbolText);
                }
            }
            // Remove symbol button was pressed
            else {
                mUnit.setSymbol("");
            }

            // Send unit back to activity
            mListener.onSubunitEditInteraction(mUnit,mReason);
            getActivity().getSupportFragmentManager().popBackStack();
        }
     }

    @Override
    public void onAttach(Context context) {
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
        void onSubunitEditInteraction(Unit unit, int resultCode);
    }
}
