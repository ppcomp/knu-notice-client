import android.service.autofill.UserData
import android.util.Log
import com.ppcomp.knu.KakaoUserInfo
import com.ppcomp.knu.RestApi
import com.ppcomp.knu.UserInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class RestApiService {
    fun addUser(userData: UserInfo, onResult: (UserInfo?) -> Unit){
        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        retrofit.addUser(userData).enqueue(
            object : Callback<UserInfo> {
                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                    Log.d("call", call.toString())
                    Log.d("t",t.toString())
                    onResult(null)
                }
                override fun onResponse( call: Call<UserInfo>, response: Response<UserInfo>) {
                    val addedUser = response.body()
                    onResult(addedUser)
                }
            }
        )
    }

    fun modifyUser(userData: UserInfo, onResult: (UserInfo?) -> Unit){
        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        retrofit.modifyUser(userData).enqueue(
            object : Callback<UserInfo> {
                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                    Log.d("call", call.toString())
                    Log.d("t",t.toString())
                    onResult(null)
                }
                override fun onResponse( call: Call<UserInfo>, response: Response<UserInfo>) {
                    val modifiedUser = response.body()
                    onResult(modifiedUser)
                }
            }
        )
    }

    fun addKakaoUser(kakaoUserData: KakaoUserInfo, onResult: (KakaoUserInfo?) -> Unit){
        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        retrofit.addKakaoUser(kakaoUserData).enqueue(
            object : Callback<KakaoUserInfo> {
                override fun onFailure(call: Call<KakaoUserInfo>, t: Throwable) {
                    Log.d("call",call.toString())
                    Log.d("t",t.toString())
                    onResult(null)
                }
                override fun onResponse( call: Call<KakaoUserInfo>, response: Response<KakaoUserInfo>) {
                    val addedKakaoUser = response.body()
                    onResult(addedKakaoUser)
                }
            }
        )
    }

    fun getKakaoUser(id: String, onResult: (KakaoUserInfo?) -> Unit){
        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        retrofit.getKakaoUser(id).enqueue(
            object : Callback<KakaoUserInfo> {
                override fun onFailure(call: Call<KakaoUserInfo>, t: Throwable) {
                    Log.d("call",call.toString())
                    Log.d("t",t.toString())
                    onResult(null)
                }

                override fun onResponse(call: Call<KakaoUserInfo>, response: Response<KakaoUserInfo>) {
                    val getKakaoUser = response.body()
                    onResult(getKakaoUser)
                }
            }
        )
    }

}