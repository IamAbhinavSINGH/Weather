package com.example.weather.features.weatherforecast.domain.repository

import com.example.weather.features.weatherforecast.data.models.autocomplete.AutocompleteItem
import com.example.weather.features.weatherforecast.data.models.realtime.CurrentForecast
import com.example.weather.features.weatherforecast.domain.util.Resource
import com.example.weather.features.weatherforecast.domain.weather.WeatherInfo

interface WeatherRepository {

    suspend fun getWeatherForecast(key:String,location: String, days: String, aqi: String, alerts: String): Resource<WeatherInfo>

    suspend fun getAutoCompleteLocation(key: String, location: String): Resource<List<AutocompleteItem>>

    suspend fun getCurrentForecast(key:String, location: String, aqi: String): Resource<CurrentForecast>
}