package com.github.module_map

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.core.PoiInfo
import com.baidu.mapapi.search.poi.PoiResult
import com.github.lib_base.base.BaseActivity
import com.github.lib_base.base.adapter.CommonAdapter
import com.github.lib_base.base.adapter.CommonViewHolder
import com.github.lib_base.helper.ARouterHelper
import com.github.lib_base.map.MapManager
import com.github.lib_base.utils.LogUtils
import com.github.lib_voice.manager.VoiceManager
import com.github.module_map.databinding.ActivityMapBinding

@Route(path = ARouterHelper.PATH_MAP)
class MapActivity : BaseActivity() {

    private lateinit var binding: ActivityMapBinding

    private val mHandler by lazy { Handler() }

    override fun getLayoutView(): View {
        binding = ActivityMapBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun getTitleText(): String {
        return getString(com.github.lib_base.R.string.app_title_map)
    }

    override fun isShowBack(): Boolean {
        return true
    }

    override fun initView() {
        MapManager.bindMapView(binding.mMapView)
        startLocation()
        initPoiList()
        //监听输入事件
        binding.etSearchPoi.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (it.isNotEmpty()) {
                        binding.ryPoiView.visibility = View.VISIBLE

                        if (mList.size > 0) {
                            mList.clear()
                            mPoiAdapter.notifyDataSetChanged()
                        }

                        poiSearch(it.toString())
                    } else {
                        binding.ryPoiView.visibility = View.GONE
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    private val mList = ArrayList<PoiInfo>()
    private lateinit var mPoiAdapter: CommonAdapter<PoiInfo>

    //初始化列表
    private fun initPoiList() {
        binding.ryPoiView.layoutManager = LinearLayoutManager(this)
        binding.ryPoiView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mPoiAdapter = CommonAdapter(mList, object : CommonAdapter.OnBindDataListener<PoiInfo> {
            override fun getLayoutId(type: Int): Int {
                return R.layout.layout_poi_item
            }

            override fun onBindViewHolder(
                model: PoiInfo,
                viewHolder: CommonViewHolder,
                type: Int,
                position: Int
            ) {
                viewHolder.setText(R.id.tvTitle, model.name)
                viewHolder.setText(R.id.tvContent, model.address)

                viewHolder.getView(R.id.ivStartNavi).setOnClickListener {
                    MapManager.setCenterMap(model.location.latitude,model.location.longitude)
                    MapManager.zoomMap(MapManager.MAX_ZOOM)
                }
            }

        })
        binding.ryPoiView.adapter = mPoiAdapter
    }

    //搜索
    private fun poiSearch(poi: String) {
        MapManager.poiSearch(poi, "", 3, object : MapManager.OnPoiResultListener {
            override fun result(result: PoiResult) {
                if (mList.size > 0) {
                    mList.clear()
                }
                mList.addAll(result.allPoi)
                mPoiAdapter.notifyDataSetChanged()
            }

        })
    }



    //开启定位
    private fun startLocation() {
        //获取关键字
        val keyword = intent.getStringExtra("keyword")
        when (intent.getStringExtra("type")) {
            "poi" -> searchNearByPoi(keyword!!)
            "route" -> route(keyword!!)
            else -> showMyLocation() //直接点进来的话，显示自身的位置
        }
    }

    //路线规划
    private fun route(address: String) {
        LogUtils.i("开始路线规划")
        MapManager.startLocationWalkingSearch(address, object : MapManager.OnNaviResultListener {
            override fun onStartNavi(
                startLa: Double,
                startLo: Double,
                endCity: String,
                address: String
            ) {
                //5S
                VoiceManager.ttsStart("开始导航")
                mHandler.postDelayed({
                    MapManager.startCode(
                        endCity,
                        address,
                        object : MapManager.OnCodeResultListener {
                            override fun result(codeLa: Double, codeLo: Double) {
                                LogUtils.i("编码成功")
                                MapManager.initNaviEngine(
                                    this@MapActivity,
                                    startLa, startLo,
                                    codeLa, codeLo
                                )
                            }

                        })
                }, 5 * 1000)
            }

        })
    }

    //显示自身的位置
    private fun showMyLocation() {
        MapManager.setLocationSwitch(true, object : MapManager.OnLocationResultListener {
            override fun result(
                la: Double,
                lo: Double,
                city: String,
                address: String,
                desc: String
            ) {
                //设置中心点
                MapManager.setCenterMap(la, lo)
                LogUtils.i("定位成功：" + address + "desc:" + desc)
                //添加个性化覆盖物
                MapManager.addMarker(LatLng(la, lo))
            }

            override fun fail() {
                LogUtils.i("定位失败")
            }

        })
    }

    //查找周边POI
    private fun searchNearByPoi(keyword: String) {
        LogUtils.i("searchNearByPoi$keyword")
        MapManager.setLocationSwitch(true, object : MapManager.OnLocationResultListener {
            override fun result(
                la: Double,
                lo: Double,
                city: String,
                address: String,
                desc: String
            ) {
                //设置中心点
                MapManager.setCenterMap(la, lo)
                MapManager.searchNearby(
                    keyword,
                    la,
                    lo,
                    10,
                    object : MapManager.OnPoiResultListener {
                        override fun result(result: PoiResult) {
                            //在UI上绘制视图
                        }
                    })
                LogUtils.i("定位成功：" + address + "desc:" + desc)
            }

            override fun fail() {
                LogUtils.i("定位失败")
            }

        })
    }



    //========================生命周期==============================
    override fun onResume() {
        super.onResume()
        MapManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        MapManager.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        MapManager.onDestroy()
    }
}