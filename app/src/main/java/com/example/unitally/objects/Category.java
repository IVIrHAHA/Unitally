/**
 * Class is a String wrapper to manage a Categories'
 * attributes
 */
package com.example.unitally.objects;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.unitally.tools.UnitallyValues;

import java.io.Serializable;

@Entity(tableName = "category_table")
public class Category implements Serializable {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    public String mCategoryName;

    @ColumnInfo(name = "id")
    public int mCatID;

    public Category(@NonNull String name) {
        mCategoryName = name;
        mCatID = name.hashCode();
    }

    public Category() {
        mCategoryName = UnitallyValues.CATEGORY_DEFAULT_NAME;
        mCatID = mCategoryName.hashCode();
    }

    // Get Category name
    public String getName() {
        return mCategoryName;
    }

    public int getID() {
        return mCatID;
    }

    @Override
    public int hashCode() {
        return mCategoryName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        // Check if same class
        try {
            if (obj.getClass() == getClass()) {
                // Check if name matches
                String thisName = mCategoryName.toLowerCase();
                String objName = ((Category) obj).getName().toLowerCase();

                if (thisName.equals(objName)) {
                    return true;
                }
            }
            else {
                String testName = (String) obj;
                if(testName.equalsIgnoreCase(mCategoryName)) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
