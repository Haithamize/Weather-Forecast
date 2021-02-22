package com.haithamghanem.weatherwizard.ui.weather.current

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haithamghanem.weatherwizard.R
import com.haithamghanem.weatherwizard.constants_and_utilities.TimeFormatChanger
import com.haithamghanem.weatherwizard.data.model.detailedweather_entry.Daily

class DailyAdapter(private val context: Context) :
    RecyclerView.Adapter<DailyAdapter.DailyAdapterViewHolder>() {
    private var arrayListOfDailyResponse: List<Daily> = ArrayList()

    class DailyAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dailyTextDate: TextView
        var dailyTextMinTemprature: TextView
        var dailyTextMaxTemprature: TextView
        var dailyImgIcon: ImageView

        init {
            dailyTextDate = itemView.findViewById(R.id.dailyTxtDate)
            dailyImgIcon = itemView.findViewById(R.id.dailyImgIcon)
            dailyTextMinTemprature = itemView.findViewById(R.id.dailyMinTemperature)
            dailyTextMaxTemprature = itemView.findViewById(R.id.dailyMaxTemperature)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyAdapterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.daily_recycler_view, parent, false)
        return DailyAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyAdapterViewHolder, position: Int) {

        if (arrayListOfDailyResponse.isEmpty()) {
            return
        }
        // Log.d("TAG", "${arrayListOfHourlyResponse[position]}")
        val txtDailyDate = TimeFormatChanger.convertDate(arrayListOfDailyResponse[position].dt)
        holder.dailyTextDate.text = txtDailyDate
        holder.dailyTextMaxTemprature.text =
            arrayListOfDailyResponse[position].temp.max.toString() + "\u00B0" + "\\"
        holder.dailyTextMinTemprature.text =
            arrayListOfDailyResponse[position].temp.min.toString() + "\u00B0"
        val imageURL =
            "http://openweathermap.org/img/w/${arrayListOfDailyResponse[position].weather[0].icon}.png"
        Glide.with(context).load(imageURL).into(holder.dailyImgIcon)
    }

    override fun getItemCount(): Int {
        return arrayListOfDailyResponse.size
    }

    fun submitResponse(incomingArrayListOfDailyResponse: List<Daily>) {
        arrayListOfDailyResponse = incomingArrayListOfDailyResponse
        notifyDataSetChanged()
    }
}
