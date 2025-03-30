package com.sprint.lock.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.springs.common.base.BaseFragment
import com.sprint.lock.app.MainActivity
import com.sprint.lock.app.databinding.NoCheckBinding

class NoCheckFragment : BaseFragment<NoCheckBinding>() {


    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NoCheckBinding =NoCheckBinding.inflate(layoutInflater)

    override fun init(savedInstanceState: Bundle?, view: View) {

    }


    /*
  * 初始化Fragment栈管理
 */
    private fun initFragment() {
        (requireActivity() as MainActivity).changeFragment(NoCheckFragment())
    }


}