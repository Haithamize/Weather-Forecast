package com.haithamghanem.weatherwizard.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

import com.haithamghanem.weatherwizard.data.model.detailedweather_entry.FullDetailsResponse




@Dao
interface CurrentWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)//hna m3naha lw fe conflict wna b3ml insert y3ni fe object already mwgod e3mlo replace w da hy7sl 3latol l2en hyb2a 3ndna 2 instances mn currentWeather 3ndhom nfs el id w da eli m3mol fl entity en el id sabet f kolo fa lma yb2a 3ndna conflict 3latol kda lw fe object hybdelo w lw mfish hyzwd
    fun upsert(weatherEntry: FullDetailsResponse)

    @Query("select * from current_weather")
    fun getStoredWeatherData(): LiveData<FullDetailsResponse>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(favoritePlacesEntry: FavoritePlaceEntity)

    @Delete
    fun delete(favoritePlacesEntry: FavoritePlaceEntity)

    @Query("select * from favorite_places")
    fun getAllFavoritePlaces(): LiveData<List<FavoritePlaceEntity>>


}

//el Dao data access object w hna bn3ml el operations eli hnst5dmha fl table bta3na eli hwa el currentWeather
//upser y3ni update or insert (h3ml insert lw mfish haga , w h3ml update lw fe haga)