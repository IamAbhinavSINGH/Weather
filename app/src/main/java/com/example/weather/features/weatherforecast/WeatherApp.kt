package com.example.weather.features.weatherforecast

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WeatherApp: Application(){

    val channel_id = "WeatherInformation";

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                channel_id,
                "Weather Forecast",
                NotificationManager.IMPORTANCE_HIGH
            )

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}