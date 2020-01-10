package com.example.unitally.room;

import android.app.Application;
import android.os.AsyncTask;

import com.example.unitally.objects.Unit;

import java.util.List;

import androidx.lifecycle.LiveData;

public class UnitObjectRepo {
    // Needed to access Unit table
    private UnitDao mUnitDao;

    // Get all saved Units
    private LiveData<List<Unit>> mUnitList;
    private LiveData<List<String>> mUnitNames;

    public UnitObjectRepo(Application app) {
        UnitallyDatabase db = UnitallyDatabase.getUnitDB(app);
        this.mUnitDao = db.unitDao();
        this.mUnitList = mUnitDao.getAllUnits();
        this.mUnitNames = mUnitDao.getNames();
    }

//------------------------------------------------------------------------------------------------//
/*                      Controls for storing/receiving "Saved" Units                              */
//------------------------------------------------------------------------------------------------//
    public LiveData<List<Unit>> getUnitList() {
        return mUnitList;
    }

    public LiveData<List<String>> getUnitNames() {
        return mUnitNames;
    }

    public void saveUnit(Unit unit) {
        new InsertUnitAsync(mUnitDao).execute(unit);
    }

    public void deleteUnit(Unit unit) {
        new DeleteUnitAsync(mUnitDao).execute(unit);
    }

    public void deleteAll() {
        new DeleteAllUnitsAsync(mUnitDao).execute();
    }

//------------------------------------------------------------------------------------------------//
/*                                      Async Tasks                                               */
//------------------------------------------------------------------------------------------------//
// Save Single Unit
    private static class InsertUnitAsync extends AsyncTask<Unit, Void, Void> {
        private UnitDao mUnitDao;

        InsertUnitAsync(UnitDao dao) {
            mUnitDao = dao;
        }

        @Override
        protected Void doInBackground(Unit... units) {
            mUnitDao.insert(units[0]);
            return null;
        }
    }

    // Delete Single Unit
    private static class DeleteUnitAsync extends AsyncTask<Unit, Void, Void> {
        private UnitDao mUnitDao;

        DeleteUnitAsync(UnitDao mUnitDao) {
            this.mUnitDao = mUnitDao;
        }

        @Override
        protected Void doInBackground(Unit... units) {
            mUnitDao.delete(units[0]);
            return null;
        }
    }

    // Delete All Units
    private static class DeleteAllUnitsAsync extends AsyncTask<Unit, Void, Void> {
        private UnitDao mUnitDao;

        DeleteAllUnitsAsync(UnitDao mUnitDao) {
            this.mUnitDao = mUnitDao;
        }

        @Override
        protected Void doInBackground(Unit... units) {
            mUnitDao.deleteAll();
            return null;
        }
    }
}
