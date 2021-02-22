package com.haithamghanem.weatherwizard.ui.weather.features.alerts

import android.app.AlertDialog
import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import com.haithamghanem.weatherwizard.R
import com.haithamghanem.weatherwizard.data.model.DataSettings
import com.haithamghanem.weatherwizard.ui.MainActivity
import kotlinx.android.synthetic.main.alerts_fragment.*
import kotlinx.android.synthetic.main.current_weather_fragment.view.*

class AlertActivity : AppCompatActivity() {
    val dataSettings = DataSettings

    val prefrences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(this)

    lateinit var r: Ringtone

    companion object{
       var title:String=""
       var message:String=""
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        loadSettings()
        super.onCreate(savedInstanceState)
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
             r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        AlertDialog.Builder(this)
            .setMessage(message)
            .setTitle(title)
            .setCancelable(true)
            .setPositiveButton("Ok") { dialog, which ->
                val intent = Intent(applicationContext,MainActivity::class.java)
                dialog.dismiss()
                finish()
                r.stop()
                startActivity(intent)
            }
            .create().show()
    }



    private fun loadSettings() {
        val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
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