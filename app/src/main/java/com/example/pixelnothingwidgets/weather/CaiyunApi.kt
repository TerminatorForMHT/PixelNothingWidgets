package com.example.pixelnothingwidgets.weather

import kotlinx.serialization.json.Json

object CaiyunApi {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    suspend fun getRealtimeWeather(
        token: String,
        longitude: String,
        latitude: String,
        client: okhttp3.OkHttpClient
    ): WeatherResponse? {
        val url = "https://api.caiyunapp.com/v2.6/$token/$longitude,$latitude/realtime?lang=en"

        return try {
            val request = okhttp3.Request.Builder()
                .url(url)
                .get()
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()

            if (body != null) {
                json.decodeFromString<WeatherResponse>(body)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
