package com.github.voiceaid.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.github.lib_base.helper.ARouterHelper
import com.github.lib_base.helper.NotificationHelper
import com.github.lib_base.helper.SoundHelper
import com.github.lib_base.helper.WindowHelper
import com.github.lib_base.helper.`fun`.AppHelper
import com.github.lib_base.helper.`fun`.CommonSettingHelper
import com.github.lib_base.helper.`fun`.ConsTellHelper
import com.github.lib_base.helper.`fun`.ContactHelper
import com.github.lib_base.utils.LogUtils
import com.github.lib_network.bean.JokeRandomData
import com.github.lib_network.http.HttpManager
import com.github.lib_voice.engine.VoiceEngineAnalyze
import com.github.lib_voice.impl.OnAsrResultListener
import com.github.lib_voice.impl.OnNluResultListener
import com.github.lib_voice.manager.VoiceManager
import com.github.lib_voice.tts.VoiceTTS
import com.github.lib_voice.words.WordsTools
import com.github.voiceaid.R
import com.github.voiceaid.adapter.ChatListAdapter
import com.github.voiceaid.data.ChatListData
import com.github.voiceaid.entity.AppConstants
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VoiceService : Service(), OnNluResultListener {

    override fun onCreate() {
        super.onCreate()
        initCoreVoiceService()
    }

    private lateinit var mFullWindowView: View
    private lateinit var mChatListView: RecyclerView
    private val mList = ArrayList<ChatListData>()
    private val mHandler = Handler()
    private lateinit var mChatAdapter: ChatListAdapter
    private lateinit var mLottieView: LottieAnimationView
    private lateinit var tvVoiceTips: TextView

    //初始化语音相关服务
    fun initCoreVoiceService() {

        WindowHelper.initHelper(this)
        mFullWindowView = WindowHelper.getView(R.layout.layout_window_item)
        mChatListView = mFullWindowView.findViewById<RecyclerView>(R.id.mChatListView)
        mLottieView = mFullWindowView.findViewById<LottieAnimationView>(R.id.mLottieView)
        tvVoiceTips = mFullWindowView.findViewById<TextView>(R.id.tvVoiceTips)
        mChatListView.layoutManager = LinearLayoutManager(this)
        mChatAdapter = ChatListAdapter(mList)
        mChatListView.adapter = mChatAdapter

        VoiceManager.initManager(this, object : OnAsrResultListener {
            override fun wakeUpReady() {
                LogUtils.i("唤醒准备就绪")
                addAiText("唤醒引擎准备就绪")
            }

            override fun asrStartSpeak() {
                LogUtils.i("开始说话")
            }

            override fun asrStopSpeak() {
                LogUtils.i("结束说话")
                hideWindow()
            }

            override fun wakeUpSuccess(result: JSONObject) {
                LogUtils.i("唤醒成功，结果为${result}")
                //唤醒词为“小度小度”才可以唤醒
                val errorCode = result.optInt("errorCode")
                //唤醒成功
                if (errorCode == 0) {
                    val word = result.optString("word")
                    if (word.equals("小度小度")) {
                        wakeUpFix()
                    }
                }
            }

            override fun updateUserText(text: String) {
                updateTips(text)
            }

            override fun asrResult(result: JSONObject) {
                LogUtils.i("====================RESULT======================")
                LogUtils.i("result：$result")

            }

            override fun nluResult(nlu: JSONObject) {
                LogUtils.i("====================NLU======================")
                LogUtils.i("nlu：$nlu")
                addMineText(nlu.optString("raw_text"))
                VoiceEngineAnalyze.analyzeNlu(nlu, this@VoiceService)
            }

            override fun voiceError(text: String) {
                hideWindow()
                LogUtils.i("唤醒错误，结果为${text}")

            }

        })

    }

    //唤醒之后的操作
    private fun wakeUpFix() {
        showWindow()
        updateTips("正在聆听...")
        SoundHelper.play(R.raw.record_start)
        //应答
        val wakeUpText = WordsTools.wakeUpWords()
        addAiText(
            wakeUpText,
            object : VoiceTTS.OnTTSResultListener {
                override fun ttsEnd() {
                    VoiceManager.startAsr()
                }
            })
        VoiceManager.startAsr()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /**
     * START_STICKY, 系统内存不足时如杀掉该服务，则在内存不紧张时重新启动服务
     * START_NOT_STICKY, 系统内存不足时如杀掉该服务，直到下次startService才启动
     * START_REDELIVER_INTENT,重新传递intent值
     * START_STICKY_COMPATIBILITY.START_STICKY兼容版本，但不能保证一定重启
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        bindNotification()
        return super.onStartCommand(intent, flags, startId)
    }

    //绑定通知栏
    private fun bindNotification() {
        startForeground(1000, NotificationHelper.bindVoiceService("正在运行"))
    }


    private fun showWindow() {
        LogUtils.i("===显示窗口===")
        mLottieView.playAnimation()
        WindowHelper.show(mFullWindowView)
    }

    private fun hideWindow() {
        LogUtils.i("===隐藏窗口===")
        mHandler.postDelayed(object : Runnable {
            override fun run() {
                WindowHelper.hide(mFullWindowView)
                mLottieView.pauseAnimation()
            }
        }, 2 * 1000)
    }

    override fun openApp(appName: String) {
        if (!TextUtils.isEmpty(appName)) {
            LogUtils.i("open APP $appName")
            val isOpen = AppHelper.launcherApp(appName)
            if (isOpen) {
                addAiText("正在为您打开$appName")
            } else {
                addAiText("很抱歉，无法为您打开$appName")
            }
        }
        hideWindow()
    }

    override fun unInstallApp(appName: String) {
        if (!TextUtils.isEmpty(appName)) {
            LogUtils.i("unInstall APP $appName")
            val unInstallApp = AppHelper.unInstallApp(appName)
            if (unInstallApp) {
                addAiText("正在为您卸载$appName")
            } else {
                addAiText("很抱歉，无法为您卸载$appName")
            }
        }
        hideWindow()
    }

    override fun otherApp(appName: String) {
        //全部跳转应用市场
        if (!TextUtils.isEmpty(appName)) {
            val isValid = AppHelper.launcherAppStore(appName)
            if (isValid) {
                addAiText("正在为您打开${appName}对应应用商店")
            } else {
                addAiText("打开${appName}对应应用商店失败")
            }
        }
        hideWindow()
    }

    override fun back() {
        CommonSettingHelper.back()
        hideWindow()
    }

    override fun home() {
        addAiText("正在为您执行返回操作")
        CommonSettingHelper.home()
        hideWindow()
    }

    override fun setVolumeUp() {
        addAiText("正在为您执行进入主页操作")
        CommonSettingHelper.setVolumeUp()
        hideWindow()
    }

    override fun setVolumeDown() {
        CommonSettingHelper.setVolumeDown()
        hideWindow()
    }

    override fun quit() {
    }

    override fun callPhoneForName(name: String) {
        val list = ContactHelper.mContactList.filter {
            it.phoneName == name
        }
        if(list.isNotEmpty()) {
            addAiText("正在为你拨打$name", object : VoiceTTS.OnTTSResultListener{
                override fun ttsEnd() {
                    ContactHelper.callPhone(list[0].phoneNumber)
                }
            })
        } else {
            addAiText("无法获取联系人")
        }
        hideWindow()
    }

    override fun callPhoneForNumber(phone: String) {
        addAiText("正在为你拨打$phone", object : VoiceTTS.OnTTSResultListener{
            override fun ttsEnd() {
                ContactHelper.callPhone(phone)            }
        })
        hideWindow()
    }

    //播放笑话
    override fun playJoke() {
        HttpManager.queryJoke(object : Callback<JokeRandomData>{
            override fun onResponse(
                call: Call<JokeRandomData>,
                response: Response<JokeRandomData>
            ) {
                if(response.isSuccessful) {
                    response.body()?.let {
                        if(it.code == 1){
                            //根据data随机拿一个content播放
                            var index = WordsTools.randomInt(it.data.size)
                            if( index <= it.data.size) {
                                val content = it.data[index].content
                                addAiText(content, object: VoiceTTS.OnTTSResultListener{
                                    override fun ttsEnd() {
                                        hideWindow()
                                    }

                                })
                            }
                        } else {
                            jokeError()
                        }
                    }
                } else {
                    jokeError()
                }
            }

            override fun onFailure(call: Call<JokeRandomData>, t: Throwable) {
                jokeError()
            }

        })
    }

    //笑话列表
    override fun jokeList() {
        addAiText("正在为你搜索笑话")
        ARouterHelper.startActivity(ARouterHelper.PATH_JOKE)
        hideWindow()
    }

    //星座时间
    override fun conTellTime(name: String) {
        LogUtils.i("conTellTime:$name")
        val text = ConsTellHelper.getConsTellTime(name)
        addAiText(text, object : VoiceTTS.OnTTSResultListener {
            override fun ttsEnd() {
                hideWindow()
            }
        })
    }

    //星座详情
    override fun conTellInfo(name: String) {
        LogUtils.i("conTellInfo:$name")
        addAiText(
            getString(R.string.text_voice_query_con_tell_info, name),
            object : VoiceTTS.OnTTSResultListener {
                override fun ttsEnd() {
                    hideWindow()
                }
            })
        ARouterHelper.startActivity(ARouterHelper.PATH_CONSTELLATION, "name", name)
    }


    override fun queryWeather(city: String) {

    }

    //周边搜索
    override fun nearByMap(poi: String) {
        LogUtils.i("nearByMap:$poi")
        addAiText(getString(R.string.text_voice_query_poi, poi))
        ARouterHelper.startActivity(ARouterHelper.PATH_MAP, "type", "poi", "keyword", poi)
        hideWindow()
    }

    //线路规划 + 导航
    override fun routeMap(address: String) {
        LogUtils.i("routeMap:$address")
        addAiText(getString(R.string.text_voice_query_navi, address))
        ARouterHelper.startActivity(ARouterHelper.PATH_MAP, "type", "route", "keyword", address)
        hideWindow()
    }

    //无法应答
    override fun nluError() {
        addAiText(WordsTools.noAnswerWords())
        hideWindow()
    }

    //添加我的文本
    private fun addMineText(text: String) {
        val bean = ChatListData(AppConstants.TYPE_MINE_TEXT)
        bean.text = text;
        baseAddItem(bean)
    }

    //添加Ai文本
    private fun addAiText(text: String) {
        val bean = ChatListData(AppConstants.TYPE_AI_TEXT)
        bean.text = text;
        baseAddItem(bean)
        VoiceManager.ttsStart(text)
    }

    //添加Ai文本
    private fun addAiText(text: String, mOnTTSResultListener: VoiceTTS.OnTTSResultListener) {
        val bean = ChatListData(AppConstants.TYPE_AI_TEXT)
        bean.text = text;
        baseAddItem(bean)
        VoiceManager.ttsStart(text, mOnTTSResultListener)
    }

    //添加信息的基础方法
    private fun baseAddItem(bean: ChatListData) {
        mList.add(bean)
        mChatAdapter.notifyItemInserted(mList.size - 1)
    }

    /**
     * 更新提示语
     */
    private fun updateTips(text: String) {
        tvVoiceTips.text = text
    }

    private fun jokeError() {
        hideWindow()
        addAiText("很抱歉，未搜索到笑话")
    }


}