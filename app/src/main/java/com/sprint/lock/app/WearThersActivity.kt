package com.sprint.lock.app

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ThreadUtils
import com.google.gson.Gson
import com.qweather.sdk.bean.base.Code
import com.qweather.sdk.bean.weather.WeatherDailyBean
import com.qweather.sdk.bean.weather.WeatherNowBean
import com.qweather.sdk.view.QWeather
import com.springs.common.base.BaseActivity
import com.springs.common.common.LiveDataBusX
import com.springs.common.dialog.PermissionDialog
import com.springs.common.widgets.AppData
import com.sprint.lock.app.adapter.DayWeatherAdapter
import com.sprint.lock.app.databinding.ActivityWeatherBinding
import com.sprint.lock.app.widge.wheather.City
import me.wsj.lib.utils.IconUtils

class WearThersActivity : BaseActivity<ActivityWeatherBinding>() {

    /**
     * 实况天气数据
     * @param location 所查询的地区，可通过该地区ID、经纬度进行查询经纬度格式：经度,纬度
     *                 （英文,分隔，十进制格式，北纬东经为正，南纬西经为负)
     * @param lang     (选填)多语言，可以不使用该参数，默认为简体中文
     * @param unit     (选填)单位选择，公制（m）或英制（i），默认为公制单位
     * @param listener 网络访问结果回调
     */

    override fun getViewBinding(): ActivityWeatherBinding =
        ActivityWeatherBinding.inflate(layoutInflater)


    override fun init(savedInstanceState: Bundle?) {
        // 隐藏状态栏
        binding.ivBack.setOnClickListener {
            finish()
        }
        val wheather = intent.getStringExtra("wheather") ?: ""
        val location = intent.getStringExtra("location") ?: ""
        val wheatherBean = Gson().fromJson(wheather, WeatherNowBean::class.java)
        val city = Gson().fromJson(location, City::class.java)
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.textView.text = city?.locationNameZh ?: "上海"
        sevenWheather( city?.locationId)

        wheatherBean?.let {
            binding.imgWheather.setImageResource(
                IconUtils.getDayIconDark(
                    this@WearThersActivity, it.now.icon.toString()
                )
            )
            binding.textView2.text = it.now.temp + "°c"
            binding.textView3.text = it.now.windDir
        }
        binding.checkCity.setOnClickListener {
            startActivity(ActivityCheckCity::class)
        }

    }

    override fun onResume() {
        super.onResume()
            val locationId = AppData.spUtils.getString(AppData.SP_WHEATHER_ID,"")
            val locationName = AppData.spUtils.getString(AppData.SP_WHEATHER_NAME,"")
        if (locationId.isNotBlank() && locationName.isNotBlank()){
            binding.textView.text = locationName ?: "上海"
            sevenWheather(locationId)
            fiveWheather(locationId)
        }
    }

    fun  sevenWheather(locationId: String?) {
        QWeather.getWeather7D(this@WearThersActivity,
            locationId ?: "101020600",
            object : QWeather.OnResultWeatherNowListener, QWeather.OnResultWeatherDailyListener {
                override fun onError(p0: Throwable?) {
                }

                override fun onSuccess(weatherBean: WeatherDailyBean?) {
                    //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                    try {
                        runOnUiThread {
                            if (Code.OK == weatherBean?.code) {
                                val adapter = DayWeatherAdapter(this@WearThersActivity, weatherBean.daily)
                                binding.recyclerView.adapter = adapter
                            } else {
                                //在此查看返回数据失败的原因
                                val code = weatherBean!!.code
                            }
                        }
                    }catch (e :Exception){
                        e.printStackTrace()
                    }
                }

                override fun onSuccess(weatherBean: WeatherNowBean?) {

                }
            })
    }

   private  fun fiveWheather(locationId: String?){
        QWeather.getWeatherNow(this@WearThersActivity, locationId ?: "101020600",
            object : QWeather.OnResultWeatherNowListener,
                QWeather.OnResultWeatherDailyListener {
                override fun onError(p0: Throwable?) {
                }

                override fun onSuccess(weatherBean: WeatherDailyBean?) {
                }

                override fun onSuccess(weatherBean: WeatherNowBean) {
                    //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                    ThreadUtils.runOnUiThread {
                        if (Code.OK == weatherBean.code) {
                            binding.imgWheather.setImageResource(
                                IconUtils.getNightIconDark(
                                    this@WearThersActivity, weatherBean.now.icon.toString()
                                )
                            )
                            binding.textView2.text = weatherBean.now.temp + "°c"
                            binding.textView3.text = weatherBean.now.windDir
                        } else {
                            //在此查看返回数据失败的原因
                            val code = weatherBean.code
                            LogUtils.d("sddsdsd", code)
                        }
                    }
                }
            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予

            } else {
                // 权限被拒绝
                PermissionDialog(this@WearThersActivity, "请打开定位权限").show()
            }
        }
    }
}