package com.example.unitally.unit_interaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitally.tools.DragSwipeHelper;
import com.example.unitally.R;
import com.example.unitally.objects.Unit;

import java.util.ArrayList;
import java.util.List;

public class UnitInterPlayAdapter
        extends RecyclerView.Adapter<UnitInterPlayAdapter.UnitInterPlayViewHolder>
        implements DragSwipeHelper.ActionCompletedContract{

    private LayoutInflater mInflater;
    private List<Unit> mSubUnits;
    private Context mContext;
    private FrameLayout mDisclaimer;
    Boolean mDisplayMode;

    public UnitInterPlayAdapter(Context context, Boolean displayMode, FrameLayout disclaimer) {
        this.mInflater = LayoutInflater.from(context);
        this.mDisplayMode = displayMode;
        this.mContext = context;
        this.mSubUnits = new ArrayList<>();
        this.mDisclaimer = disclaimer;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public UnitInterPlayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.segment_subunits, parent, false);
        return new UnitInterPlayViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UnitInterPlayViewHolder holder, int position) {
       if(!mSubUnits.isEmpty()) {
           Unit unit = mSubUnits.get(position);
           holder.bind(unit);
           mDisclaimer.setVisibility(View.GONE);
       }
       else {
           mDisclaimer.setVisibility(View.VISIBLE);
       }
    }

    @Override
    public long getItemId(int position) {
        return mSubUnits.get(position).getID();
    }

    @Override
    public int getItemCount() {
        return mSubUnits.size();
    }

    void setList(List<Unit> unitList) {
        mSubUnits.clear();
        mSubUnits.addAll(unitList);
        notifyDataSetChanged();
    }

    void add(Unit unit) {
        mSubUnits.add(unit);
        notifyDataSetChanged();
    }

    void remove(Unit unit) {
        mSubUnits.remove(unit);
        notifyDataSetChanged();
    }

    boolean modify(Unit unit) {
        int index = mSubUnits.indexOf(unit);

        if(index >= 0) {
            mSubUnits.set(index, unit);
            notifyItemChanged(index);
            return true;
        }

        return false;
    }

    void setEditable(boolean editable) {
        mDisplayMode = !editable;
    }

    /**
     * Determine if adapter is in Display mode or Edit mode.
     *
     * @return True it is editable, False if it is not.
     */
    public boolean isEditable() {
        return !mDisplayMode;
    }

    List<Unit> getList() {
        return mSubUnits;
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        Unit tempUnit = mSubUnits.get(oldPosition);
        mSubUnits.remove(oldPosition);
        mSubUnits.add(newPosition, tempUnit);
        notifyItemMoved(oldPosition, newPosition);
    }

    @Override
    public void onViewSwiped(int position, int direction) {
        mSubUnits.remove(position);
        notifyItemRemoved(position);
    }

    class UnitInterPlayViewHolder extends RecyclerView.ViewHolder {
        private Unit mUnit;
        private TextView mUnitName;
        private TextView mUnitSymbol;

        UnitInterPlayViewHolder(@NonNull View itemView) {
            super(itemView);

            mUnitName = itemView.findViewById(R.id.tv_subunitname);
            mUnitSymbol = itemView.findViewById(R.id.tv_subunitsymbol);
        }

        void bind(Unit unit) {
            String symbolLine;
            mUnit = unit;

            // add symbol before count/worth
            if(unit.isSymbolBefore()) {
                symbolLine = unit.getSymbol() + " " + unit.getWorth();
            }
            else {
                symbolLine = unit.getWorth() + " " + unit.getSymbol();
            }

            mUnitName.setText(unit.getName());
            mUnitSymbol.setText(symbolLine);

            // Set subunit edit behaviour
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                if(!mDisplayMode) {
                    if(mContext instanceof UnitInterPlayActivity) {
                        SubunitEditFragment fragment = SubunitEditFragment
                                .newInstance(mUnit, SubunitEditFragment.EDIT_SUBUNITS);
                        FragmentManager manager = ((UnitInterPlayActivity) mContext).getSupportFragmentManager();
                        UnitInterPlayActivity.mFragmentTransaction = manager.beginTransaction();
                        UnitInterPlayActivity.mFragmentTransaction.addToBackStack(null);
                        UnitInterPlayActivity.mFragmentTransaction.add(R.id.ip_container, fragment,"SUBEDIT_FRAGMENT").commit();
                    }
                }
                return true;
                }
            });
        }
    }
}
