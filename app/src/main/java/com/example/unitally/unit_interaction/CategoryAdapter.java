package com.example.unitally.unit_interaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitally.R;
import com.example.unitally.objects.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private LayoutInflater mInflater;
    private List<Category> mCategoryList;

    public CategoryAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mCategoryList = new ArrayList<>();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.category_segment, parent, false);
        return new CategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        if(!mCategoryList.isEmpty()) {
            Category category = mCategoryList.get(position);
            holder.bind(category);
        }
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }

    public void setList(List<Category> categoryList) {
        mCategoryList = categoryList;
        notifyDataSetChanged();
    }

    public void addCategory(Category category) {
        mCategoryList.add(category);
        notifyDataSetChanged();
    }

    public List<Category> getList() {
        return mCategoryList;
    }

/*------------------------------ View Holder ------------------------------*/
    class CategoryViewHolder extends RecyclerView.ViewHolder {

        private TextView aCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            aCategoryName = itemView.findViewById(R.id.category_name_tv);
        }

        protected void bind(Category category) {
            aCategoryName.setText(category.getName());
        }
    }
}
