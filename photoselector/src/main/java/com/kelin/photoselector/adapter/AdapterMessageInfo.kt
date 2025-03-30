package com.kelin.photoselector.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kelin.photoselector.R
import com.room.database.bean.Door
import com.springs.common.bean.MessageInfo


/**
 * @ClassName:  电影模块，indicator 全部，内地，港台
 * @Author:finaly
 * @CreateDate: 2023/9/05 16:25
 */
class AdapterMessageInfo : BaseQuickAdapter<Door, BaseViewHolder>(R.layout.adapter_message) {
    override fun convert(holder: BaseViewHolder, item: Door) {
            holder.setText(R.id.tv_hhss,item.time)
            holder.setText(R.id.tv_data,item.data)
            holder.setText(R.id.tv_message,item.isType)
    }
}