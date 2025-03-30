package com.springs.common.presenter

import android.content.Context
import com.springs.common.ext.netRequest
import com.springs.common.presenter.view.BaseView
import com.trello.rxlifecycle.LifecycleProvider
import rx.Observable


/*
    MVP中P层 基类
 */
open class BasePresenter<T: BaseView>{

    lateinit var mView:T

    //Dagger注入，Rx生命周期管理
    lateinit var lifecycleProvider: LifecycleProvider<*>
    lateinit var context:Context


    /**
     * 简化网络请求传参
     */
    fun <V> Observable<V>.request(onSuccess:(value:V) -> Unit, onFailure:(e:Throwable?) -> Unit = {}){
        this.netRequest(lifecycleProvider,mView,onSuccess,onFailure)
    }
}
