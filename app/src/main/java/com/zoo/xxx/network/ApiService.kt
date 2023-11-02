package com.zoo.xxx.network

import com.zoo.xxx.bean.UserInfo
import okhttp3.MultipartBody
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

    //上传图片
    @POST("/upload")
    suspend fun uploadImage(@Body file: MultipartBody): ApiResponse<Any>

}