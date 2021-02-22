package com.haithamghanem.weatherwizard.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.haithamghanem.weatherwizard.data.model.detailedweather_entry.Hourly

class DataConverterHourly {
    @TypeConverter
    fun fromWeatherList(value: List<Hourly>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Hourly>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toWeatherList(value: String): List<Hourly> {
        val gson = Gson()
        val type = object : TypeToken<List<Hourly>>() {}.type
        return gson.fromJson(value, type)
    }
}