package com.haithamghanem.weatherwizard.ui.weather.current

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.haithamghanem.weatherwizard.R
import com.haithamghanem.weatherwizard.constants_and_utilities.ConstantVals
import com.haithamghanem.weatherwizard.constants_and_utilities.LocationProvider
import com.haithamghanem.weatherwizard.constants_and_utilities.TimeFormatChanger
import com.haithamghanem.weatherwizard.data.model.DataSettings
import com.haithamghanem.weatherwizard.data.model.detailedweather_entry.Daily
import com.haithamghanem.weatherwizard.data.model.detailedweather_entry.Hourly
import com.haithamghanem.weatherwizard.data.network.Connectivity
import com.haithamghanem.weatherwizard.ui.CUSTOM_LOCATION
import com.haithamghanem.weatherwizard.ui.USE_DEVICE_LOCATION
import com.haithamghanem.weatherwizard.ui.weather.features.alerts.PeriodiWorker
import com.haithamghanem.weatherwizard.ui.weather.features.alerts.UploadWorker
import kotlinx.android.synthetic.main.current_weather_fragment.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


const val LOCATION_REQUEST_CODE = 0
const val GPS_REQUEST_CODE = 2

class CurrentWeatherFragment : Fragment() {





    private lateinit var locationManager: LocationManager

    private val locationProvider = LocationProvider

    private var dataSettings: DataSettings = DataSettings

    private lateinit var city: String



    val prefrences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(this.context)


    //recyclerView Adapters of the Hourly and the Daily RecyclerView
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter

    lateinit var currentWeatherViewModel: CurrentWeatherViewModel

    //List of the incoming response
    private lateinit var arrayListOfHourlyResponse: List<Hourly>
    private lateinit var arrayListOfDailyResponse: List<Daily>

    companion object {
        fun newInstance() = CurrentWeatherFragment()

        var unit: String? = null
        private var isFirstTime: Boolean = true

    }

//    fun removeNotificationUpdates(){
//        if(prefrences.getBoolean("USE_NOTIFICATIONS_ALERT", false) == false){
//            Toast.makeText(this.requireContext(), "ana hena" , Toast.LENGTH_SHORT).show()
//            WorkManager.getInstance(this.requireContext()).cancelAllWorkByTag("periodicWorkRequest")
//        }
//    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        changeLanguage()
        loadSettings()
        return inflater.inflate(R.layout.current_weather_fragment, container, false)

    }

    private fun setLocale(langCode: String){
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val resources = resources
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    fun changeLanguage(){
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context)
        val language = sharedPreferences.getString("LANGUAGE_SYSTEM","")
        if (language != null) {
            setLocale(language)
        }
    }




    private fun turnOnGps() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == true) {
            return
        } else {
            AlertDialog.Builder(this.requireContext())
                .setMessage(R.string.gpsOff)
                .setPositiveButton(
                    R.string.ok
                ) { dialogInterface: DialogInterface?, i: Int ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
                .create()
                .show()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val connectivity = Connectivity





        currentWeatherViewModel = ViewModelProvider(this).get(CurrentWeatherViewModel::class.java)
        initComponents()


    if(connectivity.isOnline(this.requireContext())) {
        if (isUsingDeviceLocation()) {
            turnOnGps()
            locationProvider.initLocation(this.requireContext())
            locationProvider.getDeviceLocation()
            getCurrentWeatherData()
        } else {
            Log.d("customlocation", "${prefrences.getString(CUSTOM_LOCATION, "")} ")

            getCurrentWeatherData()
        }
    }else{
        Toast.makeText(this.requireContext(), R.string.offline, Toast.LENGTH_LONG).show()
        getStoredWeatherData()
    }



    }





    private fun initComponents() { //creating both the daily and the hourly rcViews


        locationManager =
            this.requireContext().getSystemService(LOCATION_SERVICE) as LocationManager


        val horizontalLinearLayoutManager =
            LinearLayoutManager(this.requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rcHourlyWeatherList.layoutManager = horizontalLinearLayoutManager
        rcHourlyWeatherList.addItemDecoration(
            DividerItemDecoration(
                this.requireContext(),
                horizontalLinearLayoutManager.orientation
            )
        )
        hourlyAdapter = HourlyAdapter(this.requireContext())
        rcHourlyWeatherList.adapter = hourlyAdapter

        val verticalLinearLayoutManager =
            LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)
        rcDailyWeatherList.layoutManager = verticalLinearLayoutManager
        rcDailyWeatherList.addItemDecoration(
            DividerItemDecoration(
                this.requireContext(),
                verticalLinearLayoutManager.orientation
            )
        )
        dailyAdapter = DailyAdapter(this.requireContext())
        rcDailyWeatherList.adapter = dailyAdapter

        //creating the ArrayLists which we will put the response in
        arrayListOfDailyResponse = ArrayList()
        arrayListOfHourlyResponse = ArrayList()

        val sp = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        unit = sp.getString("UNIT_SYSTEM", "metric ")

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationProvider.onRequestPermissionsResult(
            requestCode = requestCode,
            permissions = permissions,
            grantResults = grantResults
        )
    }


    @SuppressLint("SetTextI18n")
    private fun getCurrentWeatherData() {

        Log.d("latlongFromCurrent", "${dataSettings.latitude} , ${dataSettings.longitude} ")
        Handler(Looper.getMainLooper()).postDelayed({
            //Do something after 100ms
            currentWeatherViewModel.getCurrentWeatherData(dataSettings.latitude, dataSettings.longitude)
            currentWeatherViewModel.getCurrentWeather().observe(viewLifecycleOwner, Observer {
                if (it == null) {
                    return@Observer
                }
                arrayListOfHourlyResponse = it.hourly
                hourlyAdapter.submitResponse(arrayListOfHourlyResponse)

                //setting the daily adapter
                arrayListOfDailyResponse = it.daily
                dailyAdapter.submitResponse(arrayListOfDailyResponse)




                // Log.d("TAG", "${it.wind?.speed}")
                loadingProgressBar.visibility = View.GONE



                if (prefrences.getString("LANGUAGE_SYSTEM","") == "en") {
                    val txtLastUpdatedVal = TimeFormatChanger.convertTime(it.current.dt.toLong())
                    txtLastUpdated.text = "Last updated $txtLastUpdatedVal"
                }else{
                    val txtLastUpdatedVal = TimeFormatChanger.convertTime(it.current.dt.toLong())
                    txtLastUpdated.text = " اّخر مرة للتحديث في $txtLastUpdatedVal"
                }
                txtHumidity.text = it.current.humidity.toString() + "%"
                if (unit == "metric") {
                    txtTemperature.text = it.current.temp.toString() + "\u00B0"+"C"
                    if(prefrences.getString("LANGUAGE_SYSTEM","") == "en") {
                        txtWindSpeed.text = it.current.wind_speed.toString() + " metre/s"
                        feelsLikeTemp.text ="Feels Like "+it.current.feels_like.toString() + "\u00B0"+"C"
                    }else{
                        txtWindSpeed.text = it.current.wind_speed.toString() + " متر/الثانية"
                        feelsLikeTemp.text = it.current.feels_like.toString() + "\u00B0"+"C" +" درجة الحرارة كأنها"
                    }

                } else if (unit == "imperial") {
                    txtTemperature.text = it.current.temp.toString() + "\u00B0"+"F"
                    if(prefrences.getString("LANGUAGE_SYSTEM","") == "en") {
                        txtWindSpeed.text = it.current.wind_speed.toString() + " mile/h"
                        feelsLikeTemp.text = "Feels Like "+it.current.feels_like.toString() + "\u00B0"+"F"
                    }else{
                        txtWindSpeed.text = it.current.wind_speed.toString() + " ميل/الساعة"
                        feelsLikeTemp.text = it.current.feels_like.toString() + "\u00B0"+"F" +" درجة الحرارة كأنها"
                    }
                } else if (unit == "standard") {
                    txtTemperature.text = it.current.temp.toString() + "K"
                    if(prefrences.getString("LANGUAGE_SYSTEM","") == "en") {
                        txtWindSpeed.text = it.current.wind_speed.toString() + " metre/s"
                        feelsLikeTemp.text = "Feels Like "+it.current.feels_like.toString() +"K"
                    }else{
                        txtWindSpeed.text = it.current.wind_speed.toString() + " متر/الثانية"
                        feelsLikeTemp.text = it.current.feels_like.toString() +"K" +" درجة الحرارة كأنها"
                    }
                }
                txtPressure.text = it.current.pressure.toString() + " hPa"
                txtWeatherDescription.text = it.current.weather[0].description

                val gcd = Geocoder(context, Locale.getDefault())
                val addresses: List<Address> = gcd.getFromLocation(
                    it.lat,
                    it.lon,
                    1
                )
                if (addresses.size > 0) {
                    city = (addresses[0].getAddressLine(0))
                }

                txtLocation.text = city
//            Log.d("TAG", "${it.weather?.get(0)?.icon}")
                val imageURL = "http://openweathermap.org/img/w/${it.current.weather[0].icon}.png"

                Glide.with(this).load(imageURL).into(currentWeatherIcon)

            })
        }, 100) //3mlt delay 3shan kan el response eli gayeli ml gps bl lat wl long mt25r

    }



    @SuppressLint("SetTextI18n")
    private fun getStoredWeatherData() {


        currentWeatherViewModel.getLocalWeatherData().observe(viewLifecycleOwner, Observer {
            if (it == null) {
                return@Observer
            }
            arrayListOfHourlyResponse = it.hourly
            hourlyAdapter.submitResponse(arrayListOfHourlyResponse)

            //setting the daily adapter
            arrayListOfDailyResponse = it.daily
            dailyAdapter.submitResponse(arrayListOfDailyResponse)

            loadingProgressBar.visibility = View.GONE

            if (prefrences.getString("LANGUAGE_SYSTEM","") == "en") {
                val txtLastUpdatedVal = TimeFormatChanger.convertTime(it.current.dt.toLong())
                txtLastUpdated.text = "Last updated $txtLastUpdatedVal"
            }else{
                val txtLastUpdatedVal = TimeFormatChanger.convertTime(it.current.dt.toLong())
                txtLastUpdated.text = " اّخر مرة للتحديث في $txtLastUpdatedVal"
            }
            txtHumidity.text = it.current.humidity.toString() + "%"
            if (unit == "metric") {
                txtTemperature.text = it.current.temp.toString() + "\u00B0"+"C"
                if(prefrences.getString("LANGUAGE_SYSTEM","") == "en") {
                    txtWindSpeed.text = it.current.wind_speed.toString() + " metre/s"
                    feelsLikeTemp.text ="Feels Like "+it.current.feels_like.toString() + "\u00B0"+"C"
                }else{
                    txtWindSpeed.text = it.current.wind_speed.toString() + " متر/الثانية"
                    feelsLikeTemp.text = it.current.feels_like.toString() + "\u00B0"+"C" +" درجة الحرارة كأنها"
                }

            } else if (unit == "imperial") {
                txtTemperature.text = it.current.temp.toString() + "\u00B0"+"F"
                if(prefrences.getString("LANGUAGE_SYSTEM","") == "en") {
                    txtWindSpeed.text = it.current.wind_speed.toString() + " mile/h"
                    feelsLikeTemp.text = "Feels Like "+it.current.feels_like.toString() + "\u00B0"+"F"
                }else{
                    txtWindSpeed.text = it.current.wind_speed.toString() + " ميل/الساعة"
                    feelsLikeTemp.text = it.current.feels_like.toString() + "\u00B0"+"F" +" درجة الحرارة كأنها"
                }
            } else if (unit == "standard") {
                txtTemperature.text = it.current.temp.toString() + "K"
                if(prefrences.getString("LANGUAGE_SYSTEM","") == "en") {
                    txtWindSpeed.text = it.current.wind_speed.toString() + " metre/s"
                    feelsLikeTemp.text = "Feels Like "+it.current.feels_like.toString() +"K"
                }else{
                    txtWindSpeed.text = it.current.wind_speed.toString() + " متر/الثانية"
                    feelsLikeTemp.text = it.current.feels_like.toString() +"K" +" درجة الحرارة كأنها"
                }
            }
            txtPressure.text = it.current.pressure.toString() + " hPa"
            txtWeatherDescription.text = it.current.weather[0].description


            val gcd = Geocoder(context, Locale.getDefault())
            val addresses: List<Address> = gcd.getFromLocation(
                it.lat,
                it.lon,
                1
            )
            if (addresses.size > 0) {
                city = (addresses[0].getAddressLine(0))
            }

            txtLocation.text = city


//            Log.d("TAG", "${it.weather?.get(0)?.icon}")
            val imageURL = "http://openweathermap.org/img/w/${it.current.weather[0].icon}.png"

            Glide.with(this).load(imageURL).into(currentWeatherIcon)

        })
    }




//    private fun updateLocationOnToolbar(location: String) {
//        //3mlt cast lel app compat activity 3shan a3rf a call el supportaction bar 3shan a8yar el title bta3o
//        (activity as AppCompatActivity).supportActionBar?.title = location
////        (activity as AppCompatActivity).supportActionBar?.
//
//    }
//
//    private fun updateTheDateSubTitle() {
//        (activity as AppCompatActivity).supportActionBar?.subtitle = "Today"
//    }

    private fun isUsingDeviceLocation(): Boolean { //returns the value of the switchkey in the prefrences xml
        return prefrences.getBoolean(USE_DEVICE_LOCATION, true)
    }




    private fun loadSettings() {
        val sp = PreferenceManager.getDefaultSharedPreferences(context?.applicationContext)
        val unit_system = sp.getString("UNIT_SYSTEM", "metric")
        val language_system = sp.getString("LANGUAGE_SYSTEM", "en")
        val deviceLocation = sp.getBoolean("USE_DEVICE_LOCATION", true)
        val notifications = sp.getBoolean("USE_NOTIFICATIONS_ALERT", false)
        val customLocations = sp.getString("CUSTOM_LOCATION", "")

        if (unit_system != null) {
            dataSettings.unitSystem = unit_system
        }
        if (language_system != null) {
            dataSettings.languageSystem = language_system
        }
        if (deviceLocation != null) {
            dataSettings.deviceLocation = deviceLocation
        }
        if (notifications != null) {
            dataSettings.notifications = notifications
        }


        if (customLocations != null) {
            dataSettings.customLocations = customLocations
        }

    }


}

