package com.example.unitally;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitally.app_modules.unit_tree_module.UnitTreeAdapter;
import com.example.unitally.unit_interaction.UnitInterPlayAdapter;

public class DragSwipeHelper extends ItemTouchHelper.Callback {

    private ActionCompletedContract mContract;
    private boolean mGrabbed;

    public DragSwipeHelper(ActionCompletedContract contract) {
        this.mContract = contract;
        mGrabbed = false;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

        if(mContract instanceof UnitInterPlayAdapter) {
            if (((UnitInterPlayAdapter) mContract).isEditable())
                return makeMovementFlags(dragFlags, swipeFlags);

            else
                return 0;
        }
        else if(mContract instanceof UnitTreeAdapter) {
            if(viewHolder instanceof UnitTreeAdapter.CalculationViewHolder) {

                if(!mGrabbed) {
                    mGrabbed = true;
                    return makeMovementFlags(dragFlags, swipeFlags);
                }
                else {
                    mGrabbed = false;
                    return makeMovementFlags(dragFlags, swipeFlags);
                }
            }
            return 0;
        }
        else
            return makeMovementFlags(dragFlags, swipeFlags);

    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
        mContract.onViewMoved(dragged.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        mContract.onViewSwiped(viewHolder.getAdapterPosition(), direction);
    }

    public interface ActionCompletedContract {
        void onViewMoved(int oldPosition, int newPosition);

        void onViewSwiped(int position, int direction);
    }
}
