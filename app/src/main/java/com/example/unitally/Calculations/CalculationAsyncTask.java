/**
 *  Calculates the current Active Count
 */

package com.example.unitally.Calculations;

import android.os.AsyncTask;
import android.util.Log;

import com.example.unitally.objects.Unit;
import java.util.Hashtable;
import java.util.List;

// Parameters, Progress, Results
class CalculationAsyncTask extends AsyncTask<List<Unit>, Integer, Hashtable<Unit,Integer>> {

    private Hashtable<Unit, Integer> mTotals;
    private CalculationAdapter mAdapter;

    public CalculationAsyncTask(CalculationAdapter adapter) {
        this.mTotals = new Hashtable<>();
        this.mAdapter = adapter;
    }

    // Calls the Unit's internal calculate method
    // Combine with equal units.
    private void calculate(Unit unit) {
        unit.calculate();
        combine(unit.getTotal());   // Passing all subunits and head unit
    }

    private void combine(List<Unit> newList) {
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

    @SafeVarargs
    @Override
    protected final Hashtable<Unit, Integer> doInBackground(List<Unit>... parents) {
        List<Unit> parentList = parents[0];

        Log.d("async", "Size: " + parents.length);
        
        for(Unit unit : parentList) {
            calculate(unit);
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

        for(Unit key : resultTable.keySet()) {
            Integer count = resultTable.get(key);
            if(count != null) {
                key.setCount(count);
                mAdapter.add(key);
            }
        }
    }
}
