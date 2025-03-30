package com.springs.common.widgets

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

object ScreenTimeoutUtil {
    // 检查并请求权限
    fun checkAndRequestPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:" + context.packageName)
                context.startActivity(intent)
            }
        }
    }

    // 设置熄屏时间
    fun setScreenTimeout(context: Context, timeoutMillis: Int) {
        val contentResolver = context.contentResolver
        try {
            Settings.System.putInt(
                contentResolver,
                Settings.System.SCREEN_OFF_TIMEOUT,
                timeoutMillis
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}