package com.github.lib_voice.words

import android.content.Context
import com.github.lib_voice.R
import kotlin.random.Random

//词条工具
object WordsTools {

    //唤醒词条
    private lateinit var wakeUpArray: Array<String>

    //无法应答
    private lateinit var noAnswerArray: Array<String>

    //暂不支持功能
    private lateinit var noSupportArray: Array<String>

    fun initTools(mContext: Context) {
        mContext.apply {
            wakeUpArray = resources.getStringArray(R.array.WakeUpListArray)
            noAnswerArray = resources.getStringArray(R.array.NoAnswerArray)
            noSupportArray = resources.getStringArray(R.array.NoSupportArray)
        }
    }

    //随机生成唤醒词
    fun wakeUpWords(): String {
        return randomArray(wakeUpArray)
    }

    //随机生成无响应词
    fun noAnswerWords(): String {
        return randomArray(noAnswerArray)
    }

    //随机生成不支持词
    fun noSupportWords(): String {
        return randomArray(noSupportArray)
    }

    //生成随机数组
    private fun randomArray(array: Array<String>): String {
        return array[Random.nextInt(array.size)]
    }

    fun randomInt(maxSize: Int): Int {
        return Random.nextInt(maxSize)
    }
}