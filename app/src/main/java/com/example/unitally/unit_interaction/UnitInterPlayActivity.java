package com.example.unitally.unit_interaction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
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

import com.example.unitally.DragSwipeHelper;
import com.example.unitally.R;
import com.example.unitally.DividerItemDecoration;
import com.example.unitally.objects.Category;
import com.example.unitally.unit_retrieval.RetrieveUnitFragment;
import com.example.unitally.SubunitEditFragment;
import com.example.unitally.tools.UnitallyValues;
import com.example.unitally.objects.Unit;
import com.example.unitally.room.UnitObjectViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class UnitInterPlayActivity extends AppCompatActivity
        implements RetrieveUnitFragment.OnFragmentInteractionListener,
                    EnterWorthFragment.OnFragmentInteractionListener,
                    SubunitEditFragment.OnFragmentInteractionListener,
                    CategoryFragment.OnFragmentInteractionListener{

    // Tells whether choice is to edit a unit or create from scratch
    public static final String REVIEW_MODE = "com.example.UnitCounterV2.ReviewMode";
    public static final String DISPLAY_UNIT="com.example.UnitCounterV2.DisplayUnit";
    public static final String PASSING_UNIT="com.example.UnitCounterV2.PassingUnit";
    public static final String UNIS_ARRAY="com.example.UnitCounterV2.Unis";

    private static final int REASON_SUBUNITS = "Get Subunits for a parent unit".hashCode();
    private static final String INVALID_NAME_ERROR = "Please enter a valid name";
    private static final String NAME_ALREADY_EXISTS = "Name already in use";

    private Boolean mReviewMode, mFinalReviewMode, mPassingMode;
    private boolean mChanged;
    // TempUnit is used as a place holder when revising subunits.
    // Use mRevisedUnit for all other functions.
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
    private ViewFlipper mViewFlipper_Red,       // Delete/Cancel float action button
                        mViewFlipper_yellow,    // Save/Cancel float action button
                        mViewFlipper_name,      // Unit name text/edit text view
                        mViewFlipper_symbol;    // Symbol text/edit text view

    // Display views
    private TextView mUnitName_TV, mUnitSymbol_TV;
    private CheckBox mDisplayUnitPosition_Checkbox;
    private TextView mCategoryName_TV;
    private FloatingActionButton mFA_delete, mFA_edit;

    // Create/Edit Views
    private final String CREATE_TITLE = "Create New Unit";
    private final String EDIT_TITLE = "Edit";
    private TextInputEditText mEditName, mEditSymbol;
    private boolean mExistsInList;
    private FloatingActionButton mFA_cancel, mFA_save;
    private ImageButton mSubunitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_inter_play);

    // Instantiating Views
        // Global Views
        mViewFlipper_name = findViewById(R.id.ip_vf_unitname);
        mViewFlipper_symbol = findViewById(R.id.ip_vf_symbol);
        mViewFlipper_Red = findViewById(R.id.ip_vf_red);
        mViewFlipper_yellow = findViewById(R.id.ip_vf_yellow);

        FrameLayout noSubunitsDisclaimer = findViewById(R.id.ip_tv_no_subunits);

        // Review Unit Views
        mUnitName_TV = findViewById(R.id.ip_tv_unitname);
        mUnitSymbol_TV = findViewById(R.id.ipreview_unit_symbol);
        mDisplayUnitPosition_Checkbox = findViewById(R.id.ip_review_checkbox);
        mCategoryName_TV = findViewById(R.id.ip_category_tv);
        mFA_delete = findViewById(R.id.ip_fab_delete);
        mFA_edit = findViewById(R.id.ip_fab_edit);

        // Setting onClick to launch Category selection
        mCategoryName_TV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                CategoryFragment catFragment = CategoryFragment
                        .newInstance(CategoryFragment.RETRIEVE_CATEGORY);

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                // TODO: Set Animations
                transaction.addToBackStack(null);
                transaction.add(R.id.ip_container, catFragment, "CAT_FRAGMENT").commit();
            }
        });
        mCategoryName_TV.setClickable(false);

        mFA_delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                deleteUnitOnClick(view);
            }
        });

        mFA_edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                editUnitOnClick(view);
            }
        });

        // Create/Edit Unit Views
        mEditName = findViewById(R.id.ip_unitname_tiet);
        mEditSymbol = findViewById(R.id.ip_symbol_tiet);
        mFA_cancel = findViewById(R.id.ip_fab_cancel);
        mFA_save = findViewById(R.id.ip_fab_save);
        mSubunitButton = findViewById(R.id.ip_addsubunit_button);

        mSubunitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                addSubunitOnClick(view);
            }
        });

        mFA_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mFA_save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                saveOnClick(view);
            }
        });

    // Previous Activity Data
    // Decide what kind of interface to begin with (Default = Create)
        Intent aIntent = getIntent();
        mFinalReviewMode = aIntent.getBooleanExtra(REVIEW_MODE, false);
        mReviewMode = mFinalReviewMode;
        mChanged = false;

        // Getting a subunits
        mPassingMode = aIntent.getBooleanExtra(PASSING_UNIT, false);

        // Retrieving Unit to be Displayed
        mRevisedUnit = (Unit) aIntent.getSerializableExtra(DISPLAY_UNIT);

    // Database data
        mViewModel = ViewModelProviders.of(this).get(UnitObjectViewModel.class);

        // Adding Observer for Name verification
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
                            }
                            else if(mRevisedUnit != null && s.toString().equals(mRevisedUnit.getName())) {
                                mExistsInList = false;
                            }
                            else
                            {
                                mExistsInList = true;
                                Toast.makeText(getApplicationContext(), NAME_ALREADY_EXISTS, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        // Observe Subunit alteration
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

        // Enable Swipe
        DragSwipeHelper moveHelper = new DragSwipeHelper(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(moveHelper);

        touchHelper.attachToRecyclerView(recyclerView);

    // Preparing FragmentTransaction
        mFragmentTransaction = null;

    // Display Unit / Prepare for editing
        if(mRevisedUnit != null) {
            unitDisplayConfiguration(mRevisedUnit);
        }

    // Unit passing externally (Case: Active Count being upgraded to Unit)
        else if(mPassingMode) {
            ArrayList<Unit> activeCount = (ArrayList<Unit>) aIntent.getSerializableExtra
                                            (UnitInterPlayActivity.UNIS_ARRAY);
            saveAsUnitConfiguration(activeCount);
        }

    // Creating a Unit from scratch
        else {
            flipViews();
            mUnitName_TV.setText(CREATE_TITLE.toUpperCase());
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
//                                   Configuration Methods                                        //
/*------------------------------------------------------------------------------------------------*/
    /**
    * Update and configure all views associated with the "Display" feature of
    * the UnitInterplay activity.
    *
    * @param displayUnit Unit to be displayed
    */
    private void unitDisplayConfiguration(Unit displayUnit) {
        // Set text to display Unit name
        mUnitName_TV.setText(displayUnit.getName());
        mAdapter.mDisplayMode = true;

        // Set text to display Unit symbol (If applicable)
        if(displayUnit.getSymbol().length() != 0) {
            mUnitSymbol_TV.setText(displayUnit.getSymbol());
        }
        else {
            mUnitSymbol_TV.setText("");
        }

        // Set symbol position indicator
        mSubunitButton.setVisibility(View.INVISIBLE);
        mDisplayUnitPosition_Checkbox.setChecked(displayUnit.isSymbolBefore());

        // Set Category views
        mCategoryName_TV.setText(displayUnit.getCategory().getName());

        // Set Subunit Adapter
        if (!displayUnit.getSubunits().isEmpty()) {
            for(Unit unit : displayUnit.getSubunits()) {    // TODO: Verify if this is the best method
                mAdapter.add(unit);
            }
        }
    }

    /**
     * Used to configure views when only subunits are passed to UnitInterPlayActivity.
     *
     * @param toBeSubunits Subunits being saved
     */
    private void saveAsUnitConfiguration(List<Unit> toBeSubunits) {
        flip(false);
        mUnitName_TV.setText(CREATE_TITLE.toUpperCase());

        for(Unit unit:toBeSubunits) {
            Unit unitCopy = unit.copy();
            unitCopy.setWorth(unit.getCount());
            mAdapter.add(unitCopy);
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
    } // End of AsyncTask

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

   /**
     * Flip views to display either display mode or edit mode
     * @param displayView
     */
    private void flip(boolean displayView) {
    // Review
        if(displayView) {
            if (mViewFlipper_name.getCurrentView() != findViewById(R.id.ip_tv_unitname)) {
                mDisplayUnitPosition_Checkbox.setChecked(mRevisedUnit.isSymbolBefore());
                mViewFlipper_name.showNext();
                mViewFlipper_yellow.showNext();
                mViewFlipper_Red.showNext();
                mViewFlipper_symbol.showNext();
                mAdapter.setEditable(true);
            }
        }

    // Create/Edit
        else {
            if (mViewFlipper_name.getCurrentView() != findViewById(R.id.ip_unitname_tiet)) {

                mAdapter.setEditable(false);
            }
        }
        mReviewMode = displayView;
    }

    /**
     * Flips views from Display Unit to Edit Unit. When accessed by Create Unit,
     * mReviewMode needs to be set True.
     */
    private void flipViews() {
        mViewFlipper_yellow.showNext();
        mViewFlipper_Red.showNext();
        mViewFlipper_symbol.showNext();
        mViewFlipper_name.showNext();

        // Alter Display and Edit interfaces
        if(mFinalReviewMode) {
            mReviewMode = !mReviewMode;

            if (mReviewMode) {
                convertViewsToReview();
                mSubunitButton.setVisibility(View.INVISIBLE);


            } else {
                convertViewsToEdit();
                mSubunitButton.setVisibility(View.VISIBLE);
            }
        }
        mDisplayUnitPosition_Checkbox.setClickable(!mReviewMode);
        mCategoryName_TV.setClickable(!mReviewMode);
        mAdapter.setEditable(!mReviewMode);
    }

    private void convertViewsToEdit() {

        if (mUnitName_TV.getText() != null) {
            mEditName.setText("");
            mEditName.setText(mUnitName_TV.getText().toString());
            mUnitName_TV.setText(EDIT_TITLE.toUpperCase());
        }

        if(mUnitSymbol_TV.getText() != null) {
            mEditSymbol.setText("");
            mEditSymbol.setText(mUnitSymbol_TV.getText().toString());
            mDisplayUnitPosition_Checkbox.setChecked(mDisplayUnitPosition_Checkbox.isChecked());
        }
    }

    private void convertViewsToReview() {
        mUnitName_TV.setText(mRevisedUnit.getName());

        if(mRevisedUnit.getSymbol().length() != 0) {
            mUnitSymbol_TV.setText(mRevisedUnit.getSymbol());
            mAdapter.setList(mRevisedUnit.getSubunits());
        }
        else {
            mUnitSymbol_TV.setText("");
        }
    }

   /**
     * Verify if text length too long or not long enough.
     *
     * @param minLength
     * @param maxLength
     * @param text
     * @return
     */
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

        // TODO: Revise this
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
       // convertViewsToEdit();
        flipViews();
    }

    // TODO: Investigate
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
        String unitName = verifyString(UnitallyValues.MIN_UNIT_NAME_LENGTH,
                                        UnitallyValues.MAX_UNIT_NAME_LENGTH,
                                        mEditName);
        String symbol = verifyString(1, UnitallyValues.MAX_UNIT_SYMBOL_LENGTH, mEditSymbol);

        hideKeyboard(this);

        // Creating new unit
        if(!mFinalReviewMode) {
            // Insuring name is valid
            if (unitName != null) {
                mRevisedUnit = new Unit(unitName);

                // Adding symbol and positioning
                if (symbol != null) {
                    mRevisedUnit.setSymbol(symbol);
                    mRevisedUnit.setSymbolPos(mDisplayUnitPosition_Checkbox.isChecked());
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
            mViewModel.deleteUnit(mRevisedUnit);

            // Rebuilding name
            if(unitName != null) {
                mRevisedUnit = new Unit(unitName);
            }
            else {
                String name = mRevisedUnit.getName();
                mRevisedUnit = new Unit(name);
            }

            // Rebuilding Symbol
            if(symbol != null) {
                mRevisedUnit.setSymbol(symbol);
            }
            else{
                mRevisedUnit.setSymbol("");
            }

            mRevisedUnit.setSymbolPos(mDisplayUnitPosition_Checkbox.isChecked());

            // Rebuilding category
            if(mCategoryName_TV.getText().toString() != null) {
                String categoryName = mCategoryName_TV.getText().toString();
                mRevisedUnit.setCategory(new Category(categoryName));
            }

            // Rebuilding subunits
            List<Unit> subunits = mAdapter.getList();
            for(Unit unit : subunits) {
                mRevisedUnit.addSubunit(unit, unit.getWorth());
            }

            mChanged = true;
            mViewModel.saveUnit(mRevisedUnit);
            //convertViewsToReview();
            flipViews();
        }
    }

    @Override
    public void onBackPressed() {
        boolean onCreateView = mViewFlipper_name.getCurrentView() == findViewById(R.id.ip_unitname_tiet);
        boolean fragmentOnScreen = mFragmentTransaction != null;

        if(fragmentOnScreen && !mReviewMode){
            getSupportFragmentManager().popBackStack();
            mFragmentTransaction = null;
        }
        // Flip if entered into create/edit mode from ReviewMode
        else if(onCreateView && mFinalReviewMode && !fragmentOnScreen) {
            mAdapter.setList(mRevisedUnit.getSubunits());
            flipViews();
            //flip(true);
           // convertViewsToEdit();
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
    // TODO: Rewrite using RetrieveUnit reason instead of mReviewMode, Unit now comes externally from Activity.
    @Override
    public void onFragmentInteraction(List<Unit> selectedUnits, int reason) {
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
                    //mUnitName_TV.setText(REVIEW_TITLE.toUpperCase());

                    flip(true);
                    mDisplayUnitPosition_Checkbox.setChecked(mRevisedUnit.isSymbolBefore());
                    mUnitName_TV.setText(mRevisedUnit.getName());
                    mUnitSymbol_TV.setText(mRevisedUnit.getSymbol());

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

    @Override
    public void onCategoryFragmentInteraction(Category category, int reason) {

        if(reason == CategoryFragment.RETRIEVE_CATEGORY && category != null) {
            mRevisedUnit.setCategory(category);
            mCategoryName_TV.setText(category.getName());
        }
    }
}
