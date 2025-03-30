package com.sprint.lock.app.dialog

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.springs.common.base.BaseDialog
import com.sprint.lock.app.SplashActivity
import com.sprint.lock.app.databinding.DialogLogitOutBinding

/**
 */
class LeaveMessageDialog(context: Context,val block :() ->Unit) : BaseDialog<DialogLogitOutBinding>(context) {

    override fun getViewBinding(): DialogLogitOutBinding =
        DialogLogitOutBinding.inflate(layoutInflater)

    override fun setDefaultBackground() {
    }

    override fun initViews(savedInstanceState: Bundle?) {
        binding.tvConfire.setOnClickListener {
            block.invoke()
            dismiss()
        }
        binding.tvCancel.setOnClickListener {
            dismiss()
        }
    }

}