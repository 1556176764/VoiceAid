package com.github.module_weather

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.github.lib_base.base.BaseActivity
import com.github.lib_base.helper.ARouterHelper
import com.github.module_weather.databinding.ActivityWeatherBinding

@Route(path = ARouterHelper.PATH_WEATHER)
class WeatherActivity : BaseActivity() {

    private lateinit var binding: ActivityWeatherBinding

    override fun getLayoutView(): View {
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun getTitleText(): String {
        return getString(com.github.lib_base.R.string.app_title_weather)
    }

    override fun initView() {

    }

    override fun isShowBack(): Boolean {
        return true
    }
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_weather)
    }
}