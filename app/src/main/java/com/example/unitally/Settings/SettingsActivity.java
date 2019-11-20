package com.example.unitally.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.unitally.R;
import com.example.unitally.objects.Unit;
import com.example.unitally.room.UnitObjectViewModel;

public class SettingsActivity extends AppCompatActivity implements GetUserNumber.OnGetUserNumberInteractionListener {

    private TextView mIncAmount, mDeleteAll, mPropagate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mIncAmount = findViewById(R.id.settings_inc_amount);
        mDeleteAll = findViewById(R.id.settings_delete_all);
        mPropagate = findViewById(R.id.settings_propogate);

        mIncAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incAmount();
            }
        });
        mDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAll();
                finish();
            }
        });
        mPropagate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                propagate();
                finish();
            }
        });
    }

    private void incAmount() {
        // SharedPrefrences
        Fragment frag = GetUserNumber.newInstance("Enter Increment/Decrement Amount");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.settings_container, frag, "NUMBER_FRAGMENT").commit();
    }

    private void deleteAll() {
        UnitObjectViewModel vm = ViewModelProviders.of(this).get(UnitObjectViewModel.class);
        vm.deleteAll();
    }

    // TODO:Remove developer feature (PROPAGATE UNITS)
    private void propagate() {
        final int STANDARD_PRICE = 12;
        final int STANDARD_TIME = 11;
        final int FRENCH_PRICE = 2;
        final int FRENCH_TIME = 1;

        String[] unitNames = {
                "Standard Window",
                "Casement Window",
                "Picture Window",
                "Sliding Door",
                "French Window"};

        UnitObjectViewModel vm = ViewModelProviders.of(this).get(UnitObjectViewModel.class);

        Unit priceUnit = new Unit("Price");
        priceUnit.setSymbol("$");
        priceUnit.setSymbolPos(true);
        vm.saveUnit(priceUnit);

        Unit timeUnit = new Unit("Time");
        timeUnit.setSymbol("min");
        priceUnit.setSymbolPos(false);
        vm.saveUnit(timeUnit);

        for(String name:unitNames){
            Unit aUnit = new Unit(name);

            if(!name.equals("French Window")) {
                aUnit.addSubunit(priceUnit, STANDARD_PRICE);
                aUnit.addSubunit(timeUnit, STANDARD_TIME);
            }
            else {
                aUnit.addSubunit(priceUnit, FRENCH_PRICE);
                aUnit.addSubunit(timeUnit, FRENCH_TIME);
            }
            vm.saveUnit(aUnit);
        }
    }

    @Override
    public void onFragmentInteraction(double userNumber) {

    }
}
