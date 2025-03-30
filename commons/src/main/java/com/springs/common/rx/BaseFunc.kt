package com.springs.common.rx

import com.springs.common.protocol.BaseResp
import rx.Observable
import rx.functions.Func1

/*
    通用数据类型转换封装
 */
class BaseFunc<T>:Func1<BaseResp<T>,Observable<T>>{
    override fun call(t: BaseResp<T>): Observable<T> {
        if (t.LmRp8tSEId5doyRCQJ0Wiou1uYOc9xmTzyn != ResultCode.SUCCESS){
            return Observable.error(BaseException(t.LmRp8tSEId5doyRCQJ0Wiou1uYOc9xmTzyn,t.dFxeJH4BgE ?: "服务器异常"))
        }
        return Observable.just(t.data)
    }
}
