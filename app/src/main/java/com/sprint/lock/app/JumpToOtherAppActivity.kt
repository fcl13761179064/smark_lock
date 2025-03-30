package com.sprint.lock.app


import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.springs.common.base.BaseActivity
import com.springs.common.ext.showTopToast
import com.springs.common.widgets.AppData
import com.sprint.lock.app.adapter.AdapterSearchCity
import com.sprint.lock.app.adapter.AppsAdapter
import com.sprint.lock.app.databinding.ActivityJumpToOtherBinding
import com.sprint.lock.app.databinding.ActivityJumpToOthersBinding


/**
 * 跳转到别的app
 */
class JumpToOtherAppActivity : BaseActivity<ActivityJumpToOthersBinding>() {

    private val adapter by lazy { AppsAdapter() }
    override fun getViewBinding(): ActivityJumpToOthersBinding =
        ActivityJumpToOthersBinding.inflate(layoutInflater)

    override fun init(savedInstanceState: Bundle?) {
        // 隐藏导航栏
        val decorView: View = window.decorView
        val uiOptions: Int =
            (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.setSystemUiVisibility(uiOptions)
        val layoutManagers = GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false)
        binding.appsRecyclerView.layoutManager = layoutManagers
        binding.appsRecyclerView.setHasFixedSize(true)
        binding.appsRecyclerView.itemAnimator = null
        binding.appsRecyclerView.adapter = adapter
        val data = getAllApps()
        data?.let {
            adapter.setNewData(it)
        }

        adapter.setOnItemClickListener { adapter, view, position ->
            launchApp((adapter.data[position] as AppInfo).packageName)
        }
        binding.ivLeft.setOnClickListener {
            finish()
        }
    }

    private fun getAllApps(): MutableList<AppInfo> {
        val pm = packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = pm.queryIntentActivities(mainIntent, 0)
        val appList = mutableListOf<AppInfo>()

        for (ri in apps) {
            val packageName = ri.activityInfo.packageName
            val appName = ri.loadLabel(pm).toString()
            val icon = ri.loadIcon(pm)
            appList.add(AppInfo(appName, packageName, icon))
        }

        // 按应用名称排序
        return appList.sortedBy { it.appName }.toMutableList()
    }


    private fun launchApp(packageName: String) {
        try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                startActivity(intent)
            } else {
                showTopToast( "无法启动该应用")
            }
        } catch (e: Exception) {
            showTopToast( "启动应用失败")
        }
    }
}

data class AppInfo(
    val appName: String, val packageName: String, val icon: Drawable
)
