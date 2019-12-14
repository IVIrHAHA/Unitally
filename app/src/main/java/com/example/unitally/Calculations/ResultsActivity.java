package com.example.unitally.Calculations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.unitally.DividerItemDecoration;
import com.example.unitally.R;
import com.example.unitally.objects.Unit;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    public static final String CALCULATION = "com.example.UnitCounterV2.Calculations";
    private List<Unit> mActiveUnits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calc_activity);

        Intent intent = getIntent();
        mActiveUnits = (ArrayList<Unit>) intent.getSerializableExtra(CALCULATION);

        RecyclerView rv = findViewById(R.id.calc_rv);
        CalculationMacroAdapter adapter = new CalculationMacroAdapter(this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.ItemDecoration decoration =
                new DividerItemDecoration(this,R.drawable.divider);

        rv.addItemDecoration(decoration);

        if(mActiveUnits == null) {
            Toast.makeText(this, "Nothing to Calculate", Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            adapter.setUncalculatedList(mActiveUnits);
            new CalculationAsyncTask(adapter).execute(mActiveUnits);
        }

    }
}
