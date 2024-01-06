package com.github.voiceaid

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.github.lib_base.base.BaseActivity
import com.github.lib_base.base.adapter.BasePagerAdapter
import com.github.lib_base.helper.ARouterHelper
import com.github.lib_base.helper.`fun`.ContactHelper
import com.github.lib_network.http.HttpManager
import com.github.voiceaid.data.MainListData
import com.github.voiceaid.databinding.ActivityMainBinding
import com.github.voiceaid.service.VoiceService
import com.yanzhenjie.permission.Action
import com.zhy.magicviewpager.transformer.ScaleInTransformer


class MainActivity : BaseActivity() {

    private val mList = ArrayList<MainListData>()
    private val mListView = ArrayList<View>()

    //后续应该根据使用到的场景是让用户同意权限
    private val permission = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.VIBRATE,
        Manifest.permission.CAMERA
    )


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun getLayoutView(): View {
        binding = ActivityMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun getTitleText(): String {
        return getString(R.string.app_name)
    }

    override fun isShowBack(): Boolean {
        return false
    }


    override fun initView() {
        //动态权限
        if(checkPermission(permission)) {
            linkService()
        }else {
            requestPermission(permission, object: Action<List<String>> {
                override fun onAction(data: List<String>?) {
                    linkService()
                }
            })
        }
        //窗口权限
        if(!checkWindowPermissions()) {
            requestWindowPermission(packageName)
        }
        initPageData()
        initPageView()
        HttpManager.queryWeather("beijing")

    }

    //初始化viewpager
    private fun initPageView() {
        binding.mViewPager.pageMargin = 20
        binding.mViewPager.offscreenPageLimit = mList.size
        binding.mViewPager.adapter = BasePagerAdapter(mListView)
        binding.mViewPager.setPageTransformer(true, ScaleInTransformer())
    }

    //初始化数据
    private fun initPageData() {
        val title = resources.getStringArray(com.github.lib_base.R.array.MainTitleArray)
        val color = resources.getIntArray(R.array.MainColorArray)
        val icon = resources.obtainTypedArray(R.array.MainIconArray)

        var position = 0
        val windowHeight = windowManager.defaultDisplay.height
        for ((index, value) in title.withIndex()) {
            mList.add(MainListData(value, icon.getResourceId(index, 0), color[index]))
            mList[position]?.let {
                val view = View.inflate(this, R.layout.layout_main_list, null)

                val mCvMainView = view.findViewById<CardView>(R.id.mCvMainView)
                val mIvMainIcon = view.findViewById<ImageView>(R.id.mIvMainIcon)
                val mTvMainText = view.findViewById<TextView>(R.id.mTvMainText)

                mCvMainView.setBackgroundColor(it.color)
                mIvMainIcon.setImageResource(it.icon)
                mTvMainText.text = it.title
                mCvMainView.layoutParams?.let { lp ->
                    lp.height = windowHeight / 5 * 3
                }
                //为每一个item设置点击事件
                view.setOnClickListener { _ ->
                    when (it.icon) {
                        R.drawable.img_main_weather -> ARouterHelper.startActivity(ARouterHelper.PATH_WEATHER)
                        R.drawable.img_mian_contell -> ARouterHelper.startActivity(ARouterHelper.PATH_CONSTELLATION)
                        R.drawable.img_main_joke_icon -> ARouterHelper.startActivity(ARouterHelper.PATH_JOKE)
                        R.drawable.img_main_map_icon -> ARouterHelper.startActivity(ARouterHelper.PATH_MAP)
                        R.drawable.img_main_app_manager -> ARouterHelper.startActivity(ARouterHelper.PATH_APP_MANAGER)
                        R.drawable.img_main_voice_setting -> ARouterHelper.startActivity(
                            ARouterHelper.PATH_VOICE_SETTING
                        )
                        R.drawable.img_main_system_setting -> ARouterHelper.startActivity(
                            ARouterHelper.PATH_SETTING
                        )
                        R.drawable.img_main_developer -> ARouterHelper.startActivity(ARouterHelper.PATH_DEVELOPER)
                    }
                }
                mListView.add(view)
                position++
            }
        }
    }

    //连接初始化服务
    private fun linkService() {

        //读取联系人
        ContactHelper.initHelper(this)

        startService(Intent(this, VoiceService::class.java))
    }

}