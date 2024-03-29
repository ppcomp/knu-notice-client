package com.ppcomp.knu.utils

import com.ppcomp.knu.`object`.DeviceInfo
import com.ppcomp.knu.`object`.noticeData.ReceivedData
import com.ppcomp.knu.`object`.UserInfo
import com.ppcomp.knu.`object`.Version
import com.ppcomp.knu.dto.device.BaseDeviceInfo
import com.ppcomp.knu.dto.device.DeviceInfoRequest
import com.ppcomp.knu.dto.device.DeviceInfoResponse
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface RestApi {
    /**
     * Rest Api Builder 싱글톤 객체로 생성
     * @author 정우
     */
    companion object {
        fun create() : RestApi {
            return Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(
                    OkHttpClient.Builder()
                    .addInterceptor(
                        HttpLoggingInterceptor(HttpPrettyLogging())
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build())
                .baseUrl("http://${PreferenceHelper.get("serverIP","")}")
                .build()
                .create(RestApi::class.java)
        }
    }

    @Headers("Content-Type: application/json")
    @GET("/support/version")
    fun getVersion(): Call<Version>

    @Headers("Content-Type: application/json")
    @GET("/accounts/device")
    @Deprecated("Getting device with GET method is deprecated. Use getDeviceInfo() instead.")
    fun getDevice(@Query("id") id: String): Call<DeviceInfoResponse>

    @Headers("Content-Type: application/json")
    @POST("/accounts/device-info")
    fun getDeviceInfo(@Body deviceData: BaseDeviceInfo): Call<DeviceInfoResponse>

    @Headers("Content-Type: application/json")
    @POST("/accounts/device")
    fun postDevice(@Body deviceData: DeviceInfoRequest): Call<DeviceInfoResponse>

    @Headers("Content-Type: application/json")
    @PUT("/accounts/device")
    fun putDevice(@Body deviceData: DeviceInfoRequest): Call<DeviceInfoResponse>

    @Headers("Content-Type: application/json")
    @GET("/accounts/user")
    fun getUser(@Query("id") id: String): Call<UserInfo>

    @Headers("Content-Type: application/json")
    @POST("/accounts/user")
    fun postUser(@Body userData: UserInfo): Call<UserInfo>

    @Headers("Content-Type: application/json")
    @PUT("/accounts/user")
    fun putUser(@Body userData: UserInfo): Call<UserInfo>

    /**
     * GET) /notice/all
     * @author 정우
     */
    @GET("/notice/all")
    fun getNoticeAll(@Query("q", encoded=true) q: String,
                     @Query("target", encoded=true) target: String,
                     @Query("page") page: Int): Single<ReceivedData>
}
