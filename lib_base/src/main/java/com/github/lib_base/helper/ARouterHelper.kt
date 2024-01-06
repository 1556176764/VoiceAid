package com.github.lib_base.helper

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.github.lib_base.base.BaseApp.Companion.isDebug

/**
 * 路由辅助类
 */
object ARouterHelper {

    //Module first run path
    const val PATH_APP_MANAGER = "/app_manager/app_manager_activity"
    const val PATH_CONSTELLATION = "/constellation/constellation_activity"
    const val PATH_DEVELOPER = "/developer/developer_activity"
    const val PATH_JOKE = "/joke/joke_activity"
    const val PATH_MAP = "/map/map_activity"
    const val PATH_MAP_NAVI = "/map/navi_activity"
    const val PATH_SETTING = "/setting/setting_activity"
    const val PATH_VOICE_SETTING = "/voice/voice_setting_activity"
    const val PATH_WEATHER = "/weather/_activity"


    fun initHelper(application: Application) {
        if (isDebug) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(application)
    }

    //跳转页面
    fun startActivity(path: String) {
        println("跳转前")
        ARouter.getInstance().build(path).navigation()
        println("跳转后")
    }

    fun startActivity(path: String, key: String, value: String){
        ARouter.getInstance().build(path)
            .withString(key, value)
            .navigation()
    }

    fun startActivity(path: String, key: String, value: Int){
        ARouter.getInstance().build(path)
            .withInt(key, value)
            .navigation()
    }

    fun startActivity(path: String, key: String, value: Boolean){
        ARouter.getInstance().build(path)
            .withBoolean(key, value)
            .navigation()
    }

    fun startActivity(path: String, key: String, bundle: Bundle){
        ARouter.getInstance().build(path)
            .withBundle(key, bundle)
            .navigation()
    }

    fun startActivity(path: String, key: String, obj: Any){
        ARouter.getInstance().build(path)
            .withObject(key, obj)
            .navigation()
    }

    //跳转界面，带resultCode
    fun startActivity(activity: Activity, path: String, resultCode: Int){
        ARouter.getInstance().build(path)
            .navigation(activity, resultCode)
    }

    //跳转页面 String
    fun startActivity(path: String, key: String, value: String, key1: String, value1: String) {
        ARouter.getInstance().build(path)
            .withString(key, value)
            .withString(key1, value1)
            .navigation()
    }
}