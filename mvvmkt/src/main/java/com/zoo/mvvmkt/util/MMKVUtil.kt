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
    //不用同一种ID，这样多个存取快
    fun instance(tag: String): MMKV {
        return MMKV.mmkvWithID(tag)
    }

    fun encodeString(key: String, data: String) {
        instance(key).encode(key, data)
    }

    fun getString(key: String, defValue: String): String? {
        return instance(key).getString(key, defValue)
    }

    fun encodeBoolean(key: String, data: Boolean) {
        instance(key).encode(key, data)
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return instance(key).getBoolean(key, defValue)
    }

    //保存json类型的数据，可以是实体类或者数组类等
    fun <T> saveData2Json(key: String, data: T) {
        val json = if (data == null) "" else Gson().toJson(data)
        instance(key).encode(key, json)
    }

    //获取保存的json类型的数据，可以是实体类或者数组类等
    inline fun <reified T> getData4Json(key: String): T? {
        val dataJson = instance(key).decodeString(key)
        if (!TextUtils.isEmpty(dataJson)) {
            return Gson().fromJson(dataJson, object : TypeToken<T>() {}.type)
        }
        // You can adjust the default value based on the actual usage
        // return T::class.java.newInstance()
        return null
    }
}