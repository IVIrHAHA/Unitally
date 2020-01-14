package com.example.unitally.unit_interaction;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
    public static final int CHOOSE_CATEGORY = 1;
    public static final int EDIT_CATEGORY = 2;
    private static final String INVALID_CATEGORY_NAME = "Please type a valid name";

    // Specialty Vars
    private CategoryViewModel mViewModel;
    private OnFragmentInteractionListener mListener;
    private CategoryAdapter mAdapter;
    private RecyclerView mRecyclerView;

    // Global Vars
    private int mIntention;
    private String mTypedName;
    private List<Category> mCategoryList;

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
                loadCategories(categories);
            }
        });

        switch (mIntention) {
            case CHOOSE_CATEGORY:
                setChooseCategoryViews(v);
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
            mListener.onCategoryFragmentInteraction(null);
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
    private void setChooseCategoryViews(View view) {
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

    private void loadCategories(List<Category> categoryList) {
        mCategoryList = categoryList;
        mAdapter.setList(categoryList);
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

    // Query SearchView methods
    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Filtering list
        if(query.length() >= UnitallyValues.MIN_QUERY_LENGTH) {

            Category temp = new Category(query);
            if(!mCategoryList.contains(temp))
                enableCreateButton();

            else {
                disableCreateButton();
            }
            mTypedName = query;
            List<Category> filteredList = filter(query, mAdapter.getList());
            mAdapter.setList(filteredList);
        }
        // Filter has no search results
        else {
            disableCreateButton();
            mAdapter.setList(mCategoryList);
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

    public interface OnFragmentInteractionListener {
        void onCategoryFragmentInteraction(Category category);
    }
}
