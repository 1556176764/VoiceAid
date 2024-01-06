package com.github.lib_voice.manager

import android.content.Context
import android.util.Log
import com.baidu.speech.EventListener
import com.baidu.speech.asr.SpeechConstant
import com.github.lib_voice.asr.VoiceAsr
import com.github.lib_voice.impl.OnAsrResultListener
import com.github.lib_voice.tts.VoiceTTS
import com.github.lib_voice.wakeup.VoiceWakeUp
import org.json.JSONObject

object VoiceManager : EventListener {

    private var TAG = VoiceManager::class.java.simpleName

    //语音Key
    const val VOICE_APP_ID = "45459198"
    const val VOICE_APP_KEY = "sISNEzOSziHtfp7yasskixEp"
    const val VOICE_APP_SECRET = "GXOYc2Tv3xoEFPNdc8j4D2kOKN0OfAKK"

    private lateinit var mOnAsrResultListener: OnAsrResultListener

    fun initManager(mContext: Context, mOnAsrResultListener: OnAsrResultListener) {
        this.mOnAsrResultListener = mOnAsrResultListener
        VoiceTTS.initTTS(mContext)
        VoiceWakeUp.initWakeUp(mContext, this)
        VoiceAsr.initAsr(mContext, this)
    }

    //设置发音人
    fun setPeople(people: String) {
        VoiceTTS.setPeople(people)
    }

    //设置语速
    fun setVoiceSpeed(speed: String) {
        VoiceTTS.setVoiceSpeed(speed)
    }

    //设置音量
    fun setVoiceVolume(volume: String) {
        VoiceTTS.setVoiceVolume(volume)
    }

    //播放
    fun ttsStart(text: String) {
        Log.d(TAG, "开始TTS：$text")
        VoiceTTS.start(text, null)
    }

    //播放
    fun ttsStart(text: String, mOnTTSResultListener: VoiceTTS.OnTTSResultListener) {
        VoiceTTS.start(text, mOnTTSResultListener)
    }

    //暂停
    fun ttsPause() {
        VoiceTTS.pause()
    }

    //继续播放
    fun ttsResume() {
        VoiceTTS.resume()
    }

    //停止播放
    fun ttsStop() {
        VoiceTTS.stop()
    }

    //释放
    fun ttsRelease() {
        VoiceTTS.release()
    }

    //启动唤醒
    fun startWakeUp() {
        Log.d(TAG, "启动唤醒")
        VoiceWakeUp.startWakeUp()
    }

    //停止唤醒
    fun stopWakeUp() {
        VoiceWakeUp.stopWakeUp()
    }

    //------------------------ASR START------------------------
    //开始识别
    fun startAsr() {
        VoiceAsr.startAsr()
    }

    //停止识别
    fun stopAsr() {
        VoiceAsr.stopAsr()
    }

    //取消识别
    fun cancelAsr() {
        VoiceAsr.cancelAsr()
    }

    //销毁
    fun releaseAsr() {
        VoiceAsr.releaseAsr(this)
    }

    //语音唤醒和识别处理逻辑
    override fun onEvent(
        name: String?,
        params: String?,
        byte: ByteArray?,
        offset: Int,
        length: Int
    ) {
        //Log.d(TAG, String.format("event: name=%s, params=%s", name, params))

        when (name) {
            SpeechConstant.CALLBACK_EVENT_WAKEUP_READY -> mOnAsrResultListener.wakeUpReady()
            SpeechConstant.CALLBACK_EVENT_ASR_BEGIN -> mOnAsrResultListener.asrStartSpeak()
            SpeechConstant.CALLBACK_EVENT_ASR_END -> mOnAsrResultListener.asrStopSpeak()
        }
        //去除脏数据
        if(params == null) {
            return
        }
        val json = JSONObject(params)
        Log.i("Test", "json$name$json")
        when(name) {
            SpeechConstant.CALLBACK_EVENT_WAKEUP_SUCCESS -> mOnAsrResultListener.wakeUpSuccess(json)
            SpeechConstant.CALLBACK_EVENT_WAKEUP_ERROR -> mOnAsrResultListener.voiceError("唤醒失败")
            SpeechConstant.CALLBACK_EVENT_ASR_FINISH -> mOnAsrResultListener.asrResult(json)
            SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL -> {
                mOnAsrResultListener.updateUserText(json.optString("best_result"))
                byte?.let {
                    val nlu = JSONObject(String(byte, offset, length))
                    mOnAsrResultListener.asrResult(nlu)
                }
            }
        }
    }
}