package com.sprint.lock.app.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sprint.lock.app.R
import com.sprint.lock.app.databinding.ItemWifiSoftNumBinding


class AdapterWifiSoftNumber : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_wifi_soft_num) {
    override fun convert(holder: BaseViewHolder, item: String) {
        val wifiBinding = ItemWifiSoftNumBinding.bind(holder.itemView)
        wifiBinding.tvSoft.text = item
        if ("删除" == item)
            wifiBinding.tvSoft.textSize = 12f
        else
            wifiBinding.tvSoft.textSize = 15f
    }
}