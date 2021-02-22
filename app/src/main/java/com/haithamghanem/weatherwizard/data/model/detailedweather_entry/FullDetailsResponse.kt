package com.haithamghanem.weatherwizard.data.model.detailedweather_entry


import androidx.annotation.Nullable
import androidx.room.*
import com.google.gson.annotations.SerializedName
import com.haithamghanem.weatherwizard.data.DataConverterAlerts
import com.haithamghanem.weatherwizard.data.DataConverterDaily
import com.haithamghanem.weatherwizard.data.DataConverterHourly


const val CURRENT_WEATHER_ID = 0

@Entity(tableName = "current_weather")
data class FullDetailsResponse(

    @SerializedName("lat")
    val lat: Double,

    @SerializedName("lon")
    val lon: Double,

    @SerializedName("timezone")
    val timezone: String,

    @SerializedName("timezone_offset")
    val timezoneOffset: Int,

    @TypeConverters(DataConverterHourly::class)
    @SerializedName("hourly")
    val hourly: List<Hourly>,

    @TypeConverters(DataConverterDaily::class)
    @SerializedName("daily")
    val daily: List<Daily>,

    @Nullable
    @TypeConverters(DataConverterAlerts::class)
    val alerts: List<Alerts>?,

    @Embedded(prefix = "current_")
    val current : Current

){
    @PrimaryKey(autoGenerate = false) //hna msh ha3ml true 3shan msh tabi3i en yb2a 3ndna aktr mn currenWeatherResponse, ehna 3ayzen wahd bs fa kda 3ayz a3ml constant id lel entity bta3tna, w bkda lma negy ngepo ml database hn3rf hngeb a l2en mfish 8ero
    var currentWeatherId: Int = CURRENT_WEATHER_ID
}

