package com.example.unitally.unit_interaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.example.unitally.R;
import com.example.unitally.objects.Category;

import java.util.Comparator;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private LayoutInflater mInflater;
    private List<Category> mCategoryList;

    // Selection Variables
    private SelectionTracker<String> mSelectionTracker;
    private final Comparator<Category> ALPHABETICAL_COMPARE = new Comparator<Category>() {
        @Override
        public int compare(Category category, Category t1) {
            return category.getName().compareTo(t1.getName());
        }
    };

    private SortedList.Callback<Category> mCallback = new SortedList.Callback<Category>() {
        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position,count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position,count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition,toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position,count);
        }

        @Override
        public int compare(Category o1, Category o2) {
            return ALPHABETICAL_COMPARE.compare(o1,o2);
        }

        @Override
        public boolean areContentsTheSame(Category oldItem, Category newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(Category item1, Category item2) {
            return item1.getName().equals(item2.getName());
        }
    };

    private SortedList<Category> mSortedList = new SortedList<>(Category.class, mCallback);

// Constructor
    public CategoryAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.category_segment, parent, false);
        return new CategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        if(mSortedList != null) {
            Category category = mSortedList.get(position);

            boolean isSelected = false;
            if(mSelectionTracker != null) {
                isSelected = mSelectionTracker.isSelected(category.getName());
            }

            holder.bind(category, isSelected);
        }
    }

    @Override
    public int getItemCount() {
        return mSortedList != null ? mSortedList.size():0;
    }

    @Override
    public long getItemId(int position) {
        return mSortedList.get(position).getID();
    }

    public void setList(List<Category> categoryList) {
        mSortedList.addAll(categoryList);
        mCategoryList = categoryList;
        notifyDataSetChanged();
    }

    public void setSelectorTracker(SelectionTracker<String> selectionTracker) {
        this.mSelectionTracker = null;
        this.mSelectionTracker = selectionTracker;
    }

    public void addCategory(Category category) {
        mCategoryList.add(category);
        mSortedList.add(category);
        notifyDataSetChanged();
    }

    public void replaceAll(List<Category> newList) {
        mSortedList.beginBatchedUpdates();

        for(int i = mSortedList.size()-1; i>=0; i--) {
            Category testCat = mSortedList.get(i);

            if(!newList.contains(testCat)) {
                mSortedList.remove(testCat);
            }
        }

        mSortedList.addAll(newList);
        mSortedList.endBatchedUpdates();
    }

    /**
     * Returns the Categories currently in the adapter.
     *
     * @return List containing categories
     */
    public List<Category> getList() {
        return mCategoryList;
    }

    /**
     * Used for Alphabetical sorting of Categories
     *
     * @return An alphabetically sorted list of Categories
     */
    public SortedList<Category> getSortedList() {
        return mSortedList;
    }

/*------------------------------ View Holder ------------------------------*/
    class CategoryViewHolder extends RecyclerView.ViewHolder {

        private TextView aCategoryName;
        private Category aCategory;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            aCategoryName = itemView.findViewById(R.id.category_name_tv);
        }

        void bind(Category category, boolean isSelected) {
            aCategory = category;
            aCategoryName.setText(category.getName());

            itemView.setActivated(isSelected);
        }

        ItemDetailsLookup.ItemDetails<String> getItemDetails() {
            String id = aCategory.getName();
            int position = mCategoryList.indexOf(aCategory);

            return new CategoryItemDetails(position, id);
        }
    }

/*------------------------------------------------------------------------------------------------*/
//                                      Item Details                                              //
/*------------------------------------------------------------------------------------------------*/

    public class CategoryItemDetails extends ItemDetailsLookup.ItemDetails<String> {

        private final int aPosition;
        private final String aSelectionKey;

        CategoryItemDetails(int position, String key) {
            aPosition = position;
            aSelectionKey = key;
        }

        @Override
        public int getPosition() {
            return aPosition;
        }

        @Nullable
        @Override
        public String getSelectionKey() {
            return aSelectionKey;
        }

        @Override
        public boolean inSelectionHotspot(@NonNull MotionEvent e) {
            return true;
        }
    }
}
