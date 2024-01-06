package com.github.module_constellation

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.github.lib_base.base.BaseActivity
import com.github.lib_base.cache.Sp
import com.github.lib_base.helper.ARouterHelper
import com.github.module_constellation.databinding.ActivityConstellationBinding
import com.github.module_constellation.fragment.MonthFragment
import com.github.module_constellation.fragment.TodayFragment
import com.github.module_constellation.fragment.WeekFragment
import com.github.module_constellation.fragment.YearFragment

@Route(path = ARouterHelper.PATH_CONSTELLATION)
class ConstellationActivity : BaseActivity() {

    private lateinit var binding: ActivityConstellationBinding

    private lateinit var mTodayFragment: TodayFragment
    private lateinit var mTomorrowFragment: TodayFragment
    private lateinit var mWeekFragment: WeekFragment
    private lateinit var mMonthFragment: MonthFragment
    private lateinit var mYearFragment: YearFragment

    private val mListFragment = ArrayList<Fragment>()

    override fun getLayoutView(): View {
        binding = ActivityConstellationBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun getTitleText(): String {
        return getString(com.github.lib_base.R.string.app_title_constellation)
    }

    override fun isShowBack(): Boolean {
        return true
    }

    override fun initView() {
        val name = intent.getStringExtra("name")
        if (!TextUtils.isEmpty(name)) {
            //语音进来的
            if (name != null) {
                initFragment(name)
            }
        } else {
            //主页进来
            //主页进来的,读取历史
            val consTellName = Sp.getString("consTell")
            consTellName?.let {
                if (!TextUtils.isEmpty(it)) {
                    initFragment(it)
                } else {
                    initFragment(getString(R.string.text_def_con_tell))
                }
            }
        }

        //View控制
        binding.mTvToday.setOnClickListener {
            checkTab(true, 0)
        }
        binding.mTvTomorrow.setOnClickListener {
            checkTab(true, 1)
        }
        binding.mTvWeek.setOnClickListener {
            checkTab(true, 2)
        }
        binding.mTvMonth.setOnClickListener {
            checkTab(true, 3)
        }
        binding.mTvYear.setOnClickListener {
            checkTab(true, 4)
        }
    }

    //viewPage + fragment来滑动切换
    fun initFragment(name: String) {
        Sp.putString("consTell", name)

        //设置标题
        supportActionBar?.title = name

        mTodayFragment = TodayFragment(true, name)
        mTomorrowFragment = TodayFragment(false, name)
        mWeekFragment = WeekFragment(name)
        mMonthFragment = MonthFragment(name)
        mYearFragment = YearFragment(name)

        mListFragment.add(mTodayFragment)
        mListFragment.add(mTomorrowFragment)
        mListFragment.add(mWeekFragment)
        mListFragment.add(mMonthFragment)
        mListFragment.add(mYearFragment)

        //初始化页面
        initViewPager()
    }

    private fun initViewPager() {
        binding.mViewPager.adapter = PageFragmentAdapter(supportFragmentManager)
        binding.mViewPager.offscreenPageLimit = mListFragment.size
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
                checkTab(false, position)
            }

        })

        //等待全部初始化之后才做UI控制操作
        checkTab(false, 0)
    }

    //适配器,需要外部调用因此是inline
    inner class PageFragmentAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int {
            return mListFragment.size
        }

        override fun getItem(position: Int): Fragment {
            return mListFragment[position]
        }

    }



    //切换选项卡
    private fun checkTab(isClick: Boolean, index: Int) {

        if (isClick) {
            binding.mViewPager.currentItem = index
        }

        binding.mTvToday.setTextColor(if (index == 0) Color.RED else Color.BLACK)
        binding.mTvTomorrow.setTextColor(if (index == 1) Color.RED else Color.BLACK)
        binding.mTvWeek.setTextColor(if (index == 2) Color.RED else Color.BLACK)
        binding.mTvMonth.setTextColor(if (index == 3) Color.RED else Color.BLACK)
        binding.mTvYear.setTextColor(if (index == 4) Color.RED else Color.BLACK)
    }
}