package com.example.unitally.activities;

import android.content.Context;
import androidx.cardview.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.unitally.MainActivity;
import com.example.unitally.R;
import com.example.unitally.objects.Unit;

public class TickerView extends CardView {

    private Unit unit;
    private Button countDisplayButton;

    private int count;
    private int longPress_count;

    public TickerView(Context context) {
        super(context);
        longPress_count = MainActivity.gIncrement_Count;
    }

    public TickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        longPress_count = MainActivity.gIncrement_Count;
    }

    public TickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        longPress_count = MainActivity.gIncrement_Count;
    }

    public void setUnit(Unit unit) {
        this.unit=unit;
        this.count=unit.getCount();
        init();
    }

    private void init() {
        TextView nametag        =this.findViewById(R.id.ticker_name);
        countDisplayButton      =this.findViewById(R.id.center_button);
        ImageButton plusButton  =this.findViewById(R.id.plus_button);
        ImageButton minusButton =this.findViewById(R.id.minus_button);

        nametag.setText(unit.getName());
        updateCount();

        //Setting plus button onClick listener
        plusButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                count++;
                updateCount();
            }
        });

        plusButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                count+=longPress_count;
                updateCount();
                return true;
            }
        });

        //Setting minus button onClick listener
        minusButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                count--;
                updateCount();
            }
        });

        minusButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                count-=longPress_count;
                updateCount();
                return true;
            }
        });
    }

    private void updateCount(){
        unit.setCount(count);
        countDisplayButton.setText(Integer.toString(count));
    }
}
