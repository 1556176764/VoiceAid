package com.github.lib_network.impl

import com.github.lib_network.bean.*
import com.github.lib_network.http.HttpUrl
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 接口请求服务
 */
interface HttpImplInterface {

    @GET(HttpUrl.WEATHER_ACTION)
    fun getWeather(@Query("key")key: String,
               @Query("location")city: String,
               @Query("language")language: String,
               @Query("unit")unit: String)
        : Call<ResponseBody>

    @GET(HttpUrl.JOKE_ONE_ACTION)
    fun queryOneJoke(@Query("app_id")appID: String,
                    @Query("app_secret")apSecret: String) : Call<JokeRandomData>

    @GET(HttpUrl.JOKE_LIST_ACTION)
    fun queryJokeList(@Query("page")page: Int,
        @Query("app_id")appID: String,
        @Query("app_secret")apSecret: String) : Call<JokeDataList>

    @GET(HttpUrl.CONS_TELL_ACTION)
    fun queryTodayConsTellInfo(
        @Query("consName") consName: String,
        @Query("type") type: String,
        @Query("key") key: String
    ): Call<TodayData>

    @GET(HttpUrl.CONS_TELL_ACTION)
    fun queryWeekConsTellInfo(
        @Query("consName") consName: String,
        @Query("type") type: String,
        @Query("key") key: String
    ): Call<WeekData>

    @GET(HttpUrl.CONS_TELL_ACTION)
    fun queryMonthConsTellInfo(
        @Query("consName") consName: String,
        @Query("type") type: String,
        @Query("key") key: String
    ): Call<MonthData>

    @GET(HttpUrl.CONS_TELL_ACTION)
    fun queryYearConsTellInfo(
        @Query("consName") consName: String,
        @Query("type") type: String,
        @Query("key") key: String
    ): Call<YearData>
}