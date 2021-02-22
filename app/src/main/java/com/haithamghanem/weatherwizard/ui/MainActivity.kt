package com.haithamghanem.weatherwizard.ui

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.haithamghanem.weatherwizard.R
import com.haithamghanem.weatherwizard.data.model.DataSettings
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


const val USE_DEVICE_LOCATION = "USE_DEVICE_LOCATION"
const val CUSTOM_LOCATION = "CUSTOM_LOCATION"

class MainActivity : AppCompatActivity() {
    val dataSettings = DataSettings



    private lateinit var navController: NavController
    val prefrences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(this)
    lateinit var language:String



    override fun onCreate(savedInstanceState: Bundle?) {
        changeLanguage()
        loadSettings()
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_WeatherWizard)
        setContentView(R.layout.activity_main)


        val actionBar: ActionBar?
        actionBar = supportActionBar

        //6495ed da 3agbni w da b3do 4682b4
        //4169e1

        val colorDrawable = ColorDrawable(Color.parseColor("#6495ed"))

        actionBar?.setBackgroundDrawable(colorDrawable)
//        loadSettings()

//        initComponents()
//        getLocation()


        //awl mara el app yybd2 le awl mara hay3ml set lel default values lel preferences bs da f awl mara bs el app ybd2
        PreferenceManager.setDefaultValues(this.applicationContext, R.xml.preferences, false)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


//        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        bottom_nav.setupWithNavController(navController) //3shan a3ml setup lel buttom nav menue m3 el controller eli lesa 3mlo

        //3shan am3l setup lel action bar m3 nav controller
        NavigationUI.setupActionBarWithNavController(this, navController)
//        language = prefrences.getString("LANGUAGE_SYSTEM","en").toString()
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
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val language = sharedPreferences.getString("LANGUAGE_SYSTEM","")
        if (language != null) {
            setLocale(language)
        }
    }

    private fun loadSettings() {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
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

    override fun onSupportNavigateUp(): Boolean {
        //el up navigation hwa el sahm eli fo2 3al shmal eli bndos beh back(y3ni lma ados 3la ay item mn ta7t w ttft7 hft7 m3aha zorar back fo2)

        return NavigationUI.navigateUp(navController, null)
    }



//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.top_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.credits -> {
//                Toast.makeText(this, "Credits", Toast.LENGTH_SHORT).show()
//                return true
//            }
//            R.id.madeBy -> {
//                Toast.makeText(this, "Made by: Haitham Ghanem", Toast.LENGTH_SHORT).show()
//                return true
//            }
//
//            else -> return super.onOptionsItemSelected(item)
//        }
//
//    }

//    fun loadSettings(){
//        val sp = PreferenceManager.getDefaultSharedPreferences(this)
//        unit = sp.getString("UNIT_SYSTEM","METRIC")
//    }




}