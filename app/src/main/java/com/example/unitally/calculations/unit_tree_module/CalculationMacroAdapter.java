package com.example.unitally.calculations.unit_tree_module;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitally.DragSwipeHelper;
import com.example.unitally.R;
import com.example.unitally.calculations.NextTierCallback;
import com.example.unitally.objects.Unit;
import com.example.unitally.tools.VHCaptureCallback;

import java.util.ArrayList;
import java.util.List;

public class CalculationMacroAdapter
        extends RecyclerView.Adapter<CalculationMacroAdapter.CalculationViewHolder>
        implements CalculationAdapter, DragSwipeHelper.ActionCompletedContract{

    private LayoutInflater mInflater;
    private List<Unit> mViewedList, mMasterListHolder;
    private Context mContext;

    private NextTierCallback mExpansionListener;
    private VHCaptureCallback mCaptureListener;
    private boolean mMasterTier;

    private ViewGroup mParentView;

    public CalculationMacroAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        mViewedList = new ArrayList<>();
        mContext = context;

        mExpansionListener = (NextTierCallback) context;
        mMasterTier = true;
        mMasterListHolder = null;

        mCaptureListener = (VHCaptureCallback) context;
    }

    CalculationMacroAdapter(Context context, List<Unit> masterList) {
        this.mInflater = LayoutInflater.from(context);
        mViewedList = new ArrayList<>();
        mContext = context;

        mExpansionListener = (NextTierCallback) context;
        mMasterTier = false;
        mMasterListHolder = masterList;
    }

    @NonNull
    @Override
    public CalculationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.calc_macro_segment,parent,false);
        mParentView = parent;
        return new CalculationViewHolder(v, mContext);
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

    public void add(Unit unit) {
        mViewedList.add(unit);
        notifyDataSetChanged();
    }

    public void setList(List<Unit> list) {
        mViewedList = list;
        notifyDataSetChanged();
    }

    public void setTier(boolean isMasterTier) {
        mMasterTier = isMasterTier;
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {

    }

    @Override
    public void onViewSwiped(int position) {
        mViewedList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onViewGrabbed(RecyclerView.ViewHolder viewHolder, int position) {
        CalculationViewHolder vh = (CalculationViewHolder) viewHolder;
        vh.collapseTree();
        mCaptureListener.onCapturedViewHolderListener(vh.getUnit(), position);
    }

/*------------------------------------------------------------------------------------------------*/
/*                                      View Holder                                               */
/*------------------------------------------------------------------------------------------------*/
    public class CalculationViewHolder extends RecyclerView.ViewHolder {
        private TextView gTitle, gCount;
        private Unit gUnit;

        private LinearLayout gTreeContainer;
        private CalculationMacroAdapter gChildTreeAdapter;

        CalculationViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            gTitle = itemView.findViewById(R.id.calc_tv_name);
            gCount = itemView.findViewById(R.id.calc_tv_count);

            gChildTreeAdapter = new CalculationMacroAdapter(context, mViewedList);
            RecyclerView rv_micro = itemView.findViewById(R.id.subs_tree_rv);
            rv_micro.setAdapter(gChildTreeAdapter);
            rv_micro.setLayoutManager(new LinearLayoutManager(context));

            gTreeContainer = itemView.findViewById(R.id.subs_tree_container);
            gTreeContainer.setVisibility(View.GONE);
        }

        private void bind(Unit unit) {
            // Setting Head segment details
            gUnit = unit;
            gTitle.setText(unit.getName());
            gCount.setText(gUnit.getCSstring());

            // Substantiating Tree
            if(!gUnit.isLeaf()) {
                gChildTreeAdapter.setList(new ArrayList<>(gUnit.getSubunits()));

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mMasterTier) {
                            expandTree();
                        } else {
                            expandToMaster();
                        }
                    }
                });
            }
        }

        Unit getUnit() {
            return gUnit;
        }

        private void expandTree() {
            if (gTreeContainer.getVisibility() == View.VISIBLE)
                gTreeContainer.setVisibility(View.GONE);

            else
                gTreeContainer.setVisibility(View.VISIBLE);
        }

        public void collapseTree() {
            if (gTreeContainer.getVisibility() == View.VISIBLE)
                gTreeContainer.setVisibility(View.GONE);
        }

        private void expandToMaster() {
            mExpansionListener.OnNextTierReached(mViewedList, mMasterListHolder);
        }
    }
}
