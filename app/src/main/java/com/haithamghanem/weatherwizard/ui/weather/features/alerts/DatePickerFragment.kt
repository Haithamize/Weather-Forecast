package com.haithamghanem.weatherwizard.ui.weather.features.alerts

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import java.text.DateFormat
import java.util.*

class DatePickerFragment: DialogFragment() ,DatePickerDialog.OnDateSetListener{


    private var mFragment: Fragment? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        //hgeb el current date 3shan a3mlo pass fl calender eli htzhar awl mara 3shan yb2a maktop feha tari5 abl ma a5tar ana bnfsi
        val year = c[Calendar.YEAR]

        val month = c[Calendar.MONTH]
        val day = c[Calendar.DAY_OF_MONTH]
        return DatePickerDialog(
            requireActivity(),
            this,
            year,
            month,
            day
        )
        // hna baraga3 shahet el calender eli htzhr w tani parameter da 3mlto kda msh "this" 3shan 3ayz arg3ha fl main activity eli wa2f feha w lazm cast, w a5er parameters dol el initials dates eli htzhr awl ma el calender ttft7
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        AlertFragment.cal[Calendar.YEAR] = year
        AlertFragment.cal[Calendar.MONTH] =month
        AlertFragment.cal[Calendar.DAY_OF_MONTH] = dayOfMonth

        val currentDateString = DateFormat.getDateInstance(DateFormat.FULL)
            .format(AlertFragment.cal.time)
        Log.d("calenderTimeafter", "onCreate: ${
            DateFormat.getDateInstance(DateFormat.SHORT).format(
                AlertFragment.cal.time)} ")
    }
}