package com.wulidanxi.mcenter.adapter

import android.view.View
import com.wulidanxi.mcenter.R
import com.wulidanxi.mcenter.db.Content
import kotlinx.android.synthetic.main.item_show.view.*

class MyAdapter(mList: List<Content>?, mLayoutId: Int?) : KtBaseAdapter<Content>(mList, mLayoutId) {
    override fun convert(itemView: View?, item: Content) {
        itemView!!.channel.text = item.channel
        itemView.account.text = item.account
        itemView.password.text = item.password
        itemView.time.text = item.date
        val resId = getResId(item.channel)
        itemView.iv_channel.setImageResource(resId)
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