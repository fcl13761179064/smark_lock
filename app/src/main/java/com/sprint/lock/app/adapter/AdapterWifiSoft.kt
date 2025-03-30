package com.sprint.lock.app.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sprint.lock.app.R
import com.sprint.lock.app.databinding.ItemWifiSoftBinding



class AdapterWifiSoft : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_wifi_soft) {
    override fun convert(holder: BaseViewHolder, item: String) {
        val wifiBinding = ItemWifiSoftBinding.bind(holder.itemView)
        wifiBinding.tvSoft.text=item
    }
}