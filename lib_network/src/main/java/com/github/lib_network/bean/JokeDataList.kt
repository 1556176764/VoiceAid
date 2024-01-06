package com.github.lib_network.bean

data class JokeDataList(
    val code: Int,
    val `data`: ListData,
    val msg: String
)

data class ListData(
    val limit: Int,
    val list: List<ListResult>,
    val page: Int,
    val totalCount: Int,
    val totalPage: Int
)

data class ListResult(
    val content: String,
    val updateTime: String
)