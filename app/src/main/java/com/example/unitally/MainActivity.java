package com.example.unitally;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.unitally.app_modules.unit_tree_module.UnitTreeAdapter;
import com.example.unitally.app_settings.SettingsActivity;
import com.example.unitally.app_modules.staging_module.StageFragment;
import com.example.unitally.app_modules.unit_tree_module.UnitTreeFragment;
import com.example.unitally.objects.Category;
import com.example.unitally.objects.UnitWrapper;
import com.example.unitally.tools.UnitTreeListManager;
import com.example.unitally.tools.UnitallyValues;
import com.example.unitally.unit_interaction.CategoryFragment;
import com.example.unitally.unit_interaction.UnitInterPlayActivity;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.unitally.objects.Unit;
import com.example.unitally.unit_retrieval.RetrieveUnitFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingFormatArgumentException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        RetrieveUnitFragment.onUnitRetrievalInteraction,
        CategoryFragment.OnFragmentInteractionListener,
        StageFragment.OnItemExitListener,
        UnitTreeAdapter.UnitTreeListener {

    // SavedInstance Tags
    private static final String SHARED_PREFS = "com.example.unitally.SHARED_PREFS";
    private static final String SAVED_LIST_TAG = "com.example.unitally.SAVED_LIST_INSTANCE";
    private static final String SAVED_STAGE_STATUS_TAG = "com.example.unitally.STAGE_STATUS";

    // "RUR" = Retrieve Unit Reason
    private static final int RUR_GET_UNIT = "Retrieve Unit Before Passing".hashCode();
    private static final int RUR_ADD_UNIT = "Retrieve unit for project".hashCode();

    // Fragment Tags
    private static final String CATEGORY_FRAGMENT = "com.example.unitally.CategoryFragment";
    private static final String RU_FRAGMENT = "com.example.unitally.RU_FRAGMENT";
    private static final String UNIT_TREE_FRAGMENT = "com.example.unitally.UnitTreeFragment";
    private static final String STAGE_FRAGMENT = "com.example.unitally.StageFragment";

    public static int gIncrement_Count;

    //RecyclerView Components
    private final LinkedList<Unit> mUserAddedUnits = new LinkedList<>();

    // Used to add and remove elements from the list
    private UnitTreeListManager mListManager;

    private boolean mItemStaged;
    private FragmentManager mFragManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragManager = getSupportFragmentManager();

        // Load
        SettingsActivity.loadData(getApplicationContext());
        // If nothing to load, then start with a clean slate
        if(!loadState()) {
            Log.i(UnitallyValues.STARTING_PROCESS, "NO LIST TO LOAD...CREATING NEW");
            mListManager = UnitTreeListManager.getInstance(this, null);
            mItemStaged = false;
        }

        // Initialize Nav drawer, app bar, toolbar...etc.
        initMenus();
        // Initialize Details, UnitTree, and Staging modules
        initModules();
    }

    private void saveState() {
        Log.d(UnitallyValues.LIST_MANAGER_PROCESS, "Saving State...");

        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if(!mListManager.isEmpty()) {
            Gson gson = new Gson();

            String json = gson.toJson(mListManager.getList());

            editor.putString(SAVED_LIST_TAG, json);
            editor.putBoolean(SAVED_STAGE_STATUS_TAG, mItemStaged);

            Log.d(UnitallyValues.QUICK_CHECK, "Not empty list");
        }
        else {
            Log.d(UnitallyValues.QUICK_CHECK, "Empty list");

            editor.clear();

            if(preferences.contains(SAVED_LIST_TAG)) {
                Log.d(UnitallyValues.QUICK_CHECK, "Cleared");

            }
            else
                Log.d(UnitallyValues.QUICK_CHECK, "Not Cleared");
        }

        editor.apply();
    }

    private boolean loadState() {
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        mItemStaged = false;

//        if(preferences.contains(SAVED_LIST_TAG)) {
//            Log.i(UnitallyValues.STARTING_PROCESS, "(LOAD)LOADING...");
//
//            Gson gson = new Gson();
//            String json = preferences.getString(SAVED_LIST_TAG,"");
//            Type type = new TypeToken<ArrayList<UnitWrapper>>() {}.getType();
//
//            ArrayList<UnitWrapper> list = gson.fromJson(json, type);
//            mListManager = UnitTreeListManager.getInstance(this, list);
//
//            Log.i(UnitallyValues.STARTING_PROCESS, "(LOAD)LOADED: LIST SIZE "
//                    + mListManager.size());
//
//            return true;
//        }

        return false;
    }


    @Override
    protected void onPause() {
        super.onPause();
        //saveState();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onCategoryFragmentInteraction(Category category, int reason) {

    }

    @Override
    public void onUnitRetrieval(List<Unit> selectedUnits, int reason) {
        if (selectedUnits != null && reason == RUR_ADD_UNIT) {
            if (!selectedUnits.isEmpty()) {
                // Also stage if only one unit was retrieved
                if (selectedUnits.size() == 1) {
                    Unit selectedUnit = selectedUnits.get(0);
                    mListManager.add(selectedUnit);
                    UnitWrapper stager = mListManager.get(selectedUnit);

                    if(stager != null) {
                        stageUnit(stager);
                    }
                    // TODO: If user add only one item stage.
                    //  Need to figure out Unit wrapping, MF vs UA
                }
            }
        } else if (selectedUnits != null && reason == RUR_GET_UNIT) {
            if (!selectedUnits.isEmpty()) {
                Unit revisedUnit = selectedUnits.get(0);
                Intent editIntent = new Intent(this, UnitInterPlayActivity.class);
                editIntent.putExtra(UnitInterPlayActivity.REVIEW_MODE, true);
                editIntent.putExtra(UnitInterPlayActivity.DISPLAY_UNIT, revisedUnit);
                startActivity(editIntent);
            }
        }
    }

/*------------------------------------------------------------------------------------------------*/
/*                                 UNIT TREE/MASTER FIELD                                         */
/*------------------------------------------------------------------------------------------------*/
    private void startUnitTreeFragment(UnitTreeFragment fragment) {
        FragmentTransaction transaction = mFragManager.beginTransaction();

        if(mFragManager.findFragmentByTag(UNIT_TREE_FRAGMENT) != null) {
            transaction.replace(R.id.unit_tree_container, fragment, UNIT_TREE_FRAGMENT).commit();
        }
        else {
            transaction.add(R.id.unit_tree_container, fragment, UNIT_TREE_FRAGMENT).commit();
        }
    }

    @Override
    public void OnItemSwiped(UnitWrapper unit, int direction) {
        if(direction == ItemTouchHelper.LEFT) {
            UnitTreeFragment fragment = UnitTreeFragment.newInstance(unit.peek());
            // TODO: Adding sometimes for some reason
            startUnitTreeFragment(fragment);
        }
        else if(direction == ItemTouchHelper.RIGHT) {
            // TODO: Add functionality
        }
    }

/*------------------------------------------------------------------------------------------------*/
/*                                        STAGING                                                 */
/*------------------------------------------------------------------------------------------------*/

    @Override
    public void OnStageExit(UnitWrapper parcel, int exitInstance) {
        mItemStaged = false;
        if(exitInstance == StageFragment.LEFT_EXIT) {
            mListManager.remove(parcel);
        }
        // Update counts of the list
        else if(exitInstance == StageFragment.RIGHT_EXIT) {
            mListManager.update(parcel);
        }
    }

    @Override
    public void fromAdapterToStage(UnitWrapper parcel) {
        stageUnit(parcel);
    }

    private void stageUnit(UnitWrapper parcel) {
        if(!mItemStaged) {
            mItemStaged = true;
            StageFragment fragment = StageFragment.newInstance(parcel);
            startStageFragment(fragment);
        }
        else
            Toast.makeText(this, "Must clear stage first", Toast.LENGTH_SHORT).show();
    }

    private void startStageFragment(StageFragment fragment) {
        FragmentTransaction transaction = mFragManager.beginTransaction();
        // TODO: Add custom animation
        transaction.add(R.id.staging_container, fragment, STAGE_FRAGMENT).commit();
    }

/*------------------------------------------------------------------------------------------------*/
/*                             VARIOUS MENU ITEMS/STATIC VIEWS                                    */
/*------------------------------------------------------------------------------------------------*/
    private void initMenus() {
        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Substantiating Views
        NavigationView navigationView = findViewById(R.id.nav_view);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        //Drawer setup
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Adding the navigation view
        navigationView.setNavigationItemSelectedListener(this);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //Unit Controls
        if (id == R.id.nav_create_new_unit) {
            Intent createIntent = new Intent(this, UnitInterPlayActivity.class);
            createIntent.putExtra(UnitInterPlayActivity.REVIEW_MODE, false);
            startActivity(createIntent);
        } else if (id == R.id.nav_edit_unit) {
            RetrieveUnitFragment fragment = RetrieveUnitFragment
                    .newInstance(false, RUR_GET_UNIT);
            startRetrieveFragment(fragment);
        }

        //Category Controls
        else if (id == R.id.nav_edit_category) {
            mFragManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            CategoryFragment fragment = CategoryFragment
                    .newInstance(CategoryFragment.EDIT_CATEGORY);
            startCategoryFragment(fragment);
        }

        //Miscellaneous
        else if (id == R.id.nav_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (id == R.id.nav_about) {
            Toast.makeText(this, "Feature coming soon", Toast.LENGTH_SHORT).show();
            // TODO: Make an about activity
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_calculate) {
            // TODO: REMOVE THIS ITEM
            return true;
        } else if (id == R.id.menu_save_as_unit) {
            if (!mUserAddedUnits.isEmpty()) {
                Intent saveUnitIntent = new Intent(this, UnitInterPlayActivity.class);
                saveUnitIntent.putExtra(UnitInterPlayActivity.PASSING_UNIT, true);
                saveUnitIntent.putExtra(UnitInterPlayActivity.UNIS_ARRAY, mUserAddedUnits);
                startActivity(saveUnitIntent);
                return true;
            } else {
                Toast.makeText(this,
                        "Need at least one active Unit",
                        Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        } else if (id == R.id.menu_clear) {
            mListManager.clear();
        }

        return super.onOptionsItemSelected(item);
    }

/*------------------------------------------------------------------------------------------------*/
/*                               ACTIVITY/BOILERPLATE METHODS                                     */
/*------------------------------------------------------------------------------------------------*/
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(mFragManager.findFragmentByTag(RU_FRAGMENT) != null) {
            mFragManager.beginTransaction()
                    .remove(mFragManager.findFragmentByTag(RU_FRAGMENT)).commit();
        }

        else if(mFragManager.findFragmentByTag(CATEGORY_FRAGMENT) != null) {
            mFragManager.beginTransaction()
                    .remove(mFragManager.findFragmentByTag(CATEGORY_FRAGMENT)).commit();
        }

        else {
            UnitTreeFragment fragment = mListManager.revert();
            if(fragment != null) {
                startUnitTreeFragment(fragment);
            }
            else {
                launchConfirmation();
            }
        }
    }

    private void launchConfirmation() {
        AlertDialog.Builder altDial = new AlertDialog.Builder(this);
        altDial.setMessage("Are you sure you want to exit?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //saveState();
                        finish();

                        // TODO: Try and fix this bug
                        //  The bug is caused when using the backbutton exit and UnitTree fragment
                        //  tries to add instead of replace.
                        System.exit(0);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alert = altDial.create();
        alert.setTitle("Exit");
        alert.show();
    }

    private void initModules() {
        // Initializing Master-Field (Initial UnitList)
        UnitTreeFragment fragment = UnitTreeFragment.newInstance(null);
        startUnitTreeFragment(fragment);

        // Add Unit Button
        ImageButton addUnitButton = findViewById(R.id.add_unit_button);
        addUnitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Unit> activeUnits = mListManager.getActiveUnits();

                // Start and remove user-added units already in the Active List.
                RetrieveUnitFragment fragment =
                        RetrieveUnitFragment.newInstance(activeUnits,true, RUR_ADD_UNIT);
                startRetrieveFragment(fragment);
            }
        });
    }

    /**
     * Start RetrieveUnitFragment.
     *
     * @param fragment RetrieveUnit Fragment
     */
    private void startRetrieveFragment(RetrieveUnitFragment fragment) {
        FragmentTransaction transaction = mFragManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_bottom, R.anim.slide_to_bottom,
                R.anim.slide_from_bottom, R.anim.slide_to_bottom);
        transaction.add(R.id.main_ru_container, fragment, RU_FRAGMENT).commit();
    }

    /**
     * Start CategoryFragment.
     *
     * @param fragment Category Fragment
     */
    private void startCategoryFragment(CategoryFragment fragment) {
        FragmentTransaction transaction = mFragManager.beginTransaction();
        // TODO: Add custom animation
        transaction.add(R.id.main_ru_container, fragment, CATEGORY_FRAGMENT).commit();
    }
}