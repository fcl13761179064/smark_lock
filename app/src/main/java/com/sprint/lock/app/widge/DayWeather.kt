package com.example.myapplication.bean


data class DayWeather(
    val code: String,
    val daily: List<Daily>,
    val fxLink: String,
    val refer: Refer,
    val updateTime: String
) {


    data class Daily(
        val cloud: String,
        val fxDate: String,
        val humidity: String,
        val iconDay: String,
        val iconNight: String,
        val precip: String,
        val pressure: String,
        val tempMax: String,
        val tempMin: String,
        val textDay: String,
        val textNight: String,
        val wind360Day: String,
        val wind360Night: String,
        val windDirDay: String,
        val windDirNight: String,
        val windScaleDay: String,
        val windScaleNight: String,
        val windSpeedDay: String,
        val windSpeedNight: String
    )

    data class Refer(
        val license: List<String>,
        val sources: List<String>
    )

}