package com.github.lib_voice.tts

import android.content.Context
import android.util.Log
import com.baidu.tts.client.SpeechError
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.SpeechSynthesizerListener
import com.baidu.tts.client.TtsMode
import com.github.lib_voice.manager.VoiceManager.VOICE_APP_ID
import com.github.lib_voice.manager.VoiceManager.VOICE_APP_KEY
import com.github.lib_voice.manager.VoiceManager.VOICE_APP_SECRET

/**
 * 百度AI语音合成
 *
 */
object VoiceTTS : SpeechSynthesizerListener {

    private var TAG = VoiceTTS::class.java.simpleName

    //TTS对象
    private lateinit var mSpeechSynthesizer: SpeechSynthesizer

    //接口对象
    private var mOnTTSResultListener: OnTTSResultListener? = null


    fun initTTS(mContext: Context) {
        //初始化对象
        mSpeechSynthesizer = SpeechSynthesizer.getInstance()
        //设置上下文
        mSpeechSynthesizer.setContext(mContext)
        //设置key
        mSpeechSynthesizer.setAppId(VOICE_APP_ID)
        mSpeechSynthesizer.setApiKey(VOICE_APP_KEY, VOICE_APP_SECRET)
        //设置监听
        mSpeechSynthesizer.setSpeechSynthesizerListener(this)

        //发声人
        setPeople(0.toString())
        //语速
        setVoiceSpeed(5.toString())
        //音量
        setVoiceVolume(5.toString())
        //初始化
        mSpeechSynthesizer.initTts(TtsMode.ONLINE)
        Log.i(TAG, "TTS init")
    }

    //设置发音人
    fun setPeople(people: String) {
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, people)
    }

    //设置语速
    fun setVoiceSpeed(speed: String) {
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, speed)
    }

    //设置音量
    fun setVoiceVolume(volume: String) {
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, volume)
    }

    override fun onSynthesizeStart(p0: String?) {
        Log.i(TAG, "合成开始")
    }

    override fun onSynthesizeDataArrived(p0: String?, p1: ByteArray?, p2: Int, p3: Int) {
        Log.i(TAG, "合成结束")
    }

    override fun onSynthesizeFinish(p0: String?) {
        Log.i(TAG, "合成结束")

    }

    override fun onSpeechStart(p0: String?) {
        Log.i(TAG, "播放开始")
    }

    override fun onSpeechProgressChanged(p0: String?, p1: Int) {
        Log.i(TAG, "合成中")
    }

    override fun onSpeechFinish(p0: String?) {
        Log.i(TAG, "播放结束")
        mOnTTSResultListener?.ttsEnd()
    }

    override fun onError(p0: String?, p1: SpeechError?) {
        Log.i(TAG, "合成出错${p0}${p1}")
    }

    //播放带回调,写成监听器为null则可进行重载效果
    fun start(text: String, mOnTTSResultListener: OnTTSResultListener?) {
        this.mOnTTSResultListener = mOnTTSResultListener
        mSpeechSynthesizer.speak(text)
    }

    //暂停
    fun pause() {
        mSpeechSynthesizer.pause()
    }

    //继续播放
    fun resume() {
        mSpeechSynthesizer.resume()
    }

    //停止播放
    fun stop() {
        mSpeechSynthesizer.stop()
    }

    //释放
    fun release() {
        mSpeechSynthesizer.release()
    }

    //播放结束事件 监听器
    interface OnTTSResultListener {
        //播放结束
        fun ttsEnd()
    }
}