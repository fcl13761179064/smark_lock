package com.springs.common.ext

import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.blankj.utilcode.util.ToastUtils
import com.springs.common.R
import com.springs.common.application.BaseApplication
import com.springs.common.presenter.view.BaseView
import com.springs.common.protocol.BaseResp
import com.springs.common.rx.BaseException
import com.springs.common.rx.BaseObserver
import com.springs.common.rx.BaseSubscriber
import com.springs.common.rx.ErrorConvert
import com.springs.common.rx.RespConvert
import com.trello.rxlifecycle.LifecycleProvider
import com.trello.rxlifecycle.android.ActivityEvent
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


/**
 * 网络请求（非MVP）
 */
fun <T> Observable<T>.netRequest(
    lifecycleProvider: RxAppCompatActivity,
    onSuccess: (value: T) -> Unit,
    onFailure: (e: Throwable?) -> Unit = {}
) {
    this.subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(ErrorConvert())
        .flatMap(RespConvert<T>())
        .compose(lifecycleProvider.bindUntilEvent(ActivityEvent.DESTROY))
        .subscribe(object : BaseObserver<T>() {
            override fun onNext(t: T) {
                super.onNext(t)
                onSuccess.invoke(t)
            }

            override fun onError(e: Throwable?) {
                super.onError(e)
                onFailure.invoke(e)
            }
        })
}

/**
 * 网络请求（MVP）
 */
fun <T> Observable<T>.netRequest(
    lifecycleProvider: LifecycleProvider<*>,
    view: BaseView,
    onSuccess: (value: T) -> Unit,
    onFailure: (e: Throwable?) -> Unit = {}
) {
    this.subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(RespConvert<T>())
        .onErrorResumeNext(ErrorConvert())
        .compose(lifecycleProvider.bindToLifecycle())
        .subscribe(object : BaseSubscriber<T>(view) {
            override fun onNext(t: T) {
                super.onNext(t)
                onSuccess.invoke(t)
            }

            override fun onError(e: Throwable?) {
                super.onError(e)
                onFailure.invoke(e)
            }
        })
}


/**
 * 网络请求（非MVP,不进行提示）
 */
fun <T> Observable<T>.requestWithoutToast(
    lifecycleProvider: LifecycleProvider<*>,
    onSuccess: (value: T) -> Unit,
    onFailure: (e: BaseException) -> Unit = {}
) {
    this.subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(ErrorConvert())
        .flatMap(RespConvert<T>())
        .compose(lifecycleProvider.bindToLifecycle())
        .subscribe(object : BaseObserver<T>() {
            override fun onNext(t: T) {
                super.onNext(t)
                onSuccess.invoke(t)
            }

            override fun onError(e: Throwable?) {
                if (e is BaseException) {
                    onFailure.invoke(e)
                } else {
                    onFailure.invoke(BaseException(-1, e?.localizedMessage ?: "服务异常"))
                }
            }
        })
}


fun showToast(message:String){
  Toast.makeText(BaseApplication.mApplication,message,Toast.LENGTH_SHORT).show()
}


fun showTopToast(message:String){
    ToastUtils.getDefaultMaker().setTextColor(Color.WHITE)
    ToastUtils.getDefaultMaker().setBgResource(R.drawable.fm_toast_bg)
    ToastUtils.getDefaultMaker().setGravity(Gravity.TOP,0,5)
    ToastUtils.getDefaultMaker().setNotUseSystemToast().setDurationIsLong(false)
    ToastUtils.showShort(message,50)
}

fun  Logutils(message: String){
    Log.d("LOG",message)
}

/*
    扩展点击事件
 */
fun View.onClick(listener: View.OnClickListener): View {
    setOnClickListener(listener)
    return this
}

/*
    扩展点击事件，参数为方法
 */
fun View.onClick(method: () -> Unit): View {
    setOnClickListener { method() }
    return this
}

/*
    扩展视图可见性
 */
fun View.setVisible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

/*
    扩展视图可见性
 */
fun View.setInvisible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

/*
    扩展视图可见性
 */
fun View.setIns(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun BaseResp<*>.isLoginExpire(): Boolean { return LmRp8tSEId5doyRCQJ0Wiou1uYOc9xmTzyn == 122001 }