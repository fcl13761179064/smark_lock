package com.sprint.lock.app.fragment

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import cn.onlyloveyd.slidetoggleview.SlideToggleView
import com.android.library.SerialManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.google.gson.Gson
import com.qweather.sdk.bean.base.Code
import com.qweather.sdk.bean.weather.WeatherDailyBean
import com.qweather.sdk.bean.weather.WeatherNowBean
import com.qweather.sdk.view.QWeather
import com.springs.common.base.BaseFragment
import com.springs.common.common.LiveDataBusX
import com.springs.common.common.px2dp
import com.springs.common.dialog.PermissionDialog
import com.springs.common.ext.showTopToast
import com.springs.common.widgets.AppData
import com.springs.common.widgets.FastClickUtils
import com.springs.common.widgets.ScheduleTimeUtils
import com.sprint.lock.app.CalendarSelectActivity
import com.sprint.lock.app.JumpToOtherAppActivity
import com.sprint.lock.app.MainActivity
import com.sprint.lock.app.R
import com.sprint.lock.app.VideoUtils
import com.sprint.lock.app.WearThersActivity
import com.sprint.lock.app.databinding.FragmentFirstPagerBinding
import com.sprint.lock.app.widge.CityUtils
import com.sprint.lock.app.widge.wheather.City
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.wsj.lib.utils.IconUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.ExecutorService
import kotlin.math.pow


typealias LumaListener = (luma: Double) -> Unit

class CameraXPreviewFragment : BaseFragment<FragmentFirstPagerBinding>() {
    companion object {
        val TAG = "CameraPreviewActivity"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    private var nearestCity: City? = null
    private val mCalendar: Calendar = Calendar.getInstance()
    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentFirstPagerBinding = FragmentFirstPagerBinding.inflate(layoutInflater)

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var isFullVideo: Boolean = false
    private var wheather: WeatherNowBean? = null
    private var city: City? = null
    private var mCamera: Camera? = null
    private var cameraId: Int = 0
    private var handler = Handler(Looper.getMainLooper())
    private var isTakingPicture = false

    private val UPDATE_INTERVAL = 60 * 60 * 1000L // 1小时（毫秒）)

    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else requireActivity().filesDir
    }

    fun scaleAnimatiion(scaleX: Float) {
        val scaleXAnim = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.plus(scaleX))
        val scaleYAnim = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.plus(scaleX))
        val animator = ObjectAnimator.ofPropertyValuesHolder(
            binding.llAnimator, scaleXAnim, scaleYAnim
        )
        animator.duration = 300
        if (!animator.isRunning) {
            animator.start()
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                animator.cancel()
            }
        })

    }


    override fun init(savedInstanceState: Bundle?, view: View) {
        val windowInsetsController = WindowCompat.getInsetsController(
            requireActivity().window, requireActivity().window.decorView
        )
        // Configure the behavior of the hidden system bars.
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        ViewCompat.setOnApplyWindowInsetsListener(requireActivity().findViewById(R.id.main)) { view, windowInsets ->
            // You can hide the caption bar even when the other system bars are visible.
            // To account for this, explicitly check the visibility of navigationBars()
            // and statusBars() rather than checking the visibility of systemBars().
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            ViewCompat.onApplyWindowInsets(view, windowInsets)
        }



        binding.slideToggleView.setSlideToggleListener(object :
            SlideToggleView.SlideToggleListener {
            override fun onBlockPositionChanged(
                view: SlideToggleView?, left: Int, total: Int, slide: Int
            ) {
                if (slide.toFloat() / total.toFloat() < 0.3) {
                    scaleAnimatiion(slide.toFloat() / total.toFloat())
                } else {
                    scaleAnimatiion(0.3f)
                }
            }

            override fun onSlideOpen(view: SlideToggleView?) {
                LogUtils.d("fdsfdsafd", "2345242432432234")
                val time = Timer()
                time.schedule(object : TimerTask() {
                    override fun run() {
                        requireActivity().runOnUiThread {
                            send()
                            time.cancel()
                            binding.slideToggleView.closeToggle()
                        }
                    }
                }, 600)
            }

        })

        outputDirectory = getOutputDirectory()
        Log.d(TAG, "outputDirectory=" + outputDirectory.absolutePath)
        cameraExecutor = java.util.concurrent.Executors.newSingleThreadExecutor()

    }

    override fun onStart() {
        super.onStart()
        startCamera()
    }

    fun send() {
        if ((requireActivity() as MainActivity).serialIsOpen) {
            SerialManager.getInstance().write("AA5500B0000000AF")
        } else {
            //Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestVideoPermission(surface: SurfaceTexture) {
        //权限申请
        PermissionUtils.permission(
            Manifest.permission.CAMERA
        ).callback(object : PermissionUtils.SimpleCallback {
            override fun onGranted() {
                try {
                    startTextUreView(surface)
                } catch (ignored: Exception) {
                    ignored.printStackTrace()
                }
            }

            override fun onDenied() {
                PermissionDialog(requireContext(), "请开启摄像头").show()
            }

        }).request()
    }

    private var weatherUpdateJob: Job? = null
    override fun onResume() {
        super.onResume()
        weatherUpdateJob = lifecycleScope.launch {
            val locationId = AppData.spUtils.getString(AppData.SP_WHEATHER_ID, "")
            val cityName = AppData.spUtils.getString(AppData.SP_WHEATHER_NAME, "")
            if (locationId.isNullOrEmpty() && cityName.isNullOrEmpty()) {
                val location = location()
                city = nearestCity(location)
                city?.let {
                    // 1小时后再次请求
                    while (isActive) {
                        wheather(it.locationId, it.locationNameZh)
                        delay(UPDATE_INTERVAL)
                    }
                }
            } else {
                // 1小时后再次请求
                while (isActive) {
                    wheather(locationId, cityName)
                    delay(UPDATE_INTERVAL)
                }
            }
        }
    }

    // 停止循环
    fun stopWeatherUpdates() {
        weatherUpdateJob?.cancel()
    }

    fun wheather(locationId: String, cityName: String) {
        QWeather.getWeatherNow(requireActivity(),
            locationId ?: "101020600",
            object : QWeather.OnResultWeatherNowListener, QWeather.OnResultWeatherDailyListener {
                override fun onError(p0: Throwable?) {
                    binding.atWeaterProgress.text = "24"
                    binding.atWeaterText.text = " $cityName"
                }

                override fun onSuccess(weatherBean: WeatherDailyBean?) {

                }

                override fun onSuccess(weatherBean: WeatherNowBean) {
                    //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                    runOnUiThread {
                        if (Code.OK == weatherBean.code) {
                            wheather = weatherBean
                            binding.image.setImageResource(
                                IconUtils.getDayIconDark(
                                    requireContext(), weatherBean.now.icon.toString()
                                )
                            )
                            binding.atWeaterProgress.text = weatherBean.now.temp
                            binding.atWeaterText.text = weatherBean.now.text + " ${cityName}"
                            city?.locationId = locationId!!
                            city?.locationNameZh = cityName
                        } else {
                            //在此查看返回数据失败的原因
                            val code = weatherBean!!.code
                            LogUtils.d("sddsdsd", code)
                        }
                    }
                }
            })


    }

    fun location(): Location? {
        val locationManager =
            requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        val provider = LocationManager.NETWORK_PROVIDER
        if (ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            return locationManager.getLastKnownLocation(provider)
        }
        return null
    }

    fun nearestCity(location: Location? = null): City? {
        // 为空返回上一次结果，可能为空
        if (location == null) {
            nearestCity = City()
            nearestCity?.locationId = "101020900"
            nearestCity?.locationNameZh = "松江"
            nearestCity?.countryRegionZh = "中国"
            nearestCity?.adm1NameZh = "上海市"
            nearestCity?.adm2NameZh = "上海市"
            nearestCity?.latitude = "31.03047"
            nearestCity?.longitude = "121.223541"
            return nearestCity
        }

        return nearestCity ?: CityUtils.cityList().minBy { city ->
            (city.longitude.toDouble() - location.longitude).pow(2) + (city.latitude.toDouble() - location.latitude).pow(
                2
            )
        }.also {
            nearestCity = it
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予

            } else {
                // 权限被拒绝
                PermissionDialog(requireContext(), "请打开定位权限").show()
            }
        }
    }


    private val runnable = Runnable {
        showTopToast("停止录屏")
        VideoUtils.getInstance().stopRecorder()
        binding.recorderVideo.isSelected = false
        binding.currentRecorder.visibility = View.GONE
        scanFile(requireContext(), outputDirectory.absolutePath)
        LiveDataBusX.getInstance().with<Boolean>(AppData.TO_SCAN_PHOTO).value = true
    }

    var flag = true
    override fun initListener() {
        super.initListener()

        binding.imageView1.setOnClickListener {
            val intent = Intent(requireActivity(), JumpToOtherAppActivity::class.java)
            startActivity(intent)
        }

        binding.imageView2.setOnClickListener {
            showTopToast("该功能正在开发中")
        }

        binding.imageView3.setOnClickListener {
            showTopToast("该功能正在开发中")
        }

        try {
            ScheduleTimeUtils.showRealTime {
                if (flag) binding.atPoint.visibility =
                    View.VISIBLE else binding.atPoint.visibility = View.INVISIBLE
                binding.atTime.text = it
                flag = !flag
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val year = mCalendar[Calendar.YEAR]
        val month = mCalendar[Calendar.MONTH] + 1 // 月份是从0开始的，所以需要+1
        val day = mCalendar[Calendar.DAY_OF_MONTH]
        val dayOfWeek = mCalendar[Calendar.DAY_OF_WEEK] // 星期几，星期天为1，星期一为2，依此类推
        binding.atTimeData.text = "${year}-${month}-${day}"
        val weekDay = weekday(dayOfWeek)
        binding.atWeek.text = weekDay

        binding.atTime.setOnClickListener {
            val intent = Intent(requireContext(), CalendarSelectActivity::class.java)
            startActivity(intent)
        }
        binding.ivScaleScreen.setOnClickListener {
            if (!isFullVideo) {
                //全屏
                (requireActivity() as MainActivity).playerFull()
                playerVideoFull()
                binding.imageMinUi.visibility = View.GONE
            } else {
                playerVideoMin()
                (requireActivity() as MainActivity).playerMin()
                binding.imageMinUi.visibility = View.VISIBLE
            }
            isFullVideo = !isFullVideo
        }

        binding.takePhoto.setOnClickListener {
            takePhoto()
        }
        binding.recorderVideo.isSelected = false
        binding.recorderVideo.setOnClickListener {
            if (FastClickUtils.isDoubleClick(2000)) {
                showTopToast("请不要快速点击...")
                return@setOnClickListener
            }
            if (!VideoUtils.getInstance().isRecording) {
                binding.recorderVideo.isSelected = true
                binding.currentRecorder.visibility = View.VISIBLE
                mCamera?.let {
                    VideoUtils.getInstance()
                        .startRecorder(requireContext(), binding.preview, it, cameraId)
                    handler.postDelayed(runnable, 16000)
                }
            } else {
                // 停止任务
                if (VideoUtils.getInstance().isRecording) {
                    handler.removeCallbacks(runnable)
                    showTopToast("停止录屏")
                    VideoUtils.getInstance().stopRecorder()
                    binding.recorderVideo.isSelected = false
                    binding.currentRecorder.visibility = View.GONE
                    scanFile(requireContext(), outputDirectory.absolutePath)
                    LiveDataBusX.getInstance().with<Boolean>(AppData.TO_SCAN_PHOTO).value = true
                }
            }
        }

        binding.csMessage.setOnClickListener {
            (requireActivity() as MainActivity).showFragment("MESSAGE")
        }
        binding.layoutWeather.setOnClickListener {
            val intent = Intent(requireContext(), WearThersActivity::class.java)
            val location = Gson().toJson(city)
            intent.putExtra("location", location)
            val wheather = Gson().toJson(wheather)
            intent.putExtra("wheather", wheather)
            startActivity(intent)
        }
    }

    //开启大屏
    fun playerVideoFull() {
        binding.preview.layoutParams =
            (binding.preview.layoutParams as ConstraintLayout.LayoutParams).apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
                topMargin = 0
                leftMargin = 0
                bottomMargin = 0
                rightMargin = 0
            }
    }

    //开启小屏幕
    fun playerVideoMin() {
        binding.preview.layoutParams =
            (binding.preview.layoutParams as ConstraintLayout.LayoutParams).apply {
                width = 0
                height = 0
                bottomMargin = px2dp(98f)
                rightMargin = px2dp(168f)
                leftMargin = px2dp(4f)
                topMargin = px2dp(4f)
                topToTop = R.id.video
                bottomToBottom = R.id.video
                leftToLeft = R.id.video
                rightToRight = R.id.video
            }
    }


    private fun weekday(dayOfWeek: Int): String {
        when (dayOfWeek) {
            Calendar.SUNDAY -> return "星期日"
            Calendar.MONDAY -> return "星期一"
            Calendar.TUESDAY -> return "星期二"
            Calendar.WEDNESDAY -> return "星期三"
            Calendar.THURSDAY -> return "星期四"
            Calendar.FRIDAY -> return "星期五"
            Calendar.SATURDAY -> return "星期六"
        }
        return "星期日"
    }


    private fun startCamera() {
        binding.preview.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture, width: Int, height: Int
            ) {
                requestVideoPermission(surface)
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture, width: Int, height: Int
            ) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                stopTextTureVIew()
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            }

        }
    }

    private fun takePhoto() {
        if (mCamera == null) {
            return
        }
        takePic()
    }

    private fun takePic() {
        if (mCamera != null) {
            if (VideoUtils.getInstance().isRecording) {
                showTopToast("当前正在录屏...")
            } else {
                if (isTakingPicture) {
                    showTopToast("正在保存拍照图片")
                    return
                }
                isTakingPicture = true
                //调用抓拍摄像头抓拍
                mCamera?.takePicture(null, null, pictureCallback)
            }
        } else {
            Log.e("TAG", "请检查摄像头！")
        }
    }

    private var mBitmap: Bitmap? = null
    private var pictureCallback = Camera.PictureCallback { data, camera ->
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") //设置日期格式
        val picName: String = df.format(Date())
        mBitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
        try {
            MediaStore.Images.Media.insertImage(
                requireActivity().contentResolver, mBitmap, "${System.currentTimeMillis()}", picName
            )
            showTopToast("抓拍成功")
            camera?.let { it.startPreview() }
            scanFile(requireContext(), outputDirectory.absolutePath)
            LiveDataBusX.getInstance().with<Boolean>(AppData.TO_SCAN_PHOTO).value = true
            // 处理 JPEG 数据
            isTakingPicture = false // 拍照完成，重置标志位
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun scanFile(context: Context, filePath: String?) {
        val scanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        scanIntent.data = Uri.fromFile(File(filePath))
        context.sendBroadcast(scanIntent)
    }

    fun startTextUreView(surface: SurfaceTexture) {
        try {
            mCamera = Camera.open(cameraId)
            if (mCamera == null) {
                cameraId = 1
                mCamera = Camera.open(cameraId)
            }
            if (mCamera != null) {
                val params: Camera.Parameters = mCamera!!.getParameters() // 获取摄像头参数
                val rotationAngle = 0 // 设置旋转的角度，例如90度
                mCamera?.setDisplayOrientation(rotationAngle)
                // 应用参数到摄像头
                mCamera?.parameters = params
                mCamera?.setPreviewTexture(surface)
                mCamera?.startPreview()
            }
        } catch (e: IOException) {
            Log.d("TAG", e.message!!)
        }
    }

    fun stopTextTureVIew() {
        if (mCamera != null) {
            mCamera?.stopPreview()
            mCamera?.release()
            mCamera = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopTextTureVIew()
        VideoUtils.getInstance().destroy()
        // 防止内存泄漏
        stopWeatherUpdates()
    }
}