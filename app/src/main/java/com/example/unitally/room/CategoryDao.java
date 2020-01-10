package com.example.unitally.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.unitally.objects.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * from category_table ORDER BY name ASC")
    LiveData<List<Category>> getAll();

    @Query("SELECT name from category_table")
    LiveData<List<String>> getNames();
}
