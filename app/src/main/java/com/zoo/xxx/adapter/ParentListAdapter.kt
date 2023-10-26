package com.zoo.xxx.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.DataBindingHolder
import com.zoo.xxx.R
import com.zoo.xxx.bean.ListBean
import com.zoo.xxx.databinding.ItemParentListBinding

/**
 * Created by SuperPer'GPT on 2023/10/12.
 * 多层RecyclerView嵌套
 */
class ParentListAdapter : BaseQuickAdapter<ListBean, DataBindingHolder<ItemParentListBinding>>() {

    //使用
//    val ids = IntArray(2)
//    ids[0] = R.id.iv_child_del
//    ids[1] = R.id.tv_child_operation
//    mAdapter.addOnChildViewItemChildClickListener(ids)
//    { adapter, view, position, parentPos ->
//        when (view.id) {
//            R.id.iv_child_del -> {
//                adapter.removeAt(position)
//            }
//
//            R.id.tv_child_operation -> {
//                ActivityMessenger.startActivity<BindTagsActivity>(this)
//            }
//
//            else -> {}
//        }
//    }


    private var onItemChildClickListener: ((adapter: ChildListAdapter, view: View, position: Int, parentPos: Int) -> Unit)? =
        null
    private var viewIds: IntArray = intArrayOf()

    fun setOnItemChildClickListener(
        viewIds: IntArray,
        listener: (childAdapter: ChildListAdapter, view: View, position: Int, parentPos: Int) -> Unit
    ) {
        this.viewIds = viewIds
        onItemChildClickListener = listener
    }

    //自定义向外提供子adapter的view点击事件
    fun addOnChildViewItemChildClickListener(
        viewIds: IntArray,
        listener: (adapter: ChildListAdapter, view: View, position: Int, parentPos: Int) -> Unit
    ) {
        this.viewIds = viewIds
        this.onItemChildClickListener = listener
    }

    override fun onBindViewHolder(
        holder: DataBindingHolder<ItemParentListBinding>,
        position: Int,
        item: ListBean?
    ) {
        if (item == null) return
        val binding: ItemParentListBinding = holder.binding
        binding.info = item
        binding.executePendingBindings()


        val mAdapter = ChildListAdapter()
        binding.rvContent.layoutManager = LinearLayoutManager(context)
        binding.rvContent.adapter = mAdapter
        mAdapter.submitList(item.dataList)

        val parentPos = position
        //处理多个子View点击事件
        viewIds.forEach { viewId ->
            mAdapter.addOnItemChildClickListener(viewId) { adapter, view, position ->
                onItemChildClickListener?.invoke(
                    mAdapter,
                    view,
                    position,
                    parentPos
                )
            }
        }
    }

    //局部刷新
    override fun onBindViewHolder(
        holder: DataBindingHolder<ItemParentListBinding>,
        position: Int,
        item: ListBean?,
        payloads: List<Any>
    ) {
        if (item == null) return
        if (payloads.isNotEmpty() && payloads[0] is Int) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            val type = payloads[0] as Int // 刷新哪个部分 标志位
            if (type == 10086) {
                val binding: ItemParentListBinding = holder.binding
                binding.info = item
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): DataBindingHolder<ItemParentListBinding> {
        return DataBindingHolder<ItemParentListBinding>(R.layout.item_list, parent)
    }

}