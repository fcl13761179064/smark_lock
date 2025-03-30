package com.sprint.lock.app


import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.springs.common.base.BaseActivity
import com.springs.common.ext.showTopToast
import com.sprint.lock.app.databinding.ActivityJumpToOtherBinding


/**
 * 跳转到别的app
 */
class JumpToOtherAppActivity : BaseActivity<ActivityJumpToOtherBinding>(){
    override fun getViewBinding(): ActivityJumpToOtherBinding  =ActivityJumpToOtherBinding.inflate(layoutInflater)

    override fun init(savedInstanceState: Bundle?) {

        binding.ivBack.setOnClickListener {
            finish()
        }
       binding.imageView1.setOnClickListener {
               // 米家App的包名
                val packageName = "com.xiaomi.smarthome"
               // 主Activity的名字，这可能需要根据不同版本的米家App进行调整
                val activityName = "com.xiaomi.smarthome.SmartHomeMainActivity"

               try {
                   // 创建一个新的Intent来启动米家App
                   val launchIntent = Intent()
                   launchIntent.component = ComponentName(packageName, activityName)
                   startActivity(launchIntent)
               } catch (e: ActivityNotFoundException) {
                   // 未找到米家App的指定Activity
                   showTopToast("目标应用未安装")
               }
       }

        binding.imageView2.setOnClickListener {
            // 米家App的包名
            val packageName = "com.tuya.smartiot"
            // 主Activity的名字，这可能需要根据不同版本的米家App进行调整
          //  val activityName = "com.thingclips.smart.login_guide.ui.NewGuideActivity"
           val activityName = "com.tuya.smartiot/com.thingclips.smart.login_guide.ui.NewGuideActivity"
            try {
                // 创建一个新的Intent来启动米家App
                val launchIntent = Intent()
                launchIntent.component = ComponentName(packageName, activityName)
                startActivity(launchIntent)
            } catch (e: ActivityNotFoundException) {
                // 未找到米家App的指定Activity
                showTopToast("目标应用未安装")
            }
        }
    }

}