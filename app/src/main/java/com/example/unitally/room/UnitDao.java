/**
 *  Data Access Objects are the main classes where you define your database interactions.
 *  They can include a variety of query methods. The class marked with @Dao should either
 *  be an interface or an abstract class. At compile time, Room will generate an implementation
 *  of this class when it is referenced by a Database. An abstract @Dao class can optionally
 *  have a constructor that takes a Database as its only parameter.
 *
 * It is recommended to have multiple Dao classes in your codebase depending on the tables they touch.
 */

package com.example.unitally.room;

import com.example.unitally.objects.Unit;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
interface UnitDao {

    // Inserts a Unit object into table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Unit unit);

    // Deletes a specified Unit Object
    @Delete
    void delete(Unit unit);

    // Returns LiveDate<List<Unit>> array of all units
    @Query("SELECT * from unit_table ORDER BY name ASC")
    LiveData<List<Unit>> getAllUnits();

    // Returns "unit name" column
    @Query("SELECT name from unit_table")
    LiveData<List<String>> getNames();

    // Wipes all Unit Objects
    @Query("Delete from unit_table")
    void deleteAll();
}
