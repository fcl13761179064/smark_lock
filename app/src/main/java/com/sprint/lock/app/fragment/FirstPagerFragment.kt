package com.sprint.lock.app.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.ToastUtils
import com.springs.common.base.BaseFragment
import com.springs.common.ext.showTopToast
import com.springs.common.widgets.ScheduleTimeUtils
import com.sprint.lock.app.CalendarSelectActivity
import com.sprint.lock.app.databinding.FragmentFirstPagerBinding
import java.util.Calendar


class FirstPagerFragment : BaseFragment<FragmentFirstPagerBinding>() {

    private val mCalendar: Calendar = Calendar.getInstance()

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentFirstPagerBinding = FragmentFirstPagerBinding.inflate(layoutInflater)

    override fun init(savedInstanceState: Bundle?, view: View) {
        ScheduleTimeUtils.showRealTime {
            binding.atTime.text = it
        }
        val year = mCalendar[Calendar.YEAR]
        val month = mCalendar[Calendar.MONTH] + 1 // 月份是从0开始的，所以需要+1
        val day = mCalendar[Calendar.DAY_OF_MONTH]
        val dayOfWeek = mCalendar[Calendar.DAY_OF_WEEK] // 星期几，星期天为1，星期一为2，依此类推
        binding.atTimeData.text = "${year}-${month}-${day}"
        val weekDay = weekday(dayOfWeek)
        binding.atWeek.text = weekDay

        binding.atTime.setOnClickListener {
            val intent = Intent(requireContext(), CalendarSelectActivity::class.java)
            startActivity(intent)
        }
    }

    private fun weekday(dayOfWeek: Int)  :String{
        when (dayOfWeek) {
            Calendar.SUNDAY -> return  "星期日"
            Calendar.MONDAY -> return "星期一"
            Calendar.TUESDAY -> return "星期二"
            Calendar.WEDNESDAY ->return "星期三"
            Calendar.THURSDAY ->return "星期四"
            Calendar.FRIDAY ->return "星期五"
            Calendar.SATURDAY ->return "星期六"
        }
        return  "星期日"
    }

    override fun onDestroy() {
        super.onDestroy()
        ScheduleTimeUtils.destroyTime()
    }
}