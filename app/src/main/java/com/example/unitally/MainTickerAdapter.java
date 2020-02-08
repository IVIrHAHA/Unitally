package com.example.unitally;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.unitally.objects.Unit;
import com.example.unitally.activities.TickerView;

import java.util.LinkedList;

public class MainTickerAdapter extends RecyclerView.Adapter<MainTickerAdapter.TickerViewHolder>
                            implements DragSwipeHelper.ActionCompletedContract{

    private LinkedList<Unit> mUnitList;
    private LayoutInflater mInflater;
    private Context context;

    public MainTickerAdapter(Context context, LinkedList<Unit> UnitList) {
        mInflater = LayoutInflater.from(context);
        this.context=context;
        this.mUnitList = UnitList;
    }

    public void clear() {
        mUnitList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MainTickerAdapter.TickerViewHolder onCreateViewHolder
            (@NonNull ViewGroup viewGroup, int i) {

        TickerView mTickerView = (TickerView) mInflater
                .inflate(R.layout.ticker_view,viewGroup,false);

        return new TickerViewHolder(mTickerView,this);
    }

    @Override
    public void onBindViewHolder
            (@NonNull MainTickerAdapter.TickerViewHolder tickerViewHolder, final int i) {

        Unit unit = mUnitList.get(i);
        tickerViewHolder.tickerView.setUnit(unit);
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        Unit tempUnit = mUnitList.get(oldPosition);
        mUnitList.remove(oldPosition);
        mUnitList.add(newPosition, tempUnit);
        notifyItemMoved(oldPosition, newPosition);
    }

    @Override
    public void onViewSwiped(int position) {
        mUnitList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onViewGrabbed(RecyclerView.ViewHolder viewHolder, int position) {
        // Do nothing
    }

    @Override
    public int getItemCount() {
        return mUnitList.size();
    }

    class TickerViewHolder extends RecyclerView.ViewHolder {
        final TickerView tickerView;
        final MainTickerAdapter mAdapter;
        final TextView countTV;

        TickerViewHolder
                (@NonNull View itemView, MainTickerAdapter adapter) {
            super(itemView);

            this.tickerView = (TickerView) itemView;
            this.mAdapter   = adapter;
            countTV = tickerView.findViewById(R.id.center_button);
        }
    }
}
