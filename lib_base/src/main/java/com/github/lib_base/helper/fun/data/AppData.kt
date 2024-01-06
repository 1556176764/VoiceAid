package com.github.lib_base.helper.`fun`.data

import android.graphics.drawable.Drawable

/**
 * app相关信息，包括包名、应用名称、Icon、第一启动类、是否是系统应用
 */
data class AppData(val packName: String,
                   val appName: String,
                   val appIcon: Drawable,
                   val firstRunName: String,
                   val isSystemApp: Boolean)
