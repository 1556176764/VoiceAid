package com.github.lib_base.base

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.yanzhenjie.permission.Action
import com.yanzhenjie.permission.AndPermission

abstract class BaseActivity : AppCompatActivity() {

    protected val codeWindowRequest = 1000

    //获取绑定视图
    abstract fun getLayoutView(): View

    //获取标题栏
    abstract fun getTitleText(): String

    //初始化
    abstract fun initView()

    //是否显示返回键
    abstract fun isShowBack(): Boolean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutView())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //不能是actionbar，而是supportActionbar
            supportActionBar?.let {
                it.title = getTitleText()
                //透明度为0,去掉阴影
                it.elevation = 0f
                //返回键设置
                it.setDisplayHomeAsUpEnabled(isShowBack())
            }
        }
        initView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //返回事件响应
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }

    //检查窗口权限
    protected fun checkWindowPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }

    //检查权限
    protected fun requestWindowPermission(packageName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startActivityForResult(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")
                ), codeWindowRequest
            )
        }
    }

    protected fun requestPermission(permissions: Array<String>, granted: Action<List<String>>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AndPermission.with(this)
                .runtime()
                .permission(permissions)
                .onGranted(granted)
                .start()
        }
    }

    //检查权限
    protected fun checkPermission(permission: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }
        return true
    }

    //检查多个权限
    protected fun checkPermission(permission: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission.forEach {
                if (checkSelfPermission(it) == PackageManager.PERMISSION_DENIED) {
                    return false
                }
            }
        }
        return true
    }
}