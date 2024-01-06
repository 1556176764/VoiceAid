package com.github.lib_base.helper

import android.annotation.SuppressLint
import android.content.Context
import android.media.SoundPool

/**
 * 铃声播放
 */
object SoundHelper {

    private lateinit var mContext: Context
    private lateinit var mSoundPool: SoundPool

    fun init(mContext: Context) {
        this.mContext = mContext

        mSoundPool = SoundPool.Builder().setMaxStreams(1).build()
    }

    //播放
    fun play(resId: Int) {
        val poolId = mSoundPool.load(mContext, resId, 1)
        mSoundPool.setOnLoadCompleteListener(@SuppressLint("StaticFieldLeak")
        object : SoundPool.OnLoadCompleteListener {
            override fun onLoadComplete(soundPool: SoundPool?, sampleId: Int, status: Int) {
                if(status == 0) {
                    /**
                     * soundID – a soundID returned by the load() function
                     * leftVolume – left volume value (range = 0.0 to 1.0)
                     * rightVolume – right volume value (range = 0.0 to 1.0)
                     * priority – stream priority (0 = lowest priority)
                     * loop – loop mode (0 = no loop, -1 = loop forever)
                     * rate – playback rate (1.0 = normal playback, range 0.5 to 2.0)
                     */
                     mSoundPool.play(poolId, 1f, 1f, 1, 0, 1f)
                }
            }

        })
    }

}