package com.zoo.xxx.viewmodel;

import androidx.lifecycle.MutableLiveData
import com.zoo.mvvmkt.viewmodel.BaseViewModel
import com.zoo.xxx.bean.ListBean

open class ListViewModel : BaseViewModel() {

    var dataList = MutableLiveData<List<ListBean>>()
    var reqState = MutableLiveData<Int>()//1去请求 2请求结束（不论失败与否）

    fun getDataList(pageNum: Int) {
        reqState.postValue(1)
        val list = ArrayList<ListBean>()
        for (i in 0 until 8) {
            list.add(ListBean("标题$i"))
        }
        reqState.postValue(2)
        dataList.postValue(list)
    }
}
