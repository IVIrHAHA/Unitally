/**
 * CategoryOrganizer will intake a list of  Units and generate a Categorized *list (See details below).
 *
 * The list generated will be a *Unit List for use in the CalculationAsyncTask.
 */
package com.example.unitally.tools;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.unitally.objects.Category;
import com.example.unitally.objects.Unit;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class CategoryOrganizer {
    private static List<Unit> mCategoryList;
    private static final String CATEGORIZING_ERROR = "Categorizing Error";

    public CategoryOrganizer() {
        mCategoryList = null;
    }
    /**
     * Receive a Unit list and reorganize according to category.
     * A Unit list will still be returned, however the head unit will
     * then consist of a "fictional" Category Unit.
     *
     * @param unitList Unit list with Units as the head units
     * @return Unit list with a Category Unit as the head unit
     */
    public List<Unit> generate(List<Unit> unitList) {
        new CategoryAsyncTask().execute(unitList);
        return mCategoryList;
    }

    private static class CategoryAsyncTask extends AsyncTask<List<Unit>, Void, Void>{
        private static Hashtable<Category, Unit> mCategoryTable;
        private static Category mMiscCategory;

        @Override
        protected void onPreExecute() {
            mCategoryTable = new Hashtable<>();
            mMiscCategory = new Category(UnitallyValues.CATEGORY_DEFAULT_NAME);
            mCategoryList = new ArrayList<>();
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Unit>... lists) {
            List<Unit> aList = lists[0];

            // Organize Units into Categories
            for(Unit analyzed_unit : aList) {
                Category category = analyzed_unit.getCategory();

                // If analyzed_unit is categorized then place accordingly in table
                if(category != null) {
                    // If category is already found in table then, then add to exiting category-unit
                    if (mCategoryTable.containsKey(category)) {
                        addToTable(category, analyzed_unit);
                    }
                    // Add category into Table
                    else {
                        // Generate a new head Unit
                        generateCategoryUnitHead(category, analyzed_unit);
                    }
                }

                // If analyzed_unit is not categorized then add under "Miscellaneous"
                else {
                    if(mCategoryTable.containsKey(mMiscCategory)) {
                        addToTable(mMiscCategory, analyzed_unit);
                    }
                    else {
                        generateCategoryUnitHead(mMiscCategory, analyzed_unit);
                    }
                }
            }

            mCategoryList.addAll(mCategoryTable.values());
            return null;
        }

        private static void generateCategoryUnitHead(Category category, Unit firstSubunit) {
            Unit category_unit = new Unit(category.getName());

            category_unit.setCount(firstSubunit.getCount());
            category_unit.addSubunit(firstSubunit);

            mCategoryTable.put(category, category_unit);
        }

        private static void addToTable(@NonNull Category head_category, Unit unit) {
            Unit category_unit = mCategoryTable.get(head_category);

            // Compiler reassurance
            // add unit to head category_unit
            if(category_unit != null) {
                category_unit.addSubunit(unit);

                int newCount = category_unit.getCount() + unit.getCount();
                category_unit.setCount(newCount);

                mCategoryTable.put(head_category, category_unit);

            } else {
              Exception e = new Exception("PULLED NULL CATEGORY WHILE CATEGORIZING");
              Log.e(UnitallyValues.BUGS, CATEGORIZING_ERROR, e);
            }
        }
    }
}
