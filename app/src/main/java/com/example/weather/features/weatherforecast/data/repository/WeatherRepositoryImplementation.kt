package com.example.weather.features.weatherforecast.data.repository

import androidx.compose.runtime.key
import com.example.weather.features.weatherforecast.data.WeatherApi
import com.example.weather.features.weatherforecast.data.mappers.toWeatherInfo
import com.example.weather.features.weatherforecast.data.models.autocomplete.AutocompleteItem
import com.example.weather.features.weatherforecast.data.models.realtime.CurrentForecast
import com.example.weather.features.weatherforecast.domain.repository.WeatherRepository
import com.example.weather.features.weatherforecast.domain.util.Resource
import com.example.weather.features.weatherforecast.domain.weather.WeatherInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherRepositoryImplementation @Inject constructor(
    private val api: WeatherApi
): WeatherRepository {

    override suspend fun getWeatherForecast(key:String, location: String, days: String, aqi: String, alerts: String):
            Resource<WeatherInfo> {
        return withContext(Dispatchers.IO) {
            try {
                Resource.Success(
                    data = api.getForecastWeather(
                        key = key,
                        location = location,
                        days = days,
                        aqi = aqi,
                        alerts = alerts
                    ).toWeatherInfo()
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Resource.Error(e.message ?: "An unknown error occurred.")
            }
        }
    }


    override suspend fun getAutoCompleteLocation(key: String, location: String): Resource<List<AutocompleteItem>> {
        return withContext(Dispatchers.IO) {
            try {
                Resource.Success(
                    data = api.getAutoCompleteLocation(key = key, location = location)
                )
            } catch (e: Exception) {
                Resource.Error(e.message ?: "An unknown error occurred.")
            }
        }
    }

    override suspend fun getCurrentForecast(
        key: String,
        location: String,
        aqi: String
    ): Resource<CurrentForecast> {
        return withContext(Dispatchers.IO){
            try{
                Resource.Success(
                    data = api.getCurrentForecast(key = key, location = location, aqi = aqi)
                )
            }catch (e:Exception){
                Resource.Error(e.message ?: "An unknown error occured.")
            }
        }
    }
}