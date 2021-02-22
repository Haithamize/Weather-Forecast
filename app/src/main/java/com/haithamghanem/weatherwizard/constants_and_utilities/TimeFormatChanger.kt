package com.haithamghanem.weatherwizard.constants_and_utilities

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class TimeFormatChanger {

    companion object {
        fun convertTime(time: Long): String {
            //val simpleDateFormat = DateFormat.getTimeInstance()
            val simpleDateFormat = SimpleDateFormat("hh:mm a")
            val date = Date(time * 1000)
            return simpleDateFormat.format(date)
        }

        fun convertDate(time: Long): String {
            //val simpleDateFormat = DateFormat.getDateTimeInstance()
            val simpleDateFormat = SimpleDateFormat("EEE',' MMM d")
            val date = Date(time * 1000)
            return simpleDateFormat.format(date)
        }

    }
}