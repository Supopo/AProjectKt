package com.zoo.xxx.network

import com.zoo.mvvmkt.network.BaseResponse

data class ApiResponse<T>(
    val code: Int,
    val msg: String,
    val token: String,
    val data: T,
    val rows: T,
    val total: Int
) :
    BaseResponse<T>() {

    override fun isSucces() = code == 200
    override fun getResponseCode() = code
    override fun getResponseTotal() = total
    override fun getResponseData() = data
    override fun getResponseListData() = rows
    override fun getResponseMsg(): String {
        return if (msg == null) "数据异常" else msg
    }

    override fun getLoginToken() = token

}