package com.sprint.lock.app.fragment

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.NetworkUtils
import com.springs.common.base.BaseFragment
import com.springs.common.ext.showTopToast
import com.sprint.lock.app.MainActivity
import com.sprint.lock.app.R
import com.sprint.lock.app.databinding.ActivitySettingBinding
import com.sprint.lock.app.dialog.AlsoUpdateDialog
import com.sprint.lock.app.widge.CacheUtils
import com.sprint.lock.app.widge.RebootUtils


/**
 * @ClassName:  UserInfoActivity
 * @Description:设置页面
 * @Author: vi1zen
 * @CreateDate: 2020/10/12 15:36
 */
class SystemInfoFragment : BaseFragment<ActivitySettingBinding>() {


    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): ActivitySettingBinding = ActivitySettingBinding.inflate(layoutInflater)

    override fun init(savedInstanceState: Bundle?, view: View) {
        binding.tvLeftTitle.text = "系统版本"
        binding.ivLeft.setImageDrawable(resources.getDrawable(R.mipmap.top_img_back_white))
        binding.ivLeft.setOnClickListener {
            (requireActivity() as MainActivity).showFragment("setting")
        }
        binding.gujianCode.text = DeviceUtils.getSDKVersionName()
        binding.snCode.text = DeviceUtils.getSDKVersionCode().toString()
        binding.ipAddress.text = NetworkUtils.getIPAddress(true)
        binding.wenjian.setOnClickListener {
            openFileManager(requireActivity())
        }
        binding.checkUp.setOnClickListener {
            AlsoUpdateDialog(requireActivity()) {}.show()
        }
        binding.restarSystem.setOnClickListener {
            RebootUtils.rebootSystem(requireContext())
        }

        binding.restarSystem.setOnClickListener {
            RebootUtils.rebootSystem(requireContext())
        }
        val size = CacheUtils.getTotalCacheSize(requireContext())
        if (size.isNotEmpty()) {
            binding.cacheSize.text = size.toString()
        }
        binding.clearCache.setOnClickListener {
            if (size.isNotEmpty() && size.toInt() > 0) {
               CacheUtils.clearAllCache(requireContext())
                showTopToast("清除成功")
            }else {
                showTopToast("没有缓存")
            }
        }

        binding.language.setOnClickListener {
          //  PermissionDialog(requireContext(), "语言设置").show()
        }


    }
}

fun openFileManager(activity: Activity) {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.type = "*/*"
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    try {
        activity.startActivityForResult(
            Intent.createChooser(intent, "Select a File"), FILE_SELECT_CODE
        )
    } catch (e: ActivityNotFoundException) {
        // If no file manager is found, display error message
        Toast.makeText(activity, "No file manager installed.", Toast.LENGTH_SHORT).show()
    }
}

val FILE_SELECT_CODE = 0

