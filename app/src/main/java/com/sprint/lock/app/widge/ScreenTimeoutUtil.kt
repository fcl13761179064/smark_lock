package com.sprint.lock.app.widge

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException

class ScreenTimeoutUtil(private val context: Context) {
    var screenTimeout: Int
        /**
         * 获取当前屏幕超时时间
         *
         * @return 超时时间（毫秒）
         */
        get() = try {
            Settings.System.getInt(
                context.contentResolver,
                Settings.System.SCREEN_OFF_TIMEOUT
            )
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
            -1 // 获取失败
        }
        /**
         * 设置屏幕超时时间
         *
         * @param timeoutMillis 超时时间（毫秒）
         */
        set(timeoutMillis) {
            try {
                // 修改系统设置中的屏幕超时时间
                Settings.System.putInt(
                    context.contentResolver,
                    Settings.System.SCREEN_OFF_TIMEOUT, timeoutMillis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    /**
     * 立即熄屏
     */
    fun turnOffScreen() {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (powerManager != null) {
            val wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "ScreenTimeoutUtil:WakeLock"
            )
            wakeLock.acquire(10) // 立即释放锁，触发熄屏
            wakeLock.release()
        }
    }

    /**
     * 检查是否具有修改系统设置的权限
     *
     * @return true：有权限；false：无权限
     */
    fun hasWriteSettingsPermission(): Boolean {
        return Settings.System.canWrite(context)
    }

    /**
     * 请求修改系统设置的权限
     */
    fun requestWriteSettingsPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.data = Uri.parse("package:" + context.packageName)
        context.startActivity(intent)
    }
}