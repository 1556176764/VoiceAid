package com.github.module_constellation.fragment

import android.view.View
import android.widget.Toast
import com.github.lib_base.base.BaseFragment
import com.github.lib_base.utils.LogUtils
import com.github.lib_network.bean.TodayData
import com.github.lib_network.http.HttpManager
import com.github.module_constellation.R
import com.github.module_constellation.databinding.FragmentTodayBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TodayFragment(val isToday: Boolean, val name: String): BaseFragment(), Callback<TodayData> {

    private lateinit var binding: FragmentTodayBinding

    override fun getLayoutView(): View {
        binding = FragmentTodayBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun initView() {
        if (isToday) {
            loadToday()
        } else {
            loadTomorrow()
        }
    }

    //加载今天的数据
    private fun loadToday() {
        HttpManager.queryTodayConsTellInfo(name, this)
    }

    //加载明天的数据
    private fun loadTomorrow() {
        HttpManager.queryTomorrowConsTellInfo(name, this)
    }

    override fun onFailure(call: Call<TodayData>, t: Throwable) {
        Toast.makeText(activity, "加载失败", Toast.LENGTH_LONG).show()
    }

    override fun onResponse(call: Call<TodayData>, response: Response<TodayData>) {
        val data = response.body()
        data?.let {

            LogUtils.i("it:$it")

            binding.tvName.text = getString(R.string.text_con_tell_name, it.name)
            binding.tvTime.text = getString(R.string.text_con_tell_time, it.datetime)
            binding.tvNumber.text = getString(R.string.text_con_tell_num, it.number)
            binding.tvFriend.text = getString(R.string.text_con_tell_pair, it.QFriend)
            binding.tvColor.text = getString(R.string.text_con_tell_color, it.color)
            binding.tvSummary.text = getString(R.string.text_con_tell_desc, it.summary)

            binding.pbAll.progress = it.all
            binding.pbHealth.progress = it.health
            binding.pbLove.progress = it.love
            binding.pbMoney.progress = it.money
            binding.pbWork.progress = it.work
        }
    }
}