/**
 *  Calculates the current Active Count
 */

package com.example.unitally.app_modules.unit_tree_module;

import android.os.AsyncTask;

import com.example.unitally.objects.Unit;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

// Parameters, Progress, Results
public class Calculator extends AsyncTask<Unit, Integer, Hashtable<Unit,Double>> {

    private Hashtable<Unit, Double> mTotals;
    private CalculationListener mListener;

    private Unit mHeadUnit;

    public Calculator(CalculationListener listener) {
        mTotals = new Hashtable<>();
        mListener = listener;
        mHeadUnit = null;
    }

    // Calls the Unit's internal calculate method
    // Combine with equal units.
    private void calculate(Unit unit) {
        combine(unit.getTotal());   // Passing all subunits and head unit
    }

    private void combine(ArrayList<Unit> newList) {
        for(Unit unit:newList) {
            // Needed to override equals method of Unit Object
            double value = unit.getCount();

            if(mTotals.containsKey(unit)) {
                Double total = mTotals.get(unit);
                if(total != null) {
                    total += value;
                    mTotals.put(unit,total);
                }
            }
            else {
                mTotals.put(unit, value);
            }
        }
    }

    @Override
    protected final Hashtable<Unit, Double> doInBackground(Unit... unit) {
        mHeadUnit = unit[0];
        mHeadUnit.calculate();
        List<Unit> parentList = unit[0].getSubunits();

        for(Unit subunit : parentList) {
            calculate(subunit);
        }
        return mTotals;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPostExecute(Hashtable<Unit, Double> resultTable) {
        super.onPostExecute(resultTable);

        ArrayList<Unit> list = new ArrayList<>();

        for(Unit key : resultTable.keySet()) {
            Double count = resultTable.get(key);
            if(count != null) {
                key.setCount(count);
                list.add(key);
            }
        }

        mListener.onCalculationFinished(list,mHeadUnit);
    }

    public interface CalculationListener{
        void onCalculationFinished(ArrayList<Unit> calculatedUnits, Unit head);
    }

}
