package com.springs.common.rx

import com.springs.common.application.BaseApplication
import com.springs.common.presenter.view.BaseView
import com.springs.common.widgets.NetWorkUtils
import rx.Subscriber

/*
    Rx订阅者默认实现
 */
open class BaseSubscriber<T>(private val baseView: BaseView):Subscriber<T>() {

    override fun onStart() {
        super.onStart()
        if(!NetWorkUtils.isWifiConnected(BaseApplication.mApplication)){
            unsubscribe()
            onError(BaseException(-1,"网络不可用"))
        }
    }

    override fun onCompleted() {
        baseView.hideLoading()
    }

    override fun onNext(t: T) {
    }

    override fun onError(e: Throwable?) {
        baseView.hideLoading()
        if (e is BaseException){
            baseView.onError(e.msg)
        }
    }
}
