package com.sprint.lock.app.widge

import com.springs.common.application.BaseApplication
import com.sprint.lock.app.widge.wheather.City
import java.io.BufferedReader
import java.io.InputStreamReader

object CityUtils {

    private val cityListFileName = "China-City-List-latest.txt"

    fun cityList(fileName: String = cityListFileName): List<City> {
        val cityList = mutableListOf<City>()
        try {
            BufferedReader(InputStreamReader(BaseApplication.mApplication.assets.open(fileName))).use { reader ->
                var line: String
                while (reader.readLine().also { line = it } != null) {
                    val fields =
                        line.split(",")
                    City().apply {
                        locationId = fields[0]
                        locationNameZh = fields[1]
                        countryRegionZh = fields[2]
                        adm1NameZh = fields[3]
                        adm2NameZh = fields[4]
                        latitude = fields[5]
                        longitude = fields[6]
                    }.let {
                        cityList += it
                    }
                }
                return cityList
            }
        } catch (e: Exception) {
            return cityList
        }
    }
}