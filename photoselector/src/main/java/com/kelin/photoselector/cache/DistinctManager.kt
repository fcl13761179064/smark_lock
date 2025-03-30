package com.kelin.photoselector.cache

import android.annotation.SuppressLint
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.SparseArray
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import com.kelin.photoselector.model.AlbumType
import com.kelin.photoselector.model.Picture
import com.kelin.photoselector.ui.AlbumFragment
import com.springs.common.ext.showTopToast


/**
 * **描述:** 从相册选择图片或视频是的去重管理。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/7/22 9:48 AM
 *
 * **版本:** v 1.0.0
 */
internal class DistinctManager private constructor() : CacheOwner<List<Picture>> {

    companion object {
        internal val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { DistinctManager() }
    }

    private val pool by lazy { SparseArray<Cache<List<Picture>>>() }

    internal fun tryNewCache(id: Int): LifecycleObserver {
        var cache = pool[id]
        if (cache == null) {
            cache = PictureCache(id, this)
            pool.put(id, cache)
        }
        return cache
    }

    internal fun getSelected(id: Int, albumType: AlbumType): List<Picture>? {
        return if (id >= 0) {
            pool[id]?.cache?.let {
                if (albumType == AlbumType.PHOTO_VIDEO) {
                    it
                } else {
                    it.filter { photo -> photo.type.type == albumType.type }
                }
            }
        } else {
            null
        }
    }

    internal fun saveSelected(id: Int, selected: List<Picture>) {
        if (id >= 0) {
            pool[id]?.onCache(selected)
        }
    }

    internal fun addSelected(id: Int, selected: Picture) {
        if (id >= 0) {
            pool[id]?.addCache(listOf(selected))
        }
    }

    internal fun remove(id: Int, position: Int) {
        if (id >= 0) {
            pool[id]?.remove(position)
        }
    }

    internal fun remove(id: Int, uri: String) {
        if (id >= 0) {
            pool[id]?.remove(uri)
        }
    }

    override fun detach(id: Int) {
        if (id >= 0) {
            pool.remove(id)
        }
    }


    fun deleteAllImages(context: Context, imgPath: String) {
        try {
            val resolver = context.contentResolver
            val cursor = MediaStore.Images.Media.query(
                resolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf<String>(MediaStore.Images.Media._ID),
                MediaStore.Images.Media.DATA + "=?",
                arrayOf<String>(imgPath),
                null
            )
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(0)
                    val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    val uri = ContentUris.withAppendedId(contentUri, id)
                    val result = resolver.delete(uri, null, null)
                    // 刷新媒体库
                    resolver.notifyChange(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null)
                       }
                cursor.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}