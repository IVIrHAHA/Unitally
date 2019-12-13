package com.example.unitally.Calculations;

import android.content.Context;
import android.service.autofill.FillEventHistory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitally.R;

import java.util.ArrayList;
import java.util.List;

public class CalculationAdapter extends RecyclerView.Adapter<CalculationAdapter.CalculationViewHolder> {
    private LayoutInflater mInflator;
    private List<ResultsUnitWrapper> mUnitList;
    private Context mContext;

    public CalculationAdapter(Context context) {
        this.mInflator = LayoutInflater.from(context);
        mUnitList = new ArrayList<>();
        mContext = context;
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
            holder.bind(mUnitList.get(position), mContext);
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
        private LinearLayout mMicroContainer;

        CalculationViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.calc_tv_name);
            mCount = itemView.findViewById(R.id.calc_tv_count);
        }

        void bind(ResultsUnitWrapper unit, Context context) {
            mUnit = unit;
            mTitle.setText(unit.getName());
            mCount.setText(mUnit.getCSstring());

            mMicroContainer = itemView.findViewById(R.id.calc_micro_results_container);
            mMicroContainer.setVisibility(View.INVISIBLE);

            RecyclerView rv_micro = itemView.findViewById(R.id.calc_micro_rv);
            CalculationMicroAdapter adapter = new CalculationMicroAdapter(context, mUnit.getSubunits());
            rv_micro.setAdapter(adapter);
            rv_micro.setLayoutManager(new LinearLayoutManager(context));

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mMicroContainer.setVisibility(View.VISIBLE);
                    return false;
                }
            });
        }
    }
}
