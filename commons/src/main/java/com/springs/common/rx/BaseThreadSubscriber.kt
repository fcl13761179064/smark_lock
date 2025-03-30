package com.springs.common.rx


import com.springs.common.ext.Logutils
import rx.Subscriber

/*
    Rx订阅者默认实现
 */
open class BaseThreadSubscriber<T>():Subscriber<T>() {

    override fun onCompleted() {
    }

    override fun onNext(t: T) {
    }

    override fun onError(e: Throwable?) {
        if (e is BaseException){
            Logutils(e.msg)
        }
    }
}
