package me.wsj.lib.utils

import android.content.Context
import com.blankj.utilcode.util.LogUtils
import com.sprint.lock.app.R

object IconUtils {



    /**
     * 获取白天深色天气图标
     */
    @JvmStatic
    fun getDayIconDark(context: Context, weather: String): Int {
        val code = parseCode(weather, "d")
        return getDrawableRes(context, "icon_$code", R.drawable.icon_100d)
    }

    /**
     * 获取晚上深色天气图标
     */
    @JvmStatic
    fun getNightIconDark(context: Context, weather: String): Int {
        val code = parseCode(weather, "n")
        return getDrawableRes(context, "icon_$code", R.drawable.icon_100n)
    }

    private fun parseCode(weather: String, postFix: String): String {
        val code = (if (weather.isEmpty()) "0" else weather).toInt()
        return when (code) {
            in 150 until 199,
            in 350 until 399,
            in 450 until 499 -> {
                (code - 50).toString() + "n"
            }
            else -> {
                code.toString() + postFix
            }
        }
    }



    fun getDrawableRes(context: Context, weather: String, def: Int): Int {
        return getRes(context, "drawable", weather, def)
    }

    fun getRes(context: Context, type: String?, weather: String, def: Int): Int {
        return try {
            var id = context.resources.getIdentifier(weather, type, context.packageName)
            if (id == 0) {
                id = def
            }
            id
        } catch (e: Exception) {
            LogUtils.e("获取资源失败：$weather")
            def
        }
    }
}