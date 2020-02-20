package com.example.unitally;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.unitally.app_settings.SettingsActivity;
import com.example.unitally.app_modules.staging_module.StageFragment;
import com.example.unitally.app_modules.unit_tree_module.UnitTreeFragment;
import com.example.unitally.objects.Category;
import com.example.unitally.tools.UnitallyValues;
import com.example.unitally.unit_interaction.CategoryFragment;
import com.example.unitally.unit_interaction.UnitInterPlayActivity;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.unitally.objects.Unit;
import com.example.unitally.unit_retrieval.RetrieveUnitFragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        RetrieveUnitFragment.onUnitRetrievalInteraction,
        CategoryFragment.OnFragmentInteractionListener,
        StageFragment.OnItemExitListener,
        UnitTreeFragment.OnUnitTreeInteraction {

    // "RUR" = Retrieve Unit Reason
    private static final int RUR_GET_UNIT = "Retrieve Unit Before Passing".hashCode();
    private static final int RUR_ADD_UNIT = "Retrieve unit for project".hashCode();

    // Fragment Tags
    private static final String CATEGORY_FRAGMENT = "com.example.unitally.CategoryFragment";
    private static final String UNIT_TREE_FRAGMENT = "com.example.unitally.UnitTreeFragment";
    private static final String STAGE_FRAGMENT = "com.example.unitally.StageFragment";

    public static int gIncrement_Count;

    //RecyclerView Components
    private final LinkedList<Unit> mUserAddedUnits = new LinkedList<>();

    private Stack<List<Unit>> mListBackStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load Settings
        SettingsActivity.loadData(getApplicationContext());
        // Initialize Nav drawer, app bar, toolbar...etc.
        initMenus();
        // Initialize Details, UnitTree, and Staging modules
        initModules();

        mListBackStack = new Stack<>();
    }

    // TODO: LOOK INTO REMOVING UNNECESSARY LISTENER
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
                    stageUnit(selectedUnit);
                }

                addUnitsToFragment(selectedUnits);
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

    @Override
    public void onUnitTreeInteraction(List<Unit> currentTier, Unit unitBranch) {

    }

    private void startUnitTreeFragment(UnitTreeFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // TODO: Add custom animation
        transaction.addToBackStack(null);
        transaction.add(R.id.unit_tree_container, fragment, UNIT_TREE_FRAGMENT).commit();
    }

    private void addUnitsToFragment(List<Unit> units) {
        // Get Fragment
        UnitTreeFragment fragment = (UnitTreeFragment) getSupportFragmentManager()
                .findFragmentByTag(UNIT_TREE_FRAGMENT);

        // Add units to fragment
        if(fragment != null) {
            fragment.appendToTier(units);
        }
        else {
            Log.d(UnitallyValues.BUGS, UnitallyValues.BAD_CODING_PROMPT);
            throw new RuntimeException(this.toString()
                    + " Failed to find UnitTreeFragment");
        }
    }

/*------------------------------------------------------------------------------------------------*/
/*                                        STAGING                                                 */
/*------------------------------------------------------------------------------------------------*/
    @Override
    public void OnStageExit(Unit unit, int exitInstance) {

    }

    private void startStageFragment(StageFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // TODO: Add custom animation
        transaction.addToBackStack(null);
        transaction.add(R.id.staging_container, fragment, CATEGORY_FRAGMENT).commit();
    }

    private void stageUnit(Unit unit) {
        StageFragment fragment = StageFragment.newInstance(unit);

        startStageFragment(fragment);
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
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
            //mAdapter.clear(); // TODO: CLEAR MASTER FIELD
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
        } else {
            super.onBackPressed();
        }
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
                ArrayList<Unit> activeUnits = new ArrayList<>(mUserAddedUnits);

                // Start and remove user-added units already in the Active List.
                RetrieveUnitFragment fragment = RetrieveUnitFragment
                                                    .newInstance(activeUnits, true);
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_bottom, R.anim.slide_to_bottom,
                R.anim.slide_from_bottom, R.anim.slide_to_bottom);
        transaction.addToBackStack(null);
        transaction.add(R.id.main_ru_container, fragment, "RU_FRAGMENT").commit();
    }

    /**
     * Start CategoryFragment.
     *
     * @param fragment Category Fragment
     */
    private void startCategoryFragment(CategoryFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // TODO: Add custom animation
        transaction.addToBackStack(null);
        transaction.add(R.id.main_ru_container, fragment, CATEGORY_FRAGMENT).commit();
    }
}