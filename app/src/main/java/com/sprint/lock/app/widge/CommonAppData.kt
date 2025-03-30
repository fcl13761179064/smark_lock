package com.sprint.lock.app.widge

import com.blankj.utilcode.util.TimeUtils
import java.util.Calendar
import java.util.Date

/**
 * Description 一些自定义数据
 * Author: finaly
 * Date:2023/10/12 09:52
 */
object CommonAppData {

    val keybordLetterData = mutableListOf(
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "L",
        "M",
        "N",
        "O",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W",
        "X",
        "Y",
        "Z",
        "数字"
        )


    val keybordLetterNumData = mutableListOf(
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "L",
        "M",
        "1",
        "2",
        "3",
        "Q",
        "4",
        "5",
        "6",
        "U",
        "7",
        "8",
        "9",
        "Y",
        "0",
        "收起"
    )

    val keybordFullSearchBox = mutableListOf(
        "A",
        "B",
        "C",
        "D",
        "E",
        "1",
        "2",
        "3",
        "I",
        "J",
        "K",
        "L",
        "M",
        "N",
        "4",
        "5",
        "6",
        "0",
        "s",
        "T",
        "U",
        "V",
        "W",
        "7",
        "8",
        "9",
        "收起"
    )

    val starContryData = mutableListOf(
        "全部",
        "内地",
        "港台",
        "欧美",
        "日韩"
    )
    val starSexData = mutableListOf(
        "全部",
        "男",
        "女",
        "组合",
    )

    fun getWifiSoftLetterList(isSmall: Boolean): List<String> {
        if (isSmall) {
            return mutableListOf(
                "a",
                "b",
                "c",
                "d",
                "e",
                "f",
                "g",
                "h",
                "i",
                "j",
                "k",
                "l",
                "m",
                "n",
                "o",
                "p",
                "q",
                "r",
                "s",
                "t",
                "u",
                "v",
                "w",
                "x",
                "y",
                "z",
                ".",
                "A/a",
                "删除"
            )
        } else {
            return mutableListOf(
                "A",
                "B",
                "C",
                "D",
                "E",
                "F",
                "G",
                "H",
                "I",
                "J",
                "K",
                "L",
                "M",
                "N",
                "O",
                "P",
                "Q",
                "R",
                "S",
                "T",
                "U",
                "V",
                "W",
                "X",
                "Y",
                "Z",
                ".",
                "A/a",
                "删除"
            )
        }
    }

    //字符
    fun getWifiSoftSymbolList(): List<String> {
        return mutableListOf(
            "-",
            "/",
            ":",
            ";",
            "(",
            ")",
            "¥",
            "&",
            "@",
            "“",
            ".",
            ",",
            "?",
            "!",
            "‘",
            "*",
            "~",
            "【",
            "】",
            "{",
            "`",
            "、",
            "《",
            "》",
            "}",
            "。",
            "#",
            "%",
            "删除"
        )
    }

    fun getWifiSoftNumberList(): List<String> {
        return mutableListOf(
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "0",
            ".",
            "删除"
        )
    }


    fun getDataByBirthDay(birthday: Date): String {
        val bir = Calendar.getInstance()
        bir.time = birthday

        val yearBirth = bir[Calendar.YEAR]
        val monthBirth = bir[Calendar.MONTH]
        val dayBirth = bir[Calendar.DAY_OF_MONTH]
        return MONTH_LIST[monthBirth] + "  " + dayBirth + "  " + yearBirth
    }

    private val MONTH_LIST = mutableListOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )

    fun getDefaultBirthDayData() = Calendar.getInstance().also {
        val nowTime = TimeUtils.getNowDate()?.time ?: 0
        val nowYear = TimeUtils.millis2String(nowTime, "yyyy").toInt()
        val nowMonth = TimeUtils.millis2String(nowTime, "MM").toInt()
        val nowDay = TimeUtils.millis2String(nowTime, "dd").toInt()
        it.set(nowYear - 18, nowMonth - 1, nowDay)
    }


    fun chooseSex(age: Any): String {
        val mAge = when (age) {
            "0" -> "female"
            "1" -> "man"
            else -> "man"
        }
        return mAge
    }


    fun onlineStatus(item: Any): String {
        var onlineStatus = when (item) {
            0 -> "online"
            1 -> "active"
            2 -> "busy"
            3 -> "offline"
            4 -> "not disturb"
            else -> {
                "offline"
            }
        }
        return onlineStatus
    }


    fun getReportType(item: Any): String {
        var type = when (item) {
            "Violence, threats & abusing" -> "1"
            "Pornographic behavior" -> "2"
            "Spam & Ads" -> "3"
            "Scam" -> "4"
            "Illegal activities" -> "5"
            "Fake gender" -> "6"
            "Others" -> "0"
            else -> "0"
        }
        return type
    }


    fun chooseSexToNum(age: Any): String {
        val mAge = when (age) {
            "female" -> "0"
            "man" -> "1"
            else -> "0"
        }
        return mAge
    }

    fun ageData(): List<String> {
        val list: ArrayList<String> = arrayListOf<String>()
        for (i in 18..120) {
            list.add("${i}")
        }
        return list
    }

    fun SexData(): List<String> {
        val list = arrayListOf<String>("man", "female")
        return list
    }
}