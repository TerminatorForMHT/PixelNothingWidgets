package com.example.pixelnothingwidgets.weather

import android.content.Context
import android.util.Log
import com.example.pixelnothingwidgets.data.SettingsDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class WeatherRepository(private val context: Context) {

    private val settingsDataStore = SettingsDataStore(context)

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private var cachedWeatherData: WeatherData? = null

    suspend fun getWeatherData(): WeatherData? = withContext(Dispatchers.IO) {
        val token = settingsDataStore.caiyunToken.first()
        val latitude = settingsDataStore.latitude.first()
        val longitude = settingsDataStore.longitude.first()

        if (token.isEmpty() || latitude.isEmpty() || longitude.isEmpty()) {
            return@withContext null
        }

        try {
            val response = CaiyunApi.getRealtimeWeather(token, longitude, latitude, okHttpClient)
            if (response != null && response.status == "ok") {
                val realtime = response.result.realtime
                val weatherData = WeatherData(
                    temperature = realtime.temperature,
                    humidity = realtime.humidity,
                    weatherCondition = realtime.weather,
                    weatherCode = realtime.weather_code,
                    lastUpdated = System.currentTimeMillis()
                )
                cachedWeatherData = weatherData
                return@withContext weatherData
            } else {
                Log.e("WeatherRepository", "API response status: ${response?.status}")
                return@withContext cachedWeatherData
            }
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Error fetching weather: ${e.message}")
            return@withContext cachedWeatherData
        }
    }

    fun getCachedWeatherData(): WeatherData? {
        return cachedWeatherData
    }
}
