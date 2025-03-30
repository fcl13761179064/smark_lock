package com.sprint.lock.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.qweather.sdk.bean.geo.GeoBean
import com.qweather.sdk.view.QWeather
import com.springs.common.application.BaseApplication
import com.springs.common.base.BaseActivity
import com.springs.common.common.LiveDataBusX
import com.springs.common.widgets.AppData
import com.sprint.lock.app.adapter.AdapterCityCheck
import com.sprint.lock.app.adapter.AdapterSearchCity
import com.sprint.lock.app.databinding.ActivityCheckCityBinding
import com.sprint.lock.app.widge.CityUtils

class ActivityCheckCity :BaseActivity<ActivityCheckCityBinding>() {

    private val mAdapter by lazy {AdapterCityCheck() }
    private val mAdapterTwo by lazy {AdapterSearchCity() }
    private val viewModel by lazy { ViewModelProvider(this)[SearchViewModel::class.java] }
    override fun getViewBinding(): ActivityCheckCityBinding=ActivityCheckCityBinding.inflate(layoutInflater)

    override fun init(savedInstanceState: Bundle?) {

        setStarHeadRecycleview()
    }


    private fun setStarHeadRecycleview() {
        val layoutManager =
            GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        binding.recycleview.layoutManager = layoutManager
        binding.recycleview.setHasFixedSize(true)
        binding.recycleview.itemAnimator = null
        binding.recycleview.adapter = mAdapter
        mAdapter.setNewData(CityUtils.cityList())


        val layoutManagers = LinearLayoutManager(this,  LinearLayoutManager.VERTICAL, false)
        binding.rvSearch.layoutManager = layoutManagers
        binding.rvSearch.setHasFixedSize(true)
        binding.rvSearch.itemAnimator = null
        binding.rvSearch.adapter = mAdapterTwo
    }

    override fun initListener() {
        super.initListener()
        binding.ivLeft.setOnClickListener {
         finish()

        }
        binding.edit.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
               if (s.isNotEmpty()){
                   QWeather.getGeoCityLookup(BaseApplication.mApplication,s.toString(),object : QWeather.OnResultGeoListener {
                       override fun onError(p0: Throwable?) {

                       }

                       override fun onSuccess(p0: GeoBean?) {
                           runOnUiThread {
                               binding.rvSearch.visibility = View.VISIBLE
                               binding.recycleview.visibility = View.GONE
                               mAdapterTwo.setNewData(p0?.locationBean)
                           }
                       }
                   })
               }else{
                   binding.rvSearch.visibility = View.GONE
                   binding.recycleview.visibility = View.VISIBLE
               }
            }

        })
        mAdapter.setOnItemChildClickListener{ adapter, view, position ->
            if (view.id ==R.id.tvCityName){
                AppData.spUtils.put(AppData.SP_WHEATHER_ID,mAdapter.data[position].locationId)
                AppData.spUtils.put(AppData.SP_WHEATHER_NAME,mAdapter.data[position].locationNameZh)
                finish()
            }
        }
        mAdapterTwo.setOnItemChildClickListener{ adapter, view, position ->
            if (view.id ==R.id.ll_search){
                AppData.spUtils.put(AppData.SP_WHEATHER_ID,mAdapterTwo.data[position].id)
                AppData.spUtils.put(AppData.SP_WHEATHER_NAME,mAdapterTwo.data[position].name)
                finish()
            }
        }
    }
}