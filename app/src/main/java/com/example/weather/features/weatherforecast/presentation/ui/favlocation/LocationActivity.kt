package com.example.weather.features.weatherforecast.presentation.ui.favlocation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.weather.R
import com.example.weather.databinding.ActivityLocationBinding
import com.example.weather.features.weatherforecast.data.models.autocomplete.AutocompleteItem
import com.example.weather.features.weatherforecast.presentation.adapters.LocationSearchResultRVAdapter
import com.example.weather.features.weatherforecast.presentation.adapters.OnItemClickListener
import com.example.weather.features.weatherforecast.presentation.adapters.RecentFAVSearchesRVAdapter
import com.example.weather.features.weatherforecast.presentation.adapters.SearchResultsRVAdatper
import com.example.weather.features.weatherforecast.presentation.adapters.onOtherFavLocationClickListener
import com.example.weather.features.weatherforecast.presentation.ui.utils.DialogForChangeFavLocation
import com.example.weather.features.weatherforecast.presentation.ui.utils.FavLocationInformationDialog
import com.example.weather.features.weatherforecast.presentation.ui.utils.RecentSearchHistoryDetails
import com.example.weather.features.weatherforecast.presentation.ui.utils.SearchHistoryManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationActivity : AppCompatActivity(), OnItemClickListener , onOtherFavLocationClickListener {

    private lateinit var binding : ActivityLocationBinding
    private val viewModel : FavLocationViewModel by viewModels<FavLocationViewModel>()
    private lateinit var searchResultAdapter : LocationSearchResultRVAdapter
    private lateinit var otherLocationAdapter : RecentFAVSearchesRVAdapter
    private lateinit var searchHistoryManager: SearchHistoryManager
    private var suggestionVisible = false
    private var initialized = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleSearchInteractions()

        searchResultAdapter = LocationSearchResultRVAdapter(this)
        binding.searchResultsRV.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = searchResultAdapter
        }

        otherLocationAdapter = RecentFAVSearchesRVAdapter(this , supportFragmentManager)
        binding.favoriteLocationsRV.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = otherLocationAdapter
        }

        searchHistoryManager = SearchHistoryManager.getInstance(applicationContext)
        initalizeFavLocation()
        inflateOtherLocations()

        binding.backBtnSettings.setOnClickListener {
            onBackPressed()
        }

        binding.favLocationInfoFABtn.setOnClickListener {
            showInfoAboutFavLocation()
        }
    }

    private fun initalizeFavLocation(){
        val searchList = searchHistoryManager.getSearchHistory()
        Log.e("searchList" , "${searchList.size}")
        if(searchList.isEmpty()){
            binding.favLocationTV.text = "Add Favorite Location Here"
            binding.favLocationTV.textSize = 12F
            binding.favLocationTempTV.visibility = View.INVISIBLE
            binding.favLocationIV.visibility = View.INVISIBLE
        }else{
            setFavLocation(searchList[0])
            initialized = false
        }
    }
    private fun handleSearchInteractions(){
        binding.searchViewLocation.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null){
                    setFavLocation(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText != null) {
                    binding.searchResultsCV.visibility = View.VISIBLE
                    binding.searchResultsRV.visibility = View.VISIBLE
                    suggestionVisible = true
                    showSearchResults(newText)
                }
                return true
            }

        })
    }
    private fun setFavLocation(location: String){
        viewModel.getCurrentWeatherData(location)

        viewModel.locationWeatherData.observe(this){
            if(it.error == null){
                binding.favLocationTV.text = location
                binding.favLocationIV.load("https://${it.currentWeather?.condition?.icon}")
                binding.favLocationTempTV.text = it.currentWeather?.tempC?.toInt().toString()

                binding.searchResultsCV.visibility = View.INVISIBLE
                binding.searchResultsRV.visibility = View.INVISIBLE
            }
        }

    }
    private fun showSearchResults(locationToBeSearched : String){
        viewModel.getAutoCompleteResults(locationToBeSearched)

        viewModel.autoCompleteResults.observe(this){
            if(it.searchResults?.isNotEmpty() == true && suggestionVisible){
                searchResultAdapter.submitList(it.searchResults)
            }
        }
    }
    override fun onItemClick(selectedItem: AutocompleteItem) {
        selectedItem.name?.let {locationName ->
            setFavLocation(locationName)
        }
    }
    private fun inflateOtherLocations(){
        val searchList = searchHistoryManager.getSearchHistory()

        if(searchList.isNotEmpty())
            viewModel.getListWeatherData(searchList)

        viewModel.listWeatherDetails.observe(this){
            val recentSearchHistoryList = mutableListOf<RecentSearchHistoryDetails>()

            it.forEach { weatherDeatil->
                recentSearchHistoryList.add(
                    RecentSearchHistoryDetails(
                        locationName = weatherDeatil.locationName!!,
                        temperature = weatherDeatil.currentWeather?.tempC?.toInt().toString(),
                        tempImageURL = weatherDeatil.currentWeather?.condition?.icon!!
                    )
                )
            }

            val locationWeatherMap = recentSearchHistoryList.associateBy { it.locationName }

            val listOfRecentSearchAdapter = searchList.mapNotNull { locationName ->
                locationWeatherMap[locationName]
            }
            otherLocationAdapter.submitList(listOfRecentSearchAdapter.toList())
        }
    }

    private fun showInfoAboutFavLocation(){
        FavLocationInformationDialog().show(supportFragmentManager, "Fav_Location_Information")
    }
    override fun onBackPressed() {
        if(binding.searchResultsCV.visibility == View.VISIBLE || binding.searchResultsRV.visibility == View.VISIBLE){
            binding.searchResultsCV.visibility = View.GONE
            binding.searchResultsRV.visibility = View.GONE
        }
        else{
            super.onBackPressed()
        }
    }
    override fun onOtherLocationClick(locationName: String) {
      setFavLocation(locationName)
    }
}