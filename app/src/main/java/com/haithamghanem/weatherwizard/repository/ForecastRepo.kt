//package com.haithamghanem.weatherwizard.repository
//
//import android.content.Context
//import android.util.Log
//import androidx.lifecycle.MutableLiveData
//import com.haithamghanem.weatherwizard.constants_and_utilities.ConstantVals
//import com.haithamghanem.weatherwizard.data.local.FavoritePlaceEntity
//import com.haithamghanem.weatherwizard.data.local.ForecastDatabase
//import com.haithamghanem.weatherwizard.data.model.DataSettings
//import com.haithamghanem.weatherwizard.data.model.PlaceReponseModel
//import com.haithamghanem.weatherwizard.data.model.currentweather_entry.CurrentWeatherResponse
//import com.haithamghanem.weatherwizard.data.model.detailedweather_entry.FullDetailsResponse
//
//import com.haithamghanem.weatherwizard.data.remote.RetrofitClient
//import com.haithamghanem.weatherwizard.ui.MainActivity
//import com.haithamghanem.weatherwizard.ui.weather.current.CurrentWeatherFragment
//
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//const val access_key = "8adccb35f7b5f432bb6fa55e44c1bcb5"
//
//
//class ForecastRepo(context: Context) {
//
//    val db = ForecastDatabase.getDatabase(context)
//
//    private var dataSettings: DataSettings = DataSettings
//    private var mutableLiveDataFromPlaceResponse: MutableLiveData<PlaceReponseModel> =
//        MutableLiveData<PlaceReponseModel>()
//    lateinit var mutableLiveDataOfCurrentWeather: MutableLiveData<FullDetailsResponse>
//
//    lateinit var mutableLiveDataOfFavoriteWeather: MutableLiveData<FavoritePlaceEntity>
//
//    init {
//        mutableLiveDataOfCurrentWeather = MutableLiveData<FullDetailsResponse>()
//
//        mutableLiveDataOfFavoriteWeather = MutableLiveData<FavoritePlaceEntity>()
//    }
//
//
//    fun getCurrentPlace(): MutableLiveData<PlaceReponseModel>{
//        return mutableLiveDataFromPlaceResponse
//    }
//
//    fun getPlaceFromPlaceDataResponseApi(city: String){
//        GlobalScope.launch {
//            Dispatchers.IO
//            try {
//
//                val response = RetrofitClient.getPlaceApiServices(city)
//                    .execute()
//                withContext(Dispatchers.Main)
//                {
//                    if (response.isSuccessful) {
//                        Log.d("tag", "loadSettings: ${DataSettings.customLocations}")
//                        mutableLiveDataFromPlaceResponse.value = response.body()
//                    }
//                }
//            } catch (e: Exception) {
//                Log.i("PlaceDataApi", "getPlaceFromPlaceDataResponseApi: " + e.message)
//            }
//        }
//
//    }
//
//
//
//    fun getWeatherData(): MutableLiveData<FullDetailsResponse>{
//        return mutableLiveDataOfCurrentWeather
//    }
//
//    fun getCurrentWeather(lat: Double, long: Double, unit: String, language: String) {
//        GlobalScope.launch(Dispatchers.IO) {
//
////            if (CurrentWeatherFragment.useDeviceLocationSwitch == true)
//            Log.d("tag", "loadSettings: ${DataSettings.unitSystem}")
//
//
//
//            //for some reason lma b3ml sleep sanya by2ra el text eli bktpo fl custom location abl my3ml fetch lel data w lw m3mltsh kda msh byl72 y7ot el data eli etktp fl data settings object
////            Thread.sleep(750)
//
//
//            val response = RetrofitClient.getWeatherService(lat , long , unit , language)
//                .execute()
//
//            withContext(Dispatchers.Main) {
//
//                if (response.isSuccessful) {
//                    mutableLiveDataOfCurrentWeather.value = response.body()
//                    //insert the data in the data base from here
//                } else {
//                    Log.d("TAG", "getCurrentWeather: ${response.body()}")
//                    //TODO el data mgtsh ml server aw mfish net eb2a hanndle el 7eta de w 7ot el cached 3ala el screen lw fe cache
//                }
//            }
//        }
//
//
//    }
//
////    fun getFullDetailedWeather(): MutableLiveData<FullDetailsResponse> {
////
//////        Thread.sleep(750)
////
////        GlobalScope.launch(Dispatchers.IO) {
////
////            val response = RetrofitClient.getWeatherService()
////                .getDetailedWeatherData(
////                    dataSettings.latitude,
////                    dataSettings.longitude,
////                    dataSettings.unitSystem,
////                    dataSettings.languageSystem,
////                    ConstantVals.EXCLUDE,
////                    ConstantVals.API_KEY
////                )
////                .execute()
////
////            withContext(Dispatchers.Main) {
////
////                if (response.isSuccessful) {
////                    mutableLiveDataOfFullDetailedWeather.value = response.body()
////                } else {
////                    Log.d("TAG", "getCurrentWeather: ${response.body()}")
////                    //TODO el data mgtsh ml server aw mfish net eb2a hanndle el 7eta de w 7ot el cached 3ala el screen lw fe cache
////                }
////            }
////        }
////
////        return mutableLiveDataOfFullDetailedWeather
////    }
//
//
////    fun getFavoriteWeather(): MutableLiveData<FavoritePlaceEntity> {
////
////
////        GlobalScope.launch(Dispatchers.IO) {
////
////            val response = RetrofitClient.getWeatherService()
////                .getFavoriteWeatherData(
////                    dataSettings.favoriteLatitude,
////                    dataSettings.favoriteLongitude,
////                    dataSettings.unitSystem,
////                    dataSettings.languageSystem,
////                    ConstantVals.EXCLUDE,
////                    ConstantVals.API_KEY
////                )
////                .execute()
////
////            withContext(Dispatchers.Main) {
////
////                if (response.isSuccessful) {
////                    mutableLiveDataOfFavoriteWeather.value = response.body()
////
////                    response.body()?.let { db.currentWeatherDao().insert(it) }
////                } else {
////                    Log.d("TAG", "getCurrentWeather: ${response.body()}")
////                    //TODO el data mgtsh ml server aw mfish net eb2a hanndle el 7eta de w 7ot el cached 3ala el screen lw fe cache
////                }
////            }
////        }
////
////        return mutableLiveDataOfFavoriteWeather
////    }
//}