package com.productfinder.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProductsTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun imagesToString(foodRecipe: List<String>): String {
        return gson.toJson(foodRecipe)
    }

    @TypeConverter
    fun stringToFoodImages(data: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(data, listType)
    }

}