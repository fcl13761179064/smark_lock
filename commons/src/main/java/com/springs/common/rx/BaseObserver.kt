package com.springs.common.rx

import com.springs.common.application.BaseApplication
import com.springs.common.ext.showTopToast
import com.springs.common.widgets.NetWorkUtils
import rx.Subscriber

/**
  *  Rx订阅者默认实现(非MVP)
  */
open class BaseObserver<T>:Subscriber<T>() {

    override fun onStart() {
        super.onStart()
        if(!NetWorkUtils.isWifiConnected(BaseApplication.mApplication)){
            unsubscribe()
            onError(BaseException(-1,"网络不可用"))
        }
    }

    override fun onCompleted() {
    }

    override fun onNext(t: T) {
    }

    override fun onError(e: Throwable?) {
        if (e is BaseException){
            showTopToast(e.msg)
        }else{
            showTopToast(e?.message ?: "服务异常")
        }
    }
}
