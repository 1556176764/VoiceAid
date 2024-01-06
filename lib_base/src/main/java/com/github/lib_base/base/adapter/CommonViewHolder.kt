package com.github.lib_base.base.adapter

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var mViews: SparseArray<View> = SparseArray()

    companion object {
        //Holder对象
        fun getViewHolder(parent: ViewGroup, layoutId: Int): CommonViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
            return CommonViewHolder(itemView)
        }
    }

    fun getView(viewId: Int): View {
        var view = mViews[viewId]
        if(view == null){
            view = itemView.findViewById(viewId)
            mViews.put(viewId, view)
        }
        return view!!
    }

    fun setText(viewId: Int, text: String) {
        (getView(viewId) as TextView).text = text
    }

    fun setImageResource(viewId: Int, resId: Int) {
        (getView(viewId) as ImageView).setImageResource(resId)
    }

}