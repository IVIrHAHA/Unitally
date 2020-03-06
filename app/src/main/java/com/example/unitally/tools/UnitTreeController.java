package com.example.unitally.tools;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class UnitTreeController extends ItemTouchHelper.Callback {
    private OnBranchSwipeListener mContract;

    public UnitTreeController(OnBranchSwipeListener listener) {
        mContract = listener;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder) {
        int swipe = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

        return makeMovementFlags(0, swipe);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        mContract.OnBranchSwipe(viewHolder.getAdapterPosition(), direction);
    }

    public interface OnBranchSwipeListener {
        void OnBranchSwipe(int position, int direction);
    }
}
