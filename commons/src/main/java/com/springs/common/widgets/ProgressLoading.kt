package com.springs.common.widgets

import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.springs.common.R

/*
    加载对话框封装
 */
class ProgressLoading private constructor(context: Context, theme: Int) : Dialog(context, theme) {

    private val handler = Handler(Looper.getMainLooper())

    companion object {

        /*
            创建加载对话框
         */
        fun create(context: Context, msg: String? = null): ProgressLoading {
            //样式引入
            val mDialog = ProgressLoading(context, R.style.LightProgressDialog)
            //设置布局
            mDialog.setContentView(R.layout.progress_dialog)
            mDialog.setCancelable(true)
            mDialog.setCanceledOnTouchOutside(false)
            mDialog.window?.attributes?.gravity = Gravity.CENTER

            val lp = mDialog.window?.attributes
            lp?.dimAmount = 0.2f
            //设置属性
            mDialog.window?.attributes = lp

            if (!TextUtils.isEmpty(msg)) {
                mDialog.findViewById<TextView>(R.id.tvMsg).apply {
                    visibility = View.VISIBLE
                    text = msg
                }
            }

            return mDialog
        }
    }

    /*
        显示加载对话框，动画开始
     */
    fun showLoading() {
        super.show()
    }

    /*
        隐藏加载对话框，动画停止
     */
    fun hideLoading() {
        super.dismiss()
    }
}
