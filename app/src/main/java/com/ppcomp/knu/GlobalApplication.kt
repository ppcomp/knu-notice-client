package com.ppcomp.knu

import RestApiService
import android.app.Application
import android.content.Context
import android.util.Log
import com.kakao.auth.KakaoSDK
import com.ppcomp.knu.`object`.KakaoUserInfo
import com.ppcomp.knu.`object`.UserInfo
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
        fun kakaoUserInfoUpload() {
            var isGetFailed: Boolean = false
            val apiService = RestApiService()
            val getId = PreferenceHelper.get("fbId","")
            val getKakaoId = PreferenceHelper.get("kakaoId","").toString()
            val userInfo = KakaoUserInfo(
                id = getKakaoId,
                device_id = getId
            )

            apiService.getKakaoUser(getKakaoId) {
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
                    apiService.addKakaoUser(userInfo) {
                        if (it?.id != null) {
                            // it = newly added user parsed as response  687618f9-8529-4ff6-be9e-60dc57a2f267
                            // it?.id = newly added user ID
                            Log.d("kakaoUser_post", "id != null")
                        } else {
                            Log.d("kakaoUser_post", "id = null")
                        }
                    }
                }
            }
        }

        /**
         * 유저 데이터 서버에 업로드
         * @author 정준, 정우
         */
        fun userInfoUpload() {
            var isGetFailed: Boolean = false
            val apiService = RestApiService()
            val getId = PreferenceHelper.get("fbId","").toString()
            val getKeywords: String? = PreferenceHelper.get("Keys", "")
            val getSubscriptions: String? = PreferenceHelper.get("Urls", "")
            val getAlarmSwitch: Boolean? = PreferenceHelper.get("alarmSwitch", false)
            val userInfo = UserInfo(
                id = getId,
                id_method = "InstanceId",
                keywords = getKeywords,
                subscriptions = getSubscriptions,
                alarmSwitch = getAlarmSwitch
            )

            apiService.getUser(getId) {
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
                    apiService.addUser(userInfo) {
                        if (it?.id != null) {
                            // it = newly added user parsed as response  687618f9-8529-4ff6-be9e-60dc57a2f267
                            // it?.id = newly added user ID
                            Log.d("User_post", "id != null")
                        } else {
                            Log.d("User_post", "id = null")
                        }
                    }
                }
                else {
                    //서버에 데이터가 있으면 데이터 변경 (PUT)
                    apiService.modifyUser(userInfo) {
                        if (it?.id != null) {
                            // it = newly added user parsed as response  687618f9-8529-4ff6-be9e-60dc57a2f267
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