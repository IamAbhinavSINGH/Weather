package com.example.weather.features.weatherforecast.data.mappers

import com.example.weather.features.weatherforecast.data.models.forecast.DailyWeather
import com.example.weather.features.weatherforecast.data.models.forecast.Forecast
import com.example.weather.features.weatherforecast.domain.weather.WeatherData
import com.example.weather.features.weatherforecast.domain.weather.WeatherInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 *     This class maps the result return by api(Forecast API CALL) to a map of List<WeatherData>
 *     each index of map contains weather forecast details for each day and list associated with
 *     every index of this map contains hourly details of weather forecast of that map
 */

fun Forecast.toWeatherData():Map<Int, List<WeatherData>>{
    val map = mutableMapOf<Int, List<WeatherData>>()

    forecastday?.forEachIndexed { index, forecastday ->

        val list = mutableListOf<WeatherData>()

        forecastday.hour?.forEachIndexed { _ , hour ->

            val dateTimeString = hour.time!!.replace(" " , "T")

            val weatherData = WeatherData(
                temperatureCelsius = hour.tempC!!,
                temperatureFahrenheit = hour.tempF!!,
                feelsLikeCelsius = hour.feelslikeC!!,
                feelsLikeFahrenheit = hour.feelslikeF!!,
                condition = hour.condition!!.text!!,
                image = hour.condition.icon!!,
                windSpeedKph = forecastday.day!!.maxwindKph!!,
                humidity = forecastday.day.avghumidity!!,
                isDay = hour.isDay!!,
                uv = hour.uv!!,
                sunrise = forecastday.astro!!.sunrise!!,
                sunset = forecastday.astro.sunset!!,
                moonrise = forecastday.astro.moonrise!!,
                moonset = forecastday.astro.moonset!!,
                maxTemperatureInCelsius = forecastday.day.maxtempC!!,
                minTemperatureInCelsius = forecastday.day.mintempC!!,
                maxTemperatureInFahrenheit = forecastday.day.maxtempF!!,
                minTemperatureInFahrenheit = forecastday.day.mintempF!!,
                avgTemperatureInCelsius = forecastday.day.avgtempC!!,
                avgTemperatureInFahrenheit = forecastday.day.avgtempF!!,
                avgmaxTemperatureInCelsius = forecastday.day.maxtempC!!,
                avgmaxTemperatureInFahrenheit = forecastday.day.maxtempF!!,
                avgminTemperatureInCelsius = forecastday.day.mintempC!!,
                avgminTemperatureInFahrenheit = forecastday.day.mintempF,
                dayImage = forecastday.day.condition!!.icon!!,
                dayCondition = forecastday.day.condition.text!!,
                visibilityInKm = forecastday.day.avgvisKm!!,
                visibilityInMiles = forecastday.day.avgvisMiles!!,
                chanceOfRain = forecastday.day.dailyChanceOfRain!!,
                chanceOfSnow = forecastday.day.dailyChanceOfSnow!!,
                location = "",
                date = forecastday.date!!,
                time = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
            )
            list.add(weatherData)
        }
        map[index] = list
    }

    return map
}

fun DailyWeather.toWeatherInfo(): WeatherInfo{
    val weathermap = forecast!!.toWeatherData()
    weathermap.forEach { t, u ->
        u.forEach {
            it.location =  location!!.name.toString()
        }
    }
    val currentWeather = current!!
        
    return WeatherInfo(weathermap, currentWeather)
}
