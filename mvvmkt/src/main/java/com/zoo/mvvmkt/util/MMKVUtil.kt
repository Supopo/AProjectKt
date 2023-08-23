package com.zoo.mvvmkt.util

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV

/**
 * Created by SuperPer's GPT on 2050/8/23.
 *
 */
object MMKVUtils {
    private const val TAG = "cache"

    fun instance(): MMKV {
        return MMKV.mmkvWithID(TAG)
    }

    //保存json类型的数据，可以是实体类或者数组类等
    fun <T> saveData2Json(key: String, data: T) {
        val json = if (data == null) "" else Gson().toJson(data)
        instance().encode(key, json)
    }

    //获取保存的json类型的数据，可以是实体类或者数组类等
    inline fun <reified T> getData4Json(key: String): T? {
        val dataJson = instance().decodeString(key)
        if (!TextUtils.isEmpty(dataJson)) {
            return Gson().fromJson(dataJson, object : TypeToken<T>() {}.type)
        }
        // You can adjust the default value based on the actual usage
        // return T::class.java.newInstance()
        return null
    }
}