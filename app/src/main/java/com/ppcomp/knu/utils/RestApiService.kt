import android.util.Log
import com.ppcomp.knu.`object`.UserInfo
import com.ppcomp.knu.utils.RestApi
import com.ppcomp.knu.`object`.DeviceInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestApiService {
    fun getDevice(id: String, onResult: (DeviceInfo?) -> Unit){
        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        retrofit.getDevice(id).enqueue(
            object : Callback<DeviceInfo> {
                override fun onFailure(call: Call<DeviceInfo>, t: Throwable) {
                    Log.d("call",call.toString())
                    Log.d("t",t.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<DeviceInfo>, response: Response<DeviceInfo>) {
                    val getUser = response.body()
                    onResult(getUser)
                }
            }
        )
    }

    fun postDevice(deviceData: DeviceInfo, onResult: (DeviceInfo?) -> Unit){
        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        retrofit.postDevice(deviceData).enqueue(
            object : Callback<DeviceInfo> {
                override fun onFailure(call: Call<DeviceInfo>, t: Throwable) {
                    Log.d("call", call.toString())
                    Log.d("t",t.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<DeviceInfo>, response: Response<DeviceInfo>) {
                    val addedUser = response.body()
                    onResult(addedUser)
                }
            }
        )
    }

    fun putDevice(deviceData: DeviceInfo, onResult: (DeviceInfo?) -> Unit){
        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        retrofit.putDevice(deviceData).enqueue(
            object : Callback<DeviceInfo> {
                override fun onFailure(call: Call<DeviceInfo>, t: Throwable) {
                    Log.d("call", call.toString())
                    Log.d("t",t.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<DeviceInfo>, response: Response<DeviceInfo>) {
                    val modifiedUser = response.body()
                    onResult(modifiedUser)
                }
            }
        )
    }

    fun getUser(id: String, onResult: (UserInfo?) -> Unit){
        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        retrofit.getUser(id).enqueue(
            object : Callback<UserInfo> {
                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                    Log.d("call",call.toString())
                    Log.d("t",t.toString())
                    onResult(null)
                }

                override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                    val getKakaoUser = response.body()
                    onResult(getKakaoUser)
                }
            }
        )
    }

    fun postUser(userData: UserInfo, onResult: (UserInfo?) -> Unit){
        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        retrofit.postUser(userData).enqueue(
            object : Callback<UserInfo> {
                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                    Log.d("call",call.toString())
                    Log.d("t",t.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                    val addedKakaoUser = response.body()
                    onResult(addedKakaoUser)
                }
            }
        )
    }

    fun putUser(userData: UserInfo, onResult: (UserInfo?) -> Unit){
        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        retrofit.putUser(userData).enqueue(
            object : Callback<UserInfo> {
                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                    Log.d("call", call.toString())
                    Log.d("t",t.toString())
                    onResult(null)
                }
                override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                    val modifiedUser = response.body()
                    onResult(modifiedUser)
                }
            }
        )
    }

}