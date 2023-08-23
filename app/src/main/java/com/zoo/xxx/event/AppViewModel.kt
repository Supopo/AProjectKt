package com.zoo.xxx.event

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.zoo.mvvmkt.viewmodel.BaseViewModel
import com.zoo.xxx.bean.UserInfo
import com.zoo.xxx.utils.CacheUtil


//统一
class AppViewModel : BaseViewModel() {

    //App的账户信息
    var userInfo: UnPeekLiveData<UserInfo>? =
        UnPeekLiveData.Builder<UserInfo>().setAllowNullValue(true).create()

    init {
        //默认值保存的账户信息，没有登陆过则为null
        userInfo?.value = CacheUtil.getUser()
    }
}