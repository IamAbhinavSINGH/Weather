package com.example.weather.features.weatherforecast.domain.weather

import com.example.weather.features.weatherforecast.data.models.Current

data class WeatherInfo(

    val weatherDataPerDay: Map<Int, List<WeatherData>>,
    val currentWeatherData: Current
)