package com.github.module_app_manager

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.github.lib_base.base.BaseActivity
import com.github.lib_base.base.adapter.BasePagerAdapter
import com.github.lib_base.helper.ARouterHelper
import com.github.lib_base.helper.`fun`.AppHelper
import com.github.lib_base.utils.LogUtils
import com.github.moudle_app_manager.databinding.ActivityAppManagerBinding

@Route(path = ARouterHelper.PATH_APP_MANAGER)
class AppManagerActivity : BaseActivity() {

    private val waitApp = 1000
    private lateinit var binding: ActivityAppManagerBinding

    @SuppressLint("HandlerLeak")
    private val mHandler = object: Handler() {
        override fun handleMessage(msg: Message) {
            if(msg.what == waitApp) {
                waitAppHandler()
            }
        }
    }

    override fun getLayoutView(): View {
        binding = ActivityAppManagerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun getTitleText(): String {
        return getString(com.github.lib_base.R.string.app_title_app_manager)
    }

    override fun isShowBack(): Boolean {
        return true
    }

    override fun initView() {
        //显示加载中
        binding.llLoading.visibility = View.VISIBLE
        //说明初始化APPView成功
        waitAppHandler()
    }

    //等待应用加载完成
    private fun waitAppHandler() {
        LogUtils.i("等待App列表刷新...")
        if (AppHelper.mAllViewList.size > 0) {
            initViewPager()
        } else {
            mHandler.sendEmptyMessageDelayed(waitApp, 1000)
        }
    }

    //初始化页面
    private fun initViewPager() {
        //确认页的数量
        binding.mViewPager.offscreenPageLimit = AppHelper.getPageSize()
        binding.mViewPager.adapter = BasePagerAdapter(AppHelper.mAllViewList)
        binding.llLoading.visibility = View.GONE
        binding.llContent.visibility = View.VISIBLE

        binding.mPointLayoutView.setPointSize(AppHelper.getPageSize())

        binding.mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                binding.mPointLayoutView.setCheck(position)
            }
        })
    }
}