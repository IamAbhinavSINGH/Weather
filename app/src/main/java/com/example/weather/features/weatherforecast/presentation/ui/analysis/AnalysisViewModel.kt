package com.example.weather.features.weatherforecast.presentation.ui.analysis

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.features.weatherforecast.domain.location.LocationTracker
import com.example.weather.features.weatherforecast.domain.repository.WeatherRepository
import com.example.weather.features.weatherforecast.domain.util.Resource
import com.example.weather.features.weatherforecast.presentation.states.WeatherState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
     private val weatherRepository: WeatherRepository,
    private val locationTracker: LocationTracker
): ViewModel(){

    private val _weatherDetailState = MutableLiveData<WeatherState>()
    val weatherDetailsState = _weatherDetailState

    private val APIKEY = "8e7c98fe55334f928ed164151232009"

     fun getForecastWeather(location : String){
        viewModelScope.launch {
            _weatherDetailState.postValue(
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
                    _weatherDetailState.postValue(
                        WeatherState(
                            weatherInfo = result.data,
                            searchResults = null,
                            isLoading = false,
                            error = null
                        )
                    )
                }
                is Resource.Error -> {
                    _weatherDetailState.postValue(
                        WeatherState(
                            weatherInfo = null,
                            searchResults = null,
                            isLoading = false,
                            error = result.message
                        )
                    )
                    Log.e("GhaziabadMap", result.message!!.toString())
                }

            }
        }
    }
}