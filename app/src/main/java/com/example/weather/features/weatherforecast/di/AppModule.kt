package com.example.weather.features.weatherforecast.di

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.example.weather.features.weatherforecast.data.WeatherApi
import com.example.weather.features.weatherforecast.data.repository.WeatherRepositoryImplementation
import com.example.weather.features.weatherforecast.domain.repository.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWeatherForecast(): WeatherApi{
        return Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(app: Application): FusedLocationProviderClient{
        return LocationServices.getFusedLocationProviderClient(app)
    }
}