package com.example.unitally.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unitally.MainActivity;
import com.example.unitally.R;
import com.example.unitally.objects.Unit;
import com.example.unitally.room.UnitObjectViewModel;

public class SettingsActivity extends AppCompatActivity {

    private static final String INCREMENT_AMOUNT_KEY = "Increment Amount";
    private static final String NIGHT_MODE_KEY = "Night Mode";
    private static final String SETTINGS_SHARED_PREFERENCES = "Settings";
    private static final int INCREMENT_DEFAULT_AMOUNT = 5;

    private EditText mIncrementCountAmount_ET;
    private Switch mNightModeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mIncrementCountAmount_ET = findViewById(R.id.settings_inc_count_amount);
        mNightModeSwitch= findViewById(R.id.settings_app_theme_switch);

        // Setting Values
        //mIncrementCountAmount_ET.setText(MainActivity.gIncrement_Count);

        TextView deleteAll_tv = findViewById(R.id.settings_delete_all);
        TextView propagate_tv = findViewById(R.id.settings_propogate);
        Button saveButton = findViewById(R.id.settings_save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int incrementNumber = INCREMENT_DEFAULT_AMOUNT;
                try{
                    incrementNumber = Integer.parseInt(mIncrementCountAmount_ET.getText().toString());
                    saveData(incrementNumber);

                } catch (Exception e) {
                    // TODO: Remove (TAG)
                    Log.d("Settings","Failed to load from shared preferences");
                }
            }
        });
        deleteAll_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAll();
                finish();
            }
        });
        propagate_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                propagate();
                finish();
            }
        });
    }

    // TODO: Add Night mode save/load
    /**
     * Save increment amount
     *
     * @param amount increment amount
     */
    private void saveData(int amount) {
        SharedPreferences preferences = getSharedPreferences(SETTINGS_SHARED_PREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(INCREMENT_AMOUNT_KEY, amount);
        editor.apply();
        MainActivity.gIncrement_Count = amount;
        finish();
        Log.d("Settings", "Saved: " + amount);
    }

    /**
     * Load data will set increment amount in MainActivity
     */
    public static void loadData(Context context) {
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(SETTINGS_SHARED_PREFERENCES,MODE_PRIVATE);

        MainActivity.gIncrement_Count = preferences
                .getInt(INCREMENT_AMOUNT_KEY, INCREMENT_DEFAULT_AMOUNT);

        Log.d("Settings", "Loaded:");
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
}
