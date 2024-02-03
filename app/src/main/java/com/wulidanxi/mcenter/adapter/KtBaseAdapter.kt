package com.wulidanxi.mcenter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.wulidanxi.mcenter.adapter.KtBaseAdapter.KtViewHolder
import com.wulidanxi.mcenter.databinding.ItemShowBinding

/**
 * Created by zfw on 2019/7/30 15:15
 * Extend by wulidanxi on 2020/4/10
 */
open class KtBaseAdapter<T>(private var mList: List<T>?, private var mLayoutId: Int?) :
    Adapter<KtViewHolder>() {


    private var itemClick: ItemClick? = null
    private var itemLongClick: ItemLongClick? = null

    interface ItemClick {
        fun onItemClick(v: View, position: Int)
    }

    interface ItemLongClick {
        fun onItemLongClick(v: View, position: Int)
    }

    fun setItemClickListener(itemClick: ItemClick) {
        this.itemClick = itemClick
    }

    fun setItemLongClickListener(itemLongClick: ItemLongClick) {
        this.itemLongClick = itemLongClick
    }

    fun updateData(items: List<T>?) {
        this.mList = items
        notifyDataSetChanged()
    }

    fun getData(): List<T>? {
        return mList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KtViewHolder {
       //  val v = LayoutInflater.from(parent.context).inflate(this.mLayoutId!!, parent, false)
        val itemShowBinding = ItemShowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return KtViewHolder(itemShowBinding)
    }

    override fun onBindViewHolder(holder: KtViewHolder, position: Int) {
        val item = mList!![position]
        // item点击事件
        holder.itemView.setOnClickListener {
            itemClick?.onItemClick(holder.itemView, position)
        }
        holder.itemView.setOnLongClickListener {
            itemLongClick?.onItemLongClick(holder.itemView, position)
            true
        }
        convert(holder.getViewBinding(), item)
    }

    open fun convert(itemShowBinding: ItemShowBinding, item: T) {

    }

    override fun getItemCount(): Int = mList!!.size

    class KtViewHolder(private val itemShowBinding: ItemShowBinding) : RecyclerView.ViewHolder(itemShowBinding.root) {
        fun getViewBinding() : ItemShowBinding{
            return itemShowBinding
        }
    }
}