package com.sprint.lock.app.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sprint.lock.app.AppInfo
import com.sprint.lock.app.R

class AppsAdapter(): BaseQuickAdapter<AppInfo, BaseViewHolder>(R.layout.adapter_lunch) {
    override fun convert(holder: BaseViewHolder, item: AppInfo) {
        holder.setText(R.id.sbgl_one, item.appName)
        holder.setImageDrawable(R.id.iv_one, item.icon)
    }
}