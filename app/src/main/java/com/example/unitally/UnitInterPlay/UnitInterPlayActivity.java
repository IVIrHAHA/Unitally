package com.example.unitally.UnitInterPlay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.unitally.R;
import com.example.unitally.DividerItemDecoration;
import com.example.unitally.RetrieveUnits.RetrieveUnitFragment;
import com.example.unitally.SubunitEditFragment;
import com.example.unitally.objects.Unit;
import com.example.unitally.room.UnitObjectViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class UnitInterPlayActivity extends AppCompatActivity
        implements RetrieveUnitFragment.OnFragmentInteractionListener,
                    EnterWorthFragment.OnFragmentInteractionListener,
                    SubunitEditFragment.OnFragmentInteractionListener {

    // Tells whether choice is to edit a unit or create from scratch
    public static final String EDIT_UNIT = "com.example.UnitCounterV2.EditUnit";
    public static final String PASSING_UNIT="com.example.UnitCounterV2.PassingUnit";
    public static final String UNIS_ARRAY="com.example.UnitCounterV2.Unis";

    private static final String INVALID_NAME_ERROR = "Please enter a valid name";
    private static final String NAME_ALREADY_EXISTS = "Name already in use";
    private static final int MIN_UNIT_NAME_LENGTH = 2;
    private static final int MAX_UNIT_NAME_LENGTH = 25;
    private static final int MAX_UNIT_SYMBOL_LENGTH = 5;

    private Boolean mReviewMode, mFinalReviewMode, mPassingMode;
    private boolean mChanged;
    private Unit mRevisedUnit, mTempUnit;
    private UnitInterPlayAdapter mAdapter;

    // Contextual Variables
    static FragmentTransaction mFragmentTransaction;
    private UnitObjectViewModel mViewModel;
    private Observer<List<Unit>> mObserver;

    // Fragments
    private EnterWorthFragment mWorthFragment;
    private RetrieveUnitFragment mRetrieveFragment;
    private static final String ENTER_WORTH_TAG = "ENTER_WORTH";
    private static final String RU_TAG = "RU_FRAG";

    // Global Views
    private ViewFlipper mViewFlipper;
    private ImageButton mButton_CancelBack, mButton_Save;
    private TextView mToolbarText;

    // Review views
    private String REVIEW_TITLE;
    private TextView mUnitName, mUnitSymbol;
    private CheckBox mReviewCheckbox;

    // Create/Edit Views
    private final String CREATE_TITLE = "Create New Unit";
    private final String EDIT_TITLE = "Edit";
    private TextInputEditText mEditName, mEditSymbol;
    private CheckBox mSymbolCheckbox;
    private boolean mExistsInList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_inter_play);

        Toolbar toolbar = findViewById(R.id.ip_toolbar);
        setSupportActionBar(toolbar);

        mViewModel = ViewModelProviders.of(this).get(UnitObjectViewModel.class);

        // Decide what kind of interface to begin with (Default = Create)
        Intent aIntent = getIntent();
        mFinalReviewMode = aIntent.getBooleanExtra(EDIT_UNIT, false);
        mReviewMode = mFinalReviewMode;
        mChanged = false;

        // Getting a passing unit
        mPassingMode = aIntent.getBooleanExtra(PASSING_UNIT, false);

    // Global Views
        mViewFlipper = findViewById(R.id.ip_view_flipper);
        mButton_CancelBack = findViewById(R.id.ip_toolbar_btn_cancel);
        mButton_Save = findViewById(R.id.ip_toolbar_btn_save);
        mToolbarText = findViewById(R.id.ip_toolbar_title);
        FrameLayout noSubunitsDisclaimer = findViewById(R.id.ip_tv_no_subunits);

    // Review Unit Views
        mUnitName = findViewById(R.id.ip_tv_unitname);
        mUnitSymbol = findViewById(R.id.ipreview_unit_symbol);
        mReviewCheckbox = findViewById(R.id.ip_review_checkbox);

    // Create/Edit Unit Views
        mEditName = findViewById(R.id.ipcreate_tiet_name);
        mEditSymbol = findViewById(R.id.ipcreate_tiet_symbol);
        mSymbolCheckbox = findViewById(R.id.ip_create_symbol_check);

        mViewModel.getUnitNames().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(final List<String> strings) {
                mEditName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(s != null) {
                            if(!strings.contains(s.toString().toLowerCase())) {
                                mExistsInList = false;
                                mButton_Save.setClickable(true);
                            }
                            else if(mRevisedUnit != null && s.toString().equals(mRevisedUnit.getName())) {
                                mExistsInList = false;
                                mButton_Save.setClickable(true);
                            }
                            else
                            {
                                mExistsInList = true;
                                mButton_Save.setClickable(false);
                                Toast.makeText(getApplicationContext(), NAME_ALREADY_EXISTS, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        mObserver = new Observer<List<Unit>>() {
            @Override
            public void onChanged(List<Unit> units) {
                if(mChanged) {
                    updateSubunits(units);
                }
            }
        };

        mViewModel.getAllUnits().observe(this, mObserver);

    // RecyclerView
        RecyclerView recyclerView = findViewById(R.id.ip_rv_subunits);
        mAdapter = new UnitInterPlayAdapter(this, mReviewMode, noSubunitsDisclaimer);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.ItemDecoration decoration =
                new DividerItemDecoration(getApplicationContext(),R.drawable.divider);
        recyclerView.addItemDecoration(decoration);

        mFragmentTransaction = null;

    // Editing a Unit
        if(mReviewMode) {
            // Substantiating Fragment
            mRetrieveFragment = RetrieveUnitFragment.newInstance(false);
            FragmentManager fragmentManager = getSupportFragmentManager();
            mFragmentTransaction = fragmentManager.beginTransaction();
            mFragmentTransaction.setCustomAnimations(R.anim.slide_from_bottom,R.anim.slide_to_bottom,
                    R.anim.slide_from_bottom,R.anim.slide_to_bottom);

            // Start fragment
            mFragmentTransaction.add(R.id.ip_container,mRetrieveFragment,RU_TAG).commit();
        }

    // Unit passing externally (Case: Active Count being upgraded to Unit)
        else if(mPassingMode) {
            flip(false);
            mToolbarText.setText(CREATE_TITLE.toUpperCase());
            ArrayList<Unit> activeCount = (ArrayList<Unit>) aIntent.getSerializableExtra
                                            (UnitInterPlayActivity.UNIS_ARRAY);

            for(Unit unit:activeCount) {
                Unit unitCopy = unit.copy();
                unitCopy.setWorth(unit.getCount());
                mAdapter.add(unitCopy);
            }
        }

    // Creating a Unit from scratch
        else {
            flip(false);
            mToolbarText.setText(CREATE_TITLE.toUpperCase());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mRevisedUnit == null && mReviewMode) {
            finish();
        }


    }

/*------------------------------------------------------------------------------------------------*/
//                                          Helper Methods                                        //
/*------------------------------------------------------------------------------------------------*/
    private static class UpdateSubunitsAsync extends AsyncTask<List<Unit>, Void, List<Unit>> {
        private Unit aOldUnit;
        private Unit aNewUnit;
        private UnitObjectViewModel aViewModel;

        UpdateSubunitsAsync(Unit revisedUnit, Unit newUnit,
                            UnitObjectViewModel viewModel) {
            this.aOldUnit = revisedUnit;
            this.aNewUnit = newUnit;
            this.aViewModel = viewModel;
        }

        @Override
        protected List<Unit> doInBackground(List<Unit>... list) {
            for(Unit unit : list[0]) {
                List<Unit> subunits = unit.getSubunits();

                if(!subunits.isEmpty()) {
                    if(subunits.contains(aOldUnit)) {
                        subunits.remove(aOldUnit);

                        if(aNewUnit != null) {
                            subunits.add(aNewUnit);
                        }
                    }
                }
            }

            return list[0];
        }

        @Override
        protected void onPostExecute(List<Unit> units) {
            super.onPostExecute(units);

            for(Unit unit : units) {
                aViewModel.saveUnit(unit);
            }
        }
    }

    private void updateSubunits(List<Unit> allUnis) {
        new UpdateSubunitsAsync(mTempUnit, mRevisedUnit, mViewModel).execute(allUnis);
        mChanged = false;
    }

    private static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void flip(boolean reviewUnit) {
    // Review
        if(reviewUnit) {
            if (mViewFlipper.getCurrentView() != findViewById(R.id.ip_flipper_option1_review)) {
                mViewFlipper.showNext();

                mButton_CancelBack.setImageResource(R.drawable.ic_back_carrot);
                mButton_Save.setVisibility(View.INVISIBLE);
                mButton_Save.setClickable(false);
                mToolbarText.setText(REVIEW_TITLE.toUpperCase());
                mReviewCheckbox.setChecked(mRevisedUnit.isSymbolBefore());
                mAdapter.setMode(true);
            }
        }

    // Create/Edit
        else {
            if (mViewFlipper.getCurrentView() != findViewById(R.id.ip_flipper_option2_create)) {
                mViewFlipper.showNext();
                mButton_CancelBack.setImageResource(R.drawable.ic_close);
                mButton_Save.setVisibility(View.VISIBLE);
                mButton_Save.setClickable(true);
                mAdapter.setMode(false);
            }
        }
        mReviewMode = reviewUnit;
    }

    private void convertViewsToEdit() {

        if (mUnitName.getText() != null) {
            mEditName.setText("");
            mEditName.setText(mUnitName.getText().toString());
            mToolbarText.setText(EDIT_TITLE.toUpperCase());
        }

        if(mUnitSymbol.getText() != null) {
            mEditSymbol.setText("");
            mEditSymbol.setText(mUnitSymbol.getText().toString());
            mSymbolCheckbox.setChecked(mReviewCheckbox.isChecked());
        }
    }

    private void convertViewsToReview() {
        mUnitName.setText(mRevisedUnit.getName());

        if(mRevisedUnit.getSymbol().length() != 0) {
            mUnitSymbol.setText(mRevisedUnit.getSymbol());
            mAdapter.setList(mRevisedUnit.getSubunits());
        }
        else {
            mUnitSymbol.setText("");
        }

    }

    // Could have used TextUtils.isEmpty for EditText
    private String verifyString
            (int minLength, int maxLength, TextInputEditText text) {
        String line;

        if(text.getText() != null) {
            line = text.getText().toString();

            if (line.length() >= minLength && line.length() <= maxLength && !mExistsInList) {
                return line.toLowerCase().trim();
            }

            // Length is too long
            else if(line.length() >= maxLength) {
                Toast.makeText(this, "name/symbol is long", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        return null;
    }

/*------------------------------------------------------------------------------------------------*/
//                                          OnClick Methods                                       //
/*------------------------------------------------------------------------------------------------*/
    public void addSubunitOnClick(View view) {
    // Substantiating Fragment
        // remove units already in subunit list
        ArrayList<Unit> aList = new ArrayList<>(mAdapter.getList());

        if(!mFinalReviewMode) {
            mRetrieveFragment = RetrieveUnitFragment.newInstance(aList, false);
        }
        else {
            aList.add(mRevisedUnit);
            mRetrieveFragment = RetrieveUnitFragment.newInstance(aList, false);
        }

        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.setCustomAnimations(R.anim.slide_from_bottom,R.anim.slide_to_bottom,
                R.anim.slide_from_bottom,R.anim.slide_to_bottom);

        // Start fragment
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.add(R.id.ip_container,mRetrieveFragment,RU_TAG).commit();
    }

    public void editUnitOnClick(View view) {
        convertViewsToEdit();
        flip(false);
    }

    public void deleteUnitOnClick(View view) {
        if(mRevisedUnit != null) {
            mChanged = true;
            mTempUnit = mRevisedUnit;
            mRevisedUnit = null;
            mViewModel.deleteUnit(mTempUnit);
            finish();
        }
    }

    public void cancelOnClick(View view) {
        onBackPressed();
    }

    public void saveOnClick(View view) {
        String unitName = verifyString(MIN_UNIT_NAME_LENGTH, MAX_UNIT_NAME_LENGTH, mEditName);
        String symbol = verifyString(1, MAX_UNIT_SYMBOL_LENGTH, mEditSymbol);

        hideKeyboard(this);

        // Creating new unit
        if(!mFinalReviewMode) {
            // Insuring name is valid
            if (unitName != null) {
                mRevisedUnit = new Unit(unitName);

                // Adding symbol and positioning
                if (symbol != null) {
                    mRevisedUnit.setSymbol(symbol);
                    mRevisedUnit.setSymbolPos(mSymbolCheckbox.isChecked());
                }

                // Add subunits
                List<Unit> subunits = mAdapter.getList();
                for (Unit subs : subunits) {
                    mRevisedUnit.addSubunit(subs, subs.getWorth());
                }
                mViewModel.saveUnit(mRevisedUnit);
                finish();
            }
            else {
                Toast.makeText(this, INVALID_NAME_ERROR, Toast.LENGTH_SHORT).show();
            }
        }

        // Editing Unit
        else {
            mTempUnit = mRevisedUnit;
            mViewModel.deleteUnit(mRevisedUnit);

            if(unitName != null) {
                mRevisedUnit = new Unit(unitName);
                REVIEW_TITLE = unitName;
            }
            else {
                String name = mRevisedUnit.getName();
                mRevisedUnit = new Unit(name);
                REVIEW_TITLE = name;
            }

            if(symbol != null) {
                mRevisedUnit.setSymbol(symbol);
            }
            else{
                mRevisedUnit.setSymbol("");
            }

            mRevisedUnit.setSymbolPos(mSymbolCheckbox.isChecked());

            List<Unit> subunits = mAdapter.getList();
            for(Unit unit : subunits) {
                mRevisedUnit.addSubunit(unit, unit.getWorth());
            }
            mChanged = true;
            mViewModel.saveUnit(mRevisedUnit);
            convertViewsToReview();
            flip(true);
        }
    }

    @Override
    public void onBackPressed() {
        boolean onCreateView = mViewFlipper.getCurrentView() == findViewById(R.id.ip_flipper_option2_create);
        boolean fragmentOnScreen = mFragmentTransaction != null;

        if(fragmentOnScreen && !mReviewMode){
            getSupportFragmentManager().popBackStack();
            mFragmentTransaction = null;
        }
        // Flip if entered into create/edit mode from ReviewMode
        else if(onCreateView && mFinalReviewMode && !fragmentOnScreen) {
            mAdapter.setList(mRevisedUnit.getSubunits());
            flip(true);
            convertViewsToEdit();
        }
        // Close activity if originally in create mode or in review mode
        else {
            finish();
        }
    }

/*------------------------------------------------------------------------------------------------*/
//                                      Fragment Interaction                                      //
/*------------------------------------------------------------------------------------------------*/
    // Retrieve Units
    @Override
    public void onFragmentInteraction(List<Unit> selectedUnits) {
        Unit tempUnit;
        if(selectedUnits != null) {
            if(!selectedUnits.isEmpty()) {
                tempUnit = selectedUnits.get(0);

                // Get subunits
                if(!mReviewMode) {
                   // Starting EnterWorth Fragment
                    mWorthFragment = EnterWorthFragment.newInstance(tempUnit, EnterWorthFragment.FROM_RU_CODE);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    mFragmentTransaction = fragmentManager.beginTransaction();
                    mFragmentTransaction.setCustomAnimations(R.anim.slide_from_bottom,R.anim.slide_to_bottom,
                            R.anim.slide_from_bottom,R.anim.slide_to_bottom);
                    mFragmentTransaction.addToBackStack(null);
                    mFragmentTransaction.add(R.id.ip_container, mWorthFragment,ENTER_WORTH_TAG).commit();
                }

                // Get Unit for Revision
                else {
                    mRevisedUnit = tempUnit;
                    REVIEW_TITLE = mRevisedUnit.getName();
                    mToolbarText.setText(REVIEW_TITLE.toUpperCase());

                    flip(true);
                    mReviewCheckbox.setChecked(mRevisedUnit.isSymbolBefore());
                    mUnitName.setText(mRevisedUnit.getName());
                    mUnitSymbol.setText(mRevisedUnit.getSymbol());

                    if (!mRevisedUnit.getSubunits().isEmpty()) {
                        for(Unit unit : mRevisedUnit.getSubunits()) {
                            mAdapter.add(unit);
                        }
                    }
                    mFragmentTransaction = null;
                }
            }
            // User did not select a Unit for revision/Editing
            else {
                finish();
            }
        }
    }

    // Enter Worth Fragment
    @Override
    public void onEnterWorthInteraction(Unit unit, int resultCode) {
        mFragmentTransaction = null;
        switch(resultCode) {
            // When adding a subunit
            case EnterWorthFragment.FROM_RU_CODE:
                if(!mAdapter.getList().contains(unit)) {
                    mAdapter.add(unit);
                }
                else {
                    Toast.makeText(this, "already in the list", Toast.LENGTH_LONG).show();
                }
                break;

            // When editing a subunit
            case EnterWorthFragment.FROM_SUE_CODE:
               if(!mAdapter.modify(unit)) {
                   Toast.makeText(getApplicationContext(),"failed to modify subunit",Toast.LENGTH_SHORT).show();
               }
        }
    }

    // Subunit Edit Fragment
    @Override
    public void onSubunitEditInteraction(Unit unit, int resultCode) {
        switch (resultCode) {
            case SubunitEditFragment.REMOVE_UNIT:
                mAdapter.remove(unit);
                break;

            case SubunitEditFragment.EDIT_UNIT:
                mWorthFragment = EnterWorthFragment.newInstance(unit,EnterWorthFragment.FROM_SUE_CODE);
                mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                mFragmentTransaction.addToBackStack(null);
                mFragmentTransaction.add(R.id.ip_container, mWorthFragment,ENTER_WORTH_TAG).commit();
                break;

            default: Toast.makeText(getApplicationContext(),"error occurred while attempting to edit subunit",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
