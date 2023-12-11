package com.example.weather.features.weatherforecast.domain.weather

import java.time.LocalDateTime

data class WeatherData (

    val time: LocalDateTime,
    val temperatureCelsius: Double,
    val temperatureFahrenheit: Double,
    val feelsLikeCelsius: Double,
    val feelsLikeFahrenheit: Double,
    val condition: String,
    val image: String,
    val windSpeedKph: Double,
    val humidity: Double,
    val isDay: Int,
    val uv: Double,
    val sunrise: String,
    val sunset: String,
    val moonrise: String,
    val moonset: String,
    val dayImage: String,
    val dayCondition: String,
    val avgTemperatureInCelsius: Double,
    val avgTemperatureInFahrenheit: Double,
    val avgmaxTemperatureInCelsius: Double,
    val avgmaxTemperatureInFahrenheit: Double,
    val avgminTemperatureInCelsius: Double,
    val avgminTemperatureInFahrenheit: Double,
    val maxTemperatureInCelsius: Double,
    val minTemperatureInCelsius: Double,
    val maxTemperatureInFahrenheit: Double,
    val minTemperatureInFahrenheit: Double,
    val visibilityInKm: Double,
    val visibilityInMiles: Double,
    val chanceOfRain: Int,
    val chanceOfSnow: Int,
    var location : String,
    val date : String
    )
