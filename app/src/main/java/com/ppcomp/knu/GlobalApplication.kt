package com.ppcomp.knu

import RestApiService
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.kakao.auth.KakaoSDK
import com.ppcomp.knu.`object`.UserInfo
import com.ppcomp.knu.`object`.DeviceInfo
import com.ppcomp.knu.adapter.KakaoSDKAdapter
import com.ppcomp.knu.utils.PreferenceHelper

/**
 * 글로벌 어플리케이션 설정
 * 액티비티나 프레그먼트에 종속되지 않는 글로벌 변수 선언 가능
 * @author 정준
 */
class GlobalApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        KakaoSDK.init(KakaoSDKAdapter())    //카카오 로그인 초기화
    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }

    fun getGlobalApplicationContext(): GlobalApplication {
        checkNotNull(instance) { "this application does not inherit com.kakao.GlobalApplication" }
        return instance!!
    }

    companion object {  //자바의 static이 없는 대신 있는 코틀린만의 구조체, 싱글톤 패턴 구현가능
        var instance: GlobalApplication? = null
        var isLogin: Boolean = false    //로그인 상태
        var isFragmentChange: Array<Boolean> = arrayOf(false, false, false, false) //프레그먼트 변경사항 확인 (notice, keywordNotice, search, bookmark)

        /**
         * 카카오 유저 데이터 서버에 업로드
         * @author 정준
         */
        fun kakaoUserInfoUpload(context: Context) {
            var isGetFailed: Boolean = false
            val apiService = RestApiService()
            val getId = PreferenceHelper.get("fbId","")
            val getKakaoId = PreferenceHelper.get("kakaoId","").toString()
            val userInfo = UserInfo(
                id = getKakaoId,
                device_id = getId
            )

            apiService.getUser(context,getKakaoId) {
                //서버에 데이터가 있는지 확인 (GET)
                if(it?.id != null) {
                    Log.d("kakaoUser_get","id != null")
                    isGetFailed = false
                } else {
                    Log.d("kakaoUser_get","id = null")
                    isGetFailed = true
                }

                if(isGetFailed) {
                    //서버에 데이터가 없으면 서버에 데이터 저장 (POST)
                    apiService.postUser(context,userInfo) {
                        if (it?.id != null) {
                            // it = newly added user parsed as response
                            // it?.id = newly added user ID
                            Log.d("kakaoUser_post", "id != null")
                        } else {
                            Log.d("kakaoUser_post", "id = null ")
                        }
                    }
                }
                else {
                    //서버에 데이터가 있으면 데이터 변경 (PUT)
                    apiService.putUser(context,userInfo) {
                        if (it?.id != null) {
                            // it = newly added user parsed as response
                            // it?.id = newly added user ID
                            Log.d("kakaoUser_put", "id != null")
                        } else {
                            Log.d("kakaoUser_put", "id = null ")
                        }
                    }
                }
            }
        }

        /**
         * 유저 데이터 서버에 업로드
         * @author 정준, 정우
         */
        fun userInfoUpload(context: Context) {
            var isGetFailed: Boolean = false
            val apiService = RestApiService()
            val getId = PreferenceHelper.get("fbId","").toString()
            val getKeywords: String? = PreferenceHelper.get("Keys", null)
            val getSubscriptions: String? = PreferenceHelper.get("Urls", null)
            val getAlarmSwitch: Boolean? = PreferenceHelper.get("alarmSwitch", false)
            val deviceInfo = DeviceInfo(
                id = getId,
                id_method = "InstanceId",
                keywords = getKeywords,
                subscriptions = getSubscriptions,
                alarmSwitch = getAlarmSwitch
            )

            apiService.getDevice(context, getId) {
                //서버에 데이터가 있는지 확인 (GET)
                if(it?.id != null) {
                    Log.d("User_get","id != null")
                    isGetFailed = false
                } else {
                    Log.d("User_get","id = null")
                    isGetFailed = true
                }

                if(isGetFailed) {
                    //서버에 데이터가 없으면 서버에 데이터 저장 (POST)
                    apiService.postDevice(context,deviceInfo) {
                        if (it?.id != null) {
                            // it = newly added user parsed as response
                            // it?.id = newly added user ID
                            Log.d("User_post", "id != null")
                        } else {
                            Log.d("User_post", "id = null")
                        }
                    }
                }
                else {
                    //서버에 데이터가 있으면 데이터 변경 (PUT)
                    apiService.putDevice(context,deviceInfo) {
                        if (it?.id != null) {
                            // it = newly added user parsed as response
                            // it?.id = newly added user ID
                            Log.d("User_put", "id != null")
                        } else {
                            Log.d("User_put", "id = null")
                        }
                    }
                }
            }
        }

    }
}