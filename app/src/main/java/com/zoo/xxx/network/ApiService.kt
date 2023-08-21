package com.zoo.xxx.network

import com.zoo.xxx.bean.UserInfo
import retrofit2.http.*


interface ApiService {

    companion object {
        const val SERVER_URL = "https://wanandroid.com/"
    }

    /**
     * 登录
     */
    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") pwd: String
    ): ApiResponse<UserInfo>


    /**
     * 获取banner数据
     */
    @GET("banner/json")
    suspend fun getBanner(): ApiResponse<List<Any>>

}