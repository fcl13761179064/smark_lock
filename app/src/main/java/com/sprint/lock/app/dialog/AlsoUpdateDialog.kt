package com.sprint.lock.app.dialog

import android.content.Context
import android.os.Bundle
import com.springs.common.base.BaseDialog
import com.sprint.lock.app.databinding.AlsoUpdateDialogBinding

/**
 * 恢复出厂设置
 */
class AlsoUpdateDialog(context: Context,val title:String?="",val subtitle :String?="",val  block :() ->Unit) : BaseDialog<AlsoUpdateDialogBinding>(context) {


    override fun getViewBinding(): AlsoUpdateDialogBinding =AlsoUpdateDialogBinding.inflate(layoutInflater)

    override fun setDefaultBackground() {

    }

    override fun initViews(savedInstanceState: Bundle?) {
        if (title?.isNotEmpty() == true){
            binding.tvTitle.text =title
            binding.atOk.text ="确认"
            binding.tvAlsoNewDesc.text =subtitle
        }

        binding.allView.setOnClickListener {
            dismiss()
        }
        binding.atOk.setOnClickListener {
            block.invoke()
            dismiss()
        }
    }

}