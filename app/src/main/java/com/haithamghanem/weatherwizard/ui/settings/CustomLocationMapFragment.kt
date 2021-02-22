package com.haithamghanem.weatherwizard.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.haithamghanem.weatherwizard.R
import com.haithamghanem.weatherwizard.data.model.DataSettings
import com.haithamghanem.weatherwizard.ui.weather.current.CurrentWeatherViewModel
import kotlinx.android.synthetic.main.favorite_fragment_map.*
import kotlinx.android.synthetic.main.fragment_custom_location_map.*
import java.util.*


class CustomLocationMapFragment : Fragment() {

    val dataSettings= DataSettings
    var markerLatitude: Double = 0.0
    var markerLongitude: Double = 0.0
    lateinit var currentWeatherViewModel: CurrentWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_custom_location_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentWeatherViewModel = ViewModelProvider(this).get(CurrentWeatherViewModel::class.java)

        goToCustomLocation.setOnClickListener {
            dataSettings.latitude = markerLatitude
            dataSettings.longitude = markerLongitude

            currentWeatherViewModel.getCurrentWeatherData(markerLatitude, markerLongitude)


            activity?.onBackPressed()
        }


        var supportMapFragment =
            childFragmentManager.findFragmentById(R.id.google_map_custom_location) as SupportMapFragment
        supportMapFragment.getMapAsync(OnMapReadyCallback {

            var googleMap = it
            //lma el mab yt3mlha load h3ml override lel setonmaplistener 3shan lama ados y7sl el action eli ana 3yzo

            it.setOnMapClickListener {

                //hna awl mados 3ayz a3ml marker fa h3ml init lel marker options
                var markerOptions = MarkerOptions()

                //b3den lazm agip el posisiton bta3 el marker
                markerOptions.position(it)


                //ha7ot el position el lat wl longitude fl datasettings bta3ti
                markerLatitude = it.latitude
                markerLongitude = it.longitude


                Log.d(
                    "mapLat",
                    "${it.latitude} : ${it.longitude}"
                )

                //b3den ha3ml title lel marker bl latitude wl longitude bta3 el makan eli wa2f 3leh
                markerOptions.title("${it.latitude} : ${it.longitude}")

                //b3den a3ml remover le kol el markers 3shan lma a3ml zoom el marker el adim yro7 w a7ot marker gded
                googleMap.clear()

                //b3den a3ml animation 3shan a3ml zoom 3ala el marker
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 10F))

                //add another marker in the new position
                googleMap.addMarker(markerOptions)
            }
        })
    }





}