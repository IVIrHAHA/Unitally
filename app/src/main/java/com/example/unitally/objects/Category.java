/**
 * Class is a String wrapper to manage a Categories'
 * attributes
 */
package com.example.unitally.objects;

import android.os.strictmode.UntaggedSocketViolation;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.unitally.tools.UnitallyValues;

import java.io.Serializable;

public class Category implements Serializable {
    private final String mCategoryName;

    public Category(@NonNull String name) {
        mCategoryName = name;
    }

    public Category() {
        mCategoryName = UnitallyValues.CATEGORY_DEFAULT_NAME;
    }

    // Get Category name
    public String getName() {
        return mCategoryName;
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
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }
}
