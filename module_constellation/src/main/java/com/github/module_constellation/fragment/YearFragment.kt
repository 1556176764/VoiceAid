package com.github.module_constellation.fragment

import android.view.View
import android.widget.Toast
import com.github.lib_base.base.BaseFragment
import com.github.lib_base.utils.LogUtils
import com.github.lib_network.bean.YearData
import com.github.lib_network.http.HttpManager
import com.github.module_constellation.R
import com.github.module_constellation.databinding.FragmentYearBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class YearFragment(val name: String) : BaseFragment() {

    private lateinit var binding: FragmentYearBinding
    override fun getLayoutView(): View {
        binding = FragmentYearBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        loadYearData()
    }

    //加载年份数据
    private fun loadYearData() {
        HttpManager.queryYearConsTellInfo(name, object : Callback<YearData> {
            override fun onFailure(call: Call<YearData>, t: Throwable) {
                Toast.makeText(activity, getString(R.string.text_load_fail), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<YearData>, response: Response<YearData>) {
                val data = response.body()
                data?.let {
                    LogUtils.i("it:$it")

                    binding.tvName.text = it.name
                    binding.tvDate.text = it.date

                    binding.tvInfo.text = it.mima.info
                    binding.tvInfoText.text = it.mima.text[0]

                    binding.tvCareer.text = it.career[0]
                    binding.tvLove.text = it.love[0]
                    binding.tvFinance.text = it.finance[0]
                }
            }

        })
    }
}