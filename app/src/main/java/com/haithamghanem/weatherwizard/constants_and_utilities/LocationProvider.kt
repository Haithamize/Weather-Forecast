package com.haithamghanem.weatherwizard.constants_and_utilities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.haithamghanem.weatherwizard.data.model.DataSettings
import com.haithamghanem.weatherwizard.ui.weather.current.LOCATION_REQUEST_CODE

object LocationProvider {

    var dataSettings = DataSettings
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationContext: Context
    private lateinit var defContext: Context


    fun initLocation(context: Context) {
        locationContext = context.applicationContext
        defContext = context
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(locationContext)
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
    }

    fun getDeviceLocation() {

        if (ActivityCompat.checkSelfPermission(
                locationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                locationContext, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                (defContext as Activity),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
                LOCATION_REQUEST_CODE
            )
            return
        } else {
            mFusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    dataSettings.latitude = it.latitude
                    dataSettings.longitude = it.longitude
                     Log.d("LocationCallBack", "${it.latitude} : ${it.latitude}")
                }
            }

        }
    }


        fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            if (requestCode == LOCATION_REQUEST_CODE) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getDeviceLocation()

                }
            }
        }


}