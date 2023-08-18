package com.zoo.mvvmkt.callback.livedata

import com.kunminx.architecture.domain.message.MutableResult


/**
 * 自定义的Boolean类型 MutableResult 提供了默认值，避免取值的时候还要判空
 */
class BooleanLiveData : MutableResult<Boolean>() {

    override fun getValue(): Boolean {
        return super.getValue() ?: false
    }
}

