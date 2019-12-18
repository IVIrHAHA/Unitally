package com.example.unitally.unit_retrieval;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.view.ActionMode;

import androidx.recyclerview.selection.SelectionTracker;

public class RetrieveUnitActionMode implements androidx.appcompat.view.ActionMode.Callback {
    private Context mContext;
    private SelectionTracker mSelectionTracker;

    RetrieveUnitActionMode(Context context, SelectionTracker selectionTracker) {
        mContext = context;
        mSelectionTracker = selectionTracker;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mSelectionTracker.clearSelection();
    }
}
