package com.sprint.lock.app


import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.android.library.ISerialDataListener
import com.android.library.SerialManager
import com.blankj.utilcode.util.FragmentUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.kelin.photoselector.ui.AlbumFragment
import com.room.database.bean.Door
import com.room.database.db.DoorUtils
import com.springs.common.application.BaseApplication
import com.springs.common.base.BaseActivity
import com.springs.common.common.LiveDataBusX
import com.springs.common.common.px2dp
import com.springs.common.ext.Logutils
import com.springs.common.widgets.AppData
import com.sprint.lock.app.databinding.ActivityMainHomeBinding
import com.sprint.lock.app.fragment.CameraXPreviewFragment
import com.sprint.lock.app.fragment.LeaveMessageFragment
import com.sprint.lock.app.fragment.NetWorkFragment
import com.sprint.lock.app.fragment.SdFragment
import com.sprint.lock.app.fragment.SettingFragment
import com.sprint.lock.app.fragment.SystemInfoFragment
import com.sprint.lock.app.widge.WifiReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date


class MainActivity : BaseActivity<ActivityMainHomeBinding>() {

    private val mListFragment: MutableList<Fragment> = mutableListOf()

    //首页
    private val firstPagerFragment by lazy { CameraXPreviewFragment() }

    //相册
    private val albumFragment by lazy { AlbumFragment() }

    //留言
    private val leavemessagefragment by lazy { LeaveMessageFragment() }

    //设置
    private val settingFragment by lazy { SettingFragment() }
    private val netWorkFragment by lazy { NetWorkFragment() }
    private val sdFragment by lazy { SdFragment() }
    private val systemFragment by lazy { SystemInfoFragment() }
    var networkChangeReceiver: WifiReceiver? = null
    var serialIsOpen = false
    private var TAG = "MainActivity"

    private val DEFAULT_WIDTH = 1080
    private val DEFAULT_HEIGHT = 960

    override fun getViewBinding(): ActivityMainHomeBinding =
        ActivityMainHomeBinding.inflate(layoutInflater)

    override fun init(savedInstanceState: Bundle?) {
        // 隐藏导航栏
        val decorView: View = window.decorView
        val uiOptions: Int =
            (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
        initFragment()
        changeFragment(firstPagerFragment)
        openSerial()
        AppUtils.recordFirstLaunchTime(this)
    }

    /**
     * 打开串口
     */
    private fun openSerial() {
        SerialManager.getInstance().openSerialPort(
            "/dev/ttyS1", 115200
        ) { success, msg ->
            serialIsOpen = success
            if (success) {
                Log.i(TAG, "open serial success ")
                SerialManager.getInstance().registerListener(mListener)
            } else {
                Log.e(TAG, "open serial failed $msg")
            }
        }
    }

    /**
     * 获取串口数据
     */
    var nowTime = 0L
    private val mListener = ISerialDataListener { data ->
        GlobalScope.launch(Dispatchers.IO) {
            val ssss = "$data".trimIndent()
            setScreenTime()
            try {
                //上报门铃
                if (ssss == "AA5503030300000000") {

                } else {
                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                    val date = Date()
                    // 获取当前日期和时间
                    val currentDateTime: String = sdf.format(date)
                    // 获取当前时间
                    sdf.applyPattern("HH:mm:ss")
                    val currentTime = sdf.format(date)
                    if (ssss.contains("AA5500A00004000") || ssss.contains("AA5500A200010000A2")) {
                        if (date.time - nowTime <= 3000) {
                            return@launch
                        }
                        Log.e("fdafdsafdsafdsa", "$date.time  -$nowTime")
                        binding.redInfo.visibility=View.VISIBLE
                        nowTime = date.time
                        val doorData = Door(data = currentDateTime, time = currentTime, isType = "门锁已开")
                        DoorUtils.getInstance(BaseApplication.mApplication).insertAllData(doorData)
                        runOnUiThread {
                            LiveDataBusX.getInstance().with<Boolean>(AppData.TO_UPDATA_DOOR).value =
                                true
                            return@runOnUiThread
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun setScreenTime() {
        LogUtils.d("11111", "111111111")
        // 唤醒屏幕（需 WAKE_LOCK 权限）
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyApp:WakeLockTag"
        )
        wakeLock.acquire(10 * 60 * 1000L) // 保持10分钟
        wakeLock.release()
        // 解锁屏幕（需设备管理员权限）
        val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        val keyguardLock = keyguardManager.newKeyguardLock("MyApp:KeyguardLock")
        keyguardLock.disableKeyguard()
    }


    //开启大屏
    fun playerFull() {
        binding.flCheck.layoutParams =
            (binding.flCheck.layoutParams as ConstraintLayout.LayoutParams).apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
                topMargin = 0
                leftMargin = 0
                bottomToBottom = 0
                rightMargin = 0
                leftToLeft = R.id.main
                rightToRight = R.id.main
                topToTop = R.id.main
                bottomToBottom = R.id.main
            }
    }


    //开启小屏幕
    fun playerMin() {
        binding.flCheck.layoutParams =
            (binding.flCheck.layoutParams as ConstraintLayout.LayoutParams).apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
                width = px2dp(0f)
                height = px2dp(0f)
                leftMargin = px2dp(70f)
                topMargin = px2dp(30f)
                bottomToBottom = R.id.main
                rightToRight = R.id.main
            }
    }

    override fun onStart() {
        super.onStart()
        networkChangeReceiver = WifiReceiver {
            runOnUiThread {
                LiveDataBusX.getInstance().with<Boolean>("NETWORK_STATUS").postValue(it)
                if (it) binding.tvWifiImg.setImageResource(R.mipmap.ic_wifi_3) else binding.tvWifiImg.setImageResource(
                    R.mipmap.wifi_no_network
                )
            }
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)
    }

    override fun initListener() {
        super.initListener()
        binding.buttonOne.setOnClickListener {
            restoreChecked()
            binding.buttonOne.isChecked = true
            changeFragment(firstPagerFragment)
        }
        binding.buttonTwo.setOnClickListener {
            binding.redInfo.visibility=View.GONE
            restoreChecked()
            binding.buttonTwo.isChecked = true
            initPhoto()
        }
        binding.buttonThree.setOnClickListener {
            SPUtils.getInstance().put(AppData.CIRCLE_SHOW, false)
            binding.redRound.visibility = View.GONE
            restoreChecked()
            binding.buttonThree.isChecked = true
            changeFragment(leavemessagefragment)
        }
        binding.buttonFour.setOnClickListener {
            restoreChecked()
            binding.buttonFour.isChecked = true
            changeFragment(settingFragment)
        }

        LiveDataBusX.getInstance().with<Boolean>("CIRCLE_SHOW").observe(this) {
            if (it) {
                binding.redRound.visibility = View.VISIBLE
            } else {
                binding.redRound.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val isShow = SPUtils.getInstance().getBoolean(AppData.CIRCLE_SHOW, false)
        if (isShow) {
            binding.redRound.visibility = View.VISIBLE
        } else {
            binding.redRound.visibility = View.GONE
        }
    }

    fun showFragment(s: String) {
        if (s == "network") {
            if (!mListFragment.contains(netWorkFragment)) {
                mListFragment.add(netWorkFragment)
                FragmentUtils.add(
                    supportFragmentManager, mListFragment, R.id.fl_check, 5
                )
            }
            netWorkFragment.onResume()
            changeFragment(netWorkFragment)
        } else if (s == "setting") {
            changeFragment(settingFragment)
        } else if (s == "SDCARD") {
            if (!mListFragment.contains(sdFragment)) {
                mListFragment.add(sdFragment)
                FragmentUtils.add(
                    supportFragmentManager, mListFragment, R.id.fl_check, 6
                )
            }
            sdFragment.onResume()
            changeFragment(sdFragment)
        } else if (s == "SYSTEM_SETTING") {
            if (!mListFragment.contains(systemFragment)) {
                mListFragment.add(systemFragment)
                FragmentUtils.add(
                    supportFragmentManager, mListFragment, R.id.fl_check, 6
                )
            }
            systemFragment.onResume()
            changeFragment(systemFragment)
        } else if (s == "MESSAGE") {
            initPhoto()
        }
    }


    /*
     *  切换Tab，切换对应的Fragment
    */
    fun changeFragment(fragment: Fragment, isshow: Boolean = false) {
        FragmentUtils.showHide(
            fragment, mListFragment
        )
    }

    fun initPhoto() {
        if (!mListFragment.contains(albumFragment)) {
            mListFragment.add(albumFragment)
            FragmentUtils.add(
                supportFragmentManager, mListFragment, R.id.fl_check, 3
            )
        }
        restoreChecked()
        binding.buttonTwo.isChecked = true
        changeFragment(albumFragment)
    }

    fun initFragment() {
        mListFragment.add(firstPagerFragment)
        mListFragment.add(leavemessagefragment)
        mListFragment.add(settingFragment)
        FragmentUtils.add(supportFragmentManager, mListFragment, R.id.fl_check, 0)
    }

    fun restoreChecked() {
        binding.buttonOne.isChecked = false
        binding.buttonTwo.isChecked = false
        binding.buttonThree.isChecked = false
        binding.buttonFour.isChecked = false

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkChangeReceiver);
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return true
    }
}