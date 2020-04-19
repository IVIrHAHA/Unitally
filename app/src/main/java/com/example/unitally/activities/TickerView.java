package com.example.unitally.activities;

import android.content.Context;
import androidx.cardview.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.unitally.MainActivity;
import com.example.unitally.R;
import com.example.unitally.objects.Unit;
import com.example.unitally.tools.UnitallyValues;

public class TickerView extends CardView {

    private Unit gUnit;
    private TextView countDisplay;

    private double mUnitCount;

    public TickerView(Context context) {
        super(context);
    }

    public TickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setUnit(Unit unit) {
        this.gUnit = unit;
        this.mUnitCount = unit.getCount();
        init();
    }

    private void init() {
        TextView nametag        =this.findViewById(R.id.ticker_name);
        countDisplay =this.findViewById(R.id.center_button);

        configureButtons();

        nametag.setText(gUnit.getName());
        updateCount();
    }


    private void configureButtons() {
        ImageButton plusButton  =this.findViewById(R.id.plus_button);
        ImageButton minusButton =this.findViewById(R.id.minus_button);

        //Setting plus button onClick listener
        plusButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mUnitCount++;
                updateCount();
            }
        });

        plusButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mUnitCount +=MainActivity.gIncrement_Count;
                updateCount();
                return true;
            }
        });

        //Setting minus button onClick listener
        minusButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mUnitCount--;
                updateCount();
            }
        });

        minusButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mUnitCount -=MainActivity.gIncrement_Count;
                updateCount();
                return true;
            }
        });
    }

    private void updateCount(){
        gUnit.setCount(mUnitCount);
        countDisplay.setText(Double.toString(mUnitCount));
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
