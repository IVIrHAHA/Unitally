package com.example.unitally.calculations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.unitally.R;
import com.example.unitally.calculations.staging_module.StageFragment;
import com.example.unitally.calculations.unit_tree_module.UnitTreeFragment;
import com.example.unitally.objects.Unit;
import com.example.unitally.tools.UnitallyValues;
import com.example.unitally.unit_retrieval.RetrieveUnitFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ResultsActivity extends AppCompatActivity
        implements StageFragment.OnItemExitListener,
        UnitTreeFragment.OnUnitTreeInteraction,
        RetrieveUnitFragment.OnFragmentInteractionListener {
    public static final String RESULT_INTENT = "com.example.unitcounterv2.CalculationVars";

    private Stack<List<Unit>> mListBackStack;

    private ImageButton mAddUnitButton;

    private UnitTreeFragment mActiveTreeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_activity);

        mListBackStack = new Stack<>();

        mAddUnitButton = findViewById(R.id.add_unit_button);
        mAddUnitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveUnit();
            }
        });

        // Launch empty Unit field
        //displayTreeTier(null);
    }

    private void retrieveUnit() {
        RetrieveUnitFragment fragment;

        // Create fragment and remove any units already in current tier (Probably going to have to revise)
        if (mActiveTreeFragment != null) {
            ArrayList<Unit> currentTierList = mActiveTreeFragment.getUnitTreeTier();
            fragment = RetrieveUnitFragment.newInstance(currentTierList, true);
        }
        else {
            fragment = RetrieveUnitFragment.newInstance(true);
        }

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.overlay_container, fragment, "FRAGMENT_LAUNCHED").commit();

        //launchFragment(fragment, R.id.overlay_container);
    }

    /**
     * Launches UnitTreeFragment which displays a particular tier of a Unit tree. However,
     * if working with the initial Unit master field, that is only root-units, then pass
     * null as the root unit.
     *
     * @param root Null if working with master field. Parent Unit if working with
     *                   unit tree.
     */
    private void displayTreeTier(Unit root) {
        mActiveTreeFragment = UnitTreeFragment.newInstance(root);
        launchFragment(mActiveTreeFragment, R.id.unit_tree_container);
    }

    private void stageUnit(Unit unit) {
        StageFragment fragment = StageFragment.newInstance(unit);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.staging_container, fragment, "FRAGMENT_LAUNCHED").commit();

        //launchFragment(fragment, R.id.staging_container);
    }

    private void launchFragment(Fragment fragment, int container) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(container, fragment, "FRAGMENT_LAUNCHED").commit();
    }

    @Override
    public void onBackPressed() {

    }

/*------------------------------------------------------------------------------------------------*/
//                                      Interaction Methods                                       //
/*------------------------------------------------------------------------------------------------*/
    @Override
    public void OnStageExit(Unit unit, int exitInstance) {
        // Depending on exitInstance, handle Unit in NumericalModule.
    }

    @Override
    public void onUnitTreeInteraction(List<Unit> currentTier, Unit unitBranch) {

    }

    @Override
    public void onFragmentInteraction(List<Unit> selectedUnits, int reason) {
        // Add directly to tree
        if(selectedUnits.size() > 1) {
            mActiveTreeFragment.appendToTier(selectedUnits);
        }

        // Add to tree and stage
        else if(selectedUnits.size() == 1) {
            Unit selectedUnit = selectedUnits.get(0);
            mActiveTreeFragment.appendToTier(selectedUnit);
            stageUnit(selectedUnit);
        }
    }
}
