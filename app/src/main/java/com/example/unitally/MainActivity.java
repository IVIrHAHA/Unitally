package com.example.unitally;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitally.calculations.ResultsActivity;
import com.example.unitally.app_settings.SettingsActivity;
import com.example.unitally.objects.Category;
import com.example.unitally.room.UnitObjectViewModel;
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
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.unitally.objects.Unit;
import com.example.unitally.unit_retrieval.RetrieveUnitFragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    RetrieveUnitFragment.OnFragmentInteractionListener,
                    CategoryFragment.OnFragmentInteractionListener{

    // Also known as edit unit
    private static final int DISPLAY_UNIT_ACTIVITY = "Retrieve Unit Before Passing".hashCode();
    private static final String CATEGORY_FRAGMENT = "com.example.unitally.CategoryFragment";

    public static final String TAG = MainActivity.class.toString();
    public static int gIncrement_Count;

    private FrameLayout mRU_Container;

    //RecyclerView Components
    private final LinkedList<Unit> mActiveUnits = new LinkedList<>();
    private RecyclerView mRecyclerView;
    public MainTickerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load Settings
        SettingsActivity.loadData(getApplicationContext());

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Substantiating Views
        mRU_Container = findViewById(R.id.main_ru_container);
        mRecyclerView = findViewById(R.id.main_recyclerview);
        NavigationView navigationView = findViewById(R.id.nav_view);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        //Drawer setup
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Adding the navigation view
        navigationView.setNavigationItemSelectedListener(this);

        //Substantiate RecyclerView
        mAdapter      = new MainTickerAdapter(this, mActiveUnits);

        DragSwipeHelper moveHelper = new DragSwipeHelper(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(moveHelper);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        touchHelper.attachToRecyclerView(mRecyclerView);

//        // TODO: REMOVE TEMP CODE
//        List<Unit> tempList = UnitallyValues.generateTempUnitList();
//
//        for(int i=2; i<5; i++) {
//            Unit unit = tempList.get(i);
//            unit.setCount(3 + i);
//            addTicker(unit);
//        }
//
//        Intent calcIntent = new Intent(this, ResultsActivity.class);
//        calcIntent.putExtra(ResultsActivity.CALCULATION,mActiveUnits);
//        startActivity(calcIntent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //OnClick methods
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_calculate) {
            Intent calcIntent = new Intent(this, ResultsActivity.class);
            calcIntent.putExtra(ResultsActivity.CALCULATION,mActiveUnits);
            startActivity(calcIntent);
            return true;
        }

        else if(id == R.id.menu_save_as_unit) {
            if(!mActiveUnits.isEmpty()) {
                Intent saveUnitIntent = new Intent(this, UnitInterPlayActivity.class);
                saveUnitIntent.putExtra(UnitInterPlayActivity.PASSING_UNIT, true);
                saveUnitIntent.putExtra(UnitInterPlayActivity.UNIS_ARRAY, mActiveUnits);
                startActivity(saveUnitIntent);
                return true;
            }
            else {
                Toast.makeText(this,
                        "Need at least one active Unit",
                        Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        }

        else if(id == R.id.menu_clear) {
            mAdapter.clear();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //Unit Controls
        if (id == R.id.nav_create_new_unit) {
            Intent createIntent = new Intent(this, UnitInterPlayActivity.class);
            createIntent.putExtra(UnitInterPlayActivity.REVIEW_MODE,false);
            startActivity(createIntent);
        }

        else if (id == R.id.nav_edit_unit) {
            RetrieveUnitFragment fragment = RetrieveUnitFragment
                    .newInstance(false,DISPLAY_UNIT_ACTIVITY);
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
        }

        else if (id == R.id.nav_about) {
            Toast.makeText(this, "Feature coming soon", Toast.LENGTH_SHORT).show();
            // TODO: Make an about activity
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Begins the retrieval process to add a ticker view.
     *
     * @param item Ticker View
     */
    public void addUnit(MenuItem item) {
        ArrayList<Unit> activeUnits = new ArrayList<>(mActiveUnits);

        // Start and remove units already in the Active List.
        RetrieveUnitFragment fragment = RetrieveUnitFragment.newInstance(activeUnits,true);
        startRetrieveFragment(fragment);
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
        transaction.add(R.id.main_ru_container,fragment,"RU_FRAGMENT").commit();
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
        transaction.add(R.id.main_ru_container,fragment,CATEGORY_FRAGMENT).commit();
    }

    @Override
    public void onFragmentInteraction(List<Unit> selectedUnits, int reason) {
        if(selectedUnits != null && reason == RetrieveUnitFragment.NO_REASON_GIVEN) {
            if(!selectedUnits.isEmpty()) {
                for(Unit unit:selectedUnits) {
                    addTicker(unit);
                }
            }
        }

        else if(selectedUnits != null && reason == DISPLAY_UNIT_ACTIVITY) {
            if(!selectedUnits.isEmpty()) {
                Unit revisedUnit = selectedUnits.get(0);
                Intent editIntent = new Intent(this, UnitInterPlayActivity.class);
                editIntent.putExtra(UnitInterPlayActivity.REVIEW_MODE,true);
                editIntent.putExtra(UnitInterPlayActivity.DISPLAY_UNIT, revisedUnit);
                startActivity(editIntent);
            }
        }
    }


    /**
     * Adds a ticker view
     * @param unit The unit to be manipulated
     */
    private void addTicker(final Unit unit) {
        if(unit != null) {
            mActiveUnits.addLast(unit);

            int count=mActiveUnits.size();
            mRecyclerView.getAdapter().notifyItemInserted(count);
            mRecyclerView.smoothScrollToPosition(count);

            mAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onCategoryFragmentInteraction(Category category, int reason) {

    }
}