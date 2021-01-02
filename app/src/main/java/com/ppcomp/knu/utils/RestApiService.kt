
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.ppcomp.knu.`object`.UserInfo
import com.ppcomp.knu.utils.RestApi
import com.ppcomp.knu.`object`.DeviceInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestApiService {
    fun getDevice(context: Context, id: String, onResult: (DeviceInfo?) -> Unit){
        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        onResult(retrofit.getDevice(id).execute().body())
    }

    fun postDevice(context: Context, deviceData: DeviceInfo, onResult: (DeviceInfo?) -> Unit){
        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        retrofit.postDevice(deviceData).enqueue(
            object : Callback<DeviceInfo> {
                override fun onFailure(call: Call<DeviceInfo>, t: Throwable) {
                    Log.d("call", call.toString())
                    Log.d("t",t.toString())
                    onResult(null)
                    Toast.makeText(context,"디바이스 post 요청 실패 (네트워크 문제)",Toast.LENGTH_SHORT)
                }
                override fun onResponse(call: Call<DeviceInfo>, response: Response<DeviceInfo>) {
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

    fun putDevice(context: Context,deviceData: DeviceInfo, onResult: (DeviceInfo?) -> Unit){
        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        retrofit.putDevice(deviceData).enqueue(
            object : Callback<DeviceInfo> {
                override fun onFailure(call: Call<DeviceInfo>, t: Throwable) {
                    Log.d("call", call.toString())
                    Log.d("t",t.toString())
                    onResult(null)
                    Toast.makeText(context,"디바이스 put 요청 실패 (네트워크 문제)",Toast.LENGTH_SHORT)
                }
                override fun onResponse(call: Call<DeviceInfo>, response: Response<DeviceInfo>) {
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
        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        onResult(retrofit.getUser(id).execute().body())
    }

    fun postUser(context: Context, userData: UserInfo, onResult: (UserInfo?) -> Unit){
        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        retrofit.postUser(userData).enqueue(
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
        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        retrofit.putUser(userData).enqueue(
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