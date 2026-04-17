package com.example.pixelnothingwidgets.weather

import android.content.Context
import android.util.Log
import com.example.pixelnothingwidgets.data.SettingsDataStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

class WeatherRepository(private val context: Context) {

    private val settingsDataStore = SettingsDataStore(context)
    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.caiyunapp.com")
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(okhttp3.MediaType.parse("application/json")))
        .build()

    private val api = retrofit.create(CaiyunApi::class.java)

    private var cachedWeatherData: WeatherData? = null

    suspend fun getWeatherData(): WeatherData? {
        val token = settingsDataStore.caiyunToken.first()
        val latitude = settingsDataStore.latitude.first()
        val longitude = settingsDataStore.longitude.first()

        if (token.isEmpty() || latitude.isEmpty() || longitude.isEmpty()) {
            return null
        }

        try {
            val response = api.getRealtimeWeather(token, longitude, latitude)
            if (response.status == "ok") {
                val realtime = response.result.realtime
                val weatherData = WeatherData(
                    temperature = realtime.temperature,
                    humidity = realtime.humidity,
                    weatherCondition = realtime.weather,
                    weatherCode = realtime.weather_code,
                    lastUpdated = System.currentTimeMillis()
                )
                cachedWeatherData = weatherData
                return weatherData
            } else {
                Log.e("WeatherRepository", "API response status: ${response.status}")
                return cachedWeatherData
            }
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Error fetching weather: ${e.message}")
            return cachedWeatherData
        }
    }

    fun getCachedWeatherData(): WeatherData? {
        return cachedWeatherData
    }
}
