package com.sprint.lock.app.widge

import android.util.Log
import android.widget.Toast
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.springs.common.application.BaseApplication

fun Any?.loge() = Log.e("haojinhui", this.toString())

fun Any?.println() = println(this.toString())

fun Any?.toast() = runOnUiThread {
    Toast.makeText( BaseApplication.mApplication, this.toString(), Toast.LENGTH_SHORT)
}

fun Any?.show() {
    try {
        loge()
    } catch (e: Exception) {
        try {
            e.loge()
        } catch (_: Exception) {
        }
    }
    try {
        println()
    } catch (e: Exception) {
        try {
            e.loge()
        } catch (_: Exception) {
        }
    }
    try {
        toast()
    } catch (e: Exception) {
        try {
            e.loge()
        } catch (_: Exception) {
        }
    }
}

fun main() {
    "测试".loge()
}
