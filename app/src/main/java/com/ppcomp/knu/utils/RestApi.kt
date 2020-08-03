package com.ppcomp.knu.utils

import com.ppcomp.knu.`object`.UserInfo
import com.ppcomp.knu.`object`.KakaoUserInfo
import retrofit2.Call
import retrofit2.http.*

interface RestApi {

    @Headers("Content-Type: application/json")
    @POST("accounts/device")
    fun addUser(@Body userData: UserInfo): Call<UserInfo>

    @Headers("Content-Type: application/json")
    @PUT("accounts/device")
    fun modifyUser(@Body userData: UserInfo): Call<UserInfo>

    @Headers("Content-Type: application/json")
    @POST("accounts/user")
    fun addKakaoUser(@Body kakaoUserData: KakaoUserInfo): Call<KakaoUserInfo>

    @Headers("Content-Type: application/json")
    @GET("accounts/user")
    fun getKakaoUser(@Query("id") id: String): Call<KakaoUserInfo>
}
