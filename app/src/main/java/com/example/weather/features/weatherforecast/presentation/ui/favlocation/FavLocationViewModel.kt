package com.example.weather.features.weatherforecast.presentation.ui.favlocation

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
class FavLocationViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val location : LocationTracker
) : ViewModel(){


    private  val API_KEY = "8e7c98fe55334f928ed164151232009"

    private val _autoCompleteResults = MutableLiveData<WeatherState>()
    val autoCompleteResults : MutableLiveData<WeatherState> = _autoCompleteResults

    private val _locationWeatherData = MutableLiveData<CurrentWeatherState>()
    val locationWeatherData : MutableLiveData<CurrentWeatherState> = _locationWeatherData

    private val _listWeatherDetails = MutableLiveData<List<CurrentWeatherState>>()
    val listWeatherDetails = _listWeatherDetails

    private var recentWeatherList = mutableListOf<CurrentWeatherState>()

    fun getAutoCompleteResults(location: String){
        viewModelScope.launch{
            _autoCompleteResults.postValue(
                WeatherState(
                    weatherInfo = null,
                    searchResults = null,
                    isLoading = true,
                    error = null
                )
            )

            when(val result = weatherRepository.getAutoCompleteLocation(API_KEY, location)){
                is Resource.Success -> {
                    _autoCompleteResults.postValue(
                        WeatherState(
                            weatherInfo = null,
                            searchResults = result.data,
                            isLoading = false,
                            error = null,
                        )
                    )
                }

                is Resource.Error -> {
                    _autoCompleteResults.postValue(
                        WeatherState(
                            weatherInfo = null,
                            searchResults = null,
                            error = result.message,
                            isLoading = false
                        )
                    )
                }
            }
        }
    }

    fun getCurrentWeatherData(location: String){
        viewModelScope.launch {
            when(val result = weatherRepository.getCurrentForecast(API_KEY, location, "NO")){
                is Resource.Success -> {
                    _locationWeatherData.postValue(
                        CurrentWeatherState(
                            currentWeather = result.data?.current,
                            locationName = location,
                            error = null
                        )
                    )
                    Log.e("Weather Info" , "Searched Succesfully!!")
                }

                is Resource.Error -> {
                        _locationWeatherData.postValue(
                            CurrentWeatherState(
                                currentWeather = null,
                                locationName = location,
                                error = result.message
                            )
                        )
                }
            }
        }
    }

    fun getListWeatherData(locationList : List<String>){
        locationList.forEachIndexed{ index: Int, location: String ->
            if(index == 0)
                getWeatherDataForOneListItem(location, true)
            else
                getWeatherDataForOneListItem(location, false)
        }


    }

    private fun getWeatherDataForOneListItem(locationName : String , firstCall : Boolean){
        if(firstCall) recentWeatherList.clear()

        _listWeatherDetails.value = emptyList<CurrentWeatherState>()

        viewModelScope.launch(Dispatchers.Main){
            when (val result = weatherRepository.getCurrentForecast(
                key = API_KEY,
                location = locationName,
                aqi = "NO"
            )
            ) {
                is Resource.Success -> {
                    recentWeatherList.add(
                        CurrentWeatherState(
                            locationName = locationName,
                            currentWeather = result.data?.current,
                            error = null
                        )
                    )
                    _listWeatherDetails.postValue(
                        recentWeatherList
                    )

                    Log.e("ItemChangeListener", "$location , result retrieve Successful ")
                }

                is Resource.Error -> {
                    recentWeatherList.add(
                        CurrentWeatherState(
                            locationName = locationName,
                            currentWeather = null,
                            error = result.message
                        )
                    )
                    _listWeatherDetails.postValue(
                        recentWeatherList
                    )
                }
            }
        }
    }
}