package com.example.weather.features.weatherforecast.tests

import android.util.Log
import com.example.weather.features.weatherforecast.data.WeatherApi
import com.example.weather.features.weatherforecast.data.mappers.toWeatherInfo
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class WeatherApiTest {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    private val api: WeatherApi by lazy {
        retrofit.create(WeatherApi::class.java)
    }

    @Test
    fun getWeatherForecast() = runBlocking {
        val result = api.getForecastWeather(
            key = "8e7c98fe55334f928ed164151232009",
            location = "Ghaziabad",
            days = "8",
            aqi = "yes",
            alerts = "yes"
        ).toWeatherInfo()

        assertNotNull(result)
    }

    @Test
    fun getAutoComplete() = runBlocking {
        val result = api.getAutoCompleteLocation(
            key = "8e7c98fe55334f928ed164151232009",
            location = "London"
        )

        assertNotNull(result)
    }

}

