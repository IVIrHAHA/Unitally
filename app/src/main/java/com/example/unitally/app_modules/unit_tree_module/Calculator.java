/**
 *  Calculates the current Active Count
 */

package com.example.unitally.app_modules.unit_tree_module;

import android.os.AsyncTask;
import android.util.Log;

import com.example.unitally.objects.Unit;
import com.example.unitally.tools.UnitallyValues;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

// Parameters, Progress, Results
public class Calculator extends AsyncTask<Unit, Integer, Hashtable<Unit,Integer>> {

    private Hashtable<Unit, Integer> mTotals;
    private CalculationListener mListener;

    public Calculator(CalculationListener listener) {
        mTotals = new Hashtable<>();
        mListener = listener;
    }

    // Calls the Unit's internal calculate method
    // Combine with equal units.
    private void calculate(Unit unit) {
        combine(unit.getTotal());   // Passing all subunits and head unit
    }

    private void combine(ArrayList<Unit> newList) {
        for(Unit unit:newList) {
            // Needed to override equals method of Unit Object
            int value = unit.getCount();

            if(mTotals.containsKey(unit)) {
                Integer total = mTotals.get(unit);
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
    protected final Hashtable<Unit, Integer> doInBackground(Unit... unit) {
        unit[0].calculate();
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
    protected void onPostExecute(Hashtable<Unit, Integer> resultTable) {
        super.onPostExecute(resultTable);

        ArrayList<Unit> list = new ArrayList<>();

        for(Unit key : resultTable.keySet()) {
            Integer count = resultTable.get(key);
            if(count != null) {
                key.setCount(count);
                list.add(key);
            }
        }

        mListener.onCalculationFinished(list);
    }

    public interface CalculationListener{
        void onCalculationFinished(ArrayList<Unit> calculatedUnits);
    }

}
