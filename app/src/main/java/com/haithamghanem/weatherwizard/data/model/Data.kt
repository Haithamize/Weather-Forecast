package com.haithamghanem.weatherwizard.data.model

import com.google.gson.annotations.SerializedName

data class Data (

        @SerializedName("latitude") val latitude : Double,
        @SerializedName("longitude") val longitude : Double,
        @SerializedName("type") val type : String,
        @SerializedName("name") val name : String,
        @SerializedName("number") val number : String,
        @SerializedName("postal_code") val postal_code : String,
        @SerializedName("street") val street : String,
        @SerializedName("confidence") val confidence : Int,
        @SerializedName("region") val region : String,
        @SerializedName("region_code") val region_code : String,
        @SerializedName("county") val county : String,
        @SerializedName("locality") val locality : String,
        @SerializedName("administrative_area") val administrative_area : String,
        @SerializedName("neighbourhood") val neighbourhood : String,
        @SerializedName("country") val country : String,
        @SerializedName("country_code") val country_code : String,
        @SerializedName("continent") val continent : String,
        @SerializedName("label") val label : String
)