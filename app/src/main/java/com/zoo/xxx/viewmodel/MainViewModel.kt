package com.zoo.xxx.viewmodel;

import com.zoo.mvvmkt.ext.request
import com.zoo.mvvmkt.viewmodel.BaseViewModel
import com.zoo.xxx.network.apiService
import com.zoo.xxx.utils.showToast

open class MainViewModel : BaseViewModel() {

    fun getBanner() {
        request({ apiService.getBanner() }, {

        }, {
            it.message?.showToast()
        }, true, "正在获取...")
    }

}
