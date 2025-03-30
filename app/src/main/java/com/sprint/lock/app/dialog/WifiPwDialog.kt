package com.sprint.lock.app.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.springs.common.base.BaseDialog
import com.springs.common.common.px2dp
import com.sprint.lock.app.R
import com.sprint.lock.app.adapter.AdapterWifiSoft
import com.sprint.lock.app.adapter.AdapterWifiSoftNumber
import com.sprint.lock.app.databinding.DialogWifiInputPwBinding
import com.sprint.lock.app.widge.CommonAppData

/**
 * des:支付成功的显示页面
 */
 class WifiPwDialog(context: Context,val wifiName: String, val onClickListener: OnClickListener) :  BaseDialog<DialogWifiInputPwBinding>(context){
    private lateinit var gridLayoutManager: GridLayoutManager
    private var isLowerCase = false//是否是小写

     fun initListener() {
        // 第二个弹框成功了之后，要刷新电影详情的数据哦，和本地的vip,
        binding.tvSure.setOnClickListener {
            dismiss()
            onClickListener.onCallBack(binding.tvPw.text.toString())
        }
        binding.cancelView.setOnClickListener {
            dismiss()
        }

         binding.tvCancel.setOnClickListener {
             dismiss()
         }
        binding.csAllView.setOnClickListener {
            dismiss()
        }

        binding.rgSoft.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                //大小写
                R.id.rb_letter -> {
                    isLowerCase = false
                    val adapter = AdapterWifiSoft()
                    binding.rvSoft.adapter = adapter
                    binding.rvSoft.setPadding(
                        px2dp( 6f),
                        0,
                        px2dp( 6f),
                        0
                    )
                    gridLayoutManager.spanCount = 5
                    gridLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (position == 28) 2 else 1
                        }
                    }

                    adapter.addData(CommonAppData.getWifiSoftLetterList(isLowerCase))
                    setAdapterClick(adapter)
                }

                //数字
                R.id.rb_number -> {
                    val adapter = AdapterWifiSoftNumber()
                    binding.rvSoft.adapter = adapter
                    binding.rvSoft.setPadding(
                        px2dp( 9f),
                        px2dp( 3f),
                        px2dp( 9f),
                        0
                    )
                    gridLayoutManager.spanCount = 3
                    gridLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return 1
                        }
                    }
                    adapter.addData(CommonAppData.getWifiSoftNumberList())
                    setAdapterClick(adapter)
                }

                //符号
                R.id.rb_symbol -> {
                    val adapter = AdapterWifiSoft()
                    binding.rvSoft.adapter = adapter
                    binding.rvSoft.setPadding(
                        px2dp( 6f),
                        0,
                        px2dp( 6f),
                        0
                    )
                    gridLayoutManager.spanCount = 5
                    gridLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (position == 28) 2 else 1
                        }
                    }
                    adapter.addData(CommonAppData.getWifiSoftSymbolList())
                    setAdapterClick(adapter)
                }
            }
        }

        binding.rbLetter.isChecked = true
    }

    private fun setAdapterClick(adapter: BaseQuickAdapter<String, BaseViewHolder>) {
        adapter.setOnItemClickListener { _, _, position ->
            val strPw = binding.tvPw.text.toString()
            val strItem = adapter.getItem(position)
            if ("删除" == strItem) {
                if (strPw.isNotEmpty())
                    binding.tvPw.text = strPw.substring(0, strPw.length - 1)
            } else if ("A/a" == strItem) {
                isLowerCase = !isLowerCase
                adapter.setNewData(CommonAppData.getWifiSoftLetterList(isLowerCase))
            } else
                binding.tvPw.text = strPw.plus(strItem)
        }
    }



    fun interface OnClickListener {
        fun onCallBack(pw: String)
    }

    override fun getViewBinding(): DialogWifiInputPwBinding =DialogWifiInputPwBinding.inflate(layoutInflater)

    override fun setDefaultBackground() {
       window.apply {
            val params = this!!.attributes
            params.gravity = Gravity.CENTER
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.MATCH_PARENT
            params.windowAnimations = R.style.fm_animBottom
            params.dimAmount = 0.8f
            window?.attributes = params
            window?.decorView?.setPadding(0, 0, 0, 0)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

    }

    override fun initViews(savedInstanceState: Bundle?) {
        binding.tvName.text = wifiName
        gridLayoutManager = GridLayoutManager(context, 5)
        binding.rvSoft.setHasFixedSize(true)
        binding.rvSoft.layoutManager = gridLayoutManager
        initListener()
    }
}