package com.springs.common.widgets

import android.net.Uri
import com.blankj.utilcode.util.SPUtils
import kotlin.collections.ArrayList

/**
 * @ClassName:  AppData
 * @Description:公用数据类（非通用数据请勿放置在本类中，请使用intent传递参数，防止界面跳转数据及逻辑不清晰的问题）
 * @Author: vi1zen
 * @CreateDate: 2020/9/28 17:40
 */
object AppData {


    /*登陆成功后获取sp数据*/
    //SP表名
    const val TABLE_PREFS = "Kotlin_mall"
    //全局SharedPreferences
    val spUtils: SPUtils by lazy { SPUtils.getInstance(TABLE_PREFS) }

    var  BASE_URL ="http://149.129.217.31:1360"
    var  TO_SCAN_PHOTO ="to_scan_photo"
    var  TO_UPDATA_DOOR ="to_updata_door"
    var  SP_WHEATHER_ID ="sp_wheather_id"
    var  SP_WHEATHER_NAME ="sp_wheather_name"
 }