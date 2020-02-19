package com.example.unitally;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.unitally.app_settings.SettingsActivity;
import com.example.unitally.calculations.staging_module.StageFragment;
import com.example.unitally.calculations.unit_tree_module.UnitTreeFragment;
import com.example.unitally.objects.Category;
import com.example.unitally.unit_interaction.CategoryFragment;
import com.example.unitally.unit_interaction.UnitInterPlayActivity;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
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
        StageFragment.OnItemExitListener {

    // Also known as edit unit
    private static final int DISPLAY_UNIT_ACTIVITY = "Retrieve Unit Before Passing".hashCode();
    private static final String CATEGORY_FRAGMENT = "com.example.unitally.CategoryFragment";

    public static int gIncrement_Count;

    //RecyclerView Components
    private final LinkedList<Unit> mUserAddedUnits = new LinkedList<>();

    private Stack<List<Unit>> mListBackStack;
    private ImageButton mAddUnitButton;
    private UnitTreeFragment mActiveTreeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load Settings
        SettingsActivity.loadData(getApplicationContext());

        // Substantiate Nav drawer, app bar, toolbar...etc.
        substantiateNavViews();

        mListBackStack = new Stack<>();

        mAddUnitButton = findViewById(R.id.add_unit_button);
        mAddUnitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Unit> activeUnits = new ArrayList<>(mUserAddedUnits);

                // Start and remove units already in the Active List.
                RetrieveUnitFragment fragment = RetrieveUnitFragment.newInstance(activeUnits, true);
                startRetrieveFragment(fragment);
            }
        });
    }

    /**
     * Sets FragmentManager and animations. Used to bypass boilerplate.
     *
     * @param fragment Fragment to start
     */
    private void startRetrieveFragment(RetrieveUnitFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_bottom, R.anim.slide_to_bottom, R.anim.slide_from_bottom, R.anim.slide_to_bottom);
        transaction.addToBackStack(null);
        transaction.add(R.id.main_ru_container, fragment, "RU_FRAGMENT").commit();
    }

    /**
     * Start CategoryFragment. Ease of use.
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

    @Override
    public void onCategoryFragmentInteraction(Category category, int reason) {

    }

    @Override
    public void onUnitRetrieval(List<Unit> selectedUnits, int reason) {
        if (selectedUnits != null && reason == RetrieveUnitFragment.NO_REASON_GIVEN) {
            if (!selectedUnits.isEmpty()) {
                if (selectedUnits.size() > 1) {
                    // mActiveTreeFragment.appendToTier(selectedUnits);
                }

                // Add to tree and stage
                else {
                    Unit selectedUnit = selectedUnits.get(0);
                    // mActiveTreeFragment.appendToTier(selectedUnit);
                    stageUnit(selectedUnit);
                }
            }
        } else if (selectedUnits != null && reason == DISPLAY_UNIT_ACTIVITY) {
            if (!selectedUnits.isEmpty()) {
                Unit revisedUnit = selectedUnits.get(0);
                Intent editIntent = new Intent(this, UnitInterPlayActivity.class);
                editIntent.putExtra(UnitInterPlayActivity.REVIEW_MODE, true);
                editIntent.putExtra(UnitInterPlayActivity.DISPLAY_UNIT, revisedUnit);
                startActivity(editIntent);
            }
        }
    }


// TODO: INVESTIGATE IF USEFUL

//    /**
//     * Adds a ticker view
//     * @param unit The unit to be manipulated
//     */
//    private void addTicker(final Unit unit) {
//        if(unit != null) {
//            mUserAddedUnits.addLast(unit);
//
//            int count=mUserAddedUnits.size();
//            mRecyclerView.getAdapter().notifyItemInserted(count);
//            mRecyclerView.smoothScrollToPosition(count);
//
//            mAdapter.notifyDataSetChanged();
//
//        }
//    }
//
//    /**
//     * Begins the retrieval process to add a ticker view.
//     *
//     * @param item Ticker View
//     */
//    public void addUnit(MenuItem item) {
//        ArrayList<Unit> activeUnits = new ArrayList<>(mUserAddedUnits);
//
//        // Start and remove units already in the Active List.
//        RetrieveUnitFragment fragment = RetrieveUnitFragment.newInstance(activeUnits,true);
//        startRetrieveFragment(fragment);
//    }


/*------------------------------------------------------------------------------------------------*/
/*                             NAVIGATION DRAWER AND APP BAR                                      */
/*------------------------------------------------------------------------------------------------*/
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
                    .newInstance(false, DISPLAY_UNIT_ACTIVITY);
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
/*                                 UNIT TREE/MASTER FIELD                                         */
/*------------------------------------------------------------------------------------------------*/

/*------------------------------------------------------------------------------------------------*/
/*                                        STAGING                                                 */
/*------------------------------------------------------------------------------------------------*/
    @Override
    public void OnStageExit(Unit unit, int exitInstance) {

    }

    private void stageUnit(Unit unit) {
        StageFragment fragment = StageFragment.newInstance(unit);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.staging_container, fragment, "FRAGMENT_LAUNCHED").commit();

        //launchFragment(fragment, R.id.staging_container);
    }

/*------------------------------------------------------------------------------------------------*/
/*                                    ACTIVITY METHODS                                            */
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

    private void substantiateNavViews() {
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
}