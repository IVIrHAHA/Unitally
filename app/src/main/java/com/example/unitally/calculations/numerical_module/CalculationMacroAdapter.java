package com.example.unitally.calculations.numerical_module;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitally.R;
import com.example.unitally.calculations.NextTierCallback;
import com.example.unitally.objects.Unit;

import java.util.ArrayList;
import java.util.List;

public class CalculationMacroAdapter
        extends RecyclerView.Adapter<CalculationMacroAdapter.CalculationViewHolder>
        implements CalculationAdapter{
    private LayoutInflater mInflator;
    private List<Unit> mCalculatedList;
    private Context mContext;

    private NextTierCallback mExpantionListener;

    public CalculationMacroAdapter(Context context) {
        this.mInflator = LayoutInflater.from(context);
        mCalculatedList = new ArrayList<>();
        mContext = context;

        mExpantionListener = (NextTierCallback) context;
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

    public List<Unit> getCalculatedList() {
        return mCalculatedList;
    }

    class CalculationViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle, mCount;
        private Unit mUnit;
        private LinearLayout mTreeContainer;
        private CalculationMacroAdapter mAdapter;

        CalculationViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.calc_tv_name);
            mCount = itemView.findViewById(R.id.calc_tv_count);

            mAdapter = new CalculationMacroAdapter(context);
            RecyclerView rv_micro = itemView.findViewById(R.id.subs_tree_rv);
            rv_micro.setAdapter(mAdapter);
            rv_micro.setLayoutManager(new LinearLayoutManager(context));
        }

        void bind(Unit unit) {
            // Setting Head segment details
            mUnit = unit;
            mTitle.setText(unit.getName());
            mCount.setText(mUnit.getCSstring());

            // Getting Tree container (Used to hide and expand) // TODO: Possibly floating around binding incorrectly
            mTreeContainer = itemView.findViewById(R.id.subs_tree_container);
            mTreeContainer.setVisibility(View.GONE);

            if (mUnit.getName().equalsIgnoreCase(mTitle.getText().toString())) {
                // Substantiating Tree
                if(mAdapter.getItemCount() == 0) {
                    mAdapter.setList(new ArrayList<>(mUnit.getSubunits()));
                }

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mTreeContainer.getVisibility() == View.VISIBLE) {
                            mTreeContainer.setVisibility(View.GONE);
                        }

                        else {
                            mTreeContainer.setVisibility(View.VISIBLE);
                            mExpantionListener.OnNextTierReached(mUnit.getSubunits(), mCalculatedList);
                        }
                        return false;
                    }
                });
            }
        }

    }
}
