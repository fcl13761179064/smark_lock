package com.sprint.lock.app.dialog

import android.content.Context
import android.os.Bundle
import com.blankj.utilcode.util.ToastUtils
import com.springs.common.base.BaseMySelfDialog
import com.springs.common.ext.showTopToast
import com.sprint.lock.app.databinding.DialogXipingBinding
import com.sprint.lock.app.widge.ScreenTimeoutUtil


/**
 * des:认证
 */
class XiPingDialog(context: Context) : BaseMySelfDialog<DialogXipingBinding>(context) {

    override fun getViewBinding(): DialogXipingBinding =
        DialogXipingBinding.inflate(layoutInflater)

    override fun initViews(savedInstanceState: Bundle?) {
         binding.atTitle1.setOnClickListener {
             setScreenTime(30000,"当前熄屏时间是5分钟")
         }
         binding.atTitle2.setOnClickListener {
             setScreenTime(600000,"当前熄屏时间是10分钟")
         }
         binding.atTitle3.setOnClickListener {
             setScreenTime(900000, "当前熄屏时间是15分钟")
         }
         binding.atTitle4.setOnClickListener {
             setScreenTime(Long.MAX_VALUE, "当前熄屏时间是永久")
         }
    }

  fun setScreenTime(time: Long, s: String) {
          val screenTimeoutUtil = ScreenTimeoutUtil(context)
          // 检查是否有修改系统设置的权限
          if (screenTimeoutUtil.hasWriteSettingsPermission()) {
              // 设置屏幕超时时间为 5 分钟（300000 毫秒）
              screenTimeoutUtil.screenTimeout = time
              showTopToast(s)
          } else {
              // 请求权限
              screenTimeoutUtil.requestWriteSettingsPermission()
          }
      dismiss()
  }

    /**
     * 设置默认背景（顶部圆角-16dp）
     */
    override  fun setDefaultBackground() {

    }

}


