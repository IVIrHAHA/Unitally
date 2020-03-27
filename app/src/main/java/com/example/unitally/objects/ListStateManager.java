package com.example.unitally.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.unitally.app_modules.unit_tree_module.UnitTreeAdapter;
import com.example.unitally.tools.UnitTreeListManager;
import com.example.unitally.tools.UnitallyValues;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListStateManager {
    public static final int IMMEDIATE = 0;     // Commit save
    public static final int BACKGROUND = 1;    // Apply save

    private static final String SAVED_LIST_TAG = "com.example.unitally.SAVED_LIST_INSTANCE";

    private static Context mContext;
    private static UnitTreeListManager mListManager;
    private static SharedPreferences mPreferences;

    public ListStateManager(Context context){
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mListManager = null;
    }

    public void setList(UnitTreeListManager listManager) {
        mListManager = listManager;
    }

    public boolean save(int state) {
        SharedPreferences.Editor editor = mPreferences.edit();

        if(!mListManager.isEmpty()) {
            Log.i(UnitallyValues.LIFE_SAVE, "SAVING STATE...");

            String json = parseList();
            editor.putString(SAVED_LIST_TAG, json);

            if(state == 1) {
                editor.apply();
                Log.i(UnitallyValues.LIFE_SAVE, "APPLIED SAVED LIST, SIZE "
                        + mListManager.size());
            }
            else {
                editor.commit();
                Log.i(UnitallyValues.LIFE_SAVE, "COMMITTED SAVED LIST, SIZE "
                        + mListManager.size());
            }
            return true;
        }
        // If UnitTreeList is empty then ensure no items are left in SharedPrefrences
        else {
            editor.clear();
            editor.apply();
        }
        return false;
    }

    public static UnitTreeListManager load(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (mPreferences.contains(SAVED_LIST_TAG)) {
            Gson gson = new Gson();

            String json = mPreferences.getString(SAVED_LIST_TAG, "");

            Type type = new TypeToken<ArrayList<Unit>>() {}.getType();

            ArrayList<Unit> list = gson.fromJson(json, type);

            if (list == null) {
                Log.d(UnitallyValues.LIFE_LOAD, "Error occured while loading");
            } else
                Log.d(UnitallyValues.LIFE_LOAD, "Array has elements: " + list.size());

            if (context instanceof UnitTreeAdapter.UnitTreeListener) {
                UnitTreeAdapter.UnitTreeListener listener =
                        (UnitTreeAdapter.UnitTreeListener) mContext;

                mListManager = UnitTreeListManager.getInstance(listener, list);
                return mListManager;
            } else {
                throw new RuntimeException(mContext.toString()
                        + " must implement UnitTreeListener");
            }
        }

        Log.i(UnitallyValues.LIFE_LOAD, "FAILED TO LOAD LIST");
        return null;
    }

    private String parseList() {
        ArrayList<Unit> list = mListManager.getActiveUnits();

        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
