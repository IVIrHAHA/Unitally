/**
 * CategoryOrganizer will intake a list of  Units and generate a Categorized *list (See details below).
 *
 * The list generated will be a *Unit List for use in the CalculationAsyncTask.
 */
package com.example.unitally.tools;

import android.os.AsyncTask;
import android.util.Log;

import com.example.unitally.objects.Category;
import com.example.unitally.objects.Unit;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class CategoryOrganizer {
    private static List<Unit> mCategoryList;
    private static final String MISC_CATEGORY_NAME = "Misc";
    private static final String CATEGORIZING_ERROR = "Categorizing Error";

    public CategoryOrganizer() {
        mCategoryList = new ArrayList<>();
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
        Hashtable<Category, Unit> mCategoryTable;
        List<Unit> mHeadList;

        CategoryAsyncTask() {
            mCategoryTable = new Hashtable<>();
            mHeadList = new ArrayList<>();
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

                        Unit headUnit = mCategoryTable.get(category);

                        // Compiler reassurance
                        //
                        if(headUnit != null) {
                            int category_count = headUnit.getCount() + analyzed_unit.getCount();
                            headUnit.setCount(category_count);

                            headUnit.addSubunit(analyzed_unit, analyzed_unit.getCount());

                            // TODO: Check if passes by pointer
                            mCategoryTable.put(category, headUnit);
                        } else {
                            Exception e = new Exception("PULLED NULL WHILE CATEGORIZING");
                            Log.e(UnitallyValues.BUGS, CATEGORIZING_ERROR, e);
                        }
                    }
                    // Add category into Table
                    else {
                        // Generate a new head Unit
                        Unit newHead = new Unit(category.getName());

                        // Add amount of units
                        newHead.setCount(analyzed_unit.getCount());
                        mCategoryTable.put(category, newHead);
                    }
                }

                // If analyzed_unit is not categorized then add under "Miscellaneous"
                else {
                    Category miscCategory = new Category(MISC_CATEGORY_NAME);
                    Unit miscCategoryHead;

                    if(mCategoryTable.contains(miscCategory)) {
                        miscCategoryHead = mCategoryTable.get(miscCategory);

                        // Compiler Reassurance
                        if (miscCategoryHead != null) {
                            miscCategoryHead.addSubunit(analyzed_unit, analyzed_unit.getCount());
                            mCategoryTable.put(miscCategory, miscCategoryHead);
                        } else {
                            Exception e = new Exception("LOST MISCELLANEOUS CATEGORY");
                            Log.e(UnitallyValues.BUGS,CATEGORIZING_ERROR, e);
                        }
                    }
                    else {
                        miscCategoryHead = new Unit(MISC_CATEGORY_NAME);
                        mCategoryTable.put(miscCategory, miscCategoryHead);
                    }
                }
            }

            mCategoryList.addAll(mCategoryTable.values());

            for(Unit unit: mCategoryList) {
                Log.d(UnitallyValues.QUICK_CHECK, unit.getName() + " contains following:");

                List<Unit> subunits = unit.getSubunits();
                for(Unit subunit:subunits) {
                    Log.d(UnitallyValues.QUICK_CHECK, "\t" + subunit.getName());
                }

            }
            return null;
        }
    }
}
