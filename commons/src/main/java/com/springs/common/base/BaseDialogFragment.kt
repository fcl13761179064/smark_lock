package com.springs.common.base


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding

/**
 * BaseDialogFragment
 * 基类
 */
abstract class BaseDialogFragment<VB : ViewBinding> : DialogFragment() {

    var isNeedRefreshOnResume = false

    private var isLoaded = false

    private var _binding: VB? = null

    val binding: VB get() = _binding!!

    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        _binding = getViewBinding(inflater, container)
        return _binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSpecialEvent()
        init(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (isNeedRefreshOnResume && isLoaded) {
            onResumeRefresh()
            isNeedRefreshOnResume = false
        }

        if (isLoaded) {
            onResumeExceptFirst()
        }
        if (!isLoaded) {
            onResumeFirst()
            isLoaded = true
        }
    }

    /**
     * 初始化一些特殊的事件，放在最底层初始化界面之前
     */
    open fun initSpecialEvent() {

    }

    /**
     * 仅初始化一些与界面相关的操作
     */
    abstract fun init(savedInstanceState: Bundle?)


    open fun onResumeRefresh() {

    }

    open fun onResumeFirst() {

    }

    open fun onResumeExceptFirst() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        isLoaded = false
    }
}