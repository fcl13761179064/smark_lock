package com.springs.common.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.springs.common.ext.showTopToast
import com.springs.common.widgets.ProgressLoading
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass
import com.springs.common.presenter.view.BaseView
import com.springs.common.presenter.BasePresenter
/*
    Activity基类，业务相关
 */
abstract class BasicMvpActivity<V:BaseView,T : BasePresenter<V>,VB : ViewBinding> : RxAppCompatActivity(), BaseView {


    lateinit var mPresenter: T
    private var _binding: VB? = null
    val binding: VB get() = _binding!!

    abstract fun getViewBinding(): VB

    private lateinit var mLoadingDialog: ProgressLoading

    override fun onCreate(savedInstanceState: Bundle?) {
        initPrePamams()
        attachMvpView()
        //初始加载框
        mLoadingDialog = ProgressLoading.create(this)
        _binding = getViewBinding()
        setContentView(_binding?.root)
        init(savedInstanceState)
        initListener()
        super.onCreate(savedInstanceState)
    }

    @Suppress("UNCHECKED_CAST")
    private fun attachMvpView() {
        mPresenter = getPresenter()
        mPresenter.mView = this as V
        mPresenter.context = this
        mPresenter.lifecycleProvider = this
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getPresenter(): T {
        val type = this::class.java.genericSuperclass
        val types = (type as ParameterizedType).actualTypeArguments
        return (types.last() as Class<T>).newInstance()
    }



    /**
     * 用于设置一些特殊参数，需要在setContent之前的
     */
    open fun initPrePamams() {

    }

    /**
     * 初始化点击事件
     */
    open fun initListener() {

    }

    /**
     * 仅初始化一些与界面相关的操作
     */
    abstract fun init(savedInstanceState: Bundle?)

    /**
     * 启动activity
     * @param activity KClass<out Activity>
     * @param block [@kotlin.ExtensionFunctionType] Function1<Intent, Unit> 带参数
     */
    fun startActivity(activity: KClass<out Activity>, block: Intent.() -> Unit = {}) {
        startActivity(Intent(this, activity.java).apply(block))
    }


    /**
     * 启动activity
     * KClass扩展方法模式
     */
    fun <T : KClass<out Activity>> T.start(block: Intent.() -> Unit) {
        startActivity(this, block)
    }



    /*
        显示加载框，默认实现
     */
    override fun showLoading() {
        mLoadingDialog.showLoading()
    }

    /*
        隐藏加载框，默认实现
     */
    override fun hideLoading() {
        mLoadingDialog.hideLoading()
    }

    /*
        错误信息提示，默认实现
     */
    override fun onError(text: String) {
        showTopToast(text)
    }



}
