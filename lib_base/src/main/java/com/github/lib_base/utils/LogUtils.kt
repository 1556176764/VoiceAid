package com.github.lib_base.utils

import android.util.Log
import com.github.lib_base.base.BaseApp

object LogUtils {
    private const val TAG: String = "VoiceAidApp"

    fun i(text: String?) {
        if (BaseApp.isDebug) {
            text?.let {
                Log.i(TAG, it)
            }
        }
    }

    fun e(text: String?) {
        if (BaseApp.isDebug) {
            text?.let {
                Log.e(TAG, it)
            }
        }
    }
}