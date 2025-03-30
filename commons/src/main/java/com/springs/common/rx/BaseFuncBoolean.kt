package com.springs.common.rx

import com.springs.common.protocol.BaseResp
import rx.Observable
import rx.functions.Func1

/*
    Boolean类型转换封装
 */
class BaseFuncBoolean<T>: Func1<BaseResp<T>, Observable<Boolean>> {
    override fun call(t: BaseResp<T>): Observable<Boolean> {
        if (t.LmRp8tSEId5doyRCQJ0Wiou1uYOc9xmTzyn != ResultCode.SUCCESS){
            return Observable.error(BaseException(t.LmRp8tSEId5doyRCQJ0Wiou1uYOc9xmTzyn, t.dFxeJH4BgE ?: "服务器异常"))
        }

        return Observable.just(true)
    }
}
