package com.zoo.mvvmkt.network

import com.kunminx.architecture.domain.message.MutableResult


class NetworkStateManager private constructor() {

    val mNetworkStateCallback = MutableResult<NetState>()

    companion object {
        val instance: NetworkStateManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkStateManager()
        }
    }

}