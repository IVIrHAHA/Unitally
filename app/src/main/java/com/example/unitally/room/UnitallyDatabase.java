package com.example.unitally.room;

import android.content.Context;

import com.example.unitally.objects.Unit;
import com.example.unitally.room.Converters.CategoryConverter;
import com.example.unitally.room.Converters.ListConverter;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Unit.class}, version = 1, exportSchema = false)
@TypeConverters({ListConverter.class, CategoryConverter.class})
abstract class UnitallyDatabase extends RoomDatabase {

    private static UnitallyDatabase mInstance;
    private final static String UNIT_DB ="com.example.unitally.UnitDatabse";
    private final static String CATEGORY_DB = "com.example.unitally.CategoryDatabase";

//------------------------------------------------------------------------------------------------//
/*                                           DAOs                                                 */
//------------------------------------------------------------------------------------------------//
    public abstract UnitDao unitDao();
    public abstract CategoryDao categoryDao();
//------------------------------------------------------------------------------------------------//
/*                                     Database Creation                                          */
//------------------------------------------------------------------------------------------------//
    static UnitallyDatabase getUnitDB(final Context context) {
        // Create Database if none exists
        if(mInstance == null) {
            synchronized (UnitallyDatabase.class) {
                if (mInstance == null) {
                    mInstance = Room.databaseBuilder(context.getApplicationContext(),
                            UnitallyDatabase.class, UNIT_DB)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }

        return mInstance;
    }

    static UnitallyDatabase getCategoryDB(final Context context) {
        // Create Database if none exists
        if(mInstance == null) {
            synchronized (UnitallyDatabase.class) {
                if (mInstance == null) {
                    mInstance = Room.databaseBuilder(context.getApplicationContext(),
                            UnitallyDatabase.class, CATEGORY_DB)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }

        return mInstance;
    }
}
