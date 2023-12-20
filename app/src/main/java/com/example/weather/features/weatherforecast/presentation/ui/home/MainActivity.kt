package com.example.weather.features.weatherforecast.presentation.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.weather.R
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.features.weatherforecast.domain.weather.WeatherData
import com.example.weather.features.weatherforecast.presentation.adapters.FutureForecastRVAdapter
import com.example.weather.features.weatherforecast.presentation.adapters.HourlyWeatherRVAdapter
import com.example.weather.features.weatherforecast.presentation.adapters.RecentSearchAdapter
import com.example.weather.features.weatherforecast.presentation.adapters.WeatherDetailRVAdapter
import com.example.weather.features.weatherforecast.presentation.ui.favlocation.LocationActivity
import com.example.weather.features.weatherforecast.presentation.ui.utils.CustomProgressDialog
import com.example.weather.features.weatherforecast.presentation.ui.utils.LocationPermissionTextProvider
import com.example.weather.features.weatherforecast.presentation.ui.utils.PermissionBottomSheetDialog
import com.example.weather.features.weatherforecast.presentation.ui.search.SearchActivity
import com.example.weather.features.weatherforecast.presentation.ui.settings.SettingsActivity
import com.example.weather.features.weatherforecast.presentation.ui.utils.DialogForCouldntFindLocation
import com.example.weather.features.weatherforecast.presentation.ui.utils.RecentSearchHistoryDetails
import com.example.weather.features.weatherforecast.presentation.ui.utils.SearchHistoryManager
import com.example.weather.features.weatherforecast.presentation.states.WeatherDetailFORRV
import com.example.weather.features.weatherforecast.presentation.ui.analysis.ReviewActivity
import com.example.weather.features.weatherforecast.presentation.ui.newupdates.NewUpdatesActivity
import com.example.weather.features.weatherforecast.presentation.weatherviewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: WeatherViewModel by viewModels<WeatherViewModel>()
    private lateinit var permissionBottomSheetDialog: PermissionBottomSheetDialog
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    private val handler = Handler(Looper.getMainLooper())
    private val hourlyWeatherRVAdapter = HourlyWeatherRVAdapter()
    private val weatherDetailRVAdapter = WeatherDetailRVAdapter()
    private val futureForecastAdapter = FutureForecastRVAdapter()
    private val recentSearchAdapter = RecentSearchAdapter(this)
    private val currentDateTime: LocalDateTime? = LocalDateTime.now()
    private var isSearchResult: Boolean = false
    private lateinit var searchResult : String
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var headerImageView: ImageView
    private lateinit var headerTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolBar)

        /*
            Create Activity from searchResults
            If the maicActivity is created through by another activity that means it provided a location name to be
            seached so, use that location to show weather instead of using current location
        */

        val searched =  intent.getStringExtra("SearchResult")
        if(searched != null) {
            val recentHistoryList = getListOfSearchedLocations()
            searchResult = searched
            isSearchResult = true
            viewModel.loadWeatherInfo(searchResult, isSearchResult,recentHistoryList[0], "")
        }

        requestPermission()
        setUpNavigationDrawer()
        showProgressDialog()
        refreshWeatherDetails()

        setDetails()
        setUpRVAdapters()

        checkSearchHistory()

        binding.searchButton.setOnClickListener{
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
    }
    private fun setUpNavigationDrawer() {

        binding.toolBar.setNavigationOnClickListener {
             binding.drawerLayout.open()
        }

        val headerView = binding.navView.getHeaderView(0)
        headerImageView = headerView.findViewById(R.id.headerIv) as ImageView
        headerTextView = headerView.findViewById(R.id.headerTV) as TextView

        binding.navView.setNavigationItemSelectedListener {
         when(it.itemId){
                R.id.settings -> {
                    Intent(this, SettingsActivity::class.java).also {intent ->
                        startActivity(intent)
                    }
                    binding.drawerLayout.close()
                    return@setNavigationItemSelectedListener false
                }
             R.id.location -> {
                 Intent(this, LocationActivity::class.java).also { intent ->
                     startActivity(intent)
                 }
                 binding.drawerLayout.close()
                 return@setNavigationItemSelectedListener false
             }

             R.id.analysis -> {
                 Intent(this, ReviewActivity::class.java).also {intent ->
                     startActivity(intent)
                 }
                 binding.drawerLayout.close()
                 return@setNavigationItemSelectedListener false
             }

                R.id.newContents ->{
                    Intent(this, NewUpdatesActivity::class.java).also{intent->
                        startActivity(intent)
                    }
                    binding.drawerLayout.close()
                    return@setNavigationItemSelectedListener false
                }
            }
            false
        }

    }

    private fun requestPermission(){

        val permissionsToRequest = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

         requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){perms ->
             if(perms[permissionsToRequest[0]] == true && perms[permissionsToRequest[1]] == true){
                 checkPermission()
             }
             else{
                 if(perms[permissionsToRequest[0]] == false && perms[permissionsToRequest[1]] == false)
                     showPermissionBottomSheetRationale(permissionsToRequest[0])
                 else if(perms[permissionsToRequest[0]] == true && perms[permissionsToRequest[1]] == false)
                     showPermissionBottomSheetRationale(permissionsToRequest[1])
                 else if(perms[permissionsToRequest[0]] == false && perms[permissionsToRequest[1]] == true)
                     showPermissionBottomSheetRationale(permissionsToRequest[0])
             }
        }

        requestPermissionLauncher.launch(permissionsToRequest)

    }

    private fun showPermissionBottomSheetRationale(permission: String){

         permissionBottomSheetDialog = PermissionBottomSheetDialog(
            permissionTextProvider = LocationPermissionTextProvider(),
            isPermanentlyDeclined = !shouldShowRequestPermissionRationale(permission),
            onOkClick = {
                permissionBottomSheetDialog.dismiss()
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            },
            onGoToAppSettings = {
                permissionBottomSheetDialog.dismiss()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null))
                startActivity(intent)
            }
        )

        permissionBottomSheetDialog.show(supportFragmentManager, PermissionBottomSheetDialog.Tag)
    }


    /*
        It will first use current location to get weather info, if it fails then it will use favLocation
        to get the weather data and if it fails then it will use last search History to get weather info.

        For the first time use when none of these will be avalable we don't really have any choice other
        than asking the user to open their GPS to get the weather info.
     */

    private fun checkPermission(){

        val recentHistory = getListOfSearchedLocations()
        val searchHistoryManager = SearchHistoryManager.getInstance(applicationContext)

        val favLocation = searchHistoryManager.getFavLocation()

        if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){

            if(!isSearchResult)
                viewModel.loadWeatherInfo("", isSearchResult , recentHistory[0], favLocation)
            else
                viewModel.loadWeatherInfo(searchResult, isSearchResult, recentHistory[0], favLocation)
        /*
            if(!isSearchResult){
                if (recentHistory.isNotEmpty())
                    viewModel.loadWeatherInfo("", false, lastSearchedLocation = recentHistory[0])
                else
                    viewModel.loadWeatherInfo("", false, lastSearchedLocation = "")
            }else {
                viewModel.loadWeatherInfo(searchResult , true , "")
            }

        */
            Log.e("LocationPermission", "Permissions Granted")
        }
        else{
            Log.e("LocationPermission", "Permission Not Granted")
        }
    }

    private fun setDetails(){
        viewModel.state.observe(this) {

            if(!it.isLoading){
                binding.swipeRefreshLayout.isRefreshing = false
                dismissProgressDialog()
            }
            else if(it.error != null){
                showDialogForCouldntFindLocation()
            }

            if(it.error == null && !it.isLoading){
                Log.e("Weather Details" , "Details Updated")
                binding.linearLayoutMainActivity.visibility = View.VISIBLE

                binding.temperatureTV.text = it.weatherInfo?.currentWeatherData?.tempC?.toInt().toString()
                val feelsLikeTemp = "Feels like ${it.weatherInfo?.currentWeatherData?.feelslikeC}"
                binding.feelsLikeTV.text = feelsLikeTemp

                viewModel.state.value?.weatherInfo?.weatherDataPerDay?.get(0)?.get(currentDateTime!!.hour)?.apply {
                    val maxMinTemp = "${maxTemperatureInCelsius}/${minTemperatureInCelsius}"

                    binding.locationTV.text = location
                    binding.conditionTV.text = condition
                    binding.maxMinTempTV.text = maxMinTemp
                    binding.timeTV.text = time.hour.toString()

                    updateAndDisplayTime()

                    binding.weatherIV.load("https:$image")
                    headerImageView.load("https:$image"){
                        placeholder(R.drawable.weather_icon)
                    }
                    headerImageView.adjustViewBounds = true
                    headerTextView.text = this.location
                    binding.toolBar.title = this.location

                    weatherDetailRVAdapter.submitList(makeListForWeatherDetailRV(this))
                }
                hourlyWeatherRVAdapter.submitList(makeListForHourlyWeather(it.weatherInfo?.weatherDataPerDay))
                futureForecastAdapter.submitList(makeListForFutureForecast(it.weatherInfo?.weatherDataPerDay))

            }
        }
    }

    private fun setUpRVAdapters(){
        binding.hourlyWeatherRv.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = hourlyWeatherRVAdapter
        }

        binding.horizontalRV.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = weatherDetailRVAdapter
        }

        binding.futureForecastRV.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = futureForecastAdapter
        }

        binding.recentSearchesRV.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = recentSearchAdapter
        }
    }

    private fun  updateAndDisplayTime(){

        val dayOfWeek = currentDateTime!!.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val amPm = if (currentDateTime.hour < 12) "AM" else "PM"
        val formatter = DateTimeFormatter.ofPattern("hh:mm" , Locale.getDefault())
        val formattedTime = currentDateTime.format(formatter)

        val finalDateTime = "$dayOfWeek, $formattedTime $amPm"
        binding.timeTV.text = finalDateTime

        handler.postDelayed({
            updateAndDisplayTime()
        }, 1000)
    }

    private fun checkSearchHistory(){
        val recentHistory = getListOfSearchedLocations()

        recentHistory.forEach {
            Log.e("ItemChangeListener" , it)
        }

        if(recentHistory.isEmpty()){
            binding.recentSearchesRL.visibility = View.GONE
        }else{
            recentHistory.forEachIndexed { index, it ->
                if(index == 0)
                    viewModel.getCurrentForecast(it, true)
                else
                    viewModel.getCurrentForecast(it, false)
            }

            viewModel.currentWeatherState.observe(this){

                val recentSearchHistoryList = mutableListOf<RecentSearchHistoryDetails>()

                it.forEach {searchHistoryWeather ->
                    recentSearchHistoryList.add(
                        RecentSearchHistoryDetails(
                            locationName = searchHistoryWeather.locationName!!,
                            tempImageURL = searchHistoryWeather.currentWeather?.condition?.icon!!,
                            temperature = searchHistoryWeather.currentWeather.tempC.toString()
                        )
                    )
                }
                searchedHistoryToRetrievedLocationWeather(recentHistory, recentSearchHistoryList)
            }
        }
    }

    private fun searchedHistoryToRetrievedLocationWeather(
        searchedLocations: List<String>,
        retrievedLocationWeather: List<RecentSearchHistoryDetails>){

        val locationWeatherMap = retrievedLocationWeather.associateBy { it.locationName }

        val listOfRecentSearchAdapter = searchedLocations.mapNotNull { locationName ->
            locationWeatherMap[locationName]
        }

        recentSearchAdapter.submitList(listOfRecentSearchAdapter.toList())
    }

    private fun makeListForWeatherDetailRV(weatherData: WeatherData): List<WeatherDetailFORRV> {
        return listOf(
            WeatherDetailFORRV("AQI", viewModel.state.value?.weatherInfo?.currentWeatherData?.airQuality?.pm10.toString()),
            WeatherDetailFORRV("UV Index", weatherData.uv.toInt().toString()),
            WeatherDetailFORRV("Sunrise", weatherData.sunrise),
            WeatherDetailFORRV("Sunset", weatherData.sunset),
            WeatherDetailFORRV("Moonrise", weatherData.moonrise),
            WeatherDetailFORRV("Moonset", weatherData.moonset),
            WeatherDetailFORRV("Wind Speed", weatherData.windSpeedKph.toInt().toString().plus(" kmph")),
            WeatherDetailFORRV("Humidity", weatherData.humidity.toInt().toString().plus("%")),
            WeatherDetailFORRV("Visibility", weatherData.visibilityInKm.toInt().toString().plus(" km"))
        )
    }

    private fun makeListForHourlyWeather(weatherDataPerDay: Map<Int, List<WeatherData>>?): List<WeatherData>{
        val currentTime = currentDateTime?.toLocalTime()
        val hourlyWeatherDataList: MutableList<WeatherData> = mutableListOf()
        val currentHour = currentTime?.hour

       for(index in currentHour!!..23){
           weatherDataPerDay?.get(0)?.get(index)?.let { hourlyWeatherDataList.add(it) }
        }
        if(hourlyWeatherDataList.size < 24){
            for(index in 0..23){
                if(hourlyWeatherDataList.size < 24)
                    weatherDataPerDay?.get(1)?.get(index)?.let { hourlyWeatherDataList.add(it) }
            }
        }

        return hourlyWeatherDataList
    }

    private fun makeListForFutureForecast(weatherDataPerDay: Map<Int, List<WeatherData>>?): List<WeatherData>{
        val forecastWeatherDataList: MutableList<WeatherData> = mutableListOf()

        weatherDataPerDay?.forEach { (_, u) ->
            u.forEachIndexed { index, weatherData ->
                if (index == 0){
                    forecastWeatherDataList.add(weatherData)
                }
            }
        }

        return forecastWeatherDataList
    }

    private fun refreshWeatherDetails(){
        binding.swipeRefreshLayout.setOnRefreshListener {
            checkPermission()
        }
    }

    private fun showProgressDialog(){
        // Progress DIALOG
        customProgressDialog =
            CustomProgressDialog(
                this
            )
        customProgressDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        customProgressDialog.show()
    }

    private fun dismissProgressDialog(){
        customProgressDialog.dismiss()
    }

    private fun getListOfSearchedLocations(): List<String>{
        val searchHistoryManager = SearchHistoryManager.getInstance(context =  applicationContext)
        return searchHistoryManager.getSearchHistory()
    }

    private fun showDialogForCouldntFindLocation(){
        DialogForCouldntFindLocation { checkPermission() }.show(supportFragmentManager, "ERROR_DIALOG")
    }

}
