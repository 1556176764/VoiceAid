package com.github.lib_network.bean

data class JokeRandomData(
    val code: Int,
    val data: List<Data>,
    val msg: String
)

data class Data(
    val content: String,
    val updateTime: String
)