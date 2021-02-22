package com.haithamghanem.weatherwizard.ui.weather.current

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haithamghanem.weatherwizard.R
import com.haithamghanem.weatherwizard.constants_and_utilities.TimeFormatChanger
import com.haithamghanem.weatherwizard.data.model.detailedweather_entry.Hourly

class HourlyAdapter(private val context: Context) : RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder>() {
    private var arrayListOfHourlyResponse: List<Hourly> = ArrayList()



    class HourlyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var hourlyTxtTemprature: TextView
        var hourlyTxtHour: TextView
        var hourlyImgIcon: ImageView

        init {
            hourlyTxtHour = itemView.findViewById(R.id.hourlyTxtHour)
            hourlyTxtTemprature = itemView.findViewById(R.id.hourlyTxtTemperature)
            hourlyImgIcon = itemView.findViewById(R.id.hourlyImgIcon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hourly_recycler_view, parent, false)
        return HourlyViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {

        if(arrayListOfHourlyResponse.isEmpty()){
            return
        }
       // Log.d("TAG", "${arrayListOfHourlyResponse[position]}")
        val txtHourlyDate = TimeFormatChanger.convertTime(arrayListOfHourlyResponse[position].dt)
        holder.hourlyTxtHour.text = txtHourlyDate
        holder.hourlyTxtTemprature.text = arrayListOfHourlyResponse[position].temp.toString()+"°"
        val imageURL = "http://openweathermap.org/img/w/${arrayListOfHourlyResponse[position].weather[0].icon}.png"
        Glide.with(context).load(imageURL).into(holder.hourlyImgIcon)
    }

    override fun getItemCount(): Int {
        return arrayListOfHourlyResponse.size
    }

    fun submitResponse(incomingHourlyArrayList: List<Hourly>) {
        arrayListOfHourlyResponse = incomingHourlyArrayList
        notifyDataSetChanged()
    }

}
