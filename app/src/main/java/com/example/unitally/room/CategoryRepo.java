package com.example.unitally.room;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.unitally.objects.Category;

import java.util.List;

public class CategoryRepo {
    private CategoryDao mCategoryDao;

    // Get all saved Units
    private LiveData<List<Category>> mCategoryList;
    private LiveData<List<String>> mCategoryNames;

    protected CategoryRepo(Application app) {
        UnitallyDatabase db = UnitallyDatabase.getCategoryDB(app);
        this.mCategoryDao = db.categoryDao();
        this.mCategoryList = mCategoryDao.getAll();
        this.mCategoryNames = mCategoryDao.getNames();
    }

//------------------------------------------------------------------------------------------------//
/*                      Controls for storing/receiving "Saved" Units                              */
//------------------------------------------------------------------------------------------------//
    public LiveData<List<Category>> getCategoryList() {
        return mCategoryList;
    }

    public LiveData<List<String>> getCategoryNames() {
        return mCategoryNames;
    }

    public void saveCategory(Category category) {
        new InsertCategoryAsync(mCategoryDao).execute(category);
    }

    public void deleteCategory(Category category) {
        new DeleteCategoryAsync(mCategoryDao).execute(category);
    }

//------------------------------------------------------------------------------------------------//
/*                                      Async Tasks                                               */
//------------------------------------------------------------------------------------------------//
    // Save single Category
    private static class InsertCategoryAsync extends AsyncTask<Category, Void, Void> {
        private CategoryDao mCategoryDao;

        public InsertCategoryAsync(CategoryDao mCategoryDao) {
            this.mCategoryDao = mCategoryDao;
        }

        @Override
        protected Void doInBackground(Category... categories) {
            mCategoryDao.insert(categories[0]);
            return null;
        }
    }

    // Delete single Category
    private static class DeleteCategoryAsync extends AsyncTask<Category, Void, Void> {
        private CategoryDao mCategoryDao;

        public DeleteCategoryAsync(CategoryDao mCategoryDao) {
            this.mCategoryDao = mCategoryDao;
        }

        @Override
        protected Void doInBackground(Category... categories) {
            mCategoryDao.delete(categories[0]);
            return null;
        }
    }
}
