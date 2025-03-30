package com.sprint.lock.app.fragment

import android.Manifest
import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.PermissionUtils
import com.springs.common.application.BaseApplication
import com.springs.common.base.BaseFragment
import com.springs.common.common.LiveDataBusX
import com.springs.common.dialog.PermissionDialog
import com.springs.common.ext.showTopToast
import com.sprint.lock.app.MainActivity
import com.sprint.lock.app.R
import com.sprint.lock.app.adapter.AdapterWifi
import com.sprint.lock.app.bean.WifiBean
import com.sprint.lock.app.databinding.FragmentNetworkBinding
import com.sprint.lock.app.dialog.WifiPwDialog
import com.sprint.lock.app.widge.WIFIUtils
import com.thanosfisherman.wifiutils.WifiUtils
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener


class NetWorkFragment : BaseFragment<FragmentNetworkBinding>() {

    private val wifiAdapter = AdapterWifi()
    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentNetworkBinding = FragmentNetworkBinding.inflate(layoutInflater)

    override fun init(savedInstanceState: Bundle?, view: View) {
        binding.tvLeftTitle.text = "网络设置"
        binding.ivLeft.setImageDrawable(resources.getDrawable(R.mipmap.top_img_back_white))
        binding.ivLeft.setOnClickListener {
            (requireActivity() as MainActivity).showFragment("setting")
        }
        binding.rvWifi.adapter = wifiAdapter
        binding.rvWifi.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvWifi.setHasFixedSize(true)
    }



    override fun initListener() {
        super.initListener()
        binding.mySwitch.isChecked = NetworkUtils.isConnected()
        LiveDataBusX.getInstance().with<Boolean>("NETWORK_STATUS").observe(this) {
            binding.mySwitch.isChecked = it
        }


        binding.mySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // 开启状态
                NetworkUtils.setWifiEnabled(true)
            } else {
                NetworkUtils.setWifiEnabled(false)

            }
        }
        wifiAdapter.setOnItemClickListener { _, _, position ->
            wifiAdapter.getItem(position).let { wifiBean ->
                if (wifiBean?.connectType == 0) {
                    if (wifiBean.needPw) {
                        val wifiPwDialog = WifiPwDialog(requireContext(), wifiBean.name) { pw ->
                            if (pw.isNotEmpty()) {
                                wifiBean.connectType = 1
                                wifiAdapter.notifyItemChanged(position)
                                WifiUtils.withContext(BaseApplication.mContext)
                                    .connectWith(wifiBean.name, wifiBean.bssid, pw)
                                    .onConnectionResult(object : ConnectionSuccessListener {
                                        override fun success() {
                                            showTopToast("连接成功${wifiBean.name}")
                                            if (!isAdded) return
                                            getWifiList()
                                        }

                                        override fun failed(errorCode: ConnectionErrorCode) {
                                            showTopToast("连接失败${wifiBean.name}错误码${errorCode.name}")
                                            if (!isAdded) return
                                            wifiBean.connectType = 0
                                            wifiAdapter.notifyItemChanged(position)
                                        }
                                    }).start()
                            }
                        }
                        wifiPwDialog.show()
                    } else {

                        wifiBean.connectType = 1
                        wifiAdapter.notifyItemChanged(position)

                        WifiUtils.withContext(BaseApplication.mContext)
                            .connectWith(wifiBean.name, wifiBean.bssid)
                            .onConnectionResult(object : ConnectionSuccessListener {
                                override fun success() {
                                    showTopToast("连接成功${wifiBean.name}")
                                    if (!isAdded) return
                                    getWifiList()
                                }

                                override fun failed(errorCode: ConnectionErrorCode) {
                                    showTopToast("连接失败${wifiBean.name}错误码${errorCode.name}")

                                    if (!isAdded) return
                                    wifiBean.connectType = 0
                                    wifiAdapter.notifyItemChanged(position)
                                }
                            }).start()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requestStoragePermission()
    }

    fun requestStoragePermission() {
        //权限申请
        PermissionUtils.permission(
            Manifest.permission.ACCESS_FINE_LOCATION
        ).callback(object : PermissionUtils.SimpleCallback {
            override fun onGranted() {
                try {
                    if (NetworkUtils.getWifiEnabled()) getWifiList()
                } catch (ignored: Exception) {
                    ignored.printStackTrace()
                }
            }

            override fun onDenied() {
                PermissionDialog(requireActivity()).show()
                //可以在此函数中调用 AlertDialog 提示用户相应操作等
            }

        }).request()
    }

    private fun getWifiList() {
        if (wifiAdapter.data.isEmpty()) {
            binding.tvWifiState.visibility = View.VISIBLE
            binding.tvWifiState.text = "搜索中..."
            binding.llNetWork.visibility = View.INVISIBLE
        }
        //获得wifi列表
        WifiUtils.withContext(requireActivity()).scanWifi {
            val listNet = filterScanResult(it)
            val alsoConnect = listNet.filter { it.connectType == 2 }
            if (alsoConnect.isNotEmpty()) {
                binding.tvWifiState.visibility = View.GONE
                binding.llNetWork.visibility = View.VISIBLE
                //连接中
                if (alsoConnect[0].connectType == 1) {
                    binding.tvConnecting.text = "连接中..."
                } else if (alsoConnect[0].connectType == 2) {
                    binding.tvConnecting.visibility = View.INVISIBLE
                    binding.tvConnecting.text = "已连接"
                }
                binding.tvName.text = alsoConnect[0].name
            }
            wifiAdapter.setNewData(listNet)
        }.start()

    }

    //转化过滤一下原始数据
    private fun filterScanResult(results: List<ScanResult>?): List<WifiBean> {
        var wifiList = mutableListOf<WifiBean>()

        if (results != null) {
            val wm =
                requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val connectWifi = wm.connectionInfo
            var wifiBean: WifiBean
            for (scanResult in results) {
                if (!TextUtils.isEmpty(scanResult.SSID)) {
                    var connectType = 0
                    if (connectWifi != null && connectWifi.bssid.equals(scanResult.BSSID)) connectType =
                        2
                    wifiBean = WifiBean(
                        scanResult.SSID,
                        scanResult.BSSID,
                        connectType,
                        WIFIUtils.getWifiSecurityInt(scanResult.capabilities) != WIFIUtils.SECURITY_TYPE_OPEN,
                        WIFIUtils.calculateSignalLevel(scanResult.level)
                    )
                    wifiList.add(wifiBean)
                }
            }
        }
        wifiList =
            wifiList.sortedWith(compareByDescending<WifiBean> { it.connectType }.thenByDescending { it.wifiLevel })
                .toMutableList()

        return wifiList
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}