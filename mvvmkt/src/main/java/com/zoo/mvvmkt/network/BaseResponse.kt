package com.zoo.mvvmkt.network

abstract class BaseResponse<T> {

    //抽象方法，用户的基类继承该类时，需要重写该方法
    abstract fun isSucces(): Boolean

    abstract fun getResponseData(): T

    abstract fun getResponseListData(): T

    abstract fun getResponseTotal(): Int

    abstract fun getResponseCode(): Int

    abstract fun getResponseMsg(): String

    abstract fun getLoginToken(): String

}