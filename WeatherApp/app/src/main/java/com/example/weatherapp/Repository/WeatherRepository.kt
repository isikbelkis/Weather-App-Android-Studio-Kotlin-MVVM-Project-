package com.example.weatherapp.Repository

import com.example.weatherapp.Server.ApiServices

class WeatherRepository(val api:ApiServices) {

    fun getCurrentWeather(lat: Double,lng:Double,unit:String)=
        api.getCurrentWeather(lat,lng,unit,"c53ffd659baa4d334fa4fc99e5e525c6")
    fun getForecastWeather(lat: Double,lng:Double,unit:String)=
        api.getForecastWeather(lat,lng,unit,"c53ffd659baa4d334fa4fc99e5e525c6")
}