package com.example.meteorologiaapp.network

import com.example.meteorologiaapp.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// diz ao Retrofit como se deve comunicar com a API do OpenWeatherMap
interface WeatherService {

    // O caminho específico na API para ir buscar o tempo atual
    @GET("data/2.5/weather")
    suspend fun getExchangeWeather(
        @Query("q") cityName: String,             // A cidade que o utilizador escreveu
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"  // Para vir em Graus Celsius e não em Fahrenheit
    ): Response<WeatherResponse>
}