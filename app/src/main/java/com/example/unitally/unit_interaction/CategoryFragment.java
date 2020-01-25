package com.example.unitally.unit_interaction;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.unitally.R;
import com.example.unitally.objects.Category;
import com.example.unitally.room.CategoryViewModel;
import com.example.unitally.tools.UnitallyValues;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment
                                implements SearchView.OnQueryTextListener{
    private static final String CATEGORY_REASON = "com.example.unitally.CategoryReason";
    private static final String SELECTION_ID = "com.example.unitally.CategorySelectionID";

    // Communication Variables
    public static final int RETRIEVE_CATEGORY = 1;
    public static final int EDIT_CATEGORY = 2;
    private static final String EDIT_CATEGORY_HEADER_TEXT = "How would you like to rename ";

    // Toast Prompts
    private static final String INVALID_CATEGORY_NAME_PROMPT = "Invalid. Character parameters "
            + UnitallyValues.MIN_UNIT_NAME_LENGTH + " - " + UnitallyValues.MAX_UNIT_NAME_LENGTH;
    private static final String EDIT_CATEGORY_PROMPT = "Please select a Category to edit";

    // Specialty Vars
    private CategoryViewModel mViewModel;
    private OnFragmentInteractionListener mListener;
    private CategoryAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SelectionTracker<String> mSelectionTracker;

    // Global Vars
    private int mIntention;
    private String mTypedName;
    private List<Category> mStaticCategoryList;
    private Category mSelectedCategory;
    private TextInputEditText mEditTextBox;
    private boolean mAtBackStackLimit;

    private boolean mLoaded;
    private Category mTempCategory;

    // Views Vars
    private Button mCreateButton, mSaveButton, mDeleteButton;
    private TextView mEditTextTitle;

    // BackStack Listener
    private FragmentManager.OnBackStackChangedListener mBackStackListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {

            if(mIntention == EDIT_CATEGORY) {
                int backstackCount = getActivity().getSupportFragmentManager().getBackStackEntryCount();

                if (mSelectedCategory != null && mAtBackStackLimit) {
                    unloadCategoryViews();
                }
                // Cat has been selected
                if (backstackCount == 2) {
                    mAtBackStackLimit = true;
                } else if (2 > backstackCount) {
                    mAtBackStackLimit = false;
                }
            }
        }
    };

    public CategoryFragment() {
        // Required empty public constructor
    }

/**
 * Used to obtain an instance of the CategoryFragment. Intent can be either
 * choose, edit or delete a category.
 *
 * @param intention Choose, Edit or Delete
 * @return A new instance of fragment CategoryFragment.
 */
    public static CategoryFragment newInstance(int intention) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putInt(CATEGORY_REASON, intention);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIntention = getArguments().getInt(CATEGORY_REASON);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.category_fragment, container, false);

        // Views
        SearchView searchView = v.findViewById(R.id.category_searchview);
        searchView.setOnQueryTextListener(this);

        // RecyclerView
        mRecyclerView = v.findViewById(R.id.rv_category_list);
        mAdapter = new CategoryAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mLoaded = false;
        mViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);

        mViewModel.getAllCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                syncAdapter(categories);
            }
        });

        // Loading appropriate views
        switch (mIntention) {
            case RETRIEVE_CATEGORY:
                setRetrievalViews(v);
                break;

            case EDIT_CATEGORY:
                setEditCategoryViews(v);
                break;

            default: finish();
                     Toast.makeText(getActivity(),
                             UnitallyValues.BAD_CODING_PROMPT, Toast.LENGTH_LONG).show();
                     break;
        }

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;

            getActivity().getSupportFragmentManager().addOnBackStackChangedListener(mBackStackListener);

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().getSupportFragmentManager().removeOnBackStackChangedListener(mBackStackListener);
        mListener = null;
    }

    /**
     * Closes the fragment.
     */
    private void finish() {
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).commit();
    }

    public interface OnFragmentInteractionListener {
        void onCategoryFragmentInteraction(Category category, int reason);
    }

    /*---------------------------- Boiler Plate ----------------------------*/

/**
 *  Synchronizes the adapter view with up to date ViewModel and SelectionTracker.
 *
 *  The mLoaded parameter is used for initial loading. Helps when adding a new Category
 *  after the fact.
 *
 * @param categoryList Initial CategoryList from database.
 */
    private void syncAdapter(List<Category> categoryList) {
        if(!mLoaded) {
            mStaticCategoryList = categoryList;
            mAdapter.setList(categoryList);

            // Selection Utilities
            mSelectionTracker = createSelectorTracker(mStaticCategoryList);
            mAdapter.setSelectorTracker(mSelectionTracker);

            mSelectionTracker.addObserver(new SelectionTracker.SelectionObserver<String>() {
                // Should only allow for one Category to be chosen
                @Override
                public void onSelectionChanged() {
                    super.onSelectionChanged();

                    for (String name : mSelectionTracker.getSelection()) {
                        if (mIntention == RETRIEVE_CATEGORY) {
                            returnCategory(name);
                        } else if (mIntention == EDIT_CATEGORY) {
                            loadCategoryIntoView(name);
                        }
                    }
                }
            });
            mLoaded = true;
        }
        else {
            mAdapter.addCategory(mTempCategory);
        }
    }


/**
 * Validated name length.
 *
 * @param name Category name
 * @return True if Category name is within correct length. False otherwise.
 */
    private boolean validName(@NonNull String name) {
        return name.length() >= UnitallyValues.MIN_UNIT_NAME_LENGTH
                && name.length() <= UnitallyValues.MAX_UNIT_NAME_LENGTH;
    }

/**
 * Will convert String into a Category object and save to database.
 *
 * @param categoryName The Category name.
 */
    private void saveNewCategory(@NonNull String categoryName) {
        Category newCategory = new Category(categoryName);
        mTempCategory = newCategory;
        mViewModel.saveCategory(newCategory);
    }

    private void saveNewCategory(@NonNull Category category) {
        mTempCategory = category;
        mViewModel.saveCategory(category);
    }

/**
 * Deletes the category passed.
 *
 * @param category Category to be deleted.
 */
    private void deleteCategory(Category category) {
        mViewModel.deleteCategory(category);
    }

/**
 * Disables button. Used for all the buttons in the Category Fragment
 *
 * @param button button to be disabled.
 */
    private void disableButton(Button button) {
        if (button != null) {
            button.setClickable(false);
            button.setBackgroundColor
                    (getResources().getColor(R.color.disabled_button_color));
        }
    }

/**
 * Enables button. Used for all the buttons in the Category Fragment
 *
 * @param button button to be enabled.
 */
    private void enableButton(Button button) {
        if (button != null) {
            button.setClickable(true);
            button.setBackgroundColor
                    (getResources().getColor(R.color.colorPrimary));
        }
    }

/*------------------------------------------------------------------------------------------------*/
//                                     Editing Methods                                            //
/*------------------------------------------------------------------------------------------------*/
/**
 * This method will setup the user to choose a Category from the list, then
 * update the header to set a new name for the Category.
 */
    private void setEditCategoryViews(View view) {
        ViewFlipper headFlipper = view.findViewById(R.id.category_header_flipper);
        headFlipper.showNext();

        mEditTextTitle = view.findViewById(R.id.category_edit_text_header);
        mEditTextBox = view.findViewById(R.id.new_category_tiet);

        // Initialize Create Button
        setRetrievalViews(view);
        mSaveButton = view.findViewById(R.id.category_save_button);
        mDeleteButton = view.findViewById(R.id.category_delete_button);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveButtonOnClick();
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCategory(mSelectedCategory);
                // TODO: add a confirmation window
                finish();
            }
        });

        unloadCategoryViews();
    }

/**
 * Unloads the views which enable the user to edit a category name.
 * Makes all the views go back to default.
 */
    private void unloadCategoryViews() {
        mSelectedCategory = null;
        mEditTextTitle.setText(EDIT_CATEGORY_PROMPT);
        mEditTextBox.setText("");

        // Disabling UI
        mEditTextBox.setFocusable(false);
        mEditTextBox.setBackgroundColor
                (getResources().getColor(R.color.disabled_button_color));
        disableButton(mSaveButton);
        disableButton(mDeleteButton);
    }

/**
 * Once a category has been selected, all the views are loaded with Category details.
 *
 * @param selectedCategoryName Category String name to be loaded.
 */
    private void loadCategoryIntoView(String selectedCategoryName) {
        if(mSelectedCategory == null) {
            int selectionIndex = mStaticCategoryList.indexOf(new Category(selectedCategoryName));

            if (selectionIndex >= 0) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction().addToBackStack(null).commit();

                mSelectedCategory = mStaticCategoryList.get(selectionIndex);

                mEditTextTitle.setText
                        (EDIT_CATEGORY_HEADER_TEXT.concat(" \"" + selectedCategoryName + "\""));

                // Enabling UI
                mEditTextBox.setFocusableInTouchMode(true);
                mEditTextBox.setBackgroundColor
                        (getResources().getColor(R.color.design_default_color_background));
                enableButton(mSaveButton);
                enableButton(mDeleteButton);
            } else {
                mSelectedCategory = null;
                Toast.makeText(getActivity(),UnitallyValues.BAD_CODING_PROMPT ,Toast.LENGTH_LONG).show();
                Log.d(UnitallyValues.BUGS, "CategoryFragment: A second Category tried to load.");
            }
        }
    }

    private void saveButtonOnClick() {
        // Editing a selected category
        Category editedCat = validateCategoryChange(mEditTextBox);
        if (editedCat != null) {
            deleteCategory(mSelectedCategory);
            saveNewCategory(editedCat);
            finish();
        } else {
            Toast.makeText(getActivity(), INVALID_CATEGORY_NAME_PROMPT, Toast.LENGTH_SHORT).show();
        }
    }

/**
 * Verifies new name is valid and does not already exists.
 *
 * @param textBox The TextBox view containing the new name.
 * @return A new Category with corresponding name. Null if name was invalid.
 */
    private Category validateCategoryChange(TextInputEditText textBox) {
        if(textBox.getText() != null) {
            Category prospect = new Category(textBox.getText().toString());

            if (validName(prospect.getName()) && !mStaticCategoryList.contains(prospect)) {
                return prospect;
            }
        }
        return null;
    }

/*------------------------------------------------------------------------------------------------*/
//                                     Retrieval Methods                                          //
/*------------------------------------------------------------------------------------------------*/
/**
 * This method will setup the user to choose from a list of exiting categories.
 * In addition, a search function and a button which will allow the user to
 * simultaneously create, choose, and save a new category.
 */
    private void setRetrievalViews(View view) {
        mCreateButton = view.findViewById(R.id.category_create_button);
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNewCategory(mTypedName);
            }
        });
        disableButton(mCreateButton);
    }

/**
 *  Returns a Category back to Activity.
 *
 * @param categoryName Name of the Category to be returned.
 */
    private void returnCategory(String categoryName) {
        Category retrievedCategory = new Category(categoryName);

        finish();

        if(mListener != null){
            mListener.onCategoryFragmentInteraction(retrievedCategory, mIntention);
        }
    }

/*------------------------------------------------------------------------------------------------*/
//                                       Filtering Methods                                        //
/*------------------------------------------------------------------------------------------------*/
    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Filtering list
        if(validName(query)) {

            Category temp = new Category(query);
            if(!mStaticCategoryList.contains(temp))
                enableButton(mCreateButton);

            else {
                disableButton(mCreateButton);
            }

            mTypedName = query;
            List<Category> filteredList = filter(query, mAdapter.getList());
            mAdapter.replaceAll(filteredList);
        }
        // Filter has no search results
        else {
            disableButton(mCreateButton);
            mAdapter.setList(mStaticCategoryList);
            mTypedName = null;
        }

        mRecyclerView.scrollToPosition(0);
        return true;
    }

    private List<Category> filter(String query, List<Category> list) {
        String lowerCaseQuery = query.toLowerCase();
        List<Category> filteredList = new ArrayList<>();

        for(Category testCategory:list) {
            String categoryName = testCategory.getName().toLowerCase();
            if(categoryName.contains(lowerCaseQuery)) {
                filteredList.add(testCategory);
            }
        }

        return filteredList;
    }

//------------------------------------------------------------------------------------------------//
/*                                      Selection Methods                                         */
//------------------------------------------------------------------------------------------------//
    private SelectionTracker<String> createSelectorTracker(List<Category> staticList) {

        SelectionTracker<String> selectionTracker = new SelectionTracker.Builder<>(
                SELECTION_ID,
                mRecyclerView,
                new CategoryKeyProvider(ItemKeyProvider.SCOPE_CACHED,
                        staticList,
                        mAdapter.getSortedList()),
                new CategoryItemLookup(mRecyclerView),
                StorageStrategy.createStringStorage())
                .withSelectionPredicate(SelectionPredicates.<String>createSelectSingleAnything())
                .build();

        return selectionTracker;
    }

    private static class CategoryKeyProvider extends ItemKeyProvider<String> {
        private List<Category> aStaticList;
        private SortedList<Category> aAdapterList;

        CategoryKeyProvider(int scope, List<Category> staticList, SortedList<Category> dynamicList) {
            super(scope);
            aAdapterList = dynamicList;
            aStaticList = staticList;
        }

        @Nullable
        @Override
        public String getKey(int position) {
            return aStaticList.get(position).getName();
        }

        @Override
        public int getPosition(@NonNull String key) {
            int position = 0;
            for(int i = 0; i<aAdapterList.size(); i++) {
                if(aAdapterList.get(i).getName().equalsIgnoreCase(key)) {
                    return i;
                }
            }
            return position;
        }
    }

    public class CategoryItemLookup extends ItemDetailsLookup<String> {
        private RecyclerView aRecyclerView;

        CategoryItemLookup(RecyclerView recyclerView) {
            this.aRecyclerView = recyclerView;
        }

        @Nullable
        @Override
        public ItemDetails<String> getItemDetails(@NonNull MotionEvent e) {
            View view = aRecyclerView.findChildViewUnder(e.getX(), e.getY());
            if(view != null) {
                RecyclerView.ViewHolder viewHolder = aRecyclerView.getChildViewHolder(view);
                if(viewHolder instanceof CategoryAdapter.CategoryViewHolder) {
                    return ((CategoryAdapter.CategoryViewHolder) viewHolder).getItemDetails();
                }
            }
            return null;
        }
    }
}
