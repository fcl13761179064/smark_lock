package com.sprint.lock.app.fragment

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.PermissionUtils
import com.springs.common.base.BaseFragment
import com.springs.common.common.LiveDataBusX
import com.springs.common.dialog.PermissionDialog
import com.springs.common.ext.showTopToast
import com.springs.common.widgets.AppData
import com.springs.common.widgets.FastClickUtils
import com.springs.common.widgets.ScheduleTimeUtils
import com.sprint.lock.app.R
import com.sprint.lock.app.databinding.EmptyLayoutBinding
import com.sprint.lock.app.databinding.FragmentLeaveInfoBinding
import com.sprint.lock.app.dialog.AlsoUpdateDialog
import com.sprint.lock.app.dialog.LeaveMessageDialog
import com.sprint.lock.app.radio.MainContract
import com.sprint.lock.app.radio.MainPresenter
import com.sprint.lock.app.radio.RecordAudioButton
import com.sprint.lock.app.radio.VideoAdapter
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.Collections


class LeaveMessageFragment : BaseFragment<FragmentLeaveInfoBinding>(), MainContract.View {

    private val emptyView by lazy { EmptyLayoutBinding.inflate(LayoutInflater.from(activity)) }
    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentLeaveInfoBinding = FragmentLeaveInfoBinding.inflate(layoutInflater)

    val mAdapter = VideoAdapter() //适配器
    var mPresenter: MainContract.Presenter? = null

    val layoutManager by lazy { LinearLayoutManager(requireContext()) }
    override fun init(savedInstanceState: Bundle?, view: View) {
        mPresenter = MainPresenter<MainContract.View>(this, requireContext())
        mPresenter?.init()
        binding.btnVoice.setOnVoiceButtonCallBack(object : RecordAudioButton.OnVoiceButtonCallBack {
            override fun onStartRecord() {
                requestRecordPermission()
            }

            override fun onStopRecord() {
                ScheduleTimeUtils.cacelTimer()
                mPresenter?.stopRecord()
            }

            override fun onWillCancelRecord() {
                mPresenter?.willCancelRecord()
            }

            override fun onContinueRecord() {
                mPresenter?.continueRecord()
            }

        })

        binding.rvMsg.layoutManager = layoutManager
        mAdapter.setOnItemChildClickListener { _, view, position ->
            if (R.id.iv_voice == view.id) {
                mPresenter?.startPlayRecord(position)
            } else if (R.id.iv_delete == view.id) {
                    LeaveMessageDialog(requireActivity()) {
                        mPresenter?.clearVoice(position)
                    }.show()
            }
        }
        binding.rvMsg.adapter = mAdapter
        mAdapter.emptyView = emptyView.root

        LiveDataBusX.getInstance().with<Int>("delete_success").observe(requireActivity()) {
            lifecycleScope.launch {
                if (it != -1000) (mPresenter as MainPresenter<MainContract.View>).loadAudioCacheData()
            }
        }
    }


    private fun requestRecordPermission() {
        //权限申请
        PermissionUtils.permission(
            Manifest.permission.RECORD_AUDIO
        ).callback(object : PermissionUtils.SimpleCallback {
            override fun onGranted() {
                try {
                    ScheduleTimeUtils.showSeconds(requireActivity()) {
                        binding.tvStartTime.text = it
                    }
                    binding.btnVoice.isSelected = true
                    mPresenter?.startRecord()
                } catch (ignored: Exception) {
                    ignored.printStackTrace()
                }
            }

            override fun onDenied() {
                PermissionDialog(requireActivity(), "请开启录音权限").show()
            }

        }).request()
    }

    override fun showList(list: List<File>) {
        Collections.sort(list, Comparator.reverseOrder())
        mAdapter.setNewData(list)
        layoutManager.scrollToPositionWithOffset(0, 0)
    }

    override fun showNormalTipView() {
    }


    override fun showTimeOutTipView(remainder: Int) {
        showTimeOutTipViews(remainder)
    }

    /**
     * 正常录制
     */
    override fun showRecordingTipView() {
        showRecordingTipViews()
    }

    override fun showRecordTooShortTipView() {
        showRecordTooShortTipViews()
    }

    override fun showCancelTipView() {
        showCancelTipViews()
    }

    override fun hideTipView() {
        hideTipViews()
    }

    override fun updateCurrentVolume(db: Int) {
        updateCurrentVolumes(db)
    }

    override fun startPlayAnim(position: Int) {
        mAdapter.startPlayAnim(position)
    }

    override fun stopPlayAnim() {
        mAdapter.stopPlayAnim()
    }


    /**
     * 即将超时
     *
     * @param remainder
     */
    fun showTimeOutTipViews(remainder: Int) {
        binding.ivRcVolume.setVisibility(View.GONE)
        binding.tvRcStatus.setText(R.string.community_chat_list_remove_above_cancel_send)
        binding.tvRcTime.setText(String.format("%s", remainder))
        binding.tvRcTime.setVisibility(View.VISIBLE)
    }

    /**
     * 正常录制
     */
    fun showRecordingTipViews() {
        binding.ivRcVolume.setVisibility(View.VISIBLE) //声音音波
        binding.tvRcStatus.setText(R.string.community_chat_list_remove_above_cancel_send)//提示文字文本
        binding.tvRcTime.setVisibility(View.GONE)
    }

    /**
     * 录制时间太短
     */
    fun showRecordTooShortTipViews() {
        binding.ivRcVolume.visibility = View.GONE
        binding.tvRcStatus.setText(R.string.community_chat_list_rec_voice_short)
        binding.tvRcTime.visibility = View.GONE
        binding.tvStartTime.text = "00:00"
    }

    /**
     * 松开手指，取消发送
     */
    fun showCancelTipViews() {
        binding.ivRcVolume.setVisibility(View.GONE)
        binding.tvRcStatus.setText(R.string.community_chat_list_loosen_cancel_send)
        binding.tvRcTime.setVisibility(View.GONE)
        binding.tvStartTime.text = "00:00"
    }

    /**
     * 说话完成，隐藏所有的东西
     */

    fun hideTipViews() {
        binding.ivRcVolume.setVisibility(View.GONE)
        binding.tvRcStatus.setText(R.string.community_chat_list_rec_voice_btn_say)
        binding.tvRcTime.setVisibility(View.GONE)
        binding.tvStartTime.text = "00:00"
        binding.btnVoice.isSelected = false
    }


    /**
     * 更新当前音量大小
     */
    fun updateCurrentVolumes(decibel: Int) {
        when (decibel / 5) {
            0 -> binding.ivRcVolume.setImageResource(R.mipmap.community_record_volume_01)
            1 -> binding.ivRcVolume.setImageResource(R.mipmap.community_record_volume_01)
            2 -> binding.ivRcVolume.setImageResource(R.mipmap.community_record_volume_02)
            3 -> binding.ivRcVolume.setImageResource(R.mipmap.community_record_volume_03)
            4 -> binding.ivRcVolume.setImageResource(R.mipmap.community_record_volume_04)
            5 -> binding.ivRcVolume.setImageResource(R.mipmap.community_record_volume_05)
            6 -> binding.ivRcVolume.setImageResource(R.mipmap.community_record_volume_06)
            else -> binding.ivRcVolume.setImageResource(R.mipmap.community_record_volume_06)
        }
    }

}