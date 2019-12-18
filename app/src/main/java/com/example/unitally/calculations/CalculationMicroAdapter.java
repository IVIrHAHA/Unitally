package com.example.unitally.calculations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitally.R;
import com.example.unitally.objects.Unit;

import java.util.ArrayList;
import java.util.List;

public class CalculationMicroAdapter
        extends RecyclerView.Adapter <CalculationMicroAdapter.CalculationVH>
        implements CalculationAdapter{

    private LayoutInflater mInflator;
    private List<Unit> mUnitList;

    public CalculationMicroAdapter(Context context, List<Unit> list) {
        this.mInflator = LayoutInflater.from(context);
        this.mUnitList = new ArrayList<>(list);
    }

    public CalculationMicroAdapter(Context context) {
        this.mInflator = LayoutInflater.from(context);
        this.mUnitList = new ArrayList<>();
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

    public void add(Unit unit) {
        mUnitList.add(unit);
        notifyDataSetChanged();
    }

    public void setList(List<Unit> list) {
        mUnitList.addAll(list);
        notifyDataSetChanged();
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

        public void bind(Unit unit) {
            mName.setText(unit.getName());
            mCount.setText(unit.getCSstring());

            if(unit.getCount() > 0)
                mName.setTextColor(itemView.getResources().getColor(R.color.calc_colors_additions));
            else
                mCount.setTextColor(itemView.getResources().getColor(R.color.calc_colors_negatives));

        }
    }
}
