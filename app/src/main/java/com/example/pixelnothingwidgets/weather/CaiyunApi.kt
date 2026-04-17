package com.example.pixelnothingwidgets.weather

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CaiyunApi {
    @GET("/v2.6/{token}/{lng},{lat}/realtime")
    suspend fun getRealtimeWeather(
        @Path("token") token: String,
        @Path("lng") longitude: String,
        @Path("lat") latitude: String,
        @Query("lang") language: String = "en"
    ): WeatherResponse
}
