package com.sprint.lock.app.bean

/*
     "appVersion": "1.3.9.1",   //APP版本
    "bag": "tuopes.nmbesf.truehgc",   //包名称
    "brand": "samsung",    //设备品牌
    "channel": "动态取值",   //返回为空或者没有返回赋值Other,返回的不为空就直接取adjust返回的
    "deviceModel": "SM-A510F",   //设备机型
    "mac": "30:CB:F8:D9:A1:B5",     //手机mac地址
    "operationSys": "android",    //系统  android 或者 ios
    "osVersion": "7.0",    //系统版本
    "fType":"1", //2.3.1.5 获取用户信息接口返回
    "androidIdOrUdid": "356911079962559", //安卓 android_id 或者 苹果 udid
    "gaidOrIdfa":"8cadd3b3-7c97-4b1c-b756-ec11ae608961" //安卓 gaid 或者 苹果 idfa
 */
data class DeviceInfo(
    val appVersion: String,
    val bag: Int,
    val brand: String,
    val channel: Int,
    val deviceModel: String,
    val mac: String,
    val operationSys: String,
    val osVersion: String?=null,
    val fType: String?=null,
    val androidIdOrUdid: String?=null,
    val gaidOrIdfa: String?=null,
)


