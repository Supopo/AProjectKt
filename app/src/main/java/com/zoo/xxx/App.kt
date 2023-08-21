package com.zoo.xxx

import com.zoo.mvvmkt.base.BaseApp
import com.zoo.xxx.event.AppViewModel
import com.zoo.xxx.event.EventViewModel

//Application全局的ViewModel，里面存放了一些账户信息，基本配置信息等
val appViewModel: AppViewModel by lazy { App.appViewModelInstance }

//Application全局的ViewModel，用于发送全局通知操作
val eventViewModel: EventViewModel by lazy { App.eventViewModelInstance }
class App : BaseApp() {

    companion object {
        lateinit var instance: App
        lateinit var eventViewModelInstance: EventViewModel
        lateinit var appViewModelInstance: AppViewModel
    }
}