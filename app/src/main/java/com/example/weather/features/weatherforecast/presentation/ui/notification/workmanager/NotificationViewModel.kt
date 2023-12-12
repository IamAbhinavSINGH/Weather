package com.example.weather.features.weatherforecast.presentation.ui.notification.workmanager

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.features.weatherforecast.domain.location.LocationTracker
import com.example.weather.features.weatherforecast.domain.repository.WeatherRepository
import com.example.weather.features.weatherforecast.domain.util.Resource
import com.example.weather.features.weatherforecast.presentation.utils.CurrentWeatherState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationTracker: LocationTracker
) : ViewModel(){

    private val _currentWeatherState = MutableLiveData<CurrentWeatherState>()
    val currentWeatherState: MutableLiveData<CurrentWeatherState> = _currentWeatherState

    private val APIKEY = "8e7c98fe55334f928ed164151232009"

    fun getCurrentWeatherForecast(locationName: String){
        viewModelScope.launch {

            locationTracker.getCurrentLocation()?.let {
                getWeatherInfo("${it.latitude},${it.longitude}")
            } ?: kotlin.run {

                if (locationName.isNotEmpty()) {
                    getWeatherInfo(locationName)
                } else {
                    _currentWeatherState.postValue(
                        CurrentWeatherState(
                            locationName = null,
                            currentWeather = null,
                            error = "Cannot find any location to show notification!!"
                        )
                    )
                }
            }
        }
    }

    private fun getWeatherInfo(locationName: String){
        viewModelScope.launch {
            when(val result = weatherRepository.getCurrentForecast(
                key = APIKEY,
                location = locationName,
                aqi = "NO"
            )){
                is Resource.Success -> {
                    _currentWeatherState.postValue(
                        CurrentWeatherState(
                            currentWeather = result.data?.current,
                            locationName = locationName,
                            error = null
                        )
                    )
                }
                is Resource.Error -> {
                    _currentWeatherState.postValue(
                        CurrentWeatherState(
                            currentWeather = null,
                            locationName = locationName,
                            error = result.message
                        )
                    )
                }

                else -> {}
            }
        }
    }

}