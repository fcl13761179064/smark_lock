package com.sprint.lock.app.fragment

import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.SDCardUtils
import com.springs.common.base.BaseFragment
import com.sprint.lock.app.MainActivity
import com.sprint.lock.app.R
import com.sprint.lock.app.databinding.FragmentSdBinding


class SdFragment : BaseFragment<FragmentSdBinding>() {


    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentSdBinding = FragmentSdBinding.inflate(layoutInflater)

    override fun init(savedInstanceState: Bundle?, view: View) {
        binding.tvLeftTitle.text = "存储空间"
        binding.ivLeft.setImageDrawable(resources.getDrawable(R.mipmap.top_img_back_white))
        binding.rgOne.setBackgroundResource(R.drawable.fm_ff8157ff_ffe056ff_radis_15)
        binding.ivLeft.setOnClickListener {
            (requireActivity() as MainActivity).showFragment("setting")
        }
    }

    override fun onResume() {
        super.onResume()
        // 获取当前总共的内存大小（字节）
        val internalSpace =getInternalStorageSpace()
        if (internalSpace != null &&internalSpace.isNotEmpty()) {
            binding.empty.visibility =View.GONE
            val internalTotalSpace = internalSpace[0]  /1024/1024/1024  // 内部存储总空间
            val internalAvailableSpace = internalSpace[1]  /1024/1024/1024 // 内部存储可用空间
            binding.circularProgress.setProgress(internalAvailableSpace.toDouble(),internalTotalSpace.toDouble())
            binding.sdDesc.text ="已用"+internalAvailableSpace.toInt() +"GB / ${internalTotalSpace.toInt()} GB"
        }else{
            binding.empty.visibility =View.VISIBLE
            binding.circularProgress.visibility=View.GONE
            binding.sdDesc.text ="请插入Micro SD 卡"
        }
    }



    override fun initListener() {
        binding.rgMenu.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.rg_one ->{
                    binding.rgOne.setBackgroundResource(R.drawable.fm_ff8157ff_ffe056ff_radis_15)
                    binding.rgTwo.setBackgroundResource(0)

                }
                R.id.rg_two ->{
                    binding.rgOne.setBackgroundResource(0)
                    binding.rgTwo.setBackgroundResource(R.drawable.fm_ff8157ff_ffe056ff_radis_15)
                }
            }
        }

        binding.rgOne.setOnClickListener {
            // 获取当前总共的内存大小（字节）
            val internalSpace =getInternalStorageSpace()
            if (internalSpace != null &&internalSpace.isNotEmpty()) {
                binding.empty.visibility =View.GONE
                val internalTotalSpace = internalSpace[0]  /1024/1024/1024  // 内部存储总空间
                val internalAvailableSpace = internalSpace[1]  /1024/1024/1024 // 内部存储可用空间
                binding.circularProgress.setProgress(internalAvailableSpace.toDouble(),internalTotalSpace.toDouble())
                binding.sdDesc.text ="已用"+internalAvailableSpace.toInt() +"GB / ${internalTotalSpace.toInt()} GB"
            }else{
                binding.empty.visibility =View.VISIBLE
                binding.circularProgress.visibility=View.GONE
                binding.sdDesc.text ="请插入Micro SD 卡"
            }

        }
        binding.rgTwo.setOnClickListener {
            if (SDCardUtils.isSDCardEnableByEnvironment()){
                binding.empty.visibility =View.GONE
                binding.circularProgress.visibility=View.VISIBLE
                val  totalSIztSize= SDCardUtils.getInternalTotalSize() /1024/1024/1024
                val  availableSize= SDCardUtils.getInternalAvailableSize()/1024/1024/1024
                binding.circularProgress.setProgress(availableSize.toDouble(),totalSIztSize.toDouble())
                binding.sdDesc.text ="已用"+availableSize.toInt() +"GB / ${totalSIztSize.toInt()} GB"
            }else {
                binding.empty.visibility =View.VISIBLE
                binding.circularProgress.visibility=View.GONE
                binding.sdDesc.text ="请插入Micro SD 卡"
            }

        }

    }

    fun getInternalStorageSpace(): LongArray? {
        val path = Environment.getDataDirectory()
        val statFs = StatFs(path.path)
        val blockSize = statFs.blockSizeLong
        val totalBlocks = statFs.blockCountLong
        val availableBlocks = statFs.availableBlocksLong
        return longArrayOf(blockSize * totalBlocks, blockSize * availableBlocks)
    }
    override fun onDestroy() {
        super.onDestroy()

    }
}