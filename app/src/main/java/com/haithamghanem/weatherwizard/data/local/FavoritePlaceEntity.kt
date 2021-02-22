package com.haithamghanem.weatherwizard.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

const val FAVORITE_PLACES_ID = 0
@Entity(tableName = "favorite_places")
data class FavoritePlaceEntity(
    val lat: Double,
    val lon: Double,
    @Embedded(prefix = "current_")
    val favoriteCurrent : FavoriteCurrent,
    @PrimaryKey
    val city: String
) {
//    @PrimaryKey(autoGenerate = true)
//    var id = FAVORITE_PLACES_ID
}