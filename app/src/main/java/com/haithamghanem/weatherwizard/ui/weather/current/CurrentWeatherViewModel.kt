package com.haithamghanem.weatherwizard.ui.weather.current

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.haithamghanem.weatherwizard.data.local.FavoritePlaceEntity
import com.haithamghanem.weatherwizard.data.local.ForecastDatabase
import com.haithamghanem.weatherwizard.data.model.DataSettings
import com.haithamghanem.weatherwizard.data.model.detailedweather_entry.FullDetailsResponse
import com.haithamghanem.weatherwizard.data.network.Connectivity
import com.haithamghanem.weatherwizard.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CurrentWeatherViewModel(application: Application) : AndroidViewModel(application) {

    val db = ForecastDatabase.getDatabase(application)


    private var dataSettings: DataSettings = DataSettings


    var mutableLiveDataOfCurrentWeather: MutableLiveData<FullDetailsResponse> = MutableLiveData<FullDetailsResponse>()




    fun getCurrentWeather(): MutableLiveData<FullDetailsResponse> {
        return mutableLiveDataOfCurrentWeather
    }



    fun getCurrentWeatherData(lat: Double, long: Double) {

            GlobalScope.launch(Dispatchers.IO) {

                Log.d("tag", "loadSettings: ${DataSettings.unitSystem}")

                //for some reason lma b3ml sleep sanya by2ra el text eli bktpo fl custom location abl my3ml fetch lel data w lw m3mltsh kda msh byl72 y7ot el data eli etktp fl data settings object
//            Thread.sleep(750)

                val response = RetrofitClient.getWeatherService(
                    lat,
                    long,
                    dataSettings.unitSystem,
                    dataSettings.languageSystem
                )
                    .execute()

                withContext(Dispatchers.Main) {

                    if (response.isSuccessful) {
                        mutableLiveDataOfCurrentWeather.value = response.body()

                        //insert the data in the data base from here
                        withContext(Dispatchers.IO) {
                            response.body()?.let { db.currentWeatherDao().upsert(it) }
                        }
                    } else {
                        Toast.makeText(getApplication(), "Failed to get the data from Api, please refresh the page" , Toast.LENGTH_SHORT).show()
                    }
                }
            }

    }

    fun getLocalWeatherData(): LiveData<FullDetailsResponse> {
        return db.currentWeatherDao().getStoredWeatherData()
    }
}