package com.zoo.xxx.event

import com.zoo.mvvmkt.callback.livedata.BooleanLiveData
import com.zoo.mvvmkt.viewmodel.BaseViewModel

//用来消息传递
class EventViewModel : BaseViewModel() {
    //添加TODO通知
    val todoEvent = BooleanLiveData()
}