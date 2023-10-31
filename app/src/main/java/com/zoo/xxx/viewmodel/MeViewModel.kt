package com.zoo.xxx.viewmodel

import com.zoo.mvvmkt.callback.databind.StringObservableField
import com.zoo.mvvmkt.viewmodel.BaseViewModel

open class MeViewModel : BaseViewModel() {

    var name = StringObservableField("请先登录~")

}
