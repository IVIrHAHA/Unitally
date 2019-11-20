package com.example.unitally.objects;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

public abstract class UnitCounterObject implements Serializable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "obj_name")
    String objName;

    UnitCounterObject(String name) {
        objName=name;
    }

    public String getName() {
        return objName;
    }

    public void setName(String new_name) {
        objName=new_name;
    }

    public String serialNum() {
       return objName.replaceAll(" ","_");
    }
}
