package com.sprint.lock.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qweather.sdk.bean.weather.WeatherDailyBean
import com.sprint.lock.app.databinding.AdapterDayWeatherBinding
import me.wsj.lib.utils.IconUtils


class DayWeatherAdapter(val context: Context, val list: MutableList<WeatherDailyBean.DailyBean>) :
    RecyclerView.Adapter<DayWeatherAdapter.ViewHolder>() {
    class ViewHolder(binding: AdapterDayWeatherBinding) : RecyclerView.ViewHolder(binding.root) {
        val text = binding.textView
        val img = binding.image
        val text2 = binding.textView2
        val text3 = binding.textView3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterDayWeatherBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list[position].run {
            // 解析时间，"2023-07-30"
            val data = try {
                fxDate.split('-').let {
                    "${it[1]}-${it[2]}"
                }
            } catch (_: Exception) {
                ""
            }
            holder.text.text = data
            holder.text2.text = textDay
           holder.img.setImageResource(IconUtils.getDayIconDark(context,this.iconDay.toString()))
            holder.text3.text = "${tempMax}°/${tempMin}°"
        }
    }
}