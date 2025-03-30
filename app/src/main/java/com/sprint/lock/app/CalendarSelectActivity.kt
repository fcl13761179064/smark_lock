package com.sprint.lock.app

import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView.OnCalendarSelectListener
import com.springs.common.base.BaseActivity
import com.springs.common.widgets.ScheduleTimeUtils
import com.sprint.lock.app.databinding.ActivityCalendarBinding


/**
 * @ClassName:  日期
 * @
 */
class CalendarSelectActivity() : BaseActivity<ActivityCalendarBinding>() {


    override fun getViewBinding(): ActivityCalendarBinding =
        ActivityCalendarBinding.inflate(layoutInflater)
    var flag =true
    override fun init(savedInstanceState: Bundle?) {
        val map: MutableMap<String, Calendar?> = java.util.HashMap()
        val year = Calendar().year
        val month = Calendar().month
        val day = Calendar().day

        map[getSchemeCalendar(year, month, day, -0xbf24db, "").toString()] =
            getSchemeCalendar(year, month, day, -0xbf24db, "")
        map[getSchemeCalendar(year, month, day, -0xbf24db, "").toString()] =
            getSchemeCalendar(year, month, day, -0xbf24db, "")
        map[getSchemeCalendar(year, month, day, -0xbf24db, "").toString()] =
            getSchemeCalendar(year, month, day, -0xbf24db, "")
        binding.calendarView.setSchemeDate(map)


        ScheduleTimeUtils.showRealTime {
            if (flag) binding.atPoint.visibility = View.VISIBLE else  binding.atPoint.visibility =
                View.INVISIBLE
            binding.tvRealTime.text = it
            flag =!flag
        }
        binding.calendarView.setOnCalendarSelectListener(object : OnCalendarSelectListener {
            override fun onCalendarOutOfRange(calendar: Calendar) {
            }

            override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {
                Log.d(
                    "CalendarSelectActivity",
                    "onCalendarSelect: " + calendar.year + calendar.month + calendar.day
                )
                binding.tvDate.text = "${calendar.year}年  ${calendar.month}月  ${calendar.day}日"
            }
        })

        binding.calendarView.scrollToNext(true);//下个月
        binding.calendarView.scrollToPre(true);//上个月

        binding.ivBack.setOnClickListener {
            finish()
        }
    }


    private fun getSchemeCalendar(
        year: Int, month: Int, day: Int, color: Int, text: String
    ): Calendar? {
        val calendar = Calendar()
        calendar.setYear(year)
        calendar.setMonth(month)
        calendar.setDay(day)
        calendar.setSchemeColor(color) //如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text)
        return calendar
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) { //表示按返回键 时的操作
                // 监听到返回按钮点击事件
                try {
                    ScheduleTimeUtils.destroyTime()
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun onDestroy() {
        super.onDestroy()
        ScheduleTimeUtils.destroyTime()
    }
}


