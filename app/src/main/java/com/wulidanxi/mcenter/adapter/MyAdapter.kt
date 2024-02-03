package com.wulidanxi.mcenter.adapter

import android.view.View
import com.wulidanxi.mcenter.R
import com.wulidanxi.mcenter.databinding.ItemShowBinding
import com.wulidanxi.mcenter.db.Content

class MyAdapter(mList: List<Content>?, mLayoutId: Int?) : KtBaseAdapter<Content>(mList, mLayoutId) {
    override fun convert(itemShowBinding: ItemShowBinding, item: Content) {
        itemShowBinding.channel.text = item.channel
        itemShowBinding.account.text = item.account
        itemShowBinding.password.text = item.password
        itemShowBinding.time.text = item.date
        val resId = getResId(item.channel)
        itemShowBinding.ivChannel.setImageResource(resId)
    }

    private fun getResId(channel: String): Int {
        return when (channel) {
            "QQ" -> R.mipmap.qq
            "微信" -> R.mipmap.weixin
            "BiliBili" -> R.mipmap.bilibili
            "Github" -> R.mipmap.github
            else -> R.mipmap.dont_know

        }
    }
}