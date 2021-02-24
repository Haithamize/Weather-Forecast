package com.haithamghanem.weatherwizard.ui.weather.features.favorites

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.haithamghanem.weatherwizard.R
import com.haithamghanem.weatherwizard.data.local.FavoritePlaceEntity
import com.haithamghanem.weatherwizard.data.model.DataSettings
import com.haithamghanem.weatherwizard.data.network.Connectivity
import kotlinx.android.synthetic.main.favorite_fragment_map.*
import java.util.*


class MapFragment : Fragment() {

    private var isFragmentVisible = false
    private lateinit var favoriteCurrent: com.haithamghanem.weatherwizard.data.local.FavoriteCurrent
    var markerLatitude: Double = 0.0
    var markerLongitude: Double = 0.0
    var dataSettings = DataSettings
    lateinit var favoritePlaceViewModel: FavoritePlaceViewModel
    private lateinit var favoritePlaceObject: FavoritePlaceEntity
    private var city: String = ""

    val connectivity = Connectivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.favorite_fragment_map, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflate the layout for this fragment

        favoritePlaceViewModel = ViewModelProvider(this).get(FavoritePlaceViewModel::class.java)

        initComponents()

        saveLocationBtn.setOnClickListener {
            if(markerLatitude == 0.0 && markerLongitude==0.0){
                Toast.makeText(this.requireContext(),"You have to choose a location.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(!connectivity.isOnline(this.requireContext())){
                Toast.makeText(this.requireContext(),"You're offline.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            dataSettings.favoriteLatitude = markerLatitude
            dataSettings.favoriteLongitude = markerLongitude

            favoritePlaceViewModel.getFavoritePlaceDataFromApi(markerLatitude, markerLongitude)
            favoritePlaceViewModel.getFavoritePlace().observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer {

                    favoriteCurrent = com.haithamghanem.weatherwizard.data.local.FavoriteCurrent(
                        it.current.dt,
                        it.current.temp,
                        it.current.pressure,
                        it.current.humidity,
                        it.current.weather[0].icon,
                        it.current.wind_speed
                    )
                    val gcd = Geocoder(context, Locale.getDefault())
                    val addresses: List<Address> = gcd.getFromLocation(
                        markerLatitude,
                        markerLongitude,
                        1
                    )
                    if (addresses.size > 0) {
                        city = (addresses[0].getAddressLine(0))
                    }

                    favoritePlaceObject = FavoritePlaceEntity(
                        dataSettings.favoriteLatitude,
                        dataSettings.longitude, favoriteCurrent,
                        city
                    )
                    favoritePlaceViewModel.insertFavoritePlace(favoritePlaceObject)

                })



//            favoritePlaceViewModel.getFavoritePlaceDataFromApi(dataSettings.favoriteLatitude, dataSettings.favoriteLongitude)

            Log.d(
                "favoriteLatLon",
                "${dataSettings.favoriteLatitude} : ${dataSettings.favoriteLongitude}"
            )
            Toast.makeText(
                this.requireContext(),
                "Location saved successfully!",
                Toast.LENGTH_SHORT
            ).show()
//            activity?.onBackPressed()
            Navigation.findNavController(it).navigateUp()

        }

        var supportMapFragment =
            childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
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

//
//                Log.d(
//                    "mapLat",
//                    "${dataSettings.favoriteLatitude} : ${dataSettings.favoriteLongitude}"
//                )

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

    override fun onResume() {
        super.onResume()
        isFragmentVisible = true
    }

    override fun onPause() {
        super.onPause()
        isFragmentVisible = false
    }


    private fun initComponents() {
        saveLocationBtn
    }


}