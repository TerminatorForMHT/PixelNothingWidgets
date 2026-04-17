package com.example.pixelnothingwidgets.work

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pixelnothingwidgets.weather.WeatherRepository

class WeatherSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        try {
            val weatherRepository = WeatherRepository(applicationContext)
            weatherRepository.getWeatherData()
            
            // Notify widgets to update
            val intent = Intent("com.example.pixelnothingwidgets.ACTION_UPDATE_WIDGETS")
            applicationContext.sendBroadcast(intent)
            
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    companion object {
        fun enqueueWork(context: Context) {
            val workManager = androidx.work.WorkManager.getInstance(context)
            val workRequest = androidx.work.PeriodicWorkRequestBuilder<WeatherSyncWorker>(30, java.util.concurrent.TimeUnit.MINUTES)
                .setConstraints(
                    androidx.work.Constraints.Builder()
                        .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                        .build()
                )
                .build()
            workManager.enqueueUniquePeriodicWork(
                "WeatherSyncWorker",
                androidx.work.ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }
    }
}
