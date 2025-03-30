package com.springs.common.dialog

import android.content.Context
import android.os.Bundle
import com.springs.common.base.BaseDialog
import com.springs.common.databinding.PermissionDialogBinding
import com.springs.common.widgets.PermissionUtils

/**
 * des:dialog
 */
class PermissionDialog(val mContext: Context,val title :String?="") : BaseDialog<PermissionDialogBinding>(mContext) {

    override fun getViewBinding(): PermissionDialogBinding =
        PermissionDialogBinding.inflate(layoutInflater)

    override fun setDefaultBackground() {
    }

    override fun initViews(savedInstanceState: Bundle?) {
        initAttributes()
        binding.tvConfire.setOnClickListener {
            PermissionUtils.toAppSetting(mContext)
            dismiss()
        }
        binding.tvCancel.setOnClickListener {
            dismiss()
        }
        if (title?.isNotEmpty() == true){
            binding.tvTitle.text =title
        }
    }

    private fun initAttributes() {
        window?.attributes?.apply {
            gravity = getLayoutGravity()
            width = (getDialogWidth())
            height = (getDialogHeight())
        }
        window?.setWindowAnimations(getAnimResId())
    }
}