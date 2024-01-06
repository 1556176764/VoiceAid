package com.github.lib_base.helper.`fun`

import android.annotation.SuppressLint
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import com.github.lib_base.helper.ThreadPoolHelper

//通用设置帮助类
@SuppressLint("StaticFieldLeak")
object CommonSettingHelper {

    private lateinit var mContext: Context

    private lateinit var inst: Instrumentation

    fun initHelper(mContext: Context) {
        this.mContext = mContext
        inst = Instrumentation()
    }

    fun back() {
        ThreadPoolHelper.pools.submit(
            Runnable { inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK) }
        )
    }

    fun home() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        mContext.startActivity(intent)
    }

    fun setVolumeUp() {
        ThreadPoolHelper.pools.submit(
            Runnable { inst.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_UP) }
        )
    }

    fun setVolumeDown() {
        ThreadPoolHelper.pools.submit(
            Runnable { inst.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_DOWN) }
        )
    }
}