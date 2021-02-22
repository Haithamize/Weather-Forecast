package com.haithamghanem.weatherwizard.data.local


data class FavoriteCurrent (
  val dt : Int,
     val temp : Double,
   val pressure : Int,
    val humidity : Int,
    val icon: String,
   val wind_speed : Double,
)