package com.example.weather.features.weatherforecast.presentation.ui.notification.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class WorkManager(context: Context, params: WorkerParameters): Worker(context, params) {

    override fun doWork(): Result {
        return Result.failure()
    }

    private fun showNotification(){

    }
}