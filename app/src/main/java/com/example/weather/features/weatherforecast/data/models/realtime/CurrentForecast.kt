package com.example.weather.features.weatherforecast.data.models.realtime


import com.example.weather.features.weatherforecast.data.models.Current
import com.example.weather.features.weatherforecast.data.models.Location
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrentForecast(
    @Json(name = "current")
    val current: Current?,
    @Json(name = "location")
    val location: Location?
)