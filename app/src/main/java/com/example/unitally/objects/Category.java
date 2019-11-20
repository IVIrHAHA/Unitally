/**
 * Class is a String wrapper to manage a Categories'
 * attributes
 */
package com.example.unitally.objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.Serializable;

public class Category implements Serializable {
    private final String mCategoryName;
    private boolean mMaskName;

    public Category(@NonNull String name) {
        mCategoryName = name;
        mMaskName = false;
    }

    // Determine whether Category name should mask Unit name
    public void setMask(boolean mask) {
        mMaskName = mask;
    }

    // Get Category name
    public String getName() {
        return mCategoryName;
    }

    // Return whether mask unit name or not
    public boolean masked() {
        return mMaskName;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        // Check if same class
        try {
            if (obj.getClass() == getClass()) {
                // Check if name matches
                if (mCategoryName.equals(((Category)obj).getName())) {
                    return true;
                }
            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }
}
