package com.haithamghanem.weatherwizard.ui.weather.features.alerts

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.SharedPreferences
import android.media.Ringtone
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
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


val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence =
    "Verbose WorkManager Notifications"
const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
    "Shows notifications whenever work starts"
const val NOTIFICATION_TITLE = "Weather Wizard"
const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
const val NOTIFICATION_ID = 1

class UploadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    val prefrences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(applicationContext)

    val dataSettings = DataSettings
    val cal = Calendar.getInstance().timeInMillis

    //    val current = (DateFormat.getDateInstance(DateFormat.FULL).format(cal.time)).toString()

    private var listOfAlerts: List<Alerts>? = ArrayList<Alerts>()

    lateinit var r: Ringtone


    override fun doWork(): Result {

        try {
            loadSettings()
            getResponseFromApi()

            if (listOfAlerts?.isNotEmpty() == true) { // de fe 7alet lw fe alerts rag3a m3 el response
                val inputime = inputData.getLong(INPUT_TIME, 0)

                if ((inputime <= listOfAlerts?.get(0)?.end!!) && (inputime >= listOfAlerts?.get(0)?.end!!)) {
                    if (!dataSettings.alarmType) { //de fe 7alet eno 3ayz notifications type
                        makeStatusNotification(
                            listOfAlerts!![0].event,
                            listOfAlerts!![0].description,
                            applicationContext
                        )
                    } else {      //de fe 7alet eno 3ayz alarm type
                        AlertActivity.title = listOfAlerts!![0].event
                        AlertActivity.message = listOfAlerts!![0].description

                        Handler(Looper.getMainLooper()).post(Runnable {
                            val intent = Intent(applicationContext, AlertActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            applicationContext.startActivity(intent)
                        })
                    }
                }

            } else {  //de fe 7alet en mfish alerts rag3a m3 el response
                if (!dataSettings.alarmType) {
                    makeStatusNotification(
                        changinglanguageOfTitle(),
                        changinglanguageOfMessage(),
                        applicationContext
                    )
                } else {
                    AlertActivity.title = changinglanguageOfTitle()
                    AlertActivity.message = changinglanguageOfMessage()
                    Handler(Looper.getMainLooper()).post(Runnable {
                        val intent = Intent(applicationContext, AlertActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        applicationContext.startActivity(intent)
                    })
                }

            }

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }



    private fun changinglanguageOfTitle(): String{
       val lang = prefrences.getString("LANGUAGE_SYSTEM", "")
        if(lang == "en"){
            return "Weather Update"
        }else{
            return "!شعار بالطقس"
        }
    }

    private fun changinglanguageOfMessage(): String{
       val lang = prefrences.getString("LANGUAGE_SYSTEM", "")
        if(lang == "en"){
            return "There are no weather alerts have a nice day."
        }else{
            return "لا يوجد خطر !ستمتع بيوم جميل."
        }
    }



    private fun getResponseFromApi() {
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

                } else {
                    Log.d("TAG", "getCurrentWeather: ${response.body()}")
                }
            }
        }
    }

    fun makeStatusNotification(title: String, message: String, context: Context) {

        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
            val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION

            val channel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH)
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
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }


    private fun makeAlarmNotifications(title: String, message: String, context: Context) {

//            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
//            r = RingtoneManager.getRingtone(applicationContext, notification)
//            r.play()

        AlertDialog.Builder(context)
            .setMessage(message)
            .setTitle(title)
            .setIcon(R.mipmap.cloud_computing)
            .setPositiveButton(
                "Ok"
            ) { dialogInterface: DialogInterface?, i: Int ->
//                    r.stop()
                dialogInterface?.dismiss()
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
}