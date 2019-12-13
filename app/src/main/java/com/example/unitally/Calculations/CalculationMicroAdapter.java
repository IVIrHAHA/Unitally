package com.example.unitally.Calculations;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitally.R;

import java.util.ArrayList;
import java.util.List;

public class CalculationMicroAdapter extends RecyclerView.Adapter
        <CalculationMicroAdapter.CalculationVH> {

    private LayoutInflater mInflator;
    private List<ResultsUnitWrapper> mUnitList;

    public CalculationMicroAdapter(Context context, List<ResultsUnitWrapper> list) {
        this.mInflator = LayoutInflater.from(context);
        this.mUnitList = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public CalculationMicroAdapter.CalculationVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mInflator.inflate(R.layout.calc_micro_segment,parent,false);
        return new CalculationMicroAdapter.CalculationVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CalculationMicroAdapter.CalculationVH holder, int position) {
        if(mUnitList != null) {
            holder.bind(mUnitList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mUnitList.size();
    }

    class CalculationVH extends RecyclerView.ViewHolder {

        private TextView mName, mCount;

        public CalculationVH(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.calc_tv_name);
            mCount = itemView.findViewById(R.id.calc_tv_count);
        }

        public void bind(ResultsUnitWrapper unit) {
            mName.setText(unit.getName());
            mCount.setText(unit.getCSstring());

            if(unit.getCount() > 0)
                mName.setTextColor(itemView.getResources().getColor(R.color.calc_colors_additions));
            else
                mCount.setTextColor(itemView.getResources().getColor(R.color.calc_colors_negatives));

        }
    }
}
