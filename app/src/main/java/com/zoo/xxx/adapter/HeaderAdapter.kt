package com.zoo.xxx.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseSingleItemAdapter
import com.zoo.xxx.R

//使用方法，设置完mAdapter之后调用
//helper.addBeforeAdapter(headerAdapter)
class HeaderAdapter: BaseSingleItemAdapter<Any, HeaderAdapter.VH>() {

    class VH(view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.head_view, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, item: Any?) {

    }
}