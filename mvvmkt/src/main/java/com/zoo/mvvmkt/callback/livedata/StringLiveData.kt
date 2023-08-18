package com.zoo.mvvmkt.callback.livedata

import com.kunminx.architecture.domain.message.MutableResult


/**
 * 自定义的String类型 MutableResult 提供了默认值，避免取值的时候还要判空
 */
class StringLiveData : MutableResult<String>() {

    override fun getValue(): String {
        return super.getValue() ?: ""
    }
}

