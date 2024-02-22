package com.example.weather.features.weatherforecast.presentation.ui.notification.workmanager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.weather.R
import com.example.weather.features.weatherforecast.data.WeatherApi
import com.example.weather.features.weatherforecast.data.repository.WeatherRepositoryImplementation
import com.example.weather.features.weatherforecast.domain.util.Resource
import com.example.weather.features.weatherforecast.presentation.ui.utils.SearchHistoryManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NotificationWorkManager @Inject constructor(
    val context: Context,
    params: WorkerParameters ): CoroutineWorker(context, params) {

    private val NOTIFICATION_ID = 123
    private lateinit var searchHistoryManager: SearchHistoryManager
    private var temperatureInCelsius = 0
    private var temperatureInFahrenheit = 0
    private var imageUrl = ""
    private var errorMsg = ""

    private val api = Retrofit.Builder()
        .baseUrl("https://api.weatherapi.com/v1/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)

    private val weatherRepository = WeatherRepositoryImplementation(api)

    override suspend fun doWork(): Result {

        searchHistoryManager = SearchHistoryManager.getInstance(applicationContext)
        val favLocation = searchHistoryManager.getFavLocation()

        var notificationText: String
        var iconInNotification: String

        return withContext(Dispatchers.IO){
            val wasCallSuccessful = getWeatherInfo(favLocation)

            if(wasCallSuccessful){
                notificationText = "$temperatureInCelsius°C in $favLocation"
                iconInNotification = imageUrl
                startForeGroundService(notificationText, iconInNotification)

                scheduleNextWork()

                Log.e("startForegroundService", notificationText)

                return@withContext Result.success()
            }else{
                notificationText = "Couldn't find weather detail!!!"
                startForeGroundService(notificationText, "")

                Log.e("startForegroundService", notificationText)
                return@withContext Result.failure()
            }
        }
    }
    private fun startForeGroundService(notificationText: String, imageUrl: String){

        val manager = NotificationManagerCompat.from(applicationContext)
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        manager.notify( NOTIFICATION_ID ,
            NotificationCompat.Builder(applicationContext, "WeatherInformation")
                .setContentText(notificationText)
                .setContentTitle("Weather")
                .setSmallIcon(R.drawable.ic_weather_icon)
                .build()
        )

    }

    private val APIKEY = "8e7c98fe55334f928ed164151232009"
    private suspend fun getWeatherInfo(locationName: String): Boolean{
        when(val result = weatherRepository.getCurrentForecast(
            key = APIKEY,
            location = locationName,
            aqi = "NO"
        )){
            is Resource.Success -> {
                temperatureInCelsius = result.data?.current?.tempC?.toInt()!!
                temperatureInFahrenheit = result.data.current.tempF?.toInt()!!
                imageUrl = result.data.current.condition?.icon!!

                return true
            }
            is Resource.Error -> {
                errorMsg = result.message.toString()
                return false
            }
        }
    }
    private fun scheduleNextWork() {
        // Calculate the time for the next work execution (e.g., 24 hours later)
        val nextExecutionTime = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24)

        // Create a new OneTimeWorkRequest for the next execution
        val nextWorkRequest = OneTimeWorkRequestBuilder<NotificationWorkManager>()
            .setInitialDelay(nextExecutionTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
            .build()

        // Enqueue the next work request
        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueueUniqueWork("NextWork", ExistingWorkPolicy.REPLACE, nextWorkRequest)
    }

}