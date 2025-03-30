package com.springs.common.widgets

import android.annotation.SuppressLint
import android.os.Build
import android.view.MotionEvent
import android.view.View
import com.blankj.utilcode.util.ConvertUtils
import com.springs.common.ext.showToast
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


//Kotlin通用扩展


/**
 * string转requestBody
 */
fun String.toReqBody(): RequestBody {
    return this.toRequestBody("application/json; charset=UTF-8".toMediaType())
}

/**
 *  dp转px
 */
fun Number.dp() = ConvertUtils.dp2px(this.toFloat())


/**
 * 获取手机系统对应的SDK_INT
 *
 * @return
 */
fun getSDKINT(): Int {
    return Build.VERSION.SDK_INT
}

/**
 * 获取手机系统版本
 *
 * @return
 */
fun getVersionRelease(): String? {
    return Build.VERSION.RELEASE
}

/**
 * 获取厂商
 *
 * @return
 */
fun getManufacturer(): String? {
    return Build.MANUFACTURER
}

/**
 * 手机型号
 *
 * @return
 */
fun getPhoneModel(): String? {
    return Build.MODEL
}

fun getOSVersionString(): String? {
    return Build.VERSION.RELEASE
}

@SuppressLint("ClickableViewAccessibility")
fun View.ViewAnimationTime(block: () -> Unit, time:Int) {
    this.setOnTouchListener { view, event ->
        if (event.action === MotionEvent.ACTION_DOWN) { //放大图片
            view.scaleX = 0.9.toFloat()
            view.scaleY = 0.9.toFloat()
        } else if (event.action === MotionEvent.ACTION_UP) { //恢复原状
            view.scaleX = 1.0.toFloat()
            view.scaleY = 1.0.toFloat()
            if (!FastClickUtils.isDoubleClick(time)) {
                block.invoke()
            } else {
                showToast("请勿操作过快哦")
            }
        } else if (event.action === MotionEvent.ACTION_CANCEL) { //恢复原状
            view.scaleX = 1.0.toFloat()
            view.scaleY = 1.0.toFloat()
        }
        true
    }
}