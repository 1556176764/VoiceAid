package com.github.lib_base.service

import android.app.IntentService
import android.content.Intent
import com.github.lib_base.helper.SoundHelper
import com.github.lib_base.helper.ThreadPoolHelper
import com.github.lib_base.helper.`fun`.AppHelper
import com.github.lib_base.helper.`fun`.CommonSettingHelper
import com.github.lib_base.helper.`fun`.ConsTellHelper
import com.github.lib_base.utils.LogUtils
import com.github.lib_voice.words.WordsTools

class InitService : IntentService(IntentService::class.simpleName){

    override fun onCreate() {
        super.onCreate()
        LogUtils.i("初始化启动")
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onHandleIntent(intent: Intent?) {
        WordsTools.initTools(this)
        LogUtils.i("执行初始化操作")
        SoundHelper.init(this)
        AppHelper.initHelper(this)
        CommonSettingHelper.initHelper(this)
        ThreadPoolHelper.initHelper()
        ConsTellHelper.initHelper(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.i("初始化完成")
    }

}