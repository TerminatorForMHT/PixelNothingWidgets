package com.example.pixelnothingwidgets.weather

import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val status: String,
    val result: Result
)

@Serializable
data class Result(
    val realtime: Realtime
)

@Serializable
class Realtime(
    val temperature: Double,
    val humidity: Double,
    val weather: String,
    val weather_code: String,
    val wind_speed: Double,
    val wind_direction: Int,
    val cloud_rate: Double
)

data class WeatherData(
    val temperature: Double,
    val humidity: Double,
    val weatherCondition: String,
    val weatherCode: String,
    val lastUpdated: Long
)

enum class WeatherCode {
    CLEAR_DAY,
    CLEAR_NIGHT,
    PARTLY_CLOUDY_DAY,
    PARTLY_CLOUDY_NIGHT,
    CLOUDY,
    LIGHT_RAIN,
    MODERATE_RAIN,
    HEAVY_RAIN,
    SNOW,
    FOG,
    WINDY,
    UNKNOWN
}
