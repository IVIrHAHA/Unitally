package com.example.unitally.calculations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.unitally.DividerItemDecoration;
import com.example.unitally.R;
import com.example.unitally.objects.Unit;
import com.example.unitally.tools.CategoryOrganizer;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    public static final String CALCULATION = "com.example.UnitCounterV2.Calculations";
    private List<Unit> mActiveUnits;
    private CalculationMacroAdapter mAdatper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calc_activity);

        Intent intent = getIntent();
        mActiveUnits = (ArrayList<Unit>) intent.getSerializableExtra(CALCULATION);

        RecyclerView rv = findViewById(R.id.calc_rv);
        mAdatper = new CalculationMacroAdapter(this);
        rv.setAdapter(mAdatper);
        rv.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.ItemDecoration decoration =
                new DividerItemDecoration(this,R.drawable.divider);

        rv.addItemDecoration(decoration);

        Button button = findViewById(R.id.cat_button);

        if(mActiveUnits == null) {
            Toast.makeText(this, "Nothing to Calculate", Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            mAdatper.setUncalculatedList(mActiveUnits);
            new CalculationAsyncTask(mAdatper).execute(mActiveUnits);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toCategoryView();
            }
        });
    }

    private void toCategoryView() {
        List<Unit> categoryList = new CategoryOrganizer().generate(mAdatper.getCalculatedList());
        mAdatper.setList(categoryList);
    }
}
