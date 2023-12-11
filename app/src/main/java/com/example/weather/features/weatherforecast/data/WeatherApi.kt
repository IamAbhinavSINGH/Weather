package com.example.weather.features.weatherforecast.data

import com.example.weather.features.weatherforecast.data.models.autocomplete.AutocompleteItem
import com.example.weather.features.weatherforecast.data.models.realtime.CurrentForecast
import com.example.weather.features.weatherforecast.data.models.forecast.DailyWeather
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("forecast.json")
    suspend fun getForecastWeather(
        @Query("key") key: String,
        @Query("q") location: String,
        @Query("days") days: String,
        @Query("aqi") aqi: String,
        @Query("alerts") alerts: String
    ): DailyWeather


    @GET("search.json")
    suspend fun getAutoCompleteLocation(
        @Query("key") key:String,
        @Query("q") location: String
    ): List<AutocompleteItem>

    @GET("current.json")
    suspend fun getCurrentForecast(
        @Query("key") key:String,
        @Query("q") location: String,
        @Query("aqi") aqi: String
    ): CurrentForecast
}