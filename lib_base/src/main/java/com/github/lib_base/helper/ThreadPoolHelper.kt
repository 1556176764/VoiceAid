package com.github.lib_base.helper

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object ThreadPoolHelper {

    lateinit var pools : ExecutorService

    fun initHelper() {
        pools = Executors.newSingleThreadExecutor()
    }

}