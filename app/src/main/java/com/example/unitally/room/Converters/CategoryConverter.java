package com.example.unitally.room.Converters;

import androidx.room.TypeConverter;
import com.example.unitally.objects.Category;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class CategoryConverter {

    @TypeConverter
    public static Category fromString(String data) {
        Type catType = new TypeToken<Category>(){}.getType();
        return new Gson().fromJson(data,catType);
    }

    @TypeConverter
    public static String fromCategory(Category category) {
        Gson gson = new Gson();
        return gson.toJson(category);
    }
}
