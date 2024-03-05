package com.example.weatherapp.Server

import android.telecom.Call
import com.example.weatherapp.model.CurrentResponseApi
import com.example.weatherapp.model.ForecastResponseApi
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {
    @GET("data/2.5/weather")
    fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lng: Double,
        @Query("units") units: String,
        @Query("appid") ApiKey: String,
    ): retrofit2.Call<CurrentResponseApi>


    @GET("data/2.5/forecast")
    fun getForecastWeather(
        @Query("lat") lat: Double,
        @Query("lon") lng: Double,
        @Query("units") units: String,
        @Query("appid") ApiKey: String,
    ): retrofit2.Callback<ForecastResponseApi>
}