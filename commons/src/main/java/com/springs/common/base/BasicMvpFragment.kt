package com.springs.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.springs.common.ext.showTopToast
import com.springs.common.presenter.BasePresenter
import com.springs.common.presenter.view.BaseView
import com.springs.common.widgets.ProgressLoading
import com.trello.rxlifecycle.components.support.RxFragment
import java.lang.reflect.ParameterizedType

/*
    Activity基类，业务相关
 */
abstract class BasicMvpFragment<V: BaseView,T : BasePresenter<V>,VB : ViewBinding> : RxFragment(), BaseView {


    lateinit var mPresenter: T
    private var _binding: VB? = null
    val binding: VB get() = _binding!!

    abstract fun getViewBinding(): VB

    private lateinit var mLoadingDialog: ProgressLoading

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachMvpView()
        initPrePamams()
        //初始加载框
        mLoadingDialog = ProgressLoading.create(requireContext())
        init(savedInstanceState)
        initListener()
    }

    @Suppress("UNCHECKED_CAST")
    private fun attachMvpView() {
        mPresenter = getPresenter()
        mPresenter.mView = this as V
        mPresenter.context = requireContext()
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
