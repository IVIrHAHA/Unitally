package com.example.unitally.RetrieveUnits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.unitally.R;
import com.example.unitally.objects.Unit;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

public class RetrieveUnitAdapter extends RecyclerView.Adapter<RetrieveUnitAdapter.UnitViewHolder> {

// Variables
    private LayoutInflater mInflater;
    private SelectionTracker<String> mSelectionTracker;

//------------------------------------------------------------------------------------------------//
/*                                     Filter List Components                                     */
//------------------------------------------------------------------------------------------------//
    private final Comparator<Unit> ALPHABETICAL_COMPARE = new Comparator<Unit>() {
        @Override
        public int compare(Unit o1, Unit o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    private SortedList.Callback<Unit> mCallback = new SortedList.Callback<Unit>() {
        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position,count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position,count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition,toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position,count);
        }

        @Override
        public int compare(Unit o1, Unit o2) {
            return ALPHABETICAL_COMPARE.compare(o1,o2);
        }

        @Override
        public boolean areContentsTheSame(Unit oldItem, Unit newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(Unit item1, Unit item2) {
            return item1.getName().equals(item2.getName());
        }
    };
    private SortedList<Unit> mList = new SortedList<>(Unit.class, mCallback);

    // List used for synchronization with SelectionTracker
    private List<Unit> mAllUnits;

    RetrieveUnitAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public RetrieveUnitAdapter.UnitViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.segment_retrieve_unit,parent,false);
        return new UnitViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RetrieveUnitAdapter.UnitViewHolder holder, int position) {
        if(mList != null) {
            final Unit unit = mList.get(position);

            boolean isSelected = false;
            if(mSelectionTracker != null) {
                isSelected = mSelectionTracker.isSelected(unit.getName());
            }

            holder.bind(unit,isSelected);
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size():0;
    }

/*------------------------------------------------------------------------------------------------*/
//                                    Setter/Getter Methods                                       //
/*------------------------------------------------------------------------------------------------*/
    void setUnitList(List<Unit> unitList){
        mList.addAll(unitList);

        // Need second list, to synchronize the filtered list with the SelectionTracker list.
        mAllUnits = unitList;
    }

    void setSelectionTracker(SelectionTracker<String> selectionTracker) {
        this.mSelectionTracker = null;
        this.mSelectionTracker = selectionTracker;
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).getID();
    }

    public SortedList<Unit> getList() {
        return mList;
    }

    void replaceAll(List<Unit> newList) {
        mList.beginBatchedUpdates();

        // Remove Units not in the newList
        for(int i = mList.size()-1 ; i>=0;i--){
            Unit testUnit = mList.get(i);

            // Test unit is not in newList, therefore remove
            if(!newList.contains(testUnit)){
                mList.remove(testUnit);
            }
        }
        mList.addAll(newList);
        mList.endBatchedUpdates();
    }

/*------------------------------------------------------------------------------------------------*/
//                                      View Holder                                               //
/*------------------------------------------------------------------------------------------------*/
    class UnitViewHolder extends RecyclerView.ViewHolder {
        private TextView mUnitView;
        private TextView mSymbolView;
        private Unit mUnit;

        UnitViewHolder(@NonNull final View itemView) {
            super(itemView);
            mUnitView = itemView.findViewById(R.id.tv_unitname);
            mSymbolView = itemView.findViewById(R.id.tv_symbol);
        }

        void bind(Unit unit, final boolean isSelected) {
            mUnit = unit;

        // Set View attributes
            mUnitView.setText(unit.getName().toLowerCase());
            mSymbolView.setText(unit.getSymbol());

        // Needed when redrawing viewHolders
            itemView.setActivated(isSelected);
        }

        ItemDetailsLookup.ItemDetails<String> getItemDetails(){
            String id = mUnit.getName();
            int position = mAllUnits.indexOf(mUnit);

            return new UnitItemDetails(position, id);
        }
    }

/*------------------------------------------------------------------------------------------------*/
//                                      Item Details                                              //
/*------------------------------------------------------------------------------------------------*/

    public class UnitItemDetails extends ItemDetailsLookup.ItemDetails<String> {

        private final int mPosition;
        private final String mSelectionKey;

        UnitItemDetails(int position, String key) {
            mPosition = position;
            mSelectionKey = key;
        }

        @Override
        public int getPosition() {
            return mPosition;
        }

        @Nullable
        @Override
        public String getSelectionKey() {
            return mSelectionKey;
        }

        @Override
        public boolean inSelectionHotspot(@NonNull MotionEvent e) {
            return true;
        }
    }
}
