package com.springs.common.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

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
/*
data class DeviceInfo(
    @SerializedName("appVersion",alternate  = ["RJGblqHQEq5gizgA6Yzmpmi5"])
    var appVersion: String?=null,
    @SerializedName("bag",alternate = ["yLmgaOijBUyPS9ikj9gn2PfIC5UWeKP3af"])
    var bag: String?=null,
    @SerializedName("brand",alternate = ["aRluuxTuuoyTAvf"])
    var brand: String?=null,
    @SerializedName("channel",alternate = ["X5gd0pMqHeXrBFqbpiYJLU"])
    var channel: String?=null,
    @SerializedName("deviceModel",alternate = ["ZecdEqstMXUiVYKHjTdaNq7tIO54ugm4ZG"])
    var deviceModel: String?=null,
    @SerializedName("mac",alternate = ["dR9sMjaRQobX"])
    var mac: String?=null,
    @SerializedName("operationSys",alternate = ["SUhjt1CFOr2fHBa3DrIaj"])
    var operationSys: String?=null,
    @SerializedName("osVersion",alternate = ["qwuuw9"])
    var osVersion: String?=null,
    @SerializedName("fType",alternate = ["HzKhPDDkI3flkbsKKyLL4yq"])
    var fType: String?=null,
    @SerializedName("androidIdOrUdid",alternate = ["nxf14zgIr1dPlTZHyKCnPV6ZSUg"])
    var androidIdOrUdid: String?=null,
    @SerializedName("gaidOrIdfa",alternate = ["WmjQZWVIzIRr8k0d0LNqqXN"])
    var gaidOrIdfa: String?=null,
)
*/

data class DeviceInfo(
    var RJGblqHQEq5gizgA6Yzmpmi5: String?=null, //appVersion
    var yLmgaOijBUyPS9ikj9gn2PfIC5UWeKP3af: String?=null, //bag
    var aRluuxTuuoyTAvf: String?=null, //brand
    var X5gd0pMqHeXrBFqbpiYJLU: String?=null, //channel
    var ZecdEqstMXUiVYKHjTdaNq7tIO54ugm4ZG: String?=null, //deviceModel
    var dR9sMjaRQobX: String?=null, //mac
    var SUhjt1CFOr2fHBa3DrIaj: String?=null, //operationSys
    var qwuuw9: String?=null, //osVersion
    var HzKhPDDkI3flkbsKKyLL4yq: String?=null, //fType
    var nxf14zgIr1dPlTZHyKCnPV6ZSUg: String?=null, //androidIdOrUdid
    var WmjQZWVIzIRr8k0d0LNqqXN: String?=null, //gaidOrIdfa
)


data class NoLoginProdeceResponce(
    val productList: List<Product>,
    val resultCode: Int,
    val resultMsg: String
)



/**
 * 没有登录的首页数据
 */

data class NoLoginFirstPageData(
    val LmRp8tSEId5doyRCQJ0Wiou1uYOc9xmTzyn: Int,
    val dFxeJH4BgE: String,
    val yvwi6rKmim6A3uBq: List<Product>
) : Serializable

data class Product(
    val CYnTK4: String,
    val GFeFdt: String,
    val VHeOIGGF41R1xCMBM7RVnfz90i5rcmytFKX: String,
    val Vi3WcKZn56wx1Qc: String,
    val ZxFFVE35x5s3vwHE8: String,
    val aCYJmgkYFIo2XkuDOJnLcaqFhrfGa: String,
    val fhgelA4mwM3orXEaCD: String
)  : Serializable



data class MessageInfo(
    var title: String="",
    var data: String="",
    var time: String="",

    )