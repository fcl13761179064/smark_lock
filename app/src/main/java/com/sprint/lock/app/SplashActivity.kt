package com.sprint.lock.app


import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.blankj.utilcode.util.PermissionUtils
import com.springs.common.base.BaseActivity
import com.springs.common.dialog.PermissionDialog
import com.sprint.lock.app.databinding.ActivityEntranceBinding


/**
 * 启动页面
 */
class SplashActivity : BaseActivity<ActivityEntranceBinding>(){
    override fun getViewBinding(): ActivityEntranceBinding  =ActivityEntranceBinding.inflate(layoutInflater)

    override fun init(savedInstanceState: Bundle?) {
        requestVideoPermission()
    }

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == 4) {
                startActivity(MainActivity::class)
                finish()
            }
        }
    }


    private fun requestVideoPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 及以上版本，请求 READ_MEDIA_IMAGES 权限
            //权限申请
            PermissionUtils.permission(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,Manifest.permission.ACCESS_FINE_LOCATION,
                 Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_MEDIA_IMAGES,  Manifest.permission.READ_MEDIA_VIDEO
            ).callback(object : PermissionUtils.SimpleCallback {
                override fun onGranted() {
                    try {
                        mHandler.sendEmptyMessageDelayed(4,500)
                    } catch (ignored: Exception) {
                        ignored.printStackTrace()
                    }
                }

                override fun onDenied() {
                    PermissionDialog(this@SplashActivity, "请打开相机权限").show()
                }

            }).request()
        } else {
            // Android 13 以下版本，请求 READ_EXTERNAL_STORAGE 权限
            //权限申请
            PermissionUtils.permission(
                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_COARSE_LOCATION
            ).callback(object : PermissionUtils.SimpleCallback {
                override fun onGranted() {
                    try {
                        mHandler.sendEmptyMessageDelayed(4,500)
                    } catch (ignored: Exception) {
                        ignored.printStackTrace()
                    }
                }

                override fun onDenied() {
                    PermissionDialog(this@SplashActivity, "请打开相机权限").show()
                }

            }).request()
        }

    }

}