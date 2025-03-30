package com.sprint.lock.app.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sprint.lock.app.R
import com.sprint.lock.app.bean.WifiBean
import com.sprint.lock.app.databinding.ItemWifiBinding

/**
 * @ClassName:  电影模块，indicator 全部，内地，港台
 * @Author:finaly
 * @CreateDate: 2023/9/05 16:25
 */
class AdapterWifi : BaseQuickAdapter<WifiBean, BaseViewHolder>(R.layout.item_wifi) {
    override fun convert(holder: BaseViewHolder, item: WifiBean) {
        val wifiBinding = ItemWifiBinding.bind(holder.itemView)

        wifiBinding.tvName.text = item.name

        if (!item.needPw) {
            wifiBinding.imgNeedPw.visibility = View.INVISIBLE
        } else
            wifiBinding.imgNeedPw.visibility = View.VISIBLE

        wifiBinding.llGen.isSelected = item.connectType == 2

        //连接中
        if (item.connectType == 1)
            wifiBinding.tvConnecting.visibility = View.VISIBLE
        else
            wifiBinding.tvConnecting.visibility = View.INVISIBLE

    }
}