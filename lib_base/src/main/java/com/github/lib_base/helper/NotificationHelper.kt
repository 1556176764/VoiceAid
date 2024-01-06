package com.github.lib_base.helper

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

//通知栏帮助类
@SuppressLint("StaticFieldLeak")
object NotificationHelper {

    private lateinit var mContext: Context
    private lateinit var nm: NotificationManager

    private const val CHANNEL_ID = "ai_voice_service"
    private const val CHANNEL_NAME = "语音服务"

    //需兼容android8.0的渠道
    public fun initHelper(context: Context) {
        this.mContext = context
        nm = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        setBindVoiceChannel()
    }

    //设置绑定服务的渠道
    private fun setBindVoiceChannel() {
        //创建渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //创建驱动对象
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            //呼吸灯
            channel.enableLights(false)
            //震动
            channel.enableVibration(false)
            //角标
            channel.setShowBadge(false)
            nm.createNotificationChannel(channel)
        }

    }

    fun bindVoiceService(contextText: String): Notification {
        val notificationCompat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(mContext, CHANNEL_ID)
        } else {
            NotificationCompat.Builder(mContext)
        }
        //设置标题
        notificationCompat.setContentTitle(CHANNEL_NAME)
        //设置描述
        notificationCompat.setContentText(contextText)
        //设置时间
        notificationCompat.setWhen(System.currentTimeMillis())
        //禁止滑动
        notificationCompat.setAutoCancel(false)
        return notificationCompat.build()
    }
}