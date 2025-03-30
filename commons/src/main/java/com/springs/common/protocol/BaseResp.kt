package com.springs.common.protocol

import com.google.gson.annotations.SerializedName


/*
    基础响应对象
    @status:响应状态码
    @message:响应文字消息
    @data:具体响应业务对象
    dFxeJH4BgE  =msg
    resultCode =LmRp8tSEId5doyRCQJ0Wiou1uYOc9xmTzyn
 */
data class BaseResp<out T>(val LmRp8tSEId5doyRCQJ0Wiou1uYOc9xmTzyn:Int, val dFxeJH4BgE:String?, val data:T)

data class BasePage<out T>(val currentPage:Int, val pageSize:Int, val totalCount:Int,val totalPages:Int,val pages:T)

