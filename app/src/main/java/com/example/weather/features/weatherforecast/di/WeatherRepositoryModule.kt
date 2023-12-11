package com.example.weather.features.weatherforecast.di

import com.example.weather.features.weatherforecast.data.repository.WeatherRepositoryImplementation
import com.example.weather.features.weatherforecast.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WeatherRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        weatherRepositoryImplementation: WeatherRepositoryImplementation
    ): WeatherRepository
}