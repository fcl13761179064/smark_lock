package com.sprint.lock.app.radio

import android.widget.ImageView
import com.blankj.utilcode.util.SPUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sprint.lock.app.R
import java.io.File

/**
 * 适配器
 */
class VideoAdapter :
    BaseQuickAdapter<File, BaseViewHolder>(R.layout.community_adapter_chat_list_right_voice) {
    private var mCurrentPlayAnimPosition = -1 //当前播放动画的位置
    var isCurrentPlaying = false
    override fun convert(helper: BaseViewHolder, item: File) {
        //这里就不考虑语音长度了，实际开发中用到的Sdk有提供保存语音信息的bean
        var ivAudio: VoiceImageView = helper.getView(R.id.iv_voice)
        if (mCurrentPlayAnimPosition == helper.getLayoutPosition()) {
            ivAudio.startPlay()
            isCurrentPlaying = true
        } else {
            isCurrentPlaying = false
            ivAudio.stopPlay()
        }
        val time = SPUtils.getInstance().getString(item.path, "2024/10/26")
        val duration = SPUtils.getInstance().getInt(item.path + "HH", 3)
        helper.setText(R.id.tv_duration, duration.toString())
        helper.setText(R.id.tv_name, time)
        helper.addOnClickListener(R.id.iv_voice)
        helper.addOnClickListener(R.id.iv_delete)
    }

    /**
     * 开始播放动画
     *
     * @param position
     */
    fun startPlayAnim(position: Int) {
        mCurrentPlayAnimPosition = position
        notifyDataSetChanged()
    }

    fun isCurrentPlay(): Boolean {
        return isCurrentPlaying
    }


    /**
     * 停止播放动画
     */
    fun stopPlayAnim() {
        mCurrentPlayAnimPosition = -1
        notifyDataSetChanged()
    }


}