package com.github.lib_network.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class HttpInterceptor : Interceptor {

    private val TAG = "HTTP"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        Log.i(TAG, "===REQUEST===")
        if(request.method() == "GET"){
            Log.i(TAG, request.url().toString())
        }
        //body一般只能读一次
//        Log.i(TAG, "===RESPONSE===")
//        request.body()?.let {
//            Log.i(TAG, it.toString())
//        }
        return response
    }
}