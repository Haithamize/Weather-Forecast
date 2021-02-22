package com.haithamghanem.weatherwizard.ui.weather.features.favorites

import android.content.DialogInterface
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.haithamghanem.weatherwizard.R
import com.haithamghanem.weatherwizard.data.local.FavoritePlaceEntity
import com.haithamghanem.weatherwizard.data.model.DataSettings
import com.haithamghanem.weatherwizard.data.network.Connectivity
import kotlinx.android.synthetic.main.current_weather_fragment.*
import kotlinx.android.synthetic.main.favorite_list.*
import kotlinx.android.synthetic.main.favorite_place_item.*
import kotlinx.android.synthetic.main.favorite_place_item.view.*

class FavoritePlaces : Fragment() ,FavoritePlacesFragmentAdapter.OnItemClickListener {

    private val connectivity = Connectivity
    lateinit var favoritePlaceViewModel: FavoritePlaceViewModel
//    private val db = ForecastDatabase.getDatabase(this.requireContext())

    private lateinit var favoriteListAdapter: FavoritePlacesFragmentAdapter

    var listOfFavoriteEntities = ArrayList<FavoritePlaceEntity>()

    val prefrences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(this.requireContext())




    companion object {
        fun newInstance() = FavoritePlaces()
        private var isFirstTime:Boolean = true
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.favorite_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritePlaceViewModel = ViewModelProvider(this).get(FavoritePlaceViewModel::class.java)

        initComponents()

        fab.setOnClickListener {
            if (!connectivity.isOnline(this.requireContext())) {
                Toast.makeText(
                    this.requireContext(),
                    R.string.offline,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                    if(isFirstTime) {
                        AlertDialog.Builder(this.requireContext())
                            .setTitle(changinglanguageOfTitle())
                            .setMessage(changinglanguageOfMessage())
                            .setPositiveButton(R.string.ok) { dialogInterface: DialogInterface?, i: Int ->
                                Toast.makeText(
                                    this.requireContext(),
                                    R.string.mapActivated,
                                    Toast.LENGTH_LONG
                                ).show()
                            }.create().show()
                        isFirstTime = false
                    }

                val mapFragment = MapFragment()
                activity?.supportFragmentManager?.beginTransaction()
                    ?.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    ?.replace(R.id.favPlacesFrameLayout, mapFragment)?.addToBackStack("mapFragment")
                    ?.commit()
            }
        }

        getLocalDataSource()
        // setData()
    }
    fun changinglanguageOfTitle(): String{
        val lang = prefrences.getString("LANGUAGE_SYSTEM", "")
        if(lang == "en"){
            return "Instructions"
        }else{
            return "تعليمات"
        }
    }

    fun changinglanguageOfMessage(): String{
        val lang = prefrences.getString("LANGUAGE_SYSTEM", "")
        if(lang == "en"){
            return "Navigate freely through the entire map and set a mark on your favorite place then make sure to save that location."
        }else{
            return "تنقل بحرية عبر الخريطة بأكملها وقم بتعيين علامة على مكانك المفضل ثم تأكد من حفظ هذا الموقع."
        }
    }


    private fun getLocalDataSource() {
        favoritePlaceViewModel.getLocalDataSource().observe(viewLifecycleOwner, Observer {
            if (it == null) {
                return@Observer
            }
//            Log.d("databaseResponsefrommap", "${it[0]} ")
            listOfFavoriteEntities = it as ArrayList<FavoritePlaceEntity>
            favoriteListAdapter.submitResponse(it)

        })
    }


    private fun initComponents() {

        val verticalLinearLayoutManager =
            LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)
        favListRc.layoutManager = verticalLinearLayoutManager

        favoriteListAdapter = FavoritePlacesFragmentAdapter(this.requireContext(), this)
        favListRc.adapter = favoriteListAdapter

//        (activity as AppCompatActivity).supportActionBar?.subtitle = "Today"
//        (activity as AppCompatActivity).supportActionBar?.title = "Favorites"

    }

    override fun onItemClick(position: Int) {
        if(Connectivity.isOnline(this.requireContext())) {
            Log.d("FavoAdapter", "onBindViewHolder: Clicked in adapter")

            favoritePlaceViewModel.setFavoriteDetailsData(
                listOfFavoriteEntities[position].lat,
                listOfFavoriteEntities[position].lon
            )

            val favoriteDetailsFragment = FavoriteDetailsFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                ?.replace(R.id.favPlacesFrameLayout, favoriteDetailsFragment)
                ?.addToBackStack("favoriteDetailsFragment")
                ?.commit()
        }else{
            Toast.makeText(this.requireContext(), R.string.offlineNotification,Toast.LENGTH_LONG).show()
        }


    }
}