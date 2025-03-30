package com.springs.common.rx

import com.springs.common.ext.isLoginExpire
import com.springs.common.protocol.BaseResp
import rx.Observable
import rx.functions.Func1


@Suppress("UNCHECKED_CAST")
class RespConvert<T> : Func1<T,Observable<T>>{
    override fun call(t: T): Observable<T> {
        if(t is BaseResp<*>){
            if(t.LmRp8tSEId5doyRCQJ0Wiou1uYOc9xmTzyn != ResultCode.SUCCESS){
                return Observable.error(BaseException(t.LmRp8tSEId5doyRCQJ0Wiou1uYOc9xmTzyn,if(t.isLoginExpire()) "登录已过期" else t.dFxeJH4BgE ?: "服务异常,请稍后重试"))
            }
        }
        return Observable.just(t)
    }
}