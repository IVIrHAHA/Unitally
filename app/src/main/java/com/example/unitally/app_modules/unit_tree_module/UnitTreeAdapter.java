package com.example.unitally.app_modules.unit_tree_module;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.example.unitally.R;
import com.example.unitally.objects.UnitWrapper;
import com.example.unitally.tools.UnitallyValues;

import java.util.List;

public class UnitTreeAdapter
        extends RecyclerView.Adapter<UnitTreeAdapter.CalculationViewHolder> {

    private OnItemToBeStaged mItemSelectedListener;
    private LayoutInflater mInflater;
    private SortedList.Callback<UnitWrapper> mCallback = new SortedList.Callback<UnitWrapper>() {

        @Override
        public int compare(UnitWrapper o1, UnitWrapper o2) {
            return 0;
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(UnitWrapper oldItem, UnitWrapper newItem) {
            return false;
        }

        @Override
        public boolean areItemsTheSame(UnitWrapper item1, UnitWrapper item2) {
            return item1.equals(item2);
        }

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }
    };

    private SortedList<UnitWrapper> mViewedList = new SortedList<>(UnitWrapper.class, mCallback);

    public UnitTreeAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        setHasStableIds(true);
        mItemSelectedListener = null;
    }

    @NonNull
    @Override
    public CalculationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.calc_micro_segment,parent,false);
        return new CalculationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CalculationViewHolder holder, int position) {
        if(mViewedList != null) {
            holder.bind(mViewedList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mViewedList.size();
    }

    @Override
    public long getItemId(int position) {
        return mViewedList.get(position).getId();
    }

    /**
     * Add to and notify adapter immediately
     *
     * @param unit user-added unit
     */
    public void add(UnitWrapper unit) {
        mViewedList.add(unit);
    }

    /**
     * Add as batch
     * @param list Auto-Added section of the list
     */
    public void updateBatch(List<UnitWrapper> list) {
        mViewedList.addAll(list);
    }

    /**
     * Add all units to the list at once, without discriminating order.
     *
     * @param list Entire branch list
     */
    public void setList(List<UnitWrapper> list) {
        mViewedList.beginBatchedUpdates();
        mViewedList.replaceAll(list);
        mViewedList.endBatchedUpdates();
    }

    /**
     * Much like addAll, except may exclude some units. Used by the filtering process.
     *
     * @param list List of units
     */
    public void replaceAll(List<UnitWrapper> list) {
        // replace list;
        // notifyDataSetChanged();
    }

    public void setItemSelectionListener(OnItemToBeStaged listener) {
        mItemSelectedListener = listener;
    }

    public boolean removeItem(UnitWrapper unit) {
        return mViewedList.remove(unit);

    }

/*------------------------------------------------------------------------------------------------*/
/*                                      View Holder                                               */
/*------------------------------------------------------------------------------------------------*/
    public class CalculationViewHolder extends RecyclerView.ViewHolder {
        UnitWrapper mUnitParcel;
        TextView name_tv, symbol_tv;

        CalculationViewHolder(@NonNull View itemView) {
            super(itemView);
            name_tv = itemView.findViewById(R.id.calc_tv_name);
            symbol_tv = itemView.findViewById(R.id.calc_tv_count);
            mUnitParcel = null;
        }

        private void bind(UnitWrapper unit) {
            mUnitParcel = unit;
            int label = unit.getLabel();

            if(label == UnitWrapper.AUTO_ADDED_LABEL) {
                setAAView();
            }
            else if(label == UnitWrapper.MF_USER_ADDED_LABEL) {
                setMFView();
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mItemSelectedListener.fromAdapterToStage(mUnitParcel);
                        return true;
                    }
                });
            }
            else if(label == UnitWrapper.USER_ADDED_LABEL) {

            }
        }

        private void setAAView() {
            name_tv.setText((mUnitParcel.peek()).getName());
            symbol_tv.setText((mUnitParcel.peek()).getCSstring());

            name_tv.setTextColor(UnitallyValues.COLORS[5]);
        }

        private void setMFView() {
            name_tv.setText((mUnitParcel.peek()).getName());
            symbol_tv.setText((mUnitParcel.peek()).getCSstring());

            name_tv.setTextColor(UnitallyValues.COLORS[0]);
        }

        private void setUAView() {

        }
    }

    public interface OnItemToBeStaged {
        void fromAdapterToStage(UnitWrapper unit);
    }
}
