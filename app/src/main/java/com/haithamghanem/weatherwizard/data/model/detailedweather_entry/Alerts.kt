package com.haithamghanem.weatherwizard.data.model.detailedweather_entry

import com.google.gson.annotations.SerializedName


data class Alerts (
    @SerializedName("sender_name") val sender_name : String,
    @SerializedName("event") val event : String,
    @SerializedName("start") val start : Long,
    @SerializedName("end") val end : Long,
    @SerializedName("description") val description : String
)