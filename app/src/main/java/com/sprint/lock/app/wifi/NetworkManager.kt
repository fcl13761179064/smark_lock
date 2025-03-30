package com.sprint.lock.app.wifi

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.provider.Settings


class NetworkManager(private val context: Context) {
    private val connManager: ConnectivityManager

    init {
        connManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    val isNetworkConnected: Boolean
        /**
         * @return 网络是否连接可用
         */
        get() {
            val networkinfo = connManager.activeNetworkInfo
            return networkinfo?.isConnected ?: false
        }
    val isWifiConnected: Boolean
        /**
         * @return wifi是否连接可用
         */
        get() {
            val mWifi = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            return mWifi?.isConnected ?: false
        }
    val isMobileConnected: Boolean
        /**
         * 当wifi不能访问网络时，mobile才会起作用
         * @return GPRS是否连接可用
         */
        get() {
            val mMobile = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            return mMobile?.isConnected ?: false
        }

    /**
     * GPRS网络开关 反射ConnectivityManager中hide的方法setMobileDataEnabled 可以开启和关闭GPRS网络
     *
     * @param isEnable
     * @throws Exception
     */
    @Throws(Exception::class)
    fun toggleGprs(isEnable: Boolean) {
        val cmClass: Class<*> = connManager.javaClass
        val argClasses = arrayOfNulls<Class<*>?>(1)
        argClasses[0] = Boolean::class.javaPrimitiveType

        // 反射ConnectivityManager中hide的方法setMobileDataEnabled，可以开启和关闭GPRS网络
        val method = cmClass.getMethod("setMobileDataEnabled", *argClasses)
        method.invoke(connManager, isEnable)
    }

    /**
     * WIFI网络开关
     *
     * @param enabled
     * @return 设置是否success
     */
    fun toggleWiFi(enabled: Boolean): Boolean {
        val wm = context
            .getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wm.setWifiEnabled(enabled)
    }

    val isAirplaneModeOn: Boolean
        /**
         *
         * @return 是否处于飞行模式
         */
        get() {
            // 返回值是1时表示处于飞行模式
            val modeIdx = Settings.System.getInt(
                context.contentResolver,
                Settings.System.AIRPLANE_MODE_ON,
                0
            )
            return modeIdx == 1
        }

    /**
     * 飞行模式开关
     * @param setAirPlane
     */
    fun toggleAirplaneMode(setAirPlane: Boolean) {
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.AIRPLANE_MODE_ON,
            if (setAirPlane) 1 else 0
        )
        // 广播飞行模式信号的改变，让相应的程序可以处理。
        // 不发送广播时，在非飞行模式下，Android 2.2.1上测试关闭了Wifi,不关闭正常的通话网络(如GMS/GPRS等)。
        // 不发送广播时，在飞行模式下，Android 2.2.1上测试无法关闭飞行模式。
        val intent = Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        // intent.putExtra("Sponsor", "Sodino");
        // 2.3及以后，需设置此状态，否则会一直处于与运营商断连的情况
        intent.putExtra("state", setAirPlane)
        context.sendBroadcast(intent)
    }
}