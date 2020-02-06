package com.example.unitally.calculations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.unitally.DragSwipeHelper;
import com.example.unitally.R;
import com.example.unitally.calculations.numerical_module.CalculationAsyncTask;
import com.example.unitally.calculations.numerical_module.CalculationMacroAdapter;
import com.example.unitally.objects.Unit;
import com.example.unitally.tools.UnitallyValues;
import com.example.unitally.tools.VHCaptureCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ResultsActivity extends AppCompatActivity
                                implements NextTierCallback, VHCaptureCallback {
    public static final String RESULT_INTENT = "com.example.unitcounterv2.CalculationVars";

    private List<Unit> mCalcUnits;
    private Stack<List<Unit>> mListBackStack;
    private CalculationMacroAdapter mAdapter;

    private FrameLayout mMovementContainter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_activity);

        mListBackStack = new Stack<>();

        // Get Units to be calculated
        Intent intent = getIntent();
        mCalcUnits = (ArrayList<Unit>) intent.getSerializableExtra(RESULT_INTENT);

        // Set up display containers
        mMovementContainter = findViewById(R.id.results_movement_matrix);
        RecyclerView rv = findViewById(R.id.numerical_rv);
        mAdapter = new CalculationMacroAdapter(this);

        DragSwipeHelper moveHelper = new DragSwipeHelper(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(moveHelper);

        rv.setAdapter(mAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        touchHelper.attachToRecyclerView(rv);

        substantiateNumericalModule();
    }



    private void substantiateNumericalModule() {
        if(mCalcUnits != null) {
            new CalculationAsyncTask(mAdapter).execute(mCalcUnits);
        }
        // Nothing to calculate, list was empty
        else {
            Toast.makeText(this,
                    UnitallyValues.EMPTY_CALCULATION_PROMPT,
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        if(!mListBackStack.empty()) {
            mAdapter.setList(mListBackStack.pop());
        }
        else
            super.onBackPressed();
    }

    @Override
    public void OnNextTierReached(List<Unit> NextTierList, List<Unit> PreviousTierList) {
        mListBackStack.push(PreviousTierList);
        mAdapter.setTier(true);
        mAdapter.setList(NextTierList);
    }

    @Override
    public void onCapturedViewHolderListener(View view, int position) {
        view.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                Log.d(UnitallyValues.QUICK_CHECK, "Dragged");
                return false;
            }
        });
        //mMovementContainter.addView(view);
    }
}
