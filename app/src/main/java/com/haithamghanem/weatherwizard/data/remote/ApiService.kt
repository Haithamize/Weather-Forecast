package com.haithamghanem.weatherwizard.data.remote

import com.haithamghanem.weatherwizard.data.local.FavoritePlaceEntity
import com.haithamghanem.weatherwizard.data.model.detailedweather_entry.FullDetailsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    @GET("onecall")
    fun getDetailedWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") unitName: String,
        @Query("lang")languageCode: String="en",
        @Query("exclude") exclude: String,
        @Query("appid") apiKey: String
    ): Call<FullDetailsResponse>

    @GET("onecall")
    fun getFavoriteWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") unitName: String,
        @Query("lang")languageCode: String="en",
        @Query("exclude") exclude: String,
        @Query("appid") apiKey: String
    ): Call<FavoritePlaceEntity>
}