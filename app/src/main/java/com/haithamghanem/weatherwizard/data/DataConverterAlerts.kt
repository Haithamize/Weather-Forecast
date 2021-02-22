package com.haithamghanem.weatherwizard.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.haithamghanem.weatherwizard.data.model.detailedweather_entry.Alerts


class DataConverterAlerts {
    @TypeConverter
    fun fromWeatherList(value: List<Alerts>?): String {
        val gson = Gson()
        val type = object : TypeToken<List<Alerts>?>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toWeatherList(value: String): List<Alerts>? {
        val gson = Gson()
        val type = object : TypeToken<List<Alerts>?>() {}.type
        return gson.fromJson(value, type)
    }
}