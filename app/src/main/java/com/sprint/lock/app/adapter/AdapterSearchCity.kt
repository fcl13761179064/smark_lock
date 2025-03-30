package com.sprint.lock.app.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qweather.sdk.bean.geo.GeoBean.LocationBean
import com.sprint.lock.app.R


class AdapterSearchCity : BaseQuickAdapter<LocationBean, BaseViewHolder>(R.layout.search_top_city) {
    override fun convert(holder: BaseViewHolder, item: LocationBean) {
         holder.setText(R.id.tvCityName,item.name)
         holder.setText(R.id.tvCityName2,item.adm2)
         holder.setText(R.id.tvCityName3,item.adm1)
         holder.setText(R.id.tvCityName4,item.country)
        holder.addOnClickListener(R.id.ll_search)
    }
}