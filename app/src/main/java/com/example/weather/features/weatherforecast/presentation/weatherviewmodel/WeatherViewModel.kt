package com.example.weather.features.weatherforecast.presentation.weatherviewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.features.weatherforecast.domain.location.LocationTracker
import com.example.weather.features.weatherforecast.domain.repository.WeatherRepository
import com.example.weather.features.weatherforecast.domain.util.Resource
import com.example.weather.features.weatherforecast.presentation.states.CurrentWeatherState
import com.example.weather.features.weatherforecast.presentation.states.WeatherState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationTracker: LocationTracker
): ViewModel(){

    private val _state = MutableLiveData<WeatherState>()
    val state: MutableLiveData<WeatherState> = _state

    private val _currentWeatherState = MutableLiveData<List<CurrentWeatherState>>()
    val currentWeatherState: MutableLiveData<List<CurrentWeatherState>> = _currentWeatherState

    private val APIKEY = "8e7c98fe55334f928ed164151232009"

    private var recentWeatherList = mutableListOf<CurrentWeatherState>()

    fun loadWeatherInfo(location : String,
                        searchResult: Boolean,
                        lastSearchedLocation: String,
                        favLocation: String
    ){
        if(!searchResult){
            viewModelScope.launch{
                _state.postValue(
                    WeatherState(
                        weatherInfo = null,
                        searchResults = null,
                        isLoading = true,
                        error = null
                    )
                )
                locationTracker.getCurrentLocation()?.let{
                    //Location retrieved Successfully
                    Log.e("Location", "Location retrieved successfully!!")
                    getForecastWeather("${it.latitude},${it.longitude}")

                }?: kotlin.run {
                    //Location couldn't retrieved, so using favLocation
                    if (favLocation.isNotBlank()) {
                        getForecastWeather(favLocation)
                    } else {
                        //favLocation is empty too so using last searched location
                        if (lastSearchedLocation.isNotEmpty())
                            getForecastWeather(lastSearchedLocation)
                        else
                            _state.postValue(
                                WeatherState(
                                    weatherInfo = null,
                                    searchResults = null,
                                    isLoading = false,
                                    error = "Location couldn't be retrieved!!"
                                )
                            )

                        Log.e("ForecastError",   "Current Location Not Found ")
                    }
                }
            }
        }
        else{
            getForecastWeather(location)
        }
    }
    private fun getForecastWeather(location : String){
        viewModelScope.launch {

            _state.postValue(
                WeatherState(
                    weatherInfo = null,
                    searchResults = null,
                    isLoading = true,
                    error = null
                )
            )

            when (val result = weatherRepository.getWeatherForecast(
                key = APIKEY,
                location = location,
                days = "10",
                aqi = "yes",
                alerts = "yes"
            )) {
                is Resource.Success -> {
                    _state.postValue(
                        WeatherState(
                            weatherInfo = result.data,
                            searchResults = null,
                            isLoading = false,
                            error = null
                        )
                    )
                }
                is Resource.Error -> {
                    _state.postValue(
                        WeatherState(
                            weatherInfo = null,
                            searchResults = null,
                            isLoading = false,
                            error = result.message
                        )
                    )
                    Log.e("ForecastError", result.message!!.toString())
                }

                else -> {}
            }
        }
    }
    fun getAutoCompleteResults(location: String){
        viewModelScope.launch {
            when (val result = weatherRepository.getAutoCompleteLocation( key = APIKEY,location = location)){
                is Resource.Success -> {
                    _state.postValue(
                        WeatherState(
                            weatherInfo = null,
                            searchResults = result.data,
                            isLoading = false,
                            error = null
                        )
                    )
                }
                is Resource.Error -> {
                    _state.postValue(
                        WeatherState(
                            weatherInfo = null,
                            searchResults = null,
                            isLoading = false,
                            error = result.message
                        )
                    )
                }
                else -> {}
            }
        }
    }
    fun getCurrentForecast(location: String , firstCall : Boolean){

        if(firstCall) recentWeatherList.clear()

        _currentWeatherState.value = emptyList<CurrentWeatherState>()

        viewModelScope.launch(Dispatchers.Main){
            when (val result = weatherRepository.getCurrentForecast(
                key = APIKEY,
                location = location,
                aqi = "NO"
            )
            ) {
                is Resource.Success -> {
                    recentWeatherList.add(CurrentWeatherState(
                        locationName = location,
                        currentWeather = result.data?.current,
                        error = null
                    ))
                    _currentWeatherState.postValue(
                        recentWeatherList
                    )

                    Log.e("ItemChangeListener", "$location , result retrieve Successful ")
                }
                is Resource.Error -> {
                    recentWeatherList.add(CurrentWeatherState(
                        locationName = location,
                        currentWeather = null,
                        error = result.message
                    ))

                    Log.e("ItemChangeListener" , "$location , result failed")
                }
            }
            Log.e("ItemChangeListener" , recentWeatherList.size.toString())
        }
    }
}