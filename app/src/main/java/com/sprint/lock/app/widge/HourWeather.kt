package com.example.myapplication.bean

data class HourWeather(
    val code: String,
    val fxLink: String,
    val hourly: List<Hourly>,
    val refer: Refer,
    val updateTime: String
) {
    data class Refer(
        val license: List<String>,
        val sources: List<String>
    )

    data class Hourly(
        val cloud: String,
        val dew: String,
        val fxTime: String,
        val humidity: String,
        val icon: String,
        val precip: String,
        val pressure: String,
        val temp: String,
        val text: String,
        val wind360: String,
        val windDir: String,
        val windScale: String,
        val windSpeed: String
    )
}