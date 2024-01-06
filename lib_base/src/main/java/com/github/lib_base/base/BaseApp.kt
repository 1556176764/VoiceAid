package com.github.lib_base.base

import android.app.Application
import android.content.Intent
import com.github.lib_base.cache.Sp
import com.github.lib_base.helper.ARouterHelper
import com.github.lib_base.helper.NotificationHelper
import com.github.lib_base.map.MapManager
import com.github.lib_base.service.InitService

/**
 * 基类app
 */
open class BaseApp : Application() {
    companion object{
        var isDebug:Boolean = false
    }
    override fun onCreate() {
        super.onCreate()

        Sp.initCache(this)
        ARouterHelper.initHelper(this)
        NotificationHelper.initHelper(this)
        startService(Intent(this, InitService::class.java))
        MapManager.initManager(this)
    }
}