package com.zoo.xxx.viewmodel;

import com.zoo.mvvmkt.ext.request
import com.zoo.mvvmkt.viewmodel.BaseViewModel
import com.zoo.xxx.network.apiService

open class MainViewModel : BaseViewModel() {

    fun getBanner() {
        request({ apiService.getBanner() }, {

        }, {

        }, true, "正在获取...")
    }

}
