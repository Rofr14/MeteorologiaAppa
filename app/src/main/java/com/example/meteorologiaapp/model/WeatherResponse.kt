package com.example.meteorologiaapp.model


data class WeatherResponse(
    val main: MainData,
    val wind: WindData,
    val sys: SysData,
    val name: String
)

// temperatura e a humidade
data class MainData(
    val temp: Double,
    val humidity: Int
)

// velocidade do vento
data class WindData(
    val speed: Double
)

//  sigla do país
data class SysData(
    val country: String
)