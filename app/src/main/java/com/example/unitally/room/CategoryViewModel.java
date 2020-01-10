package com.example.unitally.room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.unitally.objects.Category;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private CategoryRepo mRepo;
    private LiveData<List<Category>> mCategoryList;
    private LiveData<List<String>> mCategoryNames;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        this.mRepo = new CategoryRepo(application);
        this.mCategoryList = mRepo.getCategoryList();
        this.mCategoryNames = mRepo.getCategoryNames();
    }

    public LiveData<List<Category>> getAllCategories() {
        return mCategoryList;
    }

    public LiveData<List<String>> getCategoryNames() {
        return mCategoryNames;
    }

    public void saveCategory(Category category) {
        mRepo.saveCategory(category);
    }

    public void deleteCategory(Category category) {
        mRepo.deleteCategory(category);
    }
}
