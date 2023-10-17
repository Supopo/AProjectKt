package com.zoo.xxx.adapter

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.DataBindingHolder
import com.zoo.xxx.R
import com.zoo.xxx.bean.ListBean
import com.zoo.xxx.databinding.ItemListBinding

/**
 * Created by SuperPer'GPT on 2023/10/12.
 */
class ListAdapter: BaseQuickAdapter<ListBean, DataBindingHolder<ItemListBinding>>() {
    override fun onBindViewHolder(
        holder: DataBindingHolder<ItemListBinding>,
        position: Int,
        item: ListBean?
    ) {
        if (item == null) return
        val binding: ItemListBinding = holder.binding
        binding.info = item
        binding.executePendingBindings()
    }

    //局部刷新
    override fun onBindViewHolder(
        holder: DataBindingHolder<ItemListBinding>,
        position: Int,
        item: ListBean?,
        payloads: List<Any>
    ) {
        if (item == null) return
        if (payloads.isNotEmpty() && payloads[0] is Int) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            val type = payloads[0] as Int // 刷新哪个部分 标志位
            if (type == 10086) {
                val binding: ItemListBinding = holder.binding
                binding.info = item
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): DataBindingHolder<ItemListBinding> {
        return DataBindingHolder<ItemListBinding>(R.layout.item_list, parent)
    }

}