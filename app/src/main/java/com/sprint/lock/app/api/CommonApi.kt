package com.sprint.lock.app.api

import com.springs.common.bean.NoLoginFirstPageData
import com.springs.common.protocol.BaseResp
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST
import rx.Observable


/*
    获取数据接口
 */
interface CommonApi {


    /**
     * 首次启动时访问
     */
    @POST("/cPXYi/KxdZt")
    fun firstLauchData(@Body body: RequestBody): Observable<BaseResp<Any>>



    /**
     * 未登录
     */
    @POST("/cPXYi/PStgU")
    fun noLoginProduce(@Body body: RequestBody): Observable<BaseResp<NoLoginFirstPageData>>

}
