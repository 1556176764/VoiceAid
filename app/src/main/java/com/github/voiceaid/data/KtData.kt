package com.github.voiceaid.data

//data数组
data class MainListData(
    val title: String,
    val icon: Int,
    val color: Int)

/**
 * type:绘画类型
 * text:文本
 */
data class ChatListData(
    val type: Int
){
   lateinit var text: String
}