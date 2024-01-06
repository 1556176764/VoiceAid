package com.github.module_voice_setting

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.SeekBar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.github.lib_base.base.BaseActivity
import com.github.lib_base.base.adapter.CommonAdapter
import com.github.lib_base.base.adapter.CommonViewHolder
import com.github.lib_base.helper.ARouterHelper
import com.github.lib_voice.manager.VoiceManager
import com.github.module_voice_setting.databinding.ActivityVoiceSettingBinding

@Route(path = ARouterHelper.PATH_VOICE_SETTING)
class VoiceSettingActivity : BaseActivity() {
    private lateinit var binding: ActivityVoiceSettingBinding
    private var mTtsPeopleIndex: Array<String>? = null
    private val mList: ArrayList<String> = ArrayList()

    override fun getLayoutView(): View {
        binding = ActivityVoiceSettingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun getTitleText(): String {
        return getString(com.github.lib_base.R.string.app_title_voice_setting)
    }

    override fun initView() {
        //默认配置
        binding.barVoiceSpeed.progress = 5
        binding.barVoiceVolume.progress = 5

        //最大值
        binding.barVoiceSpeed.max = 15
        binding.barVoiceVolume.max = 15

        initData()

        initListener()

        initPeopleView()

        binding.btnTest.setOnClickListener {
            VoiceManager.ttsStart("大家好，我是小爱")
        }
    }

    //初始化数据
    private fun initData() {
        val mTtsPeople = resources.getStringArray(R.array.TTSPeople)

        mTtsPeopleIndex = resources.getStringArray(R.array.TTSPeopleIndex)

        mTtsPeople.forEach { mList.add(it) }
    }

    //初始化发音人列表
    private fun initPeopleView() {
        binding.rvVoicePeople.layoutManager = LinearLayoutManager(this)
        binding.rvVoicePeople.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.rvVoicePeople.adapter =
            CommonAdapter(mList, object : CommonAdapter.OnBindDataListener<String> {
                override fun onBindViewHolder(
                    model: String,
                    viewHolder: CommonViewHolder,
                    type: Int,
                    position: Int
                ) {
                    viewHolder.setText(R.id.mTvPeopleContent, model)
                    viewHolder.itemView.setOnClickListener {
                        mTtsPeopleIndex?.let {
                            VoiceManager.setPeople(it[position])
                        }

                    }
                }

                override fun getLayoutId(type: Int): Int {
                    return R.layout.layout_tts_people_list
                }

            })

    }


    private fun initListener() {
        //语速的监听
        binding.barVoiceSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.barVoiceSpeed.progress = progress
                VoiceManager.setVoiceSpeed(progress.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        //音量的监听
        binding.barVoiceVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.barVoiceVolume.progress = progress
                VoiceManager.setVoiceVolume(progress.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    override fun isShowBack(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_voice_setting)
    }
}