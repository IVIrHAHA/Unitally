package com.example.unitally.room;

import android.app.Application;

import com.example.unitally.objects.Unit;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class UnitObjectViewModel extends AndroidViewModel {
    private UnitObjectRepo mRepo;
    private LiveData<List<Unit>> mUnitList;
    private LiveData<List<String>> mUnitNames;

    public UnitObjectViewModel(@NonNull Application application) {
        super(application);
        this.mRepo = new UnitObjectRepo(application);
        this.mUnitList = mRepo.getLiveDataList();
        this.mUnitNames = mRepo.getUnitNames();
    }

    public LiveData<List<Unit>> getAllUnits() {
        return mUnitList;
    }

    public LiveData<List<String>> getUnitNames() {
        return mUnitNames;
    }

    public void saveUnit(Unit unit) {
        mRepo.saveUnit(unit);
    }

    public void deleteUnit(Unit unit) {
        mRepo.deleteUnit(unit);
    }

    public void deleteAll() {
        mRepo.deleteAll();
    }
}
