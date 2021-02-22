package com.haithamghanem.weatherwizard.ui.settings

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.haithamghanem.weatherwizard.R
import com.haithamghanem.weatherwizard.data.model.DataSettings
import com.haithamghanem.weatherwizard.data.network.Connectivity
import com.haithamghanem.weatherwizard.ui.MainActivity
import com.haithamghanem.weatherwizard.ui.weather.features.alerts.PeriodiWorker
import java.util.*
import java.util.concurrent.TimeUnit



const val OVER_RELAY_PERMISSION_CODE = 20
class SettingsFragment : PreferenceFragmentCompat() {

        private val connectivity = Connectivity
    val datasettings = DataSettings

    val prefrences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(this.context)

    companion object{
        private var isFirstTime:Boolean = true
        var unitChanged:String = "en"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.setBackgroundColor(Color.BLACK)
        return view

    }


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        addPreferencesFromResource(R.xml.preferences)


        val preference: Preference? = findPreference("CUSTOM_LOCATION")
        preference?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
//            Toast.makeText(this.requireContext(),"qweqwewq",Toast.LENGTH_SHORT).show()

            if (!connectivity.isOnline(this.requireContext())) {
                Toast.makeText(
                    this.requireContext(),
                    R.string.offline,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                if(isFirstTime) {
                    AlertDialog.Builder(this.requireContext())
                        .setTitle(R.string.instructions)
                        .setMessage(R.string.navigateFreely)
                        .setPositiveButton(R.string.ok) { dialogInterface: DialogInterface?, i: Int ->
                            Toast.makeText(
                                this.requireContext(),
                                R.string.mapActivated,
                                Toast.LENGTH_LONG
                            ).show()
                        }.create().show()
                    isFirstTime = false
                }

                val customLocationMapFragment=CustomLocationMapFragment()
                activity?.supportFragmentManager?.beginTransaction()
                    ?.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    ?.replace(this.id, customLocationMapFragment)?.addToBackStack("customLocationMapFragment")
                    ?.commit()
            }
            true
        }

        val notification: Preference? = findPreference("USE_NOTIFICATIONS_ALERT")
        notification?.onPreferenceClickListener = Preference.OnPreferenceClickListener {

                if(prefrences.getBoolean("USE_NOTIFICATIONS_ALERT", false) == false) {
//                    Toast.makeText(this.requireContext(), "ana hena", Toast.LENGTH_SHORT).show()
                    WorkManager.getInstance(this.requireContext())
                        .cancelAllWorkByTag("periodicWorkRequest")
                }else{
                    setPeriodicWorkRequest()
                }
            true
        }

        val languageChange: Preference? = findPreference("LANGUAGE_SYSTEM")
        languageChange?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->

            if(preference.preferenceManager.sharedPreferences.getString("LANGUAGE_SYSTEM", "")== "en"){

                val intent = Intent(this.requireContext(), MainActivity::class.java)
                startActivity(intent)
                true
            }else{

                val intent = Intent(this.requireContext(), MainActivity::class.java)
                startActivity(intent)
                true
            }
        }

        val alarm: Preference? = findPreference("ALARM_TYPE")
        alarm?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference: Preference, any: Any ->

            getPermission()
            true
        }

    }


     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVER_RELAY_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this.requireContext())) {
                Toast.makeText(this.requireContext(), R.string.permissionDenied, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this.requireContext())) {
            android.app.AlertDialog.Builder(this.requireContext())
                .setMessage(R.string.overRelayPermission)
                .setPositiveButton(R.string.Yes) { dialog, which ->
                    prefrences.edit().putBoolean("ALARM_TYPE", true).apply()
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    )
                    startActivityForResult(
                        intent,
                        OVER_RELAY_PERMISSION_CODE
                    )
                }
                .setNegativeButton(R.string.No) { dialog, which ->
                    prefrences.edit().putBoolean("ALARM_TYPE", false).apply()
                    Toast.makeText(
                        this.requireContext(),
                        R.string.overRelayPermissionDenied,
                        Toast.LENGTH_SHORT
                    ).show()
                }.create().show()
        }
    }

//    private fun setLocale(langCode: String){
//        val locale = Locale(langCode)
//        Locale.setDefault(locale)
//        val resources = resources
//        val configuration = resources.configuration
//        configuration.locale = locale
//        resources.updateConfiguration(configuration, resources.displayMetrics)
//    }
//
//    fun changeLanguage(){
//        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context)
//        val language = sharedPreferences.getString("LANGUAGE_SYSTEM","")
//        if (language != null) {
//            setLocale(language)
//        }
//    }

    private fun setPeriodicWorkRequest() {
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
//        val currentDate = Calendar.getInstance().timeInMillis
//
//        val delay = cal.timeInMillis - currentDate

        val workManager: WorkManager? = this.context?.let { WorkManager.getInstance(it) }
        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            PeriodiWorker::class.java, 3,
            TimeUnit.HOURS
        ).setConstraints(constraints)
            .addTag("periodicWorkRequest")
            .build()

        workManager?.enqueue(periodicWorkRequest)
        workManager?.getWorkInfoByIdLiveData(periodicWorkRequest.id)?.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                Log.d("WorkInProgress", "${it.state} ")
            })
    }



//    override fun onOptionsMenuClosed(menu: Menu) {
//        super.onOptionsMenuClosed(menu)
//        val fm: FragmentManager = requireActivity().supportFragmentManager
//        val fragment: Fragment? = fm.findFragmentById(R.id.google_map_custom_location)
//        val ft: FragmentTransaction = fm.beginTransaction()
//        if (fragment != null) {
//            ft.remove(fragment)
//        }
//        ft.commit()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        val fm: FragmentManager = requireActivity().supportFragmentManager
//        val fragment: Fragment? = fm.findFragmentById(R.id.google_map_custom_location)
//        val ft: FragmentTransaction = fm.beginTransaction()
//        if (fragment != null) {
//            ft.remove(fragment)
//        }
//        ft.commit()
//    }






}