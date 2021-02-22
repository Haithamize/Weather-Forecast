package com.haithamghanem.weatherwizard.data.local

import android.content.Context
import androidx.room.*
import com.haithamghanem.weatherwizard.data.DataConverterAlerts
import com.haithamghanem.weatherwizard.data.DataConverterDaily
import com.haithamghanem.weatherwizard.data.DataConverterHourly
import com.haithamghanem.weatherwizard.data.DataConverterWeather
import com.haithamghanem.weatherwizard.data.model.detailedweather_entry.FullDetailsResponse


@Database(
    entities = [FavoritePlaceEntity::class, FullDetailsResponse::class],
    version = 1,
    exportSchema = false //de 7attha 3shan kan fe erro 8arip el ma3alem bytl3li fl build
) //kant hya de el moshkla  lel error da "Error: entities and pojos must have a usable public constructor java" lazm a3rf el database en 3ndi class esmo DataConverter w a3mlo bl annotation bta3t @TypeConverters
@TypeConverters(DataConverterAlerts::class, DataConverterHourly::class, DataConverterDaily::class, DataConverterWeather::class)
abstract class ForecastDatabase : RoomDatabase() {
    //3shan a3ml implementation lel Dao lazm a3mlha 3la shakl abstract fun btraga3 Dao (w hna eli hyrg3 ml function de hyb2a el implemented currenWeatherDao)
    abstract fun currentWeatherDao(): CurrentWeatherDao

    companion object {
        var instance: ForecastDatabase? = null
        fun getDatabase(context: Context): ForecastDatabase {
            var appContext = context.applicationContext
            instance ?: synchronized(this)
            {
                var roomInstance =
                    Room.databaseBuilder(appContext, ForecastDatabase::class.java, "forecast.db")
                        .build()
                instance = roomInstance
            }
            return instance!!
        }
//        //lazm el instance yb2a null fl awl 3shan da singleton w m3na el volatile en kol el threads hyb2a 3ndha immediate access 3la el property de
//        @Volatile private var instance : ForecastDatabase? = null
//
//        private val LOCK = Any() //hna lazm n3ml lock object 3shan hnst5dm threads w 3mlto b Any() 3shan msh hammni hwa no3o a hwa dummy object 3shan at2kd en mfish 2 threads are currently doing the same thing 3shan mmkn yb2a 3ndna 2 instances in the same time lw fe 2 threads are working in parallel
//
//        //hna fl fun de h3ml invoke lel database a5od mnha object fa lw el instance msh b null htrg3 3latol w lw b null
//        //hnnady 3la synchronized block w adelo el lock 3shan yb2a thread safe w ha3ml check tani gwa lw el instance msh b null rg3ha w lw b null hnst5dm method ht3ml instance gdeda
//        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
//            instance ?: buildDatabase(context).also { instance = it } //eli 3mlto hna fl also en ay haga htrg3 mn el fun de h5li el instance bta3ty = eli rag3 mn el fun 3shan lw kant b null tb2a b kema
//        }
//        private fun buildDatabase(context: Context) = Room.databaseBuilder(context.applicationContext,ForecastDatabase::class.java, "forecast.db").build()
//        //gowa el fun de ktpt context.application 3shan 7ata lw el context eli gayeli gy mn fragment ana a7welo le application 3shan lazm el database ta5od application context, wl name eli 3la el local storage esmo forecast.db
    }

}
//gowa el database de han3ml instance mn el Dao interface 3shan n3ml implementation lel methods eli fl Dao