package com.github.module_developer

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.github.lib_base.base.BaseActivity
import com.github.lib_base.base.adapter.CommonAdapter
import com.github.lib_base.base.adapter.CommonViewHolder
import com.github.lib_base.helper.ARouterHelper
import com.github.lib_voice.manager.VoiceManager
import com.github.lib_voice.tts.VoiceTTS
import com.github.module_developer.data.DeveloperListData
import com.github.module_developer.databinding.ActivityDeveloperBinding

@Route(path = ARouterHelper.PATH_DEVELOPER)
class DeveloperActivity : BaseActivity() {

    //标题
    private val mTypeTitle = 0

    //内容
    private val mTypeContent = 1

    private val mList = ArrayList<DeveloperListData>()

    private lateinit var binding: ActivityDeveloperBinding

    override fun getLayoutView(): View {
        binding = ActivityDeveloperBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun getTitleText(): String {
        return getString(com.github.lib_base.R.string.app_title_developer)
    }

    override fun initView() {
        initData()
        initListView()
    }

    override fun isShowBack(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_developer)
    }

    //初始化列表数据
    private fun initListView() {
        //布局管理器
        binding.rvDeveloperView.layoutManager = LinearLayoutManager(this)
        //分割线
        binding.rvDeveloperView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        //适配器
        binding.rvDeveloperView.adapter = CommonAdapter(mList, object : CommonAdapter.OnMoreBindDataListener<DeveloperListData>{
            override fun onBindViewHolder(
                model: DeveloperListData,
                viewHolder: CommonViewHolder,
                type: Int,
                position: Int
            ) {
                when(model.type) {
                    mTypeTitle-> {
                        viewHolder.setText(R.id.mTvDeveloperTitle, model.text)
                    }
                    mTypeContent-> {
                        viewHolder.setText(R.id.mTvDeveloperContent, "${position}.${model.text}")
                        viewHolder.itemView.setOnClickListener {
                            itemClickFun(position)
                        }
                    }
                }
            }

            override fun getItemType(position: Int): Int {
                return mList[position].type
            }

            override fun getLayoutId(type: Int): Int {
                return if (type == mTypeTitle) {
                    R.layout.layout_developer_title
                }else {
                    R.layout.layout_developer_content
                }
            }

        })
    }

    private fun initData() {
        val dataArray = resources.getStringArray(com.github.lib_base.R.array.DeveloperListArray)
        dataArray.forEach {
            if (it.contains("[")) {
                //去掉左右中括号，并加入
                addItemData(mTypeTitle, it.replace("[", "").replace("]", ""))
            } else {
                addItemData(mTypeContent, it)
            }
        }
    }

    //添加列表数据
    private fun addItemData(type: Int, text: String) {
        mList.add(DeveloperListData(type, text))
    }

    //点击事件
    private fun itemClickFun(position: Int) {
        when(position){
            1 -> ARouterHelper.startActivity(ARouterHelper.PATH_APP_MANAGER)
            2 -> ARouterHelper.startActivity(ARouterHelper.PATH_CONSTELLATION)
            3 -> ARouterHelper.startActivity(ARouterHelper.PATH_JOKE)
            4 -> ARouterHelper.startActivity(ARouterHelper.PATH_MAP)
            5 -> ARouterHelper.startActivity(ARouterHelper.PATH_SETTING)
            6 -> ARouterHelper.startActivity(ARouterHelper.PATH_VOICE_SETTING)
            7 -> ARouterHelper.startActivity(ARouterHelper.PATH_WEATHER)

            9 -> {
                //println("点击")
                VoiceManager.startAsr()
            }
            10 -> {
                VoiceManager.stopAsr()
            }
            11 -> {
                VoiceManager.cancelAsr()
            }
            12 -> {
                VoiceManager.releaseAsr()
            }

            14 -> {
                VoiceManager.startWakeUp()
            }
            15 -> {
                VoiceManager.stopWakeUp()
            }

            20 -> {
                VoiceManager.ttsStart("你好,我是小度")
                Log.i(VoiceTTS::class.java.simpleName, position.toString()+"start")
            }
            21 -> {
                VoiceManager.ttsPause()
                Log.i(VoiceTTS::class.java.simpleName, position.toString()+"pause")
//                VoiceManager.start("你好,我是小爱同学，很高兴认识你", object : VoiceTTS.OnTTSResultListener{
//                    override fun ttsEnd() {
//                        Log.i("test", "TTS End")
//                    }
//                })
            }
            22 -> {
                VoiceManager.ttsResume()
                Log.i(VoiceTTS::class.java.simpleName, position.toString()+"resume")
            }
            23 -> {
                VoiceManager.ttsStop()
                Log.i(VoiceTTS::class.java.simpleName, position.toString()+"stop")
            }
            24 -> {
                VoiceManager.ttsRelease()
                //Log.i(VoiceTTS::class.java.simpleName, position.toString())
            }
        }
    }

}