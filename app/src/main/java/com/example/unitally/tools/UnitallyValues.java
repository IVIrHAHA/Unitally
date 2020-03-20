package com.example.unitally.tools;

import android.graphics.Color;
import android.util.Log;

import com.example.unitally.objects.Category;
import com.example.unitally.objects.Unit;

import java.util.ArrayList;
import java.util.List;

public class UnitallyValues {
    // UnitInterPlay Class
    public static final int MIN_UNIT_NAME_LENGTH = 3;
    public static final int MAX_UNIT_NAME_LENGTH = 15;
    public static final int MAX_UNIT_SYMBOL_LENGTH = 5;

    // Retrieve Unit Fragment
    public static final int MIN_QUERY_LENGTH = 3;

    // Unit object
    public static final String CATEGORY_DEFAULT_NAME = "Miscellaneous";

    // Log Values
    public static final String BUGS = "BUGS";
    public static final String QUICK_CHECK = "haha";
    public static final String LIST_MANAGER_PROCESS = "lcc";
    public static final String STARTING_PROCESS = "start process";
    public static final String CALC_PROCESS = "cp";

    // Error Mistake Worthy Prompts
    public static final String BAD_CODING_PROMPT = "Sorry an error has occurred on our end";

    // Confirmation window default prompt
    public static final String DEFAULT_CONFRIMATION_PROMPT = "Are you sure?";
    public static final String EMPTY_CALCULATION_PROMPT = "Nothing to calculate";

    public static final int[] COLORS = {
            Color.argb(255,0,0,0),        // Black                            [0]
            Color.argb(255,2,181,160),    // Bermuda Bay      (Tealish)       [1]
            Color.argb(255,254,197,45),   // Crushed Curry    (Yellowish)     [2]
            Color.argb(255,234,62,112),   // Melon Mambo      (Pinkish)       [3]
            Color.argb(255,138,151,71),   // Old Olive        (Greenish)      [4]
            Color.argb(255,1,128,181),    // Pacific Point    (Blueish)       [5]
            Color.argb(255,255,130,1),    // Pumpkin Pie      (Orangish)      [6]
            Color.argb(255,0,126,135),    // Island Indigo    (Darker-Tealish)[7]
            Color.argb(255,149,69,103),   // Rich Razzleberry (Purplish)      [8]
            Color.argb(255,243,114,82),   // Tangerine Tango  (Muted-Orangish)[9]
            Color.argb(255,200,75, 109),  // Rose Red         (Muted-Pinkish) [10]
    };

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
        priceUnit.setCategory(new Category("Currencies"));
        tempUnits.add(priceUnit);

        Unit timeUnit = new Unit("Time");
        timeUnit.setSymbol("min");
        priceUnit.setSymbolPos(false);
        tempUnits.add(timeUnit);

        // Make supreme units
        Unit house = new Unit("House");
        house.setCategory(new Category("Structures"));
        Unit building = new Unit("Building");
        building.setCategory(new Category("Structures"));

        for (String name : unitNames) {
            Unit aUnit = new Unit(name);

            if (!name.equals("French Window")) {
                aUnit.addSubunit(priceUnit, STANDARD_PRICE);
                aUnit.addSubunit(timeUnit, STANDARD_TIME);
            } else {
                aUnit.addSubunit(priceUnit, FRENCH_PRICE);
                aUnit.addSubunit(timeUnit, FRENCH_TIME);
            }

        // Creating House Unit
            // Standard window
            if(name.equalsIgnoreCase(unitNames[0])){
                house.addSubunit(aUnit, 15);
                building.addSubunit(aUnit, 20);
            }
            // Picture window
            else if(name.equalsIgnoreCase(unitNames[2])){
                house.addSubunit(aUnit, 2);
                building.addSubunit(aUnit, 1);
            }
            // Sliding door
            else if(name.equalsIgnoreCase(unitNames[3])){
                house.addSubunit(aUnit, 3);
                building.addSubunit(aUnit, 4);
            }

            tempUnits.add(aUnit);
        }

        tempUnits.add(building);
        tempUnits.add(house);
        return tempUnits;
    }

    public static List<Category> generateCategories() {
        String[] categoryNames = {"Windows", "Currencies", "Structures", "Fees"};
        List<Category> categories = new ArrayList<>();

        for(String catName : categoryNames) {
            categories.add(new Category(catName));
        }

        return categories;
    }
}
