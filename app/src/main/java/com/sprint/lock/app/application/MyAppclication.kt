package com.sprint.lock.app.application

import android.os.Build
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import com.kelin.photoselector.PhotoSelector
import com.qweather.sdk.view.HeConfig
import com.springs.common.application.BaseApplication


/**
 * Description application
 */
class MyAppclication : BaseApplication(), ImageLoaderFactory {


    override fun onCreate() {
        super.onCreate()
        HeConfig.init("KBB6BDFQ89", "eaec021195d44880ad2e02f59493ff15") //这一行引号里的内容要修改成你的。
        HeConfig.switchToDevService()
        PhotoSelector.init(this,"${packageName}.fileProvider", true, albumTakePictureEnable = false)
    }


    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                //添加GIF支持
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
                add(VideoFrameDecoder.Factory())
            }
            .build()
    }
}