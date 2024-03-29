package com.github.voiceaid.adapter

import com.github.lib_base.base.adapter.CommonAdapter
import com.github.lib_base.base.adapter.CommonViewHolder
import com.github.voiceaid.R
import com.github.voiceaid.data.ChatListData
import com.github.voiceaid.entity.AppConstants

/**
 * 对话列表适配器
 */
class ChatListAdapter(
    mList: List<ChatListData>,
) : CommonAdapter<ChatListData>(mList, object : OnMoreBindDataListener<ChatListData> {


    override fun onBindViewHolder(
        model: ChatListData,
        viewHolder: CommonViewHolder,
        type: Int,
        position: Int
    ) {
        when(type) {
            AppConstants.TYPE_MINE_TEXT -> viewHolder.setText(R.id.tv_mine_text, model.text)
            AppConstants.TYPE_AI_TEXT -> viewHolder.setText(R.id.tv_ai_text, model.text)
            AppConstants.TYPE_AI_WEATHER -> {

            }
        }
    }

    override fun getItemType(position: Int): Int {
        return mList[position].type
    }

    override fun getLayoutId(type: Int): Int {
        return when (type) {
            AppConstants.TYPE_MINE_TEXT -> R.layout.layout_mine_text
            AppConstants.TYPE_AI_TEXT -> R.layout.layout_ai_text
            AppConstants.TYPE_AI_WEATHER -> R.layout.layout_ai_weather
            else -> 0
        }
    }
}) {

}