package com.github.lib_voice.engine

import android.util.Log
import com.github.lib_voice.impl.OnNluResultListener
import com.github.lib_voice.words.NluWords
import org.json.JSONObject

//语音引擎分析
object VoiceEngineAnalyze {

    private var TAG = VoiceEngineAnalyze::class.java.simpleName

    private lateinit var mOnNluResultListener: OnNluResultListener

    //分析结果
    fun analyzeNlu(nlu: JSONObject, mOnNluResultListener: OnNluResultListener) {
        this.mOnNluResultListener = mOnNluResultListener
        //用户说的话
        val rawText = nlu.optString("raw_Text")
        Log.i(TAG, "rawText:$rawText")

        //解析results
        var results = nlu.optJSONArray("results") ?: return
        val nluResultsLength = results.length()
        when (nluResultsLength) {
            0 -> {
                return
            }
            else -> {
                //暂时只管第一条
                analyzeNluSingle(results[0] as JSONObject)
            }
        }
    }

    private fun analyzeNluSingle(result: JSONObject) {
        val domain = result.optString("domain")
        val intent = result.optString("intent")
        val slots = result.optJSONObject("slots")

        slots?.let {
            when (domain) {
                NluWords.NLU_APP -> {
                    when (intent) {
                        NluWords.INTENT_OPEN_APP,
                        NluWords.INTENT_UNINSTALL_APP,
                        NluWords.INTENT_UPDATE_APP,
                        NluWords.INTENT_DOWNLOAD_APP,
                        NluWords.INTENT_SEARCH_APP,
                        NluWords.INTENT_RECOMMEND_APP -> {
                            //得到打开app的名称
                            val userAppName = it.optJSONArray("user_app_name")
                            userAppName?.let { appName ->
                                if (appName.length() > 0) {
                                    val obj = appName[0] as JSONObject
                                    val word = obj.optString("word")
                                    when (intent) {
                                        NluWords.INTENT_OPEN_APP -> {
                                            mOnNluResultListener.openApp(word)
                                        }
                                        NluWords.INTENT_UNINSTALL_APP -> {
                                            mOnNluResultListener.unInstallApp(word)
                                        }
                                        else -> {
                                            mOnNluResultListener.otherApp(word)
                                        }
                                    }
                                } else {
                                    mOnNluResultListener.nluError()
                                }
                            }
                        }
                        NluWords.NLU_CONSTELL -> {
                            val consTellNameArray = slots.optJSONArray("user_constell_name")
                            consTellNameArray?.let { consTell ->
                                if (consTell.length() > 0) {
                                    val wordObject = consTell[0] as JSONObject
                                    val word = wordObject.optString("word")
                                    when (intent) {
                                        NluWords.INTENT_CONSTELL_TIME -> mOnNluResultListener.conTellTime(
                                            word
                                        )
                                        NluWords.INTENT_CONSTELL_INFO -> mOnNluResultListener.conTellInfo(
                                            word
                                        )
                                        else -> mOnNluResultListener.nluError()
                                    }
                                }
                            }
                        }

                        else -> {
                            //其他app操作
                            mOnNluResultListener.nluError()
                        }
                    }

                }
                NluWords.NLU_INSTRUCTION -> {
                    when (intent) {
                        NluWords.INTENT_RETURN -> mOnNluResultListener.back()
                        NluWords.INTENT_BACK_HOME -> mOnNluResultListener.home()
                        NluWords.INTENT_VOLUME_UP -> mOnNluResultListener.setVolumeUp()
                        NluWords.INTENT_VOLUME_DOWN -> mOnNluResultListener.setVolumeDown()
                        else -> mOnNluResultListener.nluError()
                    }
                }
                NluWords.NLU_MOVIE -> {
                    if (NluWords.INTENT_MOVIE_VOL == intent) {
                        val userD = slots.optJSONArray("user_d")
                        userD?.let { userD ->
                            if (userD.length() > 0) {
                                val word = (userD[0] as JSONObject).optString("word")
                                if (word == "大点") {
                                    mOnNluResultListener.setVolumeUp()
                                } else if (word == "小点") {
                                    mOnNluResultListener.setVolumeDown()
                                }
                            }
                        }
                    } else {
                        mOnNluResultListener.nluError()
                    }
                }
                NluWords.NLU_ROBOT -> {
                    if (NluWords.INTENT_ROBOT_VOLUME == intent) {
                        val volumeControl = slots.optJSONArray("user_volume_control")
                        volumeControl?.let { control ->
                            if (control.length() > 0) {
                                val word = (control[0] as JSONObject).optString("word")
                                if (word == "大点声") {
                                    mOnNluResultListener.setVolumeUp()
                                } else if (word == "小点声") {
                                    mOnNluResultListener.setVolumeDown()
                                }
                            }
                        }
                    } else {
                        mOnNluResultListener.nluError()
                    }
                }
                NluWords.NLU_TELEPHONE -> {
                    if (NluWords.INTENT_CALL == intent) {
                        //user_phone_number
                        //user_call_target
                        if (slots.has("user_phone_number")) {
                            val callTarget = slots.optJSONArray("user_phone_number")
                            callTarget?.let { target ->
                                if(callTarget.length() > 0) {
                                    val name = (target[0] as JSONObject).optString("word")
                                    mOnNluResultListener.callPhoneForName(name)
                                }
                            }
                        } else if(slots.has("user_call_target")){
                            val phoneNumber = slots.optJSONArray("user_call_target")
                            phoneNumber?.let { number ->
                                if(number.length() > 0) {
                                    val name = (number[0] as JSONObject).optString("word")
                                    mOnNluResultListener.callPhoneForNumber(name)
                                }else{
                                    mOnNluResultListener.nluError()
                                }

                            }
                        } else {
                            mOnNluResultListener.nluError()
                        }
                    } else {
                        mOnNluResultListener.nluError()
                    }
                }
                NluWords.NLU_JOKE -> {
                    if (intent == NluWords.INTENT_TELL_JOKE) {
                        mOnNluResultListener.playJoke()
                    } else {
                        mOnNluResultListener.jokeList()
                    }
                }
                NluWords.NLU_SEARCH, NluWords.NLU_NOVEL -> {
                    if (intent == NluWords.INTENT_SEARCH) {
                        mOnNluResultListener.jokeList()
                    } else {
                        mOnNluResultListener.nluError()
                    }
                }
                NluWords.NLU_CONSTELL -> {
                    val consTellNameArray = slots.optJSONArray("user_constell_name")
                    consTellNameArray?.let { consTell ->
                        if (consTell.length() > 0) {
                            val wordObject = consTell[0] as JSONObject
                            val word = wordObject.optString("word")
                            when (intent) {
                                NluWords.INTENT_CONSTELL_TIME -> mOnNluResultListener.conTellTime(
                                    word
                                )
                                NluWords.INTENT_CONSTELL_INFO -> mOnNluResultListener.conTellInfo(
                                    word
                                )
                                else -> mOnNluResultListener.nluError()
                            }
                        }
                    }
                }
                NluWords.NLU_MAP -> {
                    //地图操作
                    if (intent == NluWords.INTENT_MAP_ROUTE) {
                        val navigate = slots.optJSONArray("user_navigate")
                        if (navigate.length() > 0) {
                            (navigate[0] as JSONObject).apply {
                                if (optString("word") == "导航") {
                                    //如果是导航，我就获取目的地
                                    val routeArrival = slots.optJSONArray("user_route_arrival")
                                    if (routeArrival.length() > 0) {
                                        (routeArrival[0] as JSONObject).apply {
                                            val word = optString("word")
                                            mOnNluResultListener.routeMap(word)
                                        }
                                    }
                                }
                            }
                        } else {
                            mOnNluResultListener.nluError()
                        }
                    } else if (intent == NluWords.INTENT_MAP_NEARBY) {
                        val userTarget = slots.optJSONArray("user_target")
                        if (userTarget.length() > 0) {
                            (userTarget[0] as JSONObject).apply {
                                val word = optString("word")
                                mOnNluResultListener.nearByMap(word)
                            }
                        } else {
                            mOnNluResultListener.nluError()
                        }
                    } else {
                        mOnNluResultListener.nluError()
                    }
                }
                else -> {
                    mOnNluResultListener.nluError()
                }
            }
        }


    }

}