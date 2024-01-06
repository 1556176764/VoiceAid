package com.github.lib_base.event

import org.greenrobot.eventbus.EventBus
import java.util.Objects

/**
 * eventbus的管理
 */
object EventManager {

    //注册
    fun register(subscriber: Any) {
        EventBus.getDefault().register(subscriber)
    }

    //解绑
    fun unRegister(subscriber: Any) {
        EventBus.getDefault().unregister(subscriber)
    }

    //发送事件类
    private fun post(event: MessageEvent) {
        EventBus.getDefault().post(event)
    }

    //发送类型
    fun post(type: Int) {
        post(MessageEvent(type))
    }

    fun post(type: Int, string: String) {
        val event = MessageEvent(type)
        event.stringValue = string
        post(event)
    }

    fun post(type: Int, number: Int) {
        val event = MessageEvent(type)
        event.intValue = number
        post(event)
    }

    fun post(type: Int, flag: Boolean) {
        val event = MessageEvent(type)
        event.booleanValue = flag
        post(event)
    }
}