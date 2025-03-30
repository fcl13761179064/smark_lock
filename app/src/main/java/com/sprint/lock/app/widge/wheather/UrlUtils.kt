package com.sprint.lock.app.widge.wheather

// 当前，3天，7天，24小时
enum class PATH(val value: String) {
    NOW("now"),
    DAY_3("3d"),
    DAY_7("7d"),
    HOUR_24("24h"),
}

// 提供天气url
object UrlUtils {
    private val KEY = "d4071c9e44eb4fa28bf2c1d5f6d65011"

    // LocationID或经度,纬度坐标
    private fun url(path: PATH = PATH.NOW, location: String = "101010100") =
        "https://devapi.qweather.com/v7/weather/${path.value}?location=${location}&key=${KEY}"

    // 经度,纬度坐标
    fun gridUrl(path: String = "now", location: String = "116.41,39.92") =
        "https://devapi.qweather.com/v7/grid-weather/${path}?location=${location}&key=${KEY}"

}

fun main() {

}