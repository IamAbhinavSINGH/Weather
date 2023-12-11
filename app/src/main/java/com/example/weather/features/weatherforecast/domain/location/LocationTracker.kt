package com.example.weather.features.weatherforecast.domain.location

import android.location.Location

interface LocationTracker {

    suspend fun getCurrentLocation(): Location?
}