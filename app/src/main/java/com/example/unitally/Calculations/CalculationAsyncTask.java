/**
 *  Calculates the current Active Count
 */

package com.example.unitally.Calculations;

import android.os.AsyncTask;
import com.example.unitally.objects.Unit;
import java.util.Hashtable;
import java.util.List;

// Parameters, Progress, Results
class CalculationAsyncTask extends AsyncTask<List<Unit>, Integer, Hashtable<ResultsUnitWrapper,Integer>> {

    private Hashtable<ResultsUnitWrapper, Integer> mTotals;
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
            ResultsUnitWrapper wrapper = new ResultsUnitWrapper(unit);
            int value = unit.getCount();

            if(mTotals.containsKey(wrapper)) {
                Integer total = mTotals.get(wrapper);
                if(total != null) {
                    total += value;
                    mTotals.put(wrapper,total);
                }
            }
            else {
                mTotals.put(wrapper, value);
            }
        }
    }

    @SafeVarargs
    @Override
    protected final Hashtable<ResultsUnitWrapper, Integer> doInBackground(List<Unit>... parents) {
        List<Unit> parentList = parents[0];

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
    protected void onPostExecute(Hashtable<ResultsUnitWrapper, Integer> resultTable) {
        super.onPostExecute(resultTable);

        for(ResultsUnitWrapper key : resultTable.keySet()) {
            Integer count = resultTable.get(key);
            if(count != null) {
                key.setCount(count);
                mAdapter.add(key);
            }
        }
    }
}
