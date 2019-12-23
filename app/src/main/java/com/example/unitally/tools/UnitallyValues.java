package com.example.unitally.tools;

import android.util.Log;

import androidx.lifecycle.ViewModelProviders;

import com.example.unitally.objects.Category;
import com.example.unitally.objects.Unit;
import com.example.unitally.room.UnitObjectViewModel;

import java.util.ArrayList;
import java.util.List;

public class UnitallyValues {
    // UnitInterPlay Class
    public static final int MIN_UNIT_NAME_LENGTH = 2;
    public static final int MAX_UNIT_NAME_LENGTH = 25;
    public static final int MAX_UNIT_SYMBOL_LENGTH = 5;

    // Retrieve Unit Fragment
    public static final int MIN_QUERY_LENGTH = 2;

    // Log Values
    public static final String BUGS = "BUGS";
    public static final String QUICK_CHECK = "haha";

    public static List<Unit> generateTempUnitList() {
        final int STANDARD_PRICE = 12;
        final int STANDARD_TIME = 11;
        final int FRENCH_PRICE = 2;
        final int FRENCH_TIME = 1;
        List<Unit> tempUnits = new ArrayList<>();

        String[] unitNames = {
                "Standard Window",
                "Casement Window",
                "Picture Window",
                "Sliding Door",
                "French Window"};

        Unit priceUnit = new Unit("Price");
        priceUnit.setSymbol("$");
        priceUnit.setSymbolPos(true);
        tempUnits.add(priceUnit);

        Unit timeUnit = new Unit("Time");
        timeUnit.setSymbol("min");
        priceUnit.setSymbolPos(false);
        tempUnits.add(timeUnit);

        for (String name : unitNames) {
            Unit aUnit = new Unit(name);

            if (!name.equals("French Window")) {
                aUnit.addSubunit(priceUnit, STANDARD_PRICE);
                aUnit.addSubunit(timeUnit, STANDARD_TIME);
            } else {
                aUnit.addSubunit(priceUnit, FRENCH_PRICE);
                aUnit.addSubunit(timeUnit, FRENCH_TIME);
            }

            if (name.contains("Window")) {
                aUnit.setCategory(new Category("Window"));
            }

            tempUnits.add(aUnit);
        }
        return tempUnits;
    }
}
