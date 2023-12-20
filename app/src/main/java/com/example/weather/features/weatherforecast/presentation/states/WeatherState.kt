package com.example.weather.features.weatherforecast.presentation.states

import com.example.weather.features.weatherforecast.data.models.autocomplete.AutocompleteItem
import com.example.weather.features.weatherforecast.domain.weather.WeatherInfo

data class WeatherState(
    val weatherInfo: WeatherInfo? = null,
    val searchResults: List<AutocompleteItem>? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)