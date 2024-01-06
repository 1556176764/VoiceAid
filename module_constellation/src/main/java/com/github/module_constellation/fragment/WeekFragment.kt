package com.github.module_constellation.fragment

import android.view.View
import android.widget.Toast
import com.github.lib_base.base.BaseFragment
import com.github.lib_base.utils.LogUtils
import com.github.lib_network.bean.WeekData
import com.github.lib_network.http.HttpManager
import com.github.module_constellation.R
import com.github.module_constellation.databinding.FragmentTodayBinding
import com.github.module_constellation.databinding.FragmentWeekBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeekFragment(val name: String) : BaseFragment() {

    private lateinit var binding: FragmentWeekBinding

    override fun getLayoutView(): View {
        binding = FragmentWeekBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        loadWeekData()
    }

    private fun loadWeekData() {
        HttpManager.queryWeekConsTellInfo(name, object : Callback<WeekData> {
            override fun onFailure(call: Call<WeekData>, t: Throwable) {
                Toast.makeText(activity, getString(R.string.text_load_fail), Toast.LENGTH_LONG)
                    .show()
            }

            override fun onResponse(call: Call<WeekData>, response: Response<WeekData>) {
                val data = response.body()
                data?.let {
                    LogUtils.i("it:$it")
                    binding.tvName.text = it.name
                    binding.tvData.text = it.date
                    binding.tvWeekth.text = getString(R.string.text_week_select, it.weekth)
                    binding.tvHealth.text = it.health
                    binding.tvJob.text = it.job
                    binding.tvLove.text = it.love
                    binding.tvMoney.text = it.money
                    binding.tvWork.text = it.work
                }
            }

        })
    }
}