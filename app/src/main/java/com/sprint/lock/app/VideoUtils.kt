package com.sprint.lock.app

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.Surface
import android.view.TextureView
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import java.io.File
import java.io.IOException


// 签署时候，录制视频的工具，包含录制和播放
class VideoUtils {

    companion object {

        private var instance: VideoUtils? = null
        private var outputFile: File? = null
        private var mCamera: Camera? = null

        fun getInstance(): VideoUtils {
            if (instance == null) {
                instance = VideoUtils()
            }
            return instance!!
        }
    }

    // 是否正在录制
    var isRecording: Boolean = false

    // 是否开启了录制，初始化并开启录制，之后是暂停
    var started: Boolean = false
    private var mMediaRecorder: MediaRecorder? = null


    private fun configMediaRecorder(
        context: Context, textureView: TextureView, camera: Camera, cameraId: Int
    ) {/*  val videoFile = File(PathUtils.getExternalDownloadsPath(), "record.mp4")
          if (videoFile.exists()) {
              videoFile.delete()
          }*/
        // 设置输出文件路径
        // 将视频文件插入到系统媒体库
        mMediaRecorder = MediaRecorder()
        mCamera = camera
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
        val videoUri = context.contentResolver.insert(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues
        )
        val parcelFileDescriptor =
            videoUri?.let { context.contentResolver.openFileDescriptor(it, "w") }
        val fileDescriptor = parcelFileDescriptor?.fileDescriptor/*camera = Camera.open(0)
          camera?.stopPreview()
           camera?.unlock()*/
        mCamera?.stopPreview()
        mCamera?.unlock()
        val profile = if (cameraId == 0) CamcorderProfile.get(
            cameraId, CamcorderProfile.QUALITY_480P
        ) else CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_1080P)
        mMediaRecorder?.setCamera(mCamera)
        mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)//设置音频输入源  也可以使用 MediaRecorder.AudioSource.MIC
        mMediaRecorder?.setVideoSource(MediaRecorder.VideoSource.CAMERA)//设置视频输入源

        mMediaRecorder?.setOutputFormat(profile.fileFormat)//音频输出格式
        mMediaRecorder?.setAudioEncoder(profile.audioCodec)//设置音频的编码格式
        mMediaRecorder?.setVideoEncoder(profile.videoCodec)//设置图像编码格式

        mMediaRecorder?.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight)
        mMediaRecorder?.setVideoFrameRate(profile.videoFrameRate)
        mMediaRecorder?.setVideoEncodingBitRate(profile.videoBitRate)
        mMediaRecorder?.setAudioEncodingBitRate(profile.audioBitRate)

        mMediaRecorder?.setAudioSamplingRate(profile.audioSampleRate)
        mMediaRecorder?.setAudioChannels(profile.audioChannels)

//        mMediaRecorder?.setVideoFrameRate(20)//要录制的视频帧率 帧率越高视频越流畅 如果设置设备不支持的帧率会报错  按照注释说设备会支持自动帧率所以一般情况下不需要设置
//        mMediaRecorder?.setVideoSize(1080,780)//设置录制视频的分辨率  如果设置设备不支持的分辨率会报错
//        mMediaRecorder?.setVideoEncodingBitRate(5 * 100 * 100) //设置比特率,比特率是每一帧所含的字节流数量,比特率越大每帧字节越大,画面就越清晰,算法一般是 5 * 选择分辨率宽 * 选择分辨率高,一般可以调整5-10,比特率过大也会报错
//        mMediaRecorder?.setOrientationHint(360)//设置视频的摄像头角度 只会改变录制的视频文件的角度(对预览图像角度没有效果)

        val surface = Surface(textureView.surfaceTexture)
        mMediaRecorder?.setOutputFile(fileDescriptor)//MP4文件保存路径
        mMediaRecorder?.setPreviewDisplay(surface)//设置拍摄预览

    }

    fun startRecorder(context: Context, textureView: TextureView, mCamera: Camera, cameraId: Int) {
        try {
            //权限申请
            PermissionUtils.permission(
                Manifest.permission.RECORD_AUDIO,
            ).callback(object : PermissionUtils.SimpleCallback {
                override fun onGranted() {
                    try {
                        configMediaRecorder(
                            context, textureView, mCamera, cameraId
                        ) //配置MediaRecorder  因为每一次停止录制后调用重置方法后都会取消配置,所以每一次开始录制都需要重新配置一次
                        mMediaRecorder?.prepare() //准备
                        mMediaRecorder?.start() //开启
                        isRecording = true
                        started = true
                    } catch (ignored: Exception) {
                        ignored.printStackTrace()
                    }
                }

                override fun onDenied() {
                    ToastUtils.showShort("请开启录像权限")
                }

            }).request()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopRecorder() {
        // 停止录制
        try {
            mMediaRecorder?.reset() //重置 重置后将进入空闲状态,再次启动录制需要重新配置MediaRecorder
            isRecording = false
            started = false
        } catch (e: IllegalStateException) {
            e.printStackTrace() // 处理状态异常
        }finally {
            mMediaRecorder?.release()
        }
    }

    fun pauseRecorder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mMediaRecorder!!.pause() //暂停
            isRecording = false
        }
    }

    fun resumeRecorder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mMediaRecorder!!.resume() //恢复
            isRecording = true
        }
    }

    fun destroy() {
        if (mMediaRecorder != null) {
            mMediaRecorder!!.stop()
            mMediaRecorder!!.release() //释放 释放之前需要先调用stop()
            mMediaRecorder = null
        }
        instance = null
    }

}
