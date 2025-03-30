package com.sprint.lock.app.widge

import okhttp3.OkHttpClient
import okhttp3.Request

object RequestUtils {
    fun request(url: String = "https://www.baidu.com"): String? {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        return response.body?.string()
    }
}