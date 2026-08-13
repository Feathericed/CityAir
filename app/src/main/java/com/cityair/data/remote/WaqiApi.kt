package com.cityair.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WaqiApi
{
    @GET("feed/{city}")
    suspend fun getAirQuality(
        @Path("city") city: String,
        @Query("token") token: String
    ): WaqiResponse
}