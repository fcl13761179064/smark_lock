package com.springs.common.net

import android.os.Build
import com.blankj.utilcode.util.DeviceUtils
import com.google.gson.Gson
import com.springs.common.bean.DeviceInfo
import com.springs.common.widgets.AppData
import com.springs.common.widgets.LogLongUtil
import okhttp3.*
import okio.Buffer
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/*
    Retrofit工厂，单例
 */
class RetrofitFactory private constructor() {


    /**
     *  单例实现
     */
    companion object {
        val instance: RetrofitFactory by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { RetrofitFactory() }
    }

    private val retrofit: Retrofit
    /*
       具体服务实例化
    */
    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }



    init {
        //Retrofit实例化
        retrofit = Retrofit.Builder()
                .baseUrl(AppData.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(initClient())
                .build()
    }

    /*
           OKHttp创建
        */
    private fun initClient(): OkHttpClient {
        val okHttpBuilder = OkHttpClient.Builder()
                .addInterceptor(CommonHeaderInterceptor)//添加请求头
                .addInterceptor(LoggingInterceptor())//日志打印
                .addInterceptor(TokenLoginOut)
                .connectTimeout(15, TimeUnit.SECONDS)//连接超时时间
                .readTimeout(15, TimeUnit.SECONDS)//读取超时时间
                .writeTimeout(15,TimeUnit.SECONDS)//写入超时时间
                .connectionPool(ConnectionPool(32,5,TimeUnit.MINUTES))//连接池
        return okHttpBuilder.build()
    }


    /**
     * 通用Header拦截器
     */
    object CommonHeaderInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
              val deviceInfo = DeviceInfo()
            deviceInfo.RJGblqHQEq5gizgA6Yzmpmi5 ="1.0"
            deviceInfo.yLmgaOijBUyPS9ikj9gn2PfIC5UWeKP3af ="com.sprint.fund.app"
            deviceInfo.aRluuxTuuoyTAvf = Build.BRAND?:""
            deviceInfo.dR9sMjaRQobX = DeviceUtils.getMacAddress() ?:""
            deviceInfo.SUhjt1CFOr2fHBa3DrIaj ="android"
            deviceInfo.qwuuw9 ="10.0"
            deviceInfo.HzKhPDDkI3flkbsKKyLL4yq ="1"
            deviceInfo.nxf14zgIr1dPlTZHyKCnPV6ZSUg =DeviceUtils.getUniqueDeviceId() ?:""
            deviceInfo.WmjQZWVIzIRr8k0d0LNqqXN ="8cadd3b3-7c97-4b1c-b756-ec11ae608961"
            deviceInfo.ZecdEqstMXUiVYKHjTdaNq7tIO54ugm4ZG =DeviceUtils.getModel() ?:""
            deviceInfo.X5gd0pMqHeXrBFqbpiYJLU ="Other"
           val deviceInfoString =   Gson().toJson(deviceInfo)
            val requestBuilder = chain.request()
                    .newBuilder()
                    .addHeader("Vt4tAtS01hnFvhJr4", "en")
                    .addHeader("ERIb4OrkEboYrRbuUEOAsit06OjZ5", "")
                    .addHeader("YkPKsB2dSSDKOKMDFvX2h",deviceInfoString)
            return chain.proceed(requestBuilder.build())
        }
    }


    /*
       日志拦截器
    */
    class LoggingInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val UTF8 = Charset.forName("UTF-8")
            //Chain 里包含了request和response
            val request: Request = chain.request()
            val requestBody = request.body
            var body: String? = null
            if (requestBody != null) {
                val buffer = Buffer()
                requestBody.writeTo(buffer)
                var charset: Charset = UTF8
                val contentType = requestBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(UTF8)!!
                }
                body = buffer.readString(charset)
            }
            val t1 = System.nanoTime() //请求发起的时间
            LogLongUtil.longlog(
                "okhttp", java.lang.String.format(
                    "发送请求 %s on %s on %s%n%s%n%s",request.method, request.url, chain.connection(), request.headers,body
                )
            )
            val response: Response = chain.proceed(request)
            val t2 = System.nanoTime() //收到响应的时间
            //不能直接使用response.body（）.string()的方式输出日志
            //因为response.body().string()之后，response中的流会被关闭，程序会报错，
            // 我们需要创建出一个新的response给应用层处理
            val responseBody = response.peekBody(1024 * 1024)
            LogLongUtil.longlog(
                "okhttp", java.lang.String.format(
                    "接收响应：[%s] %n返回json:%s  %.1fms%n%s",
                    response.request.url,
                    responseBody.string(),
                    (t2 - t1) / 1e6,
                    response.headers
                )
            )
            return response
        }
    }




    object TokenLoginOut : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            //原始接口请求
            val originalRequest = chain.request()
            //原始接口结果
            val originalResponse = chain.proceed(originalRequest)
            val mediaType = originalResponse.body?.contentType()
            val content = originalResponse.body?.string()
            if (originalResponse.isSuccessful) {
                try {
                    val jsonObject = JSONObject(content)
                    val code = jsonObject.optInt("return_code")
                    when (code) {
                        333 -> {

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return originalResponse.newBuilder().body(ResponseBody.create(mediaType, content!!)).build()
        }
    }
}


