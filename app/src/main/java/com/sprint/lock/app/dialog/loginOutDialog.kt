package com.sprint.lock.app.dialog

import android.content.Context
import android.os.Bundle
import com.springs.common.base.BaseDialog
import com.springs.common.ext.showToast
import com.springs.common.ext.showTopToast
import com.sprint.lock.app.databinding.AlsoUpdateDialogBinding
import com.sprint.lock.app.databinding.LoginOutDialogBinding

/**
 * 恢复出厂设置
 */
class loginOutDialog(context: Context,  val  block :() ->Unit) : BaseDialog<LoginOutDialogBinding>(context) {


    override fun getViewBinding(): LoginOutDialogBinding =LoginOutDialogBinding.inflate(layoutInflater)

    override fun setDefaultBackground() {

    }

    override fun initViews(savedInstanceState: Bundle?) {
        binding.allView.setOnClickListener {
            dismiss()
        }
        binding.atOk.setOnClickListener {
            if (binding.tvAlsoNewDesc.text.toString() =="666888"){
                showTopToast("正在退出...")
                block.invoke()
                dismiss()
            }else{
                showTopToast("密码错误")
            }
        }
    }

}