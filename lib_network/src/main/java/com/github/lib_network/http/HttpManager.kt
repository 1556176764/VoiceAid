package com.github.lib_network.http

import android.util.Log
import com.github.lib_network.bean.*
import com.github.lib_network.impl.HttpImplInterface
import com.github.lib_network.interceptor.HttpInterceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 对外的网络管理工具
 */
object HttpManager {

    private fun getOkHttpClient(): OkHttpClient? {
        //日志显示级别
        val level = HttpLoggingInterceptor.Level.BODY
        //新建log拦截器
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.e(
                "Retrofit请求：",
                "OkHttp====Message:$message"
            )
        }
        loggingInterceptor.level = level
        //定制OkHttp
        val httpClientBuilder = OkHttpClient.Builder()
        //OkHttp进行添加拦截器loggingInterceptor
        httpClientBuilder.addInterceptor(loggingInterceptor)
            .addInterceptor(HttpInterceptor())
        return httpClientBuilder.build()
    }

    private fun getRetrofitClient(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .client(getOkHttpClient())
            .baseUrl(HttpUrl.WEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //===笑话===
    private val retrofitJoke by lazy {
        getRetrofitClient(HttpUrl.JOKE_BASE_URL)
    }

    private val apiJoke by lazy {
        retrofitJoke.create(HttpImplInterface::class.java)
    }

    //查询笑话
    fun queryJoke(callBack: Callback<JokeRandomData>) {
        apiJoke.queryOneJoke(HttpKey.JOKE_APP_ID, HttpKey.JOKE_APP_SECRET).enqueue(callBack)
    }

    //笑话列表
    fun queryJokeList(page: Int, callBack: Callback<JokeDataList>) {
        apiJoke.queryJokeList(page, HttpKey.JOKE_APP_ID, HttpKey.JOKE_APP_SECRET).enqueue(callBack)
    }

    //===星座===
    private val retrofitConsTell by lazy {
        getRetrofitClient(HttpUrl.CONS_TELL_BASE_URL)
    }

    private val apiConsTell by lazy {
        retrofitJoke.create(HttpImplInterface::class.java)
    }

    //查询今天星座详情
    fun queryTodayConsTellInfo(name: String, callback: Callback<TodayData>) {
        apiConsTell.queryTodayConsTellInfo(name, "today", HttpKey.CONS_TELL_KEY).enqueue(callback)
    }

    //查询明天星座详情
    fun queryTomorrowConsTellInfo(name: String, callback: Callback<TodayData>) {
        apiConsTell.queryTodayConsTellInfo(name, "tomorrow", HttpKey.CONS_TELL_KEY)
            .enqueue(callback)
    }

    //查询本周星座详情
    fun queryWeekConsTellInfo(name: String, callback: Callback<WeekData>) {
        apiConsTell.queryWeekConsTellInfo(name, "week", HttpKey.CONS_TELL_KEY).enqueue(callback)
    }

    //查询本月星座详情
    fun queryMonthConsTellInfo(name: String, callback: Callback<MonthData>) {
        apiConsTell.queryMonthConsTellInfo(name, "month", HttpKey.CONS_TELL_KEY).enqueue(callback)
    }

    //查询今年星座详情
    fun queryYearConsTellInfo(name: String, callback: Callback<YearData>) {
        apiConsTell.queryYearConsTellInfo(name, "year", HttpKey.CONS_TELL_KEY).enqueue(callback)
    }

    //===天气===

    //天气对象
    private val retrofitWeather by lazy {
        getRetrofitClient(HttpUrl.WEATHER_BASE_URL)
    }

    //天气接口对象
    private val apiWeather by lazy {
        retrofitWeather.create(HttpImplInterface::class.java)
    }

    //天气查询
    fun queryWeather(location: String): Call<ResponseBody> {
        return apiWeather.getWeather(HttpKey.WEATHER_KEY, location, "zh-Hans", "c")
    }


}