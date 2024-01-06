package com.github.module_constellation.fragment

import android.view.View
import android.widget.Toast
import com.github.lib_base.base.BaseFragment
import com.github.lib_base.utils.LogUtils
import com.github.lib_network.bean.MonthData
import com.github.lib_network.http.HttpManager
import com.github.module_constellation.R
import com.github.module_constellation.databinding.FragmentMonthBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MonthFragment(val name: String) : BaseFragment() {

    private lateinit var binding: FragmentMonthBinding

    override fun getLayoutView(): View {
        binding = FragmentMonthBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun initView() {
        loadMonthData()
    }

    //加载月份数据
    private fun loadMonthData() {
        HttpManager.queryMonthConsTellInfo(name, object : Callback<MonthData> {
            override fun onFailure(call: Call<MonthData>, t: Throwable) {
                Toast.makeText(activity, getString(R.string.text_load_fail), Toast.LENGTH_LONG)
                    .show()
            }

            override fun onResponse(call: Call<MonthData>, response: Response<MonthData>) {
                val data = response.body()
                data?.let {
                    LogUtils.i("it:$it")

                    binding.tvName.text = it.name
                    binding.tvDate.text = it.date
                    binding.tvAll.text = it.all
                    binding.tvHappy.text = it.happyMagic
                    binding.tvHealth.text = it.health
                    binding.tvLove.text = it.love
                    binding.tvMoney.text = it.money
                    binding.tvMonth.text = getString(R.string.text_month_select, it.month)
                    binding.tvWork.text = it.work
                }
            }

        })
    }

}