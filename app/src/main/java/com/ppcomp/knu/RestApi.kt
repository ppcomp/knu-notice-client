package com.ppcomp.knu

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT

interface RestApi {

    @Headers("Content-Type: application/json")
    @POST("accounts/device")
    fun addUser(@Body userData: UserInfo): Call<UserInfo>

    @Headers("Content-Type: application/json")
    @PUT("accounts/device")
    fun modifyUser(@Body userData: UserInfo): Call<UserInfo>
}
