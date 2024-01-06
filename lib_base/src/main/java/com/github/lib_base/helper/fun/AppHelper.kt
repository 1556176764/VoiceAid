package com.github.lib_base.helper.`fun`

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.github.lib_base.R
import com.github.lib_base.helper.`fun`.data.AppData
import com.github.lib_base.utils.LogUtils

/**
 * 应用帮助类
 */
@SuppressLint("StaticFieldLeak")
object AppHelper {

    private lateinit var mContext: Context

    //包管理器
    private lateinit var pm: PackageManager

    //所有应用
    private val mAllAppList = ArrayList<AppData>()

    //分页viwe
    val mAllViewList = ArrayList<View>()

    //所有商店包名
    private lateinit var mAllMarketArray: Array<String>

    fun initHelper(mContext: Context) {
        this.mContext = mContext

        pm = mContext.packageManager
        AppHelper.loadAllApp()
    }

    //加载所有的app
    fun loadAllApp() {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val appInfo = pm.queryIntentActivities(intent, 0)
        appInfo.forEachIndexed { _, resolveInfo ->
            val appData = AppData(
                resolveInfo.activityInfo.packageName,
                resolveInfo.loadLabel(pm) as String,
                resolveInfo.loadIcon(pm),
                resolveInfo.activityInfo.name,
                resolveInfo.activityInfo.flags == ApplicationInfo.FLAG_SYSTEM
            )
            mAllAppList.add(appData)
        }
        LogUtils.e("mAllAppList:$mAllAppList")
        initPageView()

        //加载商店报名
        mAllMarketArray = mContext.resources.getStringArray(R.array.AppMarketArray)
    }

    //初始化pageView
    private fun initPageView() {
        //遍历所有apk对象
        for (i in 0 until getPageSize()) {
            //对应一个frameLayout
            val rootView =
                View.inflate(mContext, R.layout.layout_app_manager_item, null) as ViewGroup
            //第一层，线性布局数据填充
            for (j in 0 until rootView.childCount) {
                //第二层，六个线性布局
                val childX = rootView.getChildAt(j) as ViewGroup
                //第三层，四个线性布局
                for (k in 0 until childX.childCount) {
                    //第四次，一个imageView和textView
                    val child = childX.getChildAt(k) as ViewGroup
                    val iv = child.getChildAt(0) as ImageView
                    val tv = child.getChildAt(1) as TextView
                    //计算当前下标，第i页 第j行 第k个
                    val index = i * 24 + j * 4 + k
                    //防止越界
                    if (index < mAllAppList.size) {
                        //数据获取
                        val data = mAllAppList[index]
                        iv.setImageDrawable(data.appIcon)
                        tv.text = data.appName
                        child.setOnClickListener {
                            intentApp(data.packName)
                        }
                    }
                }
            }
            mAllViewList.add(rootView)
        }
    }

    //获取需要加载的页面数量，一页能放置4*6个app
    fun getPageSize(): Int {
        return mAllAppList.size / 24 + 1
    }

    //获取非系统应用
    fun getNotSystemApp(): List<AppData> {
        return mAllAppList.filter { !it.isSystemApp }
    }

    //启动app
    fun launcherApp(appName: String): Boolean {
        if (mAllAppList.size > 0) {
            mAllAppList.forEach {
                if (it.appName == appName) {
                    intentApp(it.packName)
                    return true
                }
            }
        }
        return false
    }

    //卸载app
    fun unInstallApp(appName: String): Boolean {
        if (mAllAppList.size > 0) {
            mAllAppList.forEach {
                if (it.appName == appName) {
                    intentUnInstallApp(it.packName)
                    return true
                }
            }
        }
        return false
    }

    //完成启动app这一动作
    private fun intentApp(packageName: String) {
        val intent = pm.getLaunchIntentForPackage(packageName)
        intent?.let {
            //启动一个新的app栈
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext.startActivity(it)
        }
    }

    fun launcherAppStore(appName: String): Boolean {
        mAllAppList.forEach {
            if (mAllMarketArray.contains(it.packName)) {
                if (mAllAppList.size > 0) {
                    mAllAppList.forEach {data ->
                        if (data.appName == appName) {
                            intentAppStore(data.packName, it.packName)
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    private fun intentUnInstallApp(packageName: String) {
        val uri = Uri.parse("package:$packageName")
        //普通app没有卸载权限，因此需要调用系统app来卸载
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = uri
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        mContext.startActivity(intent)
    }

    //跳转应用商店
    private fun intentAppStore(packageName: String, marketPackageName: String) {
        val uri = Uri.parse("market://details?id=$packageName")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage(marketPackageName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        mContext.startActivity(intent)
    }
}