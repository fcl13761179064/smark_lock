package com.sprint.lock.app.radio

import android.content.Context
import android.net.Uri
import android.util.Log
import com.blankj.utilcode.util.SPUtils
import com.springs.common.common.LiveDataBusX
import com.springs.common.ext.showTopToast
import com.springs.common.widgets.ScheduleTimeUtils.getYMDTime
import com.sprint.lock.app.radio.MainContract.Presenter
import java.io.File

/**
 * 首页P层
 */
class MainPresenter<T : MainContract.View?>(private val mView: T, private val mContext: Context) :
    Presenter {
    private val mAudioDir: File
    private val mListData: MutableList<File> = ArrayList()

    init {
        mAudioDir = File(mContext.externalCacheDir, AUDIO_DIR_NAME)
        if (!mAudioDir.exists()) { //判断文件夹是否存在，不存在则创建
            mAudioDir.mkdirs()
        }
    }

    override fun init() {
        initAudioManager()
        loadAudioCacheData()
    }

    override fun startRecord() {
        AudioRecordManager.getInstance(mContext).startRecord()
    }

    override fun stopRecord() {
        AudioRecordManager.getInstance(mContext).stopRecord()
    }

    override fun willCancelRecord() {
        AudioRecordManager.getInstance(mContext).willCancelRecord()
    }

    override fun continueRecord() {
        AudioRecordManager.getInstance(mContext).continueRecord()
    }

    override fun startPlayRecord(position: Int) {
        val item = mListData[position]
        val audioUri = Uri.fromFile(item)
        Log.d("P_startPlayRecord", audioUri.toString())
        AudioPlayManager.getInstance().startPlay(mContext, audioUri, object : IAudioPlayListener {
            override fun onStart(var1: Uri) {
                mView!!.startPlayAnim(position)
            }

            override fun onStop(var1: Uri) {
                mView!!.stopPlayAnim()
            }

            override fun onComplete(var1: Uri) {
                mView!!.stopPlayAnim()
            }
        })
    }

    override fun clearVoice(position: Int) {
        if (mAudioDir.exists()) {
            val isSuccess = mAudioDir.listFiles()[position].delete()
            if (isSuccess) {
                LiveDataBusX.getInstance().with<Any>("delete_success").postValue(position)
                showTopToast("删除成功")
            } else {
                showTopToast("删除失败")
            }
        }
    }

    /**
     * 初始化音频播放管理对象
     */
    private fun initAudioManager() {
        AudioRecordManager.getInstance(mContext).setAudioSavePath(mAudioDir.absolutePath)
        AudioRecordManager.getInstance(mContext).maxVoiceDuration = MAX_VOICE_TIME
        AudioRecordManager.getInstance(mContext).audioRecordListener =
            object : IAudioRecordListener {
                override fun initTipView() {
                    mView!!.showNormalTipView()
                }

                override fun setTimeoutTipView(counter: Int) {
                    mView!!.showTimeOutTipView(counter)
                }

                override fun setRecordingTipView() {
                    mView!!.showRecordingTipView()
                }

                override fun setAudioShortTipView() {
                    mView!!.showRecordTooShortTipView()
                }

                override fun setCancelTipView() {
                    mView!!.showCancelTipView()
                }

                override fun destroyTipView() {
                    mView!!.hideTipView()
                }

                override fun onStartRecord() {}
                override fun onFinish(audioPath: Uri, duration: Int) {
                    getYMDTime { s: String? ->
                        SPUtils.getInstance().put(audioPath.path!!, s)
                        SPUtils.getInstance().put(audioPath.path + "HH", duration)
                        null
                    }
                    val file = File(audioPath.path)
                    if (file.exists()) {
                        loadAudioCacheData()

                    }
                }

                override fun onAudioDBChanged(db: Int) {
                    mView!!.updateCurrentVolume(db)
                }
            }
    }

    fun loadAudioCacheData() {
        mListData.clear()
        val files = mAudioDir.listFiles()
        for (file in files) {
                mListData.add(file)
        }
        mView!!.showList(mListData)
    }

    companion object {
        private const val MAX_VOICE_TIME = 180 //声音最大时间
        private const val AUDIO_DIR_NAME = "audio_cache"
    }
}