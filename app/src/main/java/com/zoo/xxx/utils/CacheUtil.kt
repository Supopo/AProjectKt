package com.zoo.xxx.utils

import com.zoo.mvvmkt.util.MMKVUtils
import com.zoo.xxx.bean.UserInfo

object CacheUtil {
    private const val USER_INFO = "userInfo"
    private const val IS_LOGIN = "isLogin"

    /**
     * 获取保存的账户信息
     */
    fun getUser(): UserInfo? {
        return MMKVUtils.getData4Json(USER_INFO)
    }

    /**
     * 设置账户信息
     */
    fun setUser(userInfo: UserInfo?) {
        MMKVUtils.saveData2Json(USER_INFO, userInfo)
    }

    /**
     * 是否已经登录
     */
    fun isLogin(): Boolean {
        return MMKVUtils.instance().decodeBool(IS_LOGIN, false)
    }

    /**
     * 设置是否已经登录
     */
    fun setIsLogin(isLogin: Boolean) {
        MMKVUtils.instance().encode(IS_LOGIN, isLogin)
    }

}