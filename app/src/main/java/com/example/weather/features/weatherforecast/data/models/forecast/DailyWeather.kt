package com.example.weather.features.weatherforecast.data.models.forecast


import com.example.weather.features.weatherforecast.data.models.Current
import com.example.weather.features.weatherforecast.data.models.Location
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DailyWeather(
    @Json(name = "alerts")
    val alerts: Alerts?,
    @Json(name = "current")
    val current: Current?,
    @Json(name = "forecast")
    val forecast: Forecast?,
    @Json(name = "location")
    var location: Location?
)