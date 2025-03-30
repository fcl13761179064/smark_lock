package com.springs.common.widgets

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.text.format.DateFormat
import com.blankj.utilcode.util.LogUtils
import com.springs.common.ext.Logutils
import java.util.Timer
import java.util.TimerTask


object ScheduleTimeUtils {

    private var timer: Timer? = null
    private var timers: Timer? = null
    private var handler: Handler? = null

    fun showRealTime(block: (time: String) -> Unit) {
        timer = Timer()
        handler = Handler()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                handler?.post { // 更新UI
                    updateTime(block)
                }
            }
        }, 0, 500) // 每秒更新一次
    }

     fun updateTime(block: (time: String) -> Unit) {
        // 获取当前时间
        val currentTimeMillis = System.currentTimeMillis()
        // 格式化时间
        val timeString: String = DateFormat.format("HH  mm", currentTimeMillis).toString()
        block.invoke(timeString)
    }

    fun getYMDTime(block: (time: String) -> Unit) {
        // 获取当前时间
        val currentTimeMillis = System.currentTimeMillis()
        // 格式化时间
        val timeString: String = DateFormat.format("yyyy/MM/dd HH:mm:ss", currentTimeMillis).toString()
        block.invoke(timeString)
    }

    fun destroyTime() {
        if (timer != null) {
            // 清理资源
            timer?.cancel()
        }
        handler?.removeCallbacksAndMessages(null)
    }

    private var count = 1
    fun showSeconds(activity: Activity, block: (time: String) -> Unit) {
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                activity.runOnUiThread {
                    if (count > 180) {
                        cacelTimer()
                        timers=null
                    } else {
                        if (count >= 10) block.invoke("00:${count}") else block.invoke("00:0${count}")
                        count++
                    }
                }
            }
        }
        timers = Timer()
        // 定时任务开始执行，每隔1秒执行一次
        timers?.schedule(timerTask, 1000, 1000)
    }



    fun cacelTimer() {
          count = 1
        if (timers != null) {
            timers?.cancel()
            timers = null
        }
    }

}

