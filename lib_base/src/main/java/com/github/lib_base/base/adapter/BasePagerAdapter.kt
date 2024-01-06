package com.github.lib_base.base.adapter

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

/**
 * ViewPager适配器
 */
class BasePagerAdapter(private val mList: List<View>): PagerAdapter() {
    override fun getCount(): Int {
        return mList.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        (container as ViewPager).addView(mList[position])
        return mList[position]
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(mList[position])
    }
}