package com.kelin.photoselector.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.loader.app.LoaderManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.blankj.utilcode.util.PermissionUtils
import com.kelin.photoselector.PhotoSelector
import com.kelin.photoselector.R
import com.kelin.photoselector.adapter.AdapterMessageInfo
import com.kelin.photoselector.cache.DistinctManager
import com.kelin.photoselector.databinding.FragmentKelinPhotoSelectorAlbumBinding
import com.kelin.photoselector.databinding.HolderKelinPhotoSelectorPictureBinding
import com.kelin.photoselector.loader.AlbumManager
import com.kelin.photoselector.loader.AlbumPictureLoadCallback
import com.kelin.photoselector.model.*
import com.kelin.photoselector.utils.compressAndRotateByDegree
import com.kelin.photoselector.widget.ProgressDialog
import com.room.database.db.DoorUtils
import com.springs.common.application.BaseApplication
import com.springs.common.common.LiveDataBusX
import com.springs.common.dialog.PermissionDialog
import com.springs.common.ext.showTopToast
import com.springs.common.widgets.AppData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * **描述:** 相册页面。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/8/19 3:12 PM
 *
 * **版本:** v 1.0.0
 */
class AlbumFragment : BasePhotoSelectorFragment<FragmentKelinPhotoSelectorAlbumBinding>() {
    companion object {
        private const val KEY_KELIN_PHOTO_SELECTOR_ALBUM_TYPE =
            "key_kelin_photo_selector_album_type"
        private const val KEY_KELIN_PHOTO_SELECTOR_MAX_COUNT = "key_kelin_photo_selector_max_count"
        private const val KEY_KELIN_PHOTO_SELECTOR_ID = "key_kelin_photo_selector_id"
        private const val KEY_KELIN_PHOTO_SELECTOR_MAX_SIZE = "key_kelin_photo_selector_max_size"
        private const val KEY_KELIN_PHOTO_SELECTOR_MAX_DURATION =
            "key_kelin_photo_selector_max_duration"
        var mLoaderManager: LoaderManager? = null

        /**
         * 配置选择意图。
         * @param albumType 相册类型。
         * @param maxLength 最多可以选择多少个。
         * @param id 本次选择的唯一ID，应当是与View关联的。
         * @param maxDuration 选择视频是的最大时长限制，单位秒。
         */
        fun configurationPictureSelectorIntent(
            intent: Intent,
            albumType: AlbumType,
            maxLength: Int,
            id: Int,
            maxSize: Float,
            maxDuration: Int
        ) {
            intent.putExtra(KEY_KELIN_PHOTO_SELECTOR_ID, id)
            intent.putExtra(KEY_KELIN_PHOTO_SELECTOR_ALBUM_TYPE, albumType.type)
            intent.putExtra(KEY_KELIN_PHOTO_SELECTOR_MAX_COUNT, maxLength)
            if (maxSize > 0) {
                //这里转换为字节，方便后面做对比。
                intent.putExtra(KEY_KELIN_PHOTO_SELECTOR_MAX_SIZE, maxSize)
            }
            if (maxDuration > 0) {
                //这里乘以1000是为了转换为毫秒，方便后面做比对。
                intent.putExtra(KEY_KELIN_PHOTO_SELECTOR_MAX_DURATION, maxDuration * 1000L)
            }
        }
    }

    private val adapterMessage by lazy { AdapterMessageInfo() }

    private val albumOffset: Int
        get() = if (PhotoSelector.isAlbumTakePictureEnable) 1 else 0

    private val dataFormat by lazy { SimpleDateFormat("yyyy-MM-dd", Locale.CHINA) }

    private val handler by lazy { Handler(Looper.getMainLooper()) }

    private val sp by lazy {
        applicationContext.getSharedPreferences(
            "${applicationContext.packageName}_photo_selector", Context.MODE_PRIVATE
        )
    }

    private val selectorId by lazy {
        requireArguments().getInt(
            KEY_KELIN_PHOTO_SELECTOR_ID, PhotoSelector.ID_REPEATABLE
        )
    }

    private val justSelectOne by lazy { selectorId == PhotoSelector.ID_SINGLE }

    private val message by lazy {
        when (albumType) {
            AlbumType.PHOTO -> getString(R.string.kelin_photo_selector_pictures)
            AlbumType.VIDEO -> getString(R.string.kelin_photo_selector_videos)
            else -> getString(R.string.kelin_photo_selector_pictures_and_videos)
        }
    }

    private val albumType by lazy {
        AlbumType.typeOf(
            requireArguments().getInt(
                KEY_KELIN_PHOTO_SELECTOR_ALBUM_TYPE, AlbumType.PHOTO_VIDEO.type
            )
        )
    }

    private val maxLength by lazy {
        requireArguments().getInt(
            KEY_KELIN_PHOTO_SELECTOR_MAX_COUNT, PhotoSelector.defMaxLength
        )
    }

    private val maxSize by lazy {
        requireArguments().getFloat(
            KEY_KELIN_PHOTO_SELECTOR_MAX_SIZE, 0F
        )
    }

    private val maxDuration by lazy {
        requireArguments().getLong(
            KEY_KELIN_PHOTO_SELECTOR_MAX_DURATION, 0
        )
    }

    private val isSingleSelector by lazy { maxLength == 1 }

    private val listAdapter by lazy {
        PhotoListAdapter(DistinctManager.instance.getSelected(selectorId, albumType))
    }

    private val listLayoutManager by lazy {
        object : GridLayoutManager(
            requireContext(), getSpanCount(isLandscape(resources.configuration))
        ) {
            override fun onLayoutChildren(
                recycler: RecyclerView.Recycler?, state: RecyclerView.State?
            ) {
                try {
                    super.onLayoutChildren(recycler, state)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.also {
            it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return 1
                }
            }
        }
    }

    private val albumManager: AlbumManager by lazy {
        AlbumManager(
            requireActivity(), viewLifecycleOwner, sp, maxSize, maxDuration
        )
    }

    override fun generateViewBinding(
        inflater: LayoutInflater, container: ViewGroup?, attachToParent: Boolean
    ): FragmentKelinPhotoSelectorAlbumBinding {
        return FragmentKelinPhotoSelectorAlbumBinding.inflate(inflater, container, attachToParent)
    }

    fun requestStoragePermission() {
        try {
            //权限申请
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                PermissionUtils.permission(
                    Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO
                ).callback(object : PermissionUtils.SimpleCallback {
                    override fun onGranted() {
                        try {
                            questPhoto()
                        } catch (ignored: Exception) {
                            ignored.printStackTrace()
                        }
                    }

                    override fun onDenied() {
                        withViewBinding {
                            emptyLayout.visibility = View.VISIBLE
                        }
                        PermissionDialog(requireContext(), "请开启相册权限").show()
                    }

                }).request()
            } else {
                // Android 13 以下版本，请求 READ_EXTERNAL_STORAGE 权限
                //权限申请
                PermissionUtils.permission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).callback(object : PermissionUtils.SimpleCallback {
                    override fun onGranted() {
                        try {
                            questPhoto()
                        } catch (ignored: Exception) {
                            ignored.printStackTrace()
                        }
                    }

                    override fun onDenied() {
                        withViewBinding {
                            emptyLayout.visibility = View.VISIBLE
                        }
                        PermissionDialog(requireContext(), "请开启相册权限").show()
                    }

                }).request()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    fun questPhoto() {
        try {
            withViewBinding {
                emptyLayout.visibility = View.GONE
            }
            withViewBinding {
                updateSelectedCount(0)
                rvKelinPhotoSelectorPhotoListView.run {
                    layoutManager = listLayoutManager
                    adapter = listAdapter
                    itemAnimator = DefaultItemAnimator().apply {
                        addDuration = 0
                        changeDuration = 200
                        removeDuration = 0
                        moveDuration = 0
                    }
                    addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrollStateChanged(
                            recyclerView: RecyclerView, newState: Int
                        ) {
                            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                                tvKelinPhotoSelectorModifiedDate.visibility = View.VISIBLE
                            } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                tvKelinPhotoSelectorModifiedDate.visibility = View.GONE
                            }
                        }

                        override fun onScrolled(
                            recyclerView: RecyclerView, dx: Int, dy: Int
                        ) {
                            tvKelinPhotoSelectorModifiedDate.text =
                                if (listAdapter.itemCount - albumOffset > 0) {
                                    listLayoutManager.findFirstVisibleItemPosition().let {
                                        listAdapter.getItem(it + albumOffset).modifyDate
                                    }
                                } else {
                                    ""
                                }
                        }
                    })
                }

                mLoaderManager = LoaderManager.getInstance(this@AlbumFragment)
                mLoaderManager?.initLoader(
                    albumType.type,
                    null,
                    AlbumPictureLoadCallback(applicationContext) { cursor ->
                        albumManager.parsePicture(
                            cursor,
                            this@AlbumFragment::onAlbumSelected,
                            this@AlbumFragment::onAlbumMorePicture
                        )
                    })

                //关闭当前页面
                //ivKelinPhotoSelectorFinish.setOnClickListener { finish() }
                //变更相册
                rlKelinPhotoSelectorAlbumName.setOnClickListener { albumManager.onSelectAlbums() }
                //重新选择
                tvKelinPhotoSelectorReselect.setOnClickListener {
                    listAdapter.clearSelected()
                    updateSelectedCount(listAdapter.selectedPictures.size)
                }
                //预览选中图片
                /*  tvKelinPhotoSelectorPreview.setOnClickListener {
                      PhotoSelector.openPicturePreviewPage(
                          requireActivity(), listAdapter.selectedPictures
                      )
                  }*/
                if (justSelectOne) {
                    btnKelinPhotoSelectorDone.setBackgroundColor(Color.TRANSPARENT)
                } else {
                    //用户点击完成按钮。
                    btnKelinPhotoSelectorDone.setOnClickListener {
                        onSelectDone()
                    }
                }
            }

        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        withViewBinding {
            rgMenu.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rg_one -> {
                        deleteSmockInfo.visibility = View.VISIBLE
                        emptyLayout.visibility = View.GONE
                        rgTwo.isChecked = false
                        rgOne.isChecked = true
                        rgOne.setBackgroundResource(R.drawable.fm_ff8157ff_ffe056ff_radis_15)
                        rgTwo.setBackgroundResource(0)
                        alumb.visibility = View.GONE
                        llPicture.visibility = View.GONE
                        flLayout.visibility = View.VISIBLE
                        tvKelinPhotoSelectorReselect.visibility = View.INVISIBLE
                    }

                    R.id.rg_two -> {
                        requestStoragePermission()
                        deleteSmockInfo.visibility = View.GONE
                        rgTwo.setBackgroundResource(R.drawable.fm_ff8157ff_ffe056ff_radis_15)
                        rgOne.setBackgroundResource(0)
                        alumb.visibility = View.VISIBLE
                        llPicture.visibility = View.VISIBLE
                        flLayout.visibility = View.GONE
                        rgOne.isChecked = false
                        rgTwo.isChecked = true
                        tvKelinPhotoSelectorReselect.apply {
                            updateSelectedCount(listAdapter.selectedPictures.size)
                        }
                    }
                }
            }
            deleteSmockInfo.setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    if (adapterMessage.data.isNotEmpty()) {
                        showTopToast("删除成功")
                        DoorUtils.getInstance(BaseApplication.mApplication).clearData()
                        requireActivity().runOnUiThread {
                            // 将列表倒序排列
                            adapterMessage.setNewData(mutableListOf())
                        }
                    } else {
                        showTopToast("没数据删除")
                    }
                }
            }
            rgOne.setBackgroundResource(R.drawable.fm_ff8157ff_ffe056ff_radis_15)
            rgTwo.setBackgroundResource(0)
            messageInfo.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            messageInfo.setHasFixedSize(true)
            messageInfo.adapter = adapterMessage
            GlobalScope.launch(Dispatchers.IO) {
                val doors = DoorUtils.getInstance(BaseApplication.mApplication).getAllDoor()
                requireActivity().runOnUiThread {
                    // 将列表倒序排列
                    val ssss = doors.reversed()
                    adapterMessage.setNewData(ssss)
                    messageInfo.scrollToPosition(0)
                }
            }
        }

        LiveDataBusX.getInstance().with<Boolean>(AppData.TO_SCAN_PHOTO).observe(requireActivity()) {
            lifecycleScope.launch {
                delay(1000)
                mLoaderManager?.destroyLoader(albumType.type)
                mLoaderManager = LoaderManager.getInstance(this@AlbumFragment)
                mLoaderManager?.initLoader(albumType.type,
                    null,
                    AlbumPictureLoadCallback(applicationContext) { cursor ->
                        albumManager.parsePicture(
                            cursor,
                            this@AlbumFragment::onAlbumSelected,
                            this@AlbumFragment::onAlbumMorePicture
                        )
                    })
            }

        }


        LiveDataBusX.getInstance().with<Boolean>(AppData.TO_UPDATA_DOOR)
            .observe(requireActivity()) {
                GlobalScope.launch(Dispatchers.IO) {
                    val doors = DoorUtils.getInstance(BaseApplication.mApplication).getAllDoor()
                    requireActivity().runOnUiThread {
                        val ssss = doors.reversed()
                        adapterMessage.setNewData(ssss)
                        withViewBinding {
                            messageInfo.scrollToPosition(0)
                        }
                    }
                }
            }

    }

    private fun onSelectDone(needProgress: Boolean = true) {
        try {
            listAdapter.selectedPictures.also { selected ->
                if (!PhotoSelector.isAutoCompress || selected.all { it.isComposeFinished }) {  //如果压缩已经完成(无论是否成功)
                    selected.forEach {
                        DistinctManager.instance.deleteAllImages(requireContext(), it.path)
                    }
                    listAdapter.removeSelect()
                    listAdapter.clearSelected()
                    showTopToast("删除成功")
                    vb.btnKelinPhotoSelectorDone.apply {
                        text = "${getString(R.string.kelin_photo_selector_selected)}(0)"
                        isEnabled = false
                    }
                } else {  //如果压缩没有完成
                    if (needProgress) {  //如果需要进度提示
                        ProgressDialog().show(parentFragmentManager, selectorId.toString())
                    }
                    handler.postDelayed({
                        onSelectDone(false)
                    }, 100)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        listLayoutManager.spanCount = getSpanCount(isLandscape(newConfig))
    }

    private fun isLandscape(config: Configuration) =
        config.orientation == Configuration.ORIENTATION_LANDSCAPE

    private fun getSpanCount(landscape: Boolean): Int = if (landscape) 7 else 4

    private fun onAlbumSelected(album: Album) {
        listAdapter.setPhotos(album.pictures)
        vb.tvKelinPhotoSelectorAlbumName.text = album.name
    }

    private fun onAlbumMorePicture(pictures: List<Picture>) {
        listAdapter.addAllPictures(pictures)
    }

    @SuppressLint("SetTextI18n")
    private fun updateSelectedCount(selectedCount: Int = 0) {
        val visible = if (selectedCount > 0) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }/*vb.tvKelinPhotoSelectorPreview.apply {
             visibility = visible
             if (selectedCount > 0) {
                 text = "${getString(R.string.kelin_photo_selector_preview)}(${selectedCount})"
             }
         }*/
        vb.tvKelinPhotoSelectorReselect.visibility =visible
        vb.btnKelinPhotoSelectorDone.apply {
            text =
                "${getString(if (justSelectOne) R.string.kelin_photo_selector_selected else R.string.kelin_photo_selector_selected)}($selectedCount)"
            isEnabled = !justSelectOne && selectedCount > 0
        }
    }

    private fun onPictureSelectedChanged(picture: Picture, isSelected: Boolean) {
        val selectedPictures = listAdapter.selectedPictures
        if (isSelected) {
            if (PhotoSelector.isAutoCompress && !picture.isVideo) {
                picture.compressAndRotateByDegree()
            }
            selectedPictures.add(picture)
        } else {
            //取消选中时判断是否是取消的最后一个，如果不是的话那还要刷新其他的条目变更序号。
            val isLast = picture == selectedPictures.lastOrNull()
            //获取当前取消的是第几个，一定要在当前资源没有从选中池里面移除的时候获取，否则永远是-1。
            val currentIndex = selectedPictures.indexOf(picture)
            selectedPictures.remove(picture)
            if (!isLast) {
                selectedPictures.forEachIndexed { i, p ->
                    if (i >= currentIndex) {
                        listAdapter.notifyItemChanged(p)
                    }
                }
            }
        }
        updateSelectedCount(listAdapter.selectedPictures.size)
    }

    private fun onPictureTook(picture: File, isVideo: Boolean) {
        val duration = if (isVideo) {
            MediaMetadataRetriever().let { m ->
                m.setDataSource(picture.absolutePath)
                m.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 1
            }
        } else {
            0
        }
        listAdapter.addPicture(
            Picture(
                picture.absolutePath,
                picture.length(),
                if (isVideo) PictureType.VIDEO else PictureType.PHOTO,
                if (isVideo) duration.formatToDurationString() else "",
                dataFormat.format(Date())
            )
        )
        if (isSingleSelector) {
            onSelectDone()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    private inner class PhotoListAdapter(
        private val initialSelected: List<Picture>?
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        init {
            if (!initialSelected.isNullOrEmpty()) {
                updateSelectedCount(initialSelected.size)
            }
        }

        private var photoList: MutableList<Picture> = ArrayList()

        val selectedPictures: MutableList<Picture> = initialSelected?.toMutableList() ?: ArrayList()

        @SuppressLint("NotifyDataSetChanged")
        fun setPhotos(pictures: List<Picture>, refresh: Boolean = true) {
            photoList.run {
                clear()
                addAll(pictures)
            }
            if (refresh) {
                notifyDataSetChanged()
            }
        }

        fun addPicture(picture: Picture, refresh: Boolean = true) {
            photoList.add(0, picture)
            onPictureSelectedChanged(picture, true)
            if (refresh) {
                notifyItemInserted(1)
            }
        }

        fun addAllPictures(pictures: List<Picture>, refresh: Boolean = true) {
            val start = itemCount
            photoList.addAll(pictures)
            if (refresh) {
                notifyItemRangeInserted(start, pictures.size)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return if (position == 0 && PhotoSelector.isAlbumTakePictureEnable) 0 else 1
        }

        override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int
        ): RecyclerView.ViewHolder {
            return if (viewType == 0) {
                TakePhotoHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.holder_kelin_photo_take_picture, parent, false
                    )
                )
            } else {
                PhotoHolder(
                    HolderKelinPhotoSelectorPictureBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }
        }

        override fun getItemCount(): Int {
            return photoList.size + albumOffset
        }

        fun notifyItemChanged(item: Picture) {
            notifyItemChanged(photoList.indexOf(item) + albumOffset)
        }

        fun getItem(position: Int): Picture {
            //减1是减去拍照按钮的位置。
            return photoList[position - albumOffset]
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is PhotoHolder) {
                holder.bindData(getItem(position))
            }
        }


        fun clearSelected() {
            selectedPictures.run {
                if (isNotEmpty()) {
                    initialSelected?.also { removeAll(it) }
                    val targetPositions = map { photoList.indexOf(it) }
                    clear()
                    initialSelected?.also { addAll(it) }
                    targetPositions.forEach {
                        notifyItemChanged(it + albumOffset)
                    }
                }
            }
        }

        fun removeSelect() {
            selectedPictures.run {
                photoList.removeAll(selectedPictures)
                notifyDataSetChanged()
            }
        }

        fun isInitSelected(data: Picture): Boolean {
            return initialSelected?.contains(data) == true
        }
    }

    private inner class PhotoHolder(private val vb: HolderKelinPhotoSelectorPictureBinding) :
        RecyclerView.ViewHolder(vb.root) {
        init {
            vb.rlKelinPhotoSelectorChecker.setOnClickListener {
                listAdapter.getItem(layoutPosition).apply {
                    //如果被选中了的资源中没有当前的资源，那么就认为当前用户的目的是选中，否则就是取消选中。
                    val isSelected = !listAdapter.selectedPictures.contains(this)
                    if (isSelected && listAdapter.selectedPictures.size >= maxLength) {
                        Toast.makeText(
                            applicationContext,
                            "最多只能选择${maxLength}${if (albumType == AlbumType.PHOTO) "张" else "个"}$message",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        onPictureSelectedChanged(this, isSelected)
                        listAdapter.notifyItemChanged(layoutPosition)
                    }
                    if (isSingleSelector) {
                        onSelectDone()
                    }
                }
            }
            itemView.setOnClickListener {
                PhotoSelector.openPicturePreviewPage(
                    requireActivity(), listOf(listAdapter.getItem(layoutPosition))
                )
            }
        }

        fun bindData(data: Picture) {
            vb.ivKelinPhotoSelectorPhotoView.load(data.uri) {
                crossfade(true)
                placeholder(R.drawable.image_placeholder)
            }
            val no = listAdapter.selectedPictures.indexOf(data)
            vb.pmKelinPhotoSelectorPhotoViewMask.isSelected = no >= 0
            val canOperation = !listAdapter.isInitSelected(data)
            vb.tvKelinPhotoSelectorChecker.run {
                isSelected = no >= 0
                text = if (no >= 0) {
                    (no + 1).toString()
                } else {
                    null
                }
                isEnabled = canOperation
            }
            vb.rlKelinPhotoSelectorChecker.isEnabled = canOperation
            vb.tvKelinPhotoSelectorVideoDuration.apply {
                visibility = if (data.type == PictureType.VIDEO) {
                    text = data.duration
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }

    private inner class TakePhotoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                if (listAdapter.selectedPictures.size >= maxLength) {
                    Toast.makeText(
                        applicationContext,
                        "最多只能选择${maxLength}${if (albumType == AlbumType.PHOTO) "张" else "个"}$message",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    onTakePicture()
                }
            }
        }

        private fun onTakePicture() {
            if (albumType == AlbumType.VIDEO) {
                PhotoSelector.takeVideo(this@AlbumFragment) {
                    if (it != null) {
                        onPictureTook(it, true)
                    }
                }
            } else {
                PhotoSelector.takePhoto(this@AlbumFragment) {
                    if (it != null) {
                        onPictureTook(it, false)
                    }
                }
            }
        }
    }
}