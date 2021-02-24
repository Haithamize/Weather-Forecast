package com.haithamghanem.weatherwizard.ui.weather.features.alerts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.media.Ringtone
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.haithamghanem.weatherwizard.R
import com.haithamghanem.weatherwizard.data.model.DataSettings
import com.haithamghanem.weatherwizard.data.model.detailedweather_entry.Alerts
import com.haithamghanem.weatherwizard.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList


val VERBOSE_NOTIFICATION_CHANNEL_NAME_P: CharSequence =
    "Verbose WorkManager Notifications"
const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION_P =
    "Shows notifications whenever work starts"
const val NOTIFICATION_TITLE_P = "Weather Wizard"
const val CHANNEL_ID_P = "VERBOSE_NOTIFICATION"
const val NOTIFICATION_ID_P = 2

class PeriodiWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private var listOfAlerts: List<Alerts>? = ArrayList<Alerts>()

    val prefrences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(applicationContext)

    val dataSettings = DataSettings
    lateinit var r: Ringtone

    override fun doWork(): Result {
        //mohem gdn -> 3shan ana f background thread fa msh hynf3 azher dialog ela lma ast5dm Handler 3shan atn2l lel main thread,,
        // w hst5dm "intent.setFlags(FLAG_ACTIVITY_NEW_TASK);" 3shan ana banadi 3la haga bara el Activity Context
        //w lazm a7ot looper fl parameter "Handler(Looper.getMainLooper())"
        // because if you create a handler in a non UI thread you will post messages to the non UI Thread.
        // A handler by default post message to the thread where it is created. - Use the context of the constructor of the Worker class that is created
        try {
            loadSettings()
            getResponseFromApi()

            return Result.success()

        } catch (e: Exception) {
            return Result.failure()
        }
    }

    fun changinglanguageOfTitle(): String{
        val lang = prefrences.getString("LANGUAGE_SYSTEM", "")
        if(lang == "en"){
            return "Weather Update"
        }else{
            return "!شعار بالطقس"
        }
    }

    fun changinglanguageOfMessage(): String{
        val lang = prefrences.getString("LANGUAGE_SYSTEM", "")
        if(lang == "en"){
            return "There are no weather alerts have a nice day."
        }else{
            return "لا يوجد خطر !ستمتع بيوم جميل."
        }
    }


    fun makeStatusNotification(title: String, message: String, context: Context) {

        // Make a channel if necessary 3shan lw el phone a2l mn 26 API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = VERBOSE_NOTIFICATION_CHANNEL_NAME_P
            val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION_P

            val channel = NotificationChannel(
                CHANNEL_ID_P,
                name,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = description

            // Add the channel
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            notificationManager?.createNotificationChannel(channel)
        }

        // Create the notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.cloud_computing)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))

        // Show the notification
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_P, builder.build())
    }


//    private fun makeAlarmNotifications(title: String, message: String, context: Context) {
//
//        try {
//            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
//            r = RingtoneManager.getRingtone(applicationContext, notification)
//            r.play()
//            AlertDialog.Builder(context)
//                .setMessage(message)
//                .setTitle(title)
//                .setIcon(R.mipmap.cloud_computing)
//                .setPositiveButton(
//                    "Ok"
//                ) { dialogInterface: DialogInterface?, i: Int ->
//                    r.stop()
//                    dialogInterface?.dismiss()
//                }
//                .create()
//                .show()
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//
//    }


    private fun loadSettings() {
        val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val unit_system = sp.getString("UNIT_SYSTEM", "")
        val language_system = sp.getString("LANGUAGE_SYSTEM", "")
        val deviceLocation = sp.getBoolean("USE_DEVICE_LOCATION", false)
        val notifications = sp.getBoolean("USE_NOTIFICATIONS_ALERT", false)
        val customLocations = sp.getString("CUSTOM_LOCATION", "")
        val alarmTypeFromSettings = sp.getBoolean("ALARM_TYPE", false)

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

        if (alarmTypeFromSettings != null) {
            dataSettings.alarmType = alarmTypeFromSettings
        }

    }


    //68.3963
    //36.9419

    fun getResponseFromApi() {
        GlobalScope.launch(Dispatchers.IO) {
            val response = RetrofitClient.getWeatherService(
                dataSettings.latitude,
                dataSettings.longitude,
                dataSettings.unitSystem,
                dataSettings.languageSystem
            )
                .execute()

            withContext(Dispatchers.Main) {

                if (response.isSuccessful) {
                    Log.d("response", "getResponseFromApi: ${response.body()}")

                    listOfAlerts = response.body()?.alerts
                    val currentTime = Calendar.getInstance().timeInMillis
                    Log.d("workInProgress", "getResponseFromApi: $currentTime")

                    if (listOfAlerts?.isNotEmpty() == true) { // de fe 7alet lw fe alerts rag3a m3 el response

                        Log.d("insideAlertAndBeforeFor", "insideAlertAndBeforeFor")
                        for (item in listOfAlerts!!){
                            if ((currentTime <= item.end *1000) && (currentTime >= item.start *1000)
                            ) {
                                Log.d("afterFor", "insideAlertAndBeforeFor")

                                makeStatusNotification(
                                    item.event,
                                    item.description,
                                    applicationContext
                                )
                            }else{
                                makeStatusNotification(
                                    changinglanguageOfTitle(),
                                    changinglanguageOfMessage(),
                                    applicationContext
                                )
                            }
                    }
                    } else {  //de fe 7alet en mfish alerts rag3a m3 el response
                        Log.d("elseInWorker", "else condition in worker")
                        makeStatusNotification(
                            changinglanguageOfTitle(),
                            changinglanguageOfMessage(),
                            applicationContext
                        )
                    }

                } else {
                    Log.d("TAG", "getCurrentWeather: ${response.body()}")
                    //TODO el data mgtsh ml server aw mfish net eb2a hanndle el 7eta de w 7ot el cached 3ala el screen lw fe cache
                }
            }
        }
    }


}