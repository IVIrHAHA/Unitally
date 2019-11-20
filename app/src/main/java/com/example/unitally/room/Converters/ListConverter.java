package com.example.unitally.room.Converters;

import com.example.unitally.objects.Unit;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.room.TypeConverter;

public class ListConverter {

    @TypeConverter
    public static ArrayList<Unit> fromString(String data) {
        Type listType = new TypeToken<ArrayList<Unit>>(){}.getType();
        return new Gson().fromJson(data,listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<Unit> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

}
