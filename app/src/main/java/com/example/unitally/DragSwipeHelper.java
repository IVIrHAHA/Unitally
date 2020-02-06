package com.example.unitally;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitally.calculations.numerical_module.CalculationMacroAdapter;
import com.example.unitally.tools.UnitallyValues;
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
        else if(mContract instanceof CalculationMacroAdapter) {
            if(viewHolder instanceof CalculationMacroAdapter.CalculationViewHolder) {

                if(!mGrabbed) {
                    mGrabbed = true;
                    mContract.onViewGrabbed(viewHolder, viewHolder.getAdapterPosition());
                    return makeMovementFlags(dragFlags, 0);
                }
                else {
                    mGrabbed = false;
                    return makeMovementFlags(dragFlags, 0);
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
        mContract.onViewSwiped(viewHolder.getAdapterPosition());
    }



    public interface ActionCompletedContract {
        void onViewMoved(int oldPosition, int newPosition);

        void onViewSwiped(int position);

        void onViewGrabbed(RecyclerView.ViewHolder viewHolder, int position);
    }
}
