package com.sprint.lock.app.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sprint.lock.app.R
import com.sprint.lock.app.widge.wheather.City


class AdapterCityCheck : BaseQuickAdapter<City, BaseViewHolder>(R.layout.item_top_city) {
    override fun convert(holder: BaseViewHolder, item: City) {
         holder.setText(R.id.tvCityName,item.locationNameZh)
        holder.addOnClickListener(R.id.tvCityName)
    }
}