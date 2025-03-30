package com.sprint.lock.app.widge

import com.example.myapplication.bean.HourWeather
import com.example.myapplication.bean.NowWeather
import com.google.gson.Gson
import com.sprint.lock.app.widge.wheather.DayWeather
import com.sprint.lock.app.widge.wheather.PATH
import com.sprint.lock.app.widge.wheather.UrlUtils

// 获取天气对象
object WeatherUtils {
    private fun request(path: PATH, location: String): String? {
        val url = UrlUtils.gridUrl(path.value, location)
        url.loge()
        return RequestUtils.request(url)
    }

    fun nowWeather(location: String): NowWeather {
        val json = request(PATH.NOW, location)
        return Gson().fromJson(json, NowWeather::class.java)
    }

    fun day3Weather(location: String): DayWeather {
        val json = request(PATH.DAY_3, location)
        return Gson().fromJson(json, DayWeather::class.java)
    }

    fun day7Weather(location: String): DayWeather {
        val json = request(PATH.DAY_7, location)
        return Gson().fromJson(json, DayWeather::class.java)
    }

    fun hour24Weather(location: String): HourWeather {
        val json = request(PATH.HOUR_24, location)
        return Gson().fromJson(json, HourWeather::class.java)
    }
}