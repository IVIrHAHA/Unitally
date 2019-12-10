package com.example.unitally.Calculations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitally.R;

import java.util.ArrayList;
import java.util.List;

public class CalculationAdapter extends RecyclerView.Adapter<CalculationAdapter.CalculationViewHolder> {
    private LayoutInflater mInflator;
    private List<ResultsUnitWrapper> mUnitList;

    public CalculationAdapter(Context context) {
        this.mInflator = LayoutInflater.from(context);
        mUnitList = new ArrayList<>();
    }

    @NonNull
    @Override
    public CalculationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mInflator.inflate(R.layout.calc_macro_segment,parent,false);
        return new CalculationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CalculationViewHolder holder, int position) {
        if(mUnitList != null) {
            holder.bind(mUnitList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mUnitList.size();
    }

    public void add(ResultsUnitWrapper unit) {
        mUnitList.add(unit);
        notifyDataSetChanged();
    }

    public void setList(List<ResultsUnitWrapper> unitList) {
        mUnitList = unitList;
        notifyDataSetChanged();
    }

    class CalculationViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle, mCount;
        private ResultsUnitWrapper mUnit;

        CalculationViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.calc_tv_name);
            mCount = itemView.findViewById(R.id.calc_tv_count);
        }

        void bind(ResultsUnitWrapper unit) {
            mUnit = unit;
            mTitle.setText(unit.getName());
            mCount.setText(mUnit.getCSstring());

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mUnitList.remove(mUnit);
                    notifyDataSetChanged();
                    return false;
                }
            });
        }
    }
}
