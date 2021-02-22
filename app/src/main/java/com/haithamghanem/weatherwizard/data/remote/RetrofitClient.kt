package com.haithamghanem.weatherwizard.data.remote

import com.haithamghanem.weatherwizard.constants_and_utilities.ConstantVals
import com.haithamghanem.weatherwizard.data.model.detailedweather_entry.FullDetailsResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory




object RetrofitClient {

    private val BASE_URL = "https://api.openweathermap.org/data/2.5/"


       val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)


    fun getWeatherService(lat: Double, long: Double, unit: String, language: String): Call<FullDetailsResponse> {
        return retrofit.getDetailedWeatherData(lat = lat,lon = long, unitName = unit , languageCode = language,ConstantVals.EXCLUDE,ConstantVals.API_KEY)
    }


}