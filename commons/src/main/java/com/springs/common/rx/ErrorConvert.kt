package com.springs.common.rx

import retrofit2.HttpException
import rx.Observable
import rx.functions.Func1
import java.io.IOException


class ErrorConvert<T> : Func1<Throwable,Observable<T>>{
    override fun call(t: Throwable?): Observable<T> {
        return if(t is BaseException){
            Observable.error(t)
        }else{
            val ex = when(t){
                is HttpException -> BaseException(t.code(), "服务异常，请稍后再试")
                is IOException ->   BaseException(-1, "网络请求失败，请稍后再试")
                else -> BaseException(-1, t?.message ?: "请求失败，请检查网络后重试")
            }
            Observable.error(ex)
        }
    }
}