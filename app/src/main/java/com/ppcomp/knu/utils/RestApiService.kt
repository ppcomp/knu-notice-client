package com.ppcomp.knu.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.ppcomp.knu.`object`.UserInfo
import com.ppcomp.knu.`object`.DeviceInfo
import com.ppcomp.knu.`object`.Version
import com.ppcomp.knu.dto.device.BaseDeviceInfo
import com.ppcomp.knu.dto.device.DeviceInfoRequest
import com.ppcomp.knu.dto.device.DeviceInfoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestApiService {
    companion object {
        private val restApi = RestApi.create()
    }

    fun getVersion(): Version? {
        return restApi.getVersion().execute().body()
    }

    fun getDevice(context: Context, id: String, onResult: (DeviceInfoResponse?) -> Unit){
        onResult(restApi.getDeviceInfo(BaseDeviceInfo(id=id, id_method = "InstanceId")).execute().body())
    }

    fun postDevice(context: Context, deviceData: DeviceInfo, onResult: (DeviceInfoResponse?) -> Unit){
        restApi.postDevice(DeviceInfoRequest(deviceData)).enqueue(
            object : Callback<DeviceInfoResponse> {
                override fun onFailure(call: Call<DeviceInfoResponse>, t: Throwable) {
                    Log.d("call", call.toString())
                    Log.d("t",t.toString())
                    onResult(null)
                    Toast.makeText(context,"디바이스 post 요청 실패 (네트워크 문제)",Toast.LENGTH_SHORT)
                }
                override fun onResponse(
                    call: Call<DeviceInfoResponse>,
                    response: Response<DeviceInfoResponse>
                ) {
                    if(response.isSuccessful) {
                        val addedUser = response.body()
                        onResult(addedUser)
                    }
                    else {
                        Toast.makeText(context,"디바이스 post 요청 실패",Toast.LENGTH_SHORT)
                        onResult(null)
                    }
                }
            }
        )
    }

    fun putDevice(context: Context,deviceData: DeviceInfo, onResult: (DeviceInfoResponse?) -> Unit){
        restApi.putDevice(DeviceInfoRequest(deviceData)).enqueue(
            object : Callback<DeviceInfoResponse> {
                override fun onFailure(call: Call<DeviceInfoResponse>, t: Throwable) {
                    Log.d("call", call.toString())
                    Log.d("t",t.toString())
                    onResult(null)
                    Toast.makeText(context,"디바이스 put 요청 실패 (네트워크 문제)",Toast.LENGTH_SHORT)
                }
                override fun onResponse(
                    call: Call<DeviceInfoResponse>,
                    response: Response<DeviceInfoResponse>
                ) {
                    if(response.isSuccessful) {
                        val modifiedUser = response.body()
                        onResult(modifiedUser)
                    }
                    else {
                        Toast.makeText(context, "디바이스 put 요청 실패", Toast.LENGTH_SHORT)
                        onResult(null)
                    }
                }
            }
        )
    }

    fun getUser(context: Context, id: String, onResult: (UserInfo?) -> Unit){
        onResult(restApi.getUser(id).execute().body())
    }

    fun postUser(context: Context, userData: UserInfo, onResult: (UserInfo?) -> Unit){
        restApi.postUser(userData).enqueue(
            object : Callback<UserInfo> {
                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                    Log.d("call",call.toString())
                    Log.d("t",t.toString())
                    onResult(null)
                    Toast.makeText(context,"유저 post 요청 실패 (네트워크 문제)",Toast.LENGTH_SHORT)
                }
                override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                    if(response.isSuccessful) {
                        val addedKakaoUser = response.body()
                        onResult(addedKakaoUser)
                    }
                    else {
                        Toast.makeText(context,"유저 post 요청 실패",Toast.LENGTH_SHORT)
                        onResult(null)
                    }
                }
            }
        )
    }

    fun putUser(context: Context, userData: UserInfo, onResult: (UserInfo?) -> Unit){
        restApi.putUser(userData).enqueue(
            object : Callback<UserInfo> {
                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                    Log.d("call", call.toString())
                    Log.d("t",t.toString())
                    onResult(null)
                    Toast.makeText(context,"유저 put 요청 실패 (네트워크 문제)",Toast.LENGTH_SHORT)
                }
                override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                    if (response.isSuccessful) {
                        val modifiedUser = response.body()
                        onResult(modifiedUser)
                    } else {
                        Toast.makeText(context, "유저 put 요청 실패", Toast.LENGTH_SHORT)
                        onResult(null)
                    }
                }
            }
        )
    }

}