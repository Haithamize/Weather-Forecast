package com.haithamghanem.weatherwizard.ui.weather.features.favorites

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.haithamghanem.weatherwizard.data.local.FavoritePlaceEntity
import com.haithamghanem.weatherwizard.data.local.ForecastDatabase
import com.haithamghanem.weatherwizard.data.model.DataSettings
import com.haithamghanem.weatherwizard.data.model.detailedweather_entry.FullDetailsResponse
import com.haithamghanem.weatherwizard.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FavoritePlaceViewModel(application: Application) : AndroidViewModel(application) {

    companion object{
        var mutableLiveDataOfFavoriteObjects: MutableLiveData<FullDetailsResponse> = MutableLiveData<FullDetailsResponse>()
        var mutableLiveDataOfFavoriteDetailsWeather: MutableLiveData<FullDetailsResponse> = MutableLiveData<FullDetailsResponse>()
    }

    private val db = ForecastDatabase.getDatabase(application)

    private var dataSettings: DataSettings = DataSettings



    fun getFavoritePlace(): MutableLiveData<FullDetailsResponse> {
        return mutableLiveDataOfFavoriteObjects
    }

    fun getFavoritePlaceDataFromApi(lat: Double, long: Double) {
        GlobalScope.launch(Dispatchers.IO) {

            val response = RetrofitClient.getWeatherService(lat , long , dataSettings.unitSystem , dataSettings.languageSystem)
                .execute()

            withContext(Dispatchers.Main) {

                if (response.isSuccessful) {
                    mutableLiveDataOfFavoriteObjects.value = response.body()
                    //insert the data in the data base from here

                } else {
                    Log.d("TAG", "getCurrentWeather: ${response.body()}")
                    //TODO el data mgtsh ml server aw mfish net eb2a hanndle el 7eta de w 7ot el cached 3ala el screen lw fe cache
                }
            }
        }
    }


    fun insertFavoritePlace(favoritePlaceEntity: FavoritePlaceEntity){
        GlobalScope.launch(Dispatchers.IO){
            db.currentWeatherDao().insert(favoritePlaceEntity)
        }
    }


    fun getLocalDataSource(): LiveData<List<FavoritePlaceEntity>>{
        return db.currentWeatherDao().getAllFavoritePlaces()
    }


    fun getFavoriteDetailsData(): MutableLiveData<FullDetailsResponse> {
        return mutableLiveDataOfFavoriteDetailsWeather
    }



    fun setFavoriteDetailsData(lat: Double, long: Double) {

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
                    mutableLiveDataOfFavoriteDetailsWeather.value = response.body()

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
}