package com.sprint.lock.app

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*


class SearchViewModel() : ViewModel() {

    val searchResult = MutableLiveData<List<Location>>()

    /**
     * 搜索城市
     */
    fun getDatasync(keywords: String) {
        Thread {
            try {
                val url = "https://geoapi.qweather.com/v2/city/lookup"
                val param = HashMap<String, Any>()
                param["location"] = "beiji"
                param["key"] = "eaec021195d44880ad2e02f59493ff15"
                val urlBuilder = url.toHttpUrlOrNull()?.newBuilder()
                param.let {
                    it.keys.forEach { key ->
                        urlBuilder?.addQueryParameter(key, it[key].toString())
                    }
                }
              val sss =  urlBuilder?.build()
                val client = OkHttpClient() //创建OkHttpClient对象
                val request: Request = Request.Builder()
                    .url(sss!!) //请求接口。如果需要传参拼接到接口后面。
                    .build() //创建Request 对象
              val  response = client.newCall(request).execute() //得到Response 对象
                if (response.isSuccessful) {
                    Log.d("kwwl", "response.code()==" + response.code)
                    Log.d("kwwl", "res==" + response?.body!!.string())
                    //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }.start()
    }


}