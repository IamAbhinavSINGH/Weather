package com.example.weather.features.weatherforecast.presentation.states

import com.example.weather.features.weatherforecast.data.models.Current

data class CurrentWeatherState(
    val locationName: String?,
    val currentWeather: Current?,
    val error: String?
)
