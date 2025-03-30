package com.springs.common.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import kotlin.reflect.KClass


/**
 * @desc:   基础类
 */
abstract class BaseActivity<VB : ViewBinding> : RxAppCompatActivity(){


    private var _binding: VB? = null

    val binding: VB get() = _binding!!

    abstract fun getViewBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 隐藏状态栏
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        initPrePamams()
        _binding = getViewBinding()
        setContentView(_binding?.root)
        init(savedInstanceState)
        initListener()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}