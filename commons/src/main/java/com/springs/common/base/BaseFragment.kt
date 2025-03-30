package com.springs.common.base


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.trello.rxlifecycle.components.support.RxFragment


/**
 * @Description:    所有Fragment的基类，
 */
abstract class BaseFragment<VB : ViewBinding> : RxFragment() {



    private var isLoaded = false

    private var _binding: VB? = null

    val binding: VB
        get() = _binding!!


    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding(inflater, container)
       return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSpecialEvent()
        init(savedInstanceState,view)
        initListener()
    }

    override fun onResume() {
        super.onResume()
        if (!isLoaded) {
            onResumeFirst()
            isLoaded = true
        }
    }


    /**
     * 初始化点击事件
     */
    open fun initListener() {

    }

    /**
     * 初始化一些特殊的事件，放在最底层初始化界面之前
     */
    open fun initSpecialEvent() {

    }

    /**
     * 仅初始化一些与界面相关的操作
     */
    abstract fun init(savedInstanceState: Bundle?, view: View)



    open fun onResumeFirst() {

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        isLoaded = false
    }
}