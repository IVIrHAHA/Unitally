package com.example.unitally.calculations;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitally.R;
import com.example.unitally.objects.Unit;
import com.example.unitally.tools.UnitallyValues;

import java.util.ArrayList;
import java.util.List;

public class CalculationMacroAdapter
        extends RecyclerView.Adapter<CalculationMacroAdapter.CalculationViewHolder>
        implements CalculationAdapter{
    private LayoutInflater mInflator;
    private List<Unit> mCalculatedList, mUncalculatedList;
    private Context mContext;

    CalculationMacroAdapter(Context context) {
        this.mInflator = LayoutInflater.from(context);
        mCalculatedList = new ArrayList<>();
        mUncalculatedList = new ArrayList<>();
        mContext = context;
    }

    @NonNull
    @Override
    public CalculationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mInflator.inflate(R.layout.calc_macro_segment,parent,false);
        return new CalculationViewHolder(v, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull CalculationViewHolder holder, int position) {
        if(mCalculatedList != null) {
            holder.bind(mCalculatedList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mCalculatedList.size();
    }

    public void add(Unit unit) {
        mCalculatedList.add(unit);
        notifyDataSetChanged();
    }

    public void setList(List<Unit> list) {
        mCalculatedList = list;
        notifyDataSetChanged();
    }

    public void setUncalculatedList(List<Unit> unitList) {
        mUncalculatedList = unitList;
    }

    public List<Unit> getCalculatedList() {
        return mCalculatedList;
    }

    class CalculationViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle, mCount;
        private Unit mUnit;
        private LinearLayout mMicroContainer;
        private CalculationMicroAdapter mAdapter;
        private List<Unit> mUnitHolderList;

        CalculationViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.calc_tv_name);
            mCount = itemView.findViewById(R.id.calc_tv_count);

            mAdapter = new CalculationMicroAdapter(context);
            RecyclerView rv_micro = itemView.findViewById(R.id.calc_micro_rv);
            rv_micro.setAdapter(mAdapter);
            rv_micro.setLayoutManager(new LinearLayoutManager(context));
        }

        void bind(Unit unit) {
            mUnit = unit;
            mTitle.setText(unit.getName());
            mCount.setText(mUnit.getCSstring());

            mMicroContainer = itemView.findViewById(R.id.calc_micro_results_container);
            mMicroContainer.setVisibility(View.GONE);

            // Only set onClickListener for parents of the list
        // TODO: (BUG FIX) Won't display child Units of child units.
        // case: if "House" has standard window as child, price and
        // time won't appear in micro adapter
            int thisUnit = mUncalculatedList.indexOf(mUnit);

           // Log.d(UnitallyValues.QUICK_CHECK, "Binding: " + unit.getName());
            if(thisUnit >= 0) {
                mUnitHolderList = new ArrayList<>(mUnit.getSubunits());
                mUnitHolderList.remove(mUnit);

                mAdapter.setList(mUnitHolderList);

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(mMicroContainer.getVisibility() == View.VISIBLE)
                            mMicroContainer.setVisibility(View.GONE);

                        else
                            mMicroContainer.setVisibility(View.VISIBLE);

                        return false;
                    }
                });
            }
        }

    }
}
