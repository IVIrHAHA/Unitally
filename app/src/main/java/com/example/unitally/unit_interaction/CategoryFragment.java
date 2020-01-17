package com.example.unitally.unit_interaction;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
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

import com.example.unitally.R;
import com.example.unitally.objects.Category;
import com.example.unitally.room.CategoryViewModel;
import com.example.unitally.tools.UnitallyValues;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment
                                implements SearchView.OnQueryTextListener{
    private static final String CATEGORY_REASON = "com.example.unitally.CategoryReason";

    // Communication Variables
    public static final int RETRIEVE_CATEGORY = 1;
    public static final int EDIT_CATEGORY = 2;
    private static final String INVALID_CATEGORY_NAME = "Please type a valid name";
    private static final String SELECTION_ID = "com.example.unitally.CategorySelectionID";

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

    // Views Vars
    private Button mCreateButton;

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

        mViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);

                mViewModel.getAllCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                loadCategoryDetails(categories);
            }
        });

        switch (mIntention) {
            case RETRIEVE_CATEGORY:
                setRetrievalViews(v);
                break;

            case EDIT_CATEGORY:
                setEditCategoryViews();
                break;

            default: //TODO: implement invalid intention
        }

        return v;
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onCategoryFragmentInteraction(null, 0);
        }
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

    /*---------------------------- Helper Methods ----------------------------*/
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
                saveNewCategory();
            }
        });
        disableCreateButton();
    }

    /**
     * This method will setup the user to choose a Category from the list, then
     * update the header to set a new name for the Category.
     *
     * - Remove the "add button"
     */
    private void setEditCategoryViews() {

    }

    private void loadCategoryDetails(List<Category> categoryList) {
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

                for(String name : mSelectionTracker.getSelection()) {
                    if(mIntention == RETRIEVE_CATEGORY) {
                        returnCategory(name);
                    }
                    else if(mIntention == EDIT_CATEGORY) {

                    }
                }
            }
        });
    }

    private void saveNewCategory() {
        if(mTypedName != null) {
            Category newCategory = new Category(mTypedName);
            mViewModel.saveCategory(newCategory);
            mAdapter.addCategory(newCategory);
            // TODO: Add observer to view model to automatically update adapter
        } else {
            // TODO: Create invalid name indication
        }
    }

    private void disableCreateButton(){
        mCreateButton.setClickable(false);
        mCreateButton.setBackgroundColor
                (getResources().getColor(R.color.disabled_button_color));
    }

    private void enableCreateButton() {
        mCreateButton.setClickable(true);
        mCreateButton.setBackgroundColor
                (getResources().getColor(R.color.colorPrimary));
    }

    private void returnCategory(String categoryName) {
        Category retrievedCategory = new Category(categoryName);

        getActivity().getSupportFragmentManager().beginTransaction().detach(this).commit();

        if(mListener != null){
            mListener.onCategoryFragmentInteraction(retrievedCategory, mIntention);
        }
    }

/*------------------------------------------------------------------------------------------------*/
//                                          Filter Methods                                        //
/*------------------------------------------------------------------------------------------------*/
    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Filtering list
        if(query.length() >= UnitallyValues.MIN_QUERY_LENGTH) {

            Category temp = new Category(query);
            if(!mStaticCategoryList.contains(temp))
                enableCreateButton();

            else {
                disableCreateButton();
            }

            mTypedName = query;
            List<Category> filteredList = filter(query, mAdapter.getList());
            mAdapter.replaceAll(filteredList);
        }
        // Filter has no search results
        else {
            disableCreateButton();
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

public interface OnFragmentInteractionListener {
        void onCategoryFragmentInteraction(Category category, int reason);
    }
}
