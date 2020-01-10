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

    private static UnitallyDatabase INSTANCE;
    private final static String DB_NAME="unitally_db";

    // TEMP: Populate Units
    // TODO: Remove this function
    private static RoomDatabase.Callback sRoomCallback =
            new RoomDatabase.Callback() {

                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                }
            };
//------------------------------------------------------------------------------------------------//
/*                                           DAOs                                                 */
//------------------------------------------------------------------------------------------------//
    public abstract UnitDao unitDao();                                                              //TODO: Add Dao for Templates and projects

//------------------------------------------------------------------------------------------------//
/*                                     Database Creation                                          */
//------------------------------------------------------------------------------------------------//
    static UnitallyDatabase getDatabase(final Context context) {

        // Create Database if none exists
        if(INSTANCE == null) {
            synchronized (UnitallyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UnitallyDatabase.class, DB_NAME)
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomCallback)
                            .build();
                }
            }
        }

        return INSTANCE;
    }
}
