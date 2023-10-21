package com.zoo.xxx.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by SuperPer'GPT on 2023/10/12.
 * 添加实例化对象
 */
@Parcelize
class ListBean(
    var title: String = "",
    var dataList: ArrayList<ListBean> = ArrayList<ListBean>()
) : Parcelable {
}