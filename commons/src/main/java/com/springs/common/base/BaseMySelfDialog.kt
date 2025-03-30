package com.springs.common.base

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ScreenUtils
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.springs.common.R
import com.springs.common.widgets.dp

/**
 * @ClassName:  BaseDialog
 * @Description:dialog基类
 * @Author: vi1zen
 * @CreateDate: 2020/11/24 10:56
 */
abstract class BaseMySelfDialog<VB : ViewBinding> : Dialog {

    constructor(context: Context) : this(context, R.style.CommonDialog)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    abstract fun getViewBinding(): VB
    private var _binding: VB? = null
    val binding: VB get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        setContentView(_binding!!.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setDefaultBackground()
        initViews(savedInstanceState)
        initAttributes()
    }

  abstract  fun setDefaultBackground()



    private fun initAttributes() {
        window?.attributes?.apply {
            gravity = getLayoutGravity()
            width = (getDialogWidth()*0.5).toInt()
            height = (getDialogHeight()*0.7).toInt()
        }
    }

    open fun getAnimResId(): Int {
        return R.style.AnimBottom
    }

    open fun getDialogHeight(): Int {
        return ScreenUtils.getScreenHeight()
    }

    open fun getDialogWidth(): Int {
        return ScreenUtils.getAppScreenWidth()
    }

    open fun getLayoutGravity(): Int {
        return Gravity.CENTER
    }

    override fun show() {
        window?.let { BarUtils.setNavBarVisibility(it, false) }
        super.show()
    }


    /**
     * 初始化视图
     */
    abstract fun initViews(savedInstanceState: Bundle?)
}