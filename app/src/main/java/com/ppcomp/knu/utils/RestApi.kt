package com.ppcomp.knu.utils

import com.ppcomp.knu.`object`.DeviceInfo
import com.ppcomp.knu.`object`.UserInfo
import retrofit2.Call
import retrofit2.http.*

interface RestApi {

    @Headers("Content-Type: application/json")
    @GET("accounts/device")
    fun getDevice(@Query("id") id: String): Call<DeviceInfo>

    @Headers("Content-Type: application/json")
    @POST("accounts/device")
    fun postDevice(@Body deviceData: DeviceInfo): Call<DeviceInfo>

    @Headers("Content-Type: application/json")
    @PUT("accounts/device")
    fun putDevice(@Body deviceData: DeviceInfo): Call<DeviceInfo>

    @Headers("Content-Type: application/json")
    @GET("accounts/user")
    fun getUser(@Query("id") id: String): Call<UserInfo>

    @Headers("Content-Type: application/json")
    @POST("accounts/user")
    fun postUser(@Body userData: UserInfo): Call<UserInfo>

    @Headers("Content-Type: application/json")
    @PUT("accounts/user")
    fun putUser(@Body userData: UserInfo): Call<UserInfo>


}
