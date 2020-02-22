package com.example.unitally.app_modules.unit_tree_module;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.example.unitally.R;
import com.example.unitally.objects.Unit;
import com.example.unitally.objects.UnitWrapper;

import java.util.List;

public class UnitTreeAdapter
        extends RecyclerView.Adapter<UnitTreeAdapter.CalculationViewHolder> {

    private LayoutInflater mInflater;
    private List<UnitWrapper> mViewedList;

    public UnitTreeAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CalculationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.calc_macro_segment,parent,false);
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
        // Create an id for Unit
        return super.getItemId(position);
    }

    public void setList(List<UnitWrapper> list) {
        mViewedList = list;
        notifyDataSetChanged();
    }

    public void replaceAll(List<UnitWrapper> list) {
        // replace list;
        // notifyDataSetChanged();
    }

/*------------------------------------------------------------------------------------------------*/
/*                                      View Holder                                               */
/*------------------------------------------------------------------------------------------------*/
    public class CalculationViewHolder extends RecyclerView.ViewHolder {

        CalculationViewHolder(@NonNull View itemView) {
            super(itemView);

        }

        private void bind(UnitWrapper unit) {

        }
    }
}
