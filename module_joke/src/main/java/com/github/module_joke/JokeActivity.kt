package com.github.module_joke

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.github.lib_base.base.BaseActivity
import com.github.lib_base.base.adapter.CommonAdapter
import com.github.lib_base.base.adapter.CommonViewHolder
import com.github.lib_base.cache.Sp
import com.github.lib_base.helper.ARouterHelper
import com.github.lib_base.utils.LogUtils
import com.github.lib_network.bean.JokeDataList
import com.github.lib_network.bean.ListResult
import com.github.lib_network.http.HttpManager
import com.github.lib_voice.manager.VoiceManager
import com.github.lib_voice.tts.VoiceTTS
import com.github.module_joke.databinding.ActivityJokeBinding
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Route(path = ARouterHelper.PATH_JOKE)
class JokeActivity : BaseActivity(), OnRefreshListener, OnRefreshLoadMoreListener {

    private lateinit var binding: ActivityJokeBinding
    //页码
    private var currentPage = 1

    //数据源
    private val mList = ArrayList<ListResult>()
    private lateinit var mJokeAdapter: CommonAdapter<ListResult>
    //当前播放的下标
    private var currentPlayIndex = -1


    override fun getLayoutView(): View {
        binding = ActivityJokeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun getTitleText(): String {
        return getString(com.github.lib_base.R.string.app_title_joke)
    }

    override fun initView() {
        initList()

        loadData(0)
    }

    override fun isShowBack(): Boolean {
        return true
    }

    //加载数据,0:下拉,1:上拉
    private fun loadData(orientation: Int) {
        HttpManager.queryJokeList(currentPage, object : Callback<JokeDataList> {
            override fun onFailure(call: Call<JokeDataList>, t: Throwable) {
                LogUtils.i("onFailure$t")
                if (orientation == 0) {
                    binding.refreshLayout.finishRefresh()
                } else if (orientation == 1) {
                    binding.refreshLayout.finishLoadMore()
                }
            }

            override fun onResponse(call: Call<JokeDataList>, response: Response<JokeDataList>) {
                LogUtils.i("onResponse")
                if (orientation == 0) {
                    binding.refreshLayout.finishRefresh()
                    //列表要清空
                    mList.clear()
                    response.body()?.data?.list?.let { mList.addAll(it) }
                    //适配器要全部刷新
                    mJokeAdapter.notifyDataSetChanged()
                } else if (orientation == 1) {
                    binding.refreshLayout.finishLoadMore()
                    //追加在尾部
                    response.body()?.data?.list?.let {
                        //上一次的最大值
                        val positionStart = mList.size
                        mList.addAll(it)
                        //局部刷新
                        mJokeAdapter.notifyItemRangeInserted(positionStart, it.size)
                    }
                }
            }
        })
    }

    //初始化列表
    private fun initList() {

        //刷新组件
        binding.refreshLayout.setRefreshHeader(ClassicsHeader(this))
        binding.refreshLayout.setRefreshFooter(ClassicsFooter(this))

        //监听
        binding.refreshLayout.setOnRefreshListener(this)
        binding.refreshLayout.setOnRefreshLoadMoreListener(this)

        binding.mJokeListView.layoutManager = LinearLayoutManager(this)
        mJokeAdapter = CommonAdapter(mList, object : CommonAdapter.OnBindDataListener<ListResult> {
            override fun onBindViewHolder(
                model: ListResult,
                viewHolder: CommonViewHolder,
                type: Int,
                position: Int
            ) {
                //内容
                viewHolder.setText(R.id.tvContent, model.content)

                //播放按钮
                val tvPlay = viewHolder.getView(R.id.tvPlay) as TextView
                //设置文本
                tvPlay.text =
                    if (currentPlayIndex == position) getString(R.string.app_media_pause) else getString(
                        R.string.app_media_play
                    )

                //点击事件
                tvPlay.setOnClickListener {
                    //说明点击了正在播放的下标
                    if (currentPlayIndex == position) {
                        VoiceManager.ttsPause()
                        currentPlayIndex = -1
                        tvPlay.text = getString(R.string.app_media_play)
                    } else {
                        val oldIndex = currentPlayIndex
                        currentPlayIndex = position
                        VoiceManager.ttsStop()
                        VoiceManager.ttsStart(model.content, object : VoiceTTS.OnTTSResultListener {
                            override fun ttsEnd() {
                                currentPlayIndex = -1
                                tvPlay.text = getString(R.string.app_media_play)
                            }
                        })
                        tvPlay.text = getString(R.string.app_media_pause)
                        //刷新旧的
                        mJokeAdapter.notifyItemChanged(oldIndex)
                    }
                }
            }

            override fun getLayoutId(type: Int): Int {
                return R.layout.layout_joke_list_item
            }
        })
        binding.mJokeListView.adapter = mJokeAdapter
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        currentPage = 1
        loadData(0)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        currentPage++
        loadData(1)
    }

    override fun onDestroy() {
        super.onDestroy()
        val isJoke = Sp.getBoolean("isJoke", true)
        if (!isJoke) {
            VoiceManager.ttsStop()
        }
    }

}