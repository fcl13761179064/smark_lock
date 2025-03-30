package com.springs.common.application

import android.content.Context
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.multidex.MultiDexApplication


open class BaseApplication : MultiDexApplication(), ViewModelStoreOwner{


    private lateinit var mViewModelStore: ViewModelStore
    companion object {
        // 全局Context   @SuppressLint("StaticFieldLeak") 加上注解让忽略警告的作用
        lateinit var mContext: Context
        lateinit var mApplication: BaseApplication
        lateinit var appVersion:String
        lateinit var appFlavors:String
    }


    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        mContext = context
    }

    override fun onCreate() {
        super.onCreate()
        mApplication = this
        mViewModelStore = ViewModelStore()
    }


    override val viewModelStore: ViewModelStore
        get() = mViewModelStore



}