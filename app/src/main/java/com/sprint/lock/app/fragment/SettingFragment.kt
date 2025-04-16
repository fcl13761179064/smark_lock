package com.sprint.lock.app.fragment

import android.graphics.PixelFormat
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.blankj.utilcode.util.Utils
import com.springs.common.base.BaseFragment
import com.springs.common.databinding.LayoutVolumeBinding
import com.springs.common.ext.showTopToast
import com.springs.common.widgets.AudioMngHelper
import com.springs.common.widgets.ProgressLoading
import com.springs.common.widgets.ViewAnimationTime
import com.sprint.lock.app.MainActivity
import com.sprint.lock.app.databinding.SettingFragmentBinding
import com.sprint.lock.app.dialog.AlsoUpdateDialog
import com.sprint.lock.app.dialog.XiPingDialog
import com.sprint.lock.app.dialog.loginOutDialog
import com.sprint.lock.app.widge.CacheUtils
import java.io.IOException
import java.util.Timer
import java.util.TimerTask


class SettingFragment : BaseFragment<SettingFragmentBinding>() {

    private val loadingDialog by lazy { ProgressLoading.create(requireActivity(), "正在恢复中...") }
    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): SettingFragmentBinding = SettingFragmentBinding.inflate(layoutInflater)

    override fun init(savedInstanceState: Bundle?, view: View) {

        binding.time.setOnClickListener {
           XiPingDialog(requireContext()).show()
        }
        binding.imageView1.setOnClickListener {
            initFragment()
        }

        binding.imageView2.setOnClickListener {
            initFragmentSd()
        }

        binding.imageView3.setOnClickListener {
            initFragmentSystem()
        }

        binding.imageView4.setOnClickListener {
            AlsoUpdateDialog(requireActivity(), "恢复出场设置","恢复出厂设置后所有配置讲清除！") {
                try {
                    loadingDialog.showLoading()
                    val time = Timer()
                    time.schedule(object : TimerTask() {
                        override fun run() {
                            requireActivity().runOnUiThread {
                                loadingDialog.hideLoading()
                                CacheUtils.clearAllCache(requireContext())
                                showTopToast("恢复出厂成功")
                                time.cancel()
                            }
                        }
                    }, 3000)

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.show()

        }

        binding.atMinusVolue.ViewAnimationTime({
            oPeraSound(false)

        }, 100)



        binding.atPlusVolue.ViewAnimationTime({
            oPeraSound(true)
        }, 100)

        binding.imageMima.ViewAnimationTime({
            loginOutDialog(requireContext()){
          (  requireActivity()  as MainActivity).finish()
            }.show()
        }, 100)

    }

    private val audioMngHelper by lazy { AudioMngHelper(requireContext()) }
    private var volumeBinding: LayoutVolumeBinding? = null
    fun oPeraSound(isAdjustPlus: Boolean) {
        //WindowManager方式
        if (volumeBinding == null) {
            volumeBinding = LayoutVolumeBinding.inflate(layoutInflater)
            val mParams: WindowManager.LayoutParams =
                WindowManager.LayoutParams()/* <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>*//* if (Build.VERSION.SDK_INT >= 26) {
                         mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                     } else {
                         mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                     }*/
            mParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            mParams.width = WindowManager.LayoutParams.WRAP_CONTENT
            mParams.format = PixelFormat.TRANSLUCENT
            mParams.windowAnimations = android.R.style.Animation_Toast
            mParams.title = "ToastWithoutNotification"
            mParams.flags =
                (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            mParams.packageName = Utils.getApp().packageName

            mParams.gravity = Gravity.CENTER
            if (mParams.gravity and Gravity.HORIZONTAL_GRAVITY_MASK == Gravity.FILL_HORIZONTAL) {
                mParams.horizontalWeight = 1.0f
            }
            if (mParams.gravity and Gravity.VERTICAL_GRAVITY_MASK == Gravity.FILL_VERTICAL) {
                mParams.verticalWeight = 1.0f
            }
            requireActivity().windowManager.addView(volumeBinding!!.root, mParams)
        }
        //如果是禁音，调大音乐歌曲，让其不禁音
        if (audioMngHelper.isStreamMute) {
            audioMngHelper.isStreamMute = false
        }
        volumeBinding?.let {
            val a: Int = if (isAdjustPlus) {
                it.imgIcon.setImageResource(com.sprint.lock.app.R.drawable.fm_media_music)
                audioMngHelper.addVoice100()
            } else {
                it.imgIcon.setImageResource(com.sprint.lock.app.R.drawable.fm_media_music)
                audioMngHelper.subVoice100()
            }
            if (a <= 0) {
                it.tvTxt.text = "0%"
            } else if (a >= 100) {
                it.tvTxt.text = "100%"
            } else {
                it.tvTxt.text = "$a%"
            }

            volumeBinding!!.root.removeCallbacks(volumeRunnable)
            volumeBinding!!.root.postDelayed(volumeRunnable, 2000)

        }
    }


    private val volumeRunnable: Runnable = Runnable {
        try {
            requireActivity().windowManager.removeViewImmediate(volumeBinding!!.root)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        volumeBinding = null
    }


    /*
  * 初始化Fragment栈管理
 */
    private fun initFragment() {
        (requireActivity() as MainActivity).showFragment("network")
    }

    private fun initFragmentSd() {
        (requireActivity() as MainActivity).showFragment("SDCARD")
    }

    private fun initFragmentSystem() {
        (requireActivity() as MainActivity).showFragment("SYSTEM_SETTING")
    }

}