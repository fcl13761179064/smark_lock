package com.springs.common.common

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.google.gson.JsonObject
import okhttp3.RequestBody
import java.security.MessageDigest
import java.util.Locale
import okhttp3.MediaType.Companion.toMediaTypeOrNull

//Kotlin通用扩展

// 无参数
fun <T : Activity> FragmentActivity.openActivity(clz: Class<T>) {
    val intent = Intent(this, clz)
    startActivity(intent)
}

// 有参数
inline fun <T : Activity> Activity.openActivity(clz: Class<T>, block: Intent.() -> Unit) {
    val intent = Intent(this, clz)
    intent.block()
    startActivity(intent)
}

/*
    扩展点击事件
 */
fun View.onClick(listener: View.OnClickListener): View {
    setOnClickListener(listener)
    return this
}

fun json2Body(jsonObject: JsonObject): RequestBody{
    return RequestBody.create(
        "application/json; charset=UTF-8".toMediaTypeOrNull(),
        jsonObject.toString()
    )
}
/*
    扩展点击事件，参数为方法
 */
fun View.onClick(method: () -> Unit): View {
    setOnClickListener { method() }
    return this
}


/*
	 * MD5加密
	 * 注意so中有使用此接口, 不能混淆和删除, 必须生成大写
	 */
fun  getMD5Str(str: String): String? {
    var messageDigest: MessageDigest? = null
    var result: String? = null
    try {
        messageDigest = MessageDigest.getInstance("MD5")
        messageDigest.reset()
        messageDigest.update(str.toByteArray(charset("UTF-8")))
        val byteArray = messageDigest.digest()
        val md5StrBuff = StringBuffer()
        for (i in byteArray.indices) {
            md5StrBuff.append(String.format("%02x", byteArray[i].toInt() and 0xff))
        }
        result = md5StrBuff.toString().uppercase(Locale.getDefault())
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return result
}


/**
 *  dp转px
 */
fun Number.px2dp() = px2dp(this.toFloat())

fun  px2dp( dpValue :Float): Int {
    val scale = Resources.getSystem().displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

/*
    扩展视图可见性
 */
fun View.setVisible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

/*
    扩展视图可见性
 */
fun View.setInvisible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}








