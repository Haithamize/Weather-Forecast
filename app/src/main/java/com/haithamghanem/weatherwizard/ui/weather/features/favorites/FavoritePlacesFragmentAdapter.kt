package com.haithamghanem.weatherwizard.ui.weather.features.favorites

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.haithamghanem.weatherwizard.R
import com.haithamghanem.weatherwizard.data.local.FavoritePlaceEntity
import com.haithamghanem.weatherwizard.data.local.ForecastDatabase
import com.haithamghanem.weatherwizard.data.model.DataSettings
import com.haithamghanem.weatherwizard.data.model.detailedweather_entry.FullDetailsResponse
import com.haithamghanem.weatherwizard.data.remote.RetrofitClient
import kotlinx.android.synthetic.main.favorite_place_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FavoritePlacesFragmentAdapter(
    private val context: Context, private val listner :OnItemClickListener
) :
    RecyclerView.Adapter<FavoritePlacesFragmentAdapter.FavoritePlacesViewHolder>() {
    private var arrayListOfFavoriteEntities: List<FavoritePlaceEntity> = ArrayList()
    private var singleEntityDetailsResponse: FullDetailsResponse? = null


    val db = ForecastDatabase.getDatabase(context)
    val dataSettings = DataSettings
    val prefrences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(this.context)

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }


    inner class FavoritePlacesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var mCardView: CardView = itemView.findViewById(R.id.cardViewExpandable)

        val goToDetailsFragment = itemView.goToDetailsFragment
        val dataSettings = DataSettings
        var expandableView: LinearLayout = itemView.expandableView
        var expandableCardView: CardView = itemView.cardViewExpandable
        var favoriteTemprature: TextView = itemView.tempValue
        var favoriteCountry: TextView = itemView.favoriteCountryNew
        var favoriteHumidity: TextView = itemView.humidityValue
        var favoriteWeatherIcon: ImageView = itemView.favoriteWeatherIcon
        var deleteIcon: ImageView = itemView.deleteIcon
        var favoritePressure = itemView.pressureValue
        var favoriteWindSpeed = itemView.windSpeedValue


        init {
            loadSettings()

            deleteIcon.setOnClickListener {
                val builder = AlertDialog.Builder(it.context)
                builder.setTitle(R.string.confirm)
                builder.setMessage(R.string.areYouSure)
                builder.setPositiveButton(R.string.Yes) { dialog, which ->
                    GlobalScope.launch(Dispatchers.IO) {
                        val favoritePlaceEntity: FavoritePlaceEntity =
                            arrayListOfFavoriteEntities[adapterPosition] //el adapterPosition method de eli htgebli el specific place of the selected item in adapter and also in the arrayListOfFavoritePlaces
                        db.currentWeatherDao().delete(favoritePlaceEntity)
                    }
                }
                builder.setNegativeButton(
                    R.string.No
                ) { dialog, which ->
                    dialog.dismiss()
                }
                val alert = builder.create()
                alert.show()
            }


        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritePlacesViewHolder {
        loadSettings()
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_place_item, parent, false)
        return FavoritePlacesViewHolder(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FavoritePlacesViewHolder, position: Int) {

        if (arrayListOfFavoriteEntities.isEmpty()) {
            return
        }

        holder.mCardView.tag = position

        holder.goToDetailsFragment.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                listner.onItemClick(position)
            }
        }

        fun showDetails() {
            if (holder.expandableView.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(
                    holder.expandableCardView,
                    AutoTransition()
                )
                holder.expandableView.visibility = View.VISIBLE
            } else {
                TransitionManager.beginDelayedTransition(
                    holder.expandableCardView,
                    AutoTransition()
                )
                holder.expandableView.visibility = View.GONE
            }
        }




        holder.favoriteCountry.text = arrayListOfFavoriteEntities[position].city
        val imageURL =
            "http://openweathermap.org/img/w/${arrayListOfFavoriteEntities[position].favoriteCurrent.icon}.png"
        Glide.with(context).load(imageURL).into(holder.favoriteWeatherIcon)

////********************
        holder.favoriteWeatherIcon.setOnClickListener {
            showDetails()

            if (prefrences.getString("LANGUAGE_SYSTEM", "") == "en") {
                if (prefrences.getString("UNIT_SYSTEM", "") == "metric") {
                    holder.favoriteTemprature.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.temp.toString() + "\u00B0" + "C"
                    holder.favoriteHumidity.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.humidity.toString() + " %"
                    holder.favoritePressure.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.pressure.toString() + " hPa"
                    holder.favoriteWindSpeed.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.wind_speed.toString() + " metre/s"
                } else if (prefrences.getString("UNIT_SYSTEM", "") == "imperial") {
                    holder.favoriteTemprature.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.temp.toString() + "\u00B0" + "F"
                    holder.favoriteHumidity.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.humidity.toString() + " %"
                    holder.favoritePressure.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.pressure.toString() + " hPa"
                    holder.favoriteWindSpeed.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.wind_speed.toString() + " mile/h"
                } else if (prefrences.getString("UNIT_SYSTEM", "") == "standard") {
                    holder.favoriteTemprature.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.temp.toString() + "K"
                    holder.favoriteHumidity.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.humidity.toString() + " %"
                    holder.favoritePressure.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.pressure.toString() + " hPa"
                    holder.favoriteWindSpeed.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.wind_speed.toString() + " metre/s"
                }
            } else {
                if (prefrences.getString("UNIT_SYSTEM", "") == "metric") {
                    holder.favoriteTemprature.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.temp.toString() + "\u00B0" + "C"
                    holder.favoriteHumidity.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.humidity.toString() + " %"
                    holder.favoritePressure.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.pressure.toString() + " باسكال"
                    holder.favoriteWindSpeed.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.wind_speed.toString() + " متر/الثانية"
                } else if (prefrences.getString("UNIT_SYSTEM", "") == "imperial") {
                    holder.favoriteTemprature.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.temp.toString() + "\u00B0" + "F"
                    holder.favoriteHumidity.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.humidity.toString() + " %"
                    holder.favoritePressure.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.pressure.toString() + " باسكال"
                    holder.favoriteWindSpeed.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.wind_speed.toString() + " ميل/الساعة"
                } else if (prefrences.getString("UNIT_SYSTEM", "") == "standard") {
                    holder.favoriteTemprature.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.temp.toString() + "K"
                    holder.favoriteHumidity.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.humidity.toString() + " %"
                    holder.favoritePressure.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.pressure.toString() + " باسكال"
                    holder.favoriteWindSpeed.text =
                        arrayListOfFavoriteEntities[position].favoriteCurrent.wind_speed.toString() + " متر/الثانية"
                }
            }
        }
    }


    override fun getItemCount(): Int {
        if (arrayListOfFavoriteEntities.isNotEmpty()) {
            return arrayListOfFavoriteEntities.size
        } else {
            return 0
        }
    }

    fun submitResponse(incomingFavoriteArrayList: List<FavoritePlaceEntity>) {
        arrayListOfFavoriteEntities = incomingFavoriteArrayList
        notifyDataSetChanged()
    }

    fun setData(detailsEntryResponse: FullDetailsResponse?) {
        if (detailsEntryResponse != null) {
            Log.d("singleEntityResponse", "${detailsEntryResponse}")
            singleEntityDetailsResponse = detailsEntryResponse
        }
    }

    fun getFavoriteEntityPosition(position: Int): FavoritePlaceEntity {
        return arrayListOfFavoriteEntities[position]
    }

    private fun loadSettings() {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val unit_system = sp.getString("UNIT_SYSTEM", "")
        val language_system = sp.getString("LANGUAGE_SYSTEM", "")
        val deviceLocation = sp.getBoolean("USE_DEVICE_LOCATION", false)
        val notifications = sp.getBoolean("USE_NOTIFICATIONS_ALERT", false)
        val customLocations = sp.getString("CUSTOM_LOCATION", "")

        if (unit_system != null) {
            dataSettings.unitSystem = unit_system
        }
        if (language_system != null) {
            dataSettings.languageSystem = language_system
        }
        if (deviceLocation != null) {
            dataSettings.deviceLocation = deviceLocation
        }
        if (notifications != null) {
            dataSettings.notifications = notifications
        }


        if (customLocations != null) {
            dataSettings.customLocations = customLocations
        }

    }
}