package com.haithamghanem.weatherwizard.ui.weather.features.favorites

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.haithamghanem.weatherwizard.R
import com.haithamghanem.weatherwizard.constants_and_utilities.TimeFormatChanger
import com.haithamghanem.weatherwizard.data.model.DataSettings
import com.haithamghanem.weatherwizard.data.model.detailedweather_entry.Daily
import com.haithamghanem.weatherwizard.data.model.detailedweather_entry.Hourly
import com.haithamghanem.weatherwizard.ui.weather.current.CurrentWeatherFragment
import com.haithamghanem.weatherwizard.ui.weather.current.DailyAdapter
import com.haithamghanem.weatherwizard.ui.weather.current.HourlyAdapter
import kotlinx.android.synthetic.main.current_weather_fragment.*
import kotlinx.android.synthetic.main.current_weather_fragment.rcDailyWeatherList
import kotlinx.android.synthetic.main.current_weather_fragment.rcHourlyWeatherList
import kotlinx.android.synthetic.main.fragment_favorite_details.*
import java.util.*
import kotlin.collections.ArrayList


class FavoriteDetailsFragment : Fragment() {
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter
    val dataSettings = DataSettings

    private var city: String = ""

    private lateinit var arrayListOfHourlyResponse: List<Hourly>
    private lateinit var arrayListOfDailyResponse: List<Daily>

    val prefrences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(this.context)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loadSettings()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_details, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponents()
        val favoritePlaceViewModel = ViewModelProvider(this).get(FavoritePlaceViewModel::class.java)
        favoritePlaceViewModel.getFavoriteDetailsData().observe(viewLifecycleOwner, Observer {
            if (it == null) {
                return@Observer
            }

            Log.d("FavoAdapterDetails", "${it.lat} // ${it.lon}")

            arrayListOfHourlyResponse = it.hourly
            hourlyAdapter.submitResponse(arrayListOfHourlyResponse)

            //setting the daily adapter
            arrayListOfDailyResponse = it.daily
            dailyAdapter.submitResponse(arrayListOfDailyResponse)


            //getting the location data

            // Log.d("TAG", "${it.wind?.speed}")
            loadingFavProgressBar.visibility = View.GONE
//            updateLocationOnToolbar(it.timezone)
//            updateTheDateSubTitle()

            if (prefrences.getString("LANGUAGE_SYSTEM", "") == "en") {
                val txtLastUpdatedVal = TimeFormatChanger.convertTime(it.current.dt.toLong())
                txtFavLastUpdated.text = "Last updated $txtLastUpdatedVal"
            } else {
                val txtLastUpdatedVal = TimeFormatChanger.convertTime(it.current.dt.toLong())
                txtFavLastUpdated.text = " اّخر مرة للتحديث في $txtLastUpdatedVal"
            }

            txtFavPressure.text = it.current.pressure.toString() +" "+getString(R.string.pressureUnit)
            txtFavHumidity.text = it.current.humidity.toString() + "%"


            if (prefrences.getString("UNIT_SYSTEM", "") == "metric") {
                txtFavTemperature.text = it.current.temp.toString() + "\u00B0" + "C"
                if (prefrences.getString("LANGUAGE_SYSTEM", "") == "en") {
                    txtFavWindSpeed.text = it.current.wind_speed.toString() + " metre/s"
                    feelsLikeFavTemp.text =
                        "Feels Like " + it.current.feels_like.toString() + "\u00B0" + "C"
                } else {
                    txtFavWindSpeed.text = it.current.wind_speed.toString() + " متر/الثانية"
                    feelsLikeFavTemp.text =
                        it.current.feels_like.toString() + "\u00B0" + "C" + " درجة الحرارة كأنها"
                }

            } else if (prefrences.getString("UNIT_SYSTEM", "") == "imperial") {
                txtFavTemperature.text = it.current.temp.toString() + "\u00B0" + "F"
                if (prefrences.getString("LANGUAGE_SYSTEM", "") == "en") {
                    txtFavWindSpeed.text = it.current.wind_speed.toString() + " mile/h"
                    feelsLikeFavTemp.text =
                        "Feels Like " + it.current.feels_like.toString() + "\u00B0" + "F"
                } else {
                    txtFavWindSpeed.text = it.current.wind_speed.toString() + " ميل/الساعة"
                    feelsLikeFavTemp.text =
                        it.current.feels_like.toString() + "\u00B0" + "F" + " درجة الحرارة كأنها"
                }
            } else if (prefrences.getString("UNIT_SYSTEM", "") == "standard") {
                txtFavTemperature.text = it.current.temp.toString() + "K"
                if (prefrences.getString("LANGUAGE_SYSTEM", "") == "en") {
                    txtFavWindSpeed.text = it.current.wind_speed.toString() + " metre/s"
                    feelsLikeFavTemp.text = "Feels Like " + it.current.feels_like.toString() + "K"
                } else {
                    txtFavWindSpeed.text = it.current.wind_speed.toString() + " متر/الثانية"
                    feelsLikeFavTemp.text =
                        it.current.feels_like.toString() + "K" + " درجة الحرارة كأنها"
                }
            }
            txtFavWeatherDescription.text = it.current.weather[0].description

            val gcd = Geocoder(context, Locale.getDefault())
            val addresses: List<Address> = gcd.getFromLocation(
                it.lat,
                it.lon,
                1
            )
            if (addresses.size > 0) {
                city = (addresses[0].getAddressLine(0))
            }


            txtFavLocation.text = city
//            Log.d("TAG", "${it.weather?.get(0)?.icon}")
            val imageURL = "http://openweathermap.org/img/w/${it.current.weather[0].icon}.png"

            Glide.with(this).load(imageURL).into(currentFavWeatherIcon)

        })


    }

    private fun initComponents() {
        val horizontalLinearLayoutManager =
            LinearLayoutManager(this.requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rcHourlyFavWeatherList.layoutManager = horizontalLinearLayoutManager
        rcHourlyFavWeatherList.addItemDecoration(
            DividerItemDecoration(
                this.requireContext(),
                horizontalLinearLayoutManager.orientation
            )
        )
        hourlyAdapter = HourlyAdapter(this.requireContext())
        rcHourlyFavWeatherList.adapter = hourlyAdapter

        val verticalLinearLayoutManager =
            LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)
        rcDailyFavWeatherList.layoutManager = verticalLinearLayoutManager
        rcDailyFavWeatherList.addItemDecoration(
            DividerItemDecoration(
                this.requireContext(),
                verticalLinearLayoutManager.orientation
            )
        )
        dailyAdapter = DailyAdapter(this.requireContext())
        rcDailyFavWeatherList.adapter = dailyAdapter


        arrayListOfDailyResponse = ArrayList()
        arrayListOfHourlyResponse = ArrayList()

        val sp = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        CurrentWeatherFragment.unit = sp.getString("UNIT_SYSTEM", "metric ")
    }

    private fun loadSettings() {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val unit_system = sp.getString("UNIT_SYSTEM", "")
        val language_system = sp.getString("LANGUAGE_SYSTEM", "")
        val deviceLocation = sp.getBoolean("USE_DEVICE_LOCATION", false)
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