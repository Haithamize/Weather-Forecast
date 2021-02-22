package com.haithamghanem.weatherwizard.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.haithamghanem.weatherwizard.data.model.detailedweather_entry.Current
import com.haithamghanem.weatherwizard.data.model.detailedweather_entry.Daily

class DataConverterDaily {
    @TypeConverter
    fun fromWeatherList(value: List<Daily>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Daily>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toWeatherList(value: String): List<Daily> {
        val gson = Gson()
        val type = object : TypeToken<List<Daily>>() {}.type
        return gson.fromJson(value, type)
    }
}