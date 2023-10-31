package com.zoo.xxx.utils

import com.zoo.mvvmkt.util.MMKVUtils
import com.zoo.xxx.bean.UserInfo

object CacheUtil {
    private const val USER_INFO = "userInfo"
    private const val IS_LOGIN = "isLogin"
    private const val TOKEN = "token"
    private const val ACC = "acc"
    private const val PWD = "pwd"

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
        return MMKVUtils.getBoolean(IS_LOGIN, false)
    }

    /**
     * 设置是否已经登录
     */
    fun setIsLogin(isLogin: Boolean) {
        MMKVUtils.encodeBoolean(IS_LOGIN, isLogin)
        if (!isLogin) {
            //退出，清除token
            setToken("")
        }
    }

    fun setToken(token: String) {
        MMKVUtils.encodeString(TOKEN, token)
    }

    fun getToken(): String? {
        return MMKVUtils.getString(TOKEN, "")
    }

    fun setAcc(acc: String) {
        MMKVUtils.encodeString(ACC, acc)
    }

    fun getAcc(): String? {
        return MMKVUtils.getString(ACC, "")
    }

    fun setPwd(pwd: String) {
        MMKVUtils.encodeString(PWD, pwd)
    }

    fun getPwd(): String? {
        return MMKVUtils.getString(PWD, "")
    }
}