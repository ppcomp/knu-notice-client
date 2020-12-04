package com.ppcomp.knu

import RestApiService
import android.app.Application
import android.content.Context
import android.util.Log
import com.facebook.stetho.Stetho
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
        Stetho.initializeWithDefaults(this) //DB 확인용 라이브러리
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
        var isFragmentChange: Array<Boolean> = arrayOf(false, false, false) //프레그먼트 변경사항 확인 (notice, keywordNotice, bookmark)
        var isServerConnect: Boolean = true    //서버 연결 상태
        var isFirstLogin: Boolean = true    //앱 시작할 때 로그인 확인

        /**
         * 유저 데이터 서버에 업로드
         * @author 정준
         */
        fun userInfoUpload(context: Context) {
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
         * 서버에 유저 데이터 저장되어 있는지 확인 후
         * 구독리스트, 키워드 다운로드 (GET)
         * @author 정준
         */
        fun userInfoDownload(context: Context) {
            val apiService = RestApiService()
            val getKakaoId = PreferenceHelper.get("kakaoId","").toString()
            var getLocalDeviceId = PreferenceHelper.get("fbId","")
            var getSyncDeviceId: String
            apiService.getUser(context, getKakaoId) { userInfo ->
                //서버에 유저데이터가 있는지 확인 (GET)
                if(userInfo?.id != null) {
                    getSyncDeviceId = userInfo.device_id.toString()

                    if(getSyncDeviceId != getLocalDeviceId) {
                        //서버에 저장된 기기Id랑 로컬기기Id가 다르면
                        apiService.getDevice(context, getSyncDeviceId) { deviceInfo ->
                            //서버에 저장된 기기Id의 데이터 다운 (GET)
                            if(deviceInfo?.id != null) {
                                PreferenceHelper.put("Keys",deviceInfo.keywords.toString())
                                PreferenceHelper.put("Urls",deviceInfo.subscriptions.toString())
                                PreferenceHelper.put("alarmSwitch",(deviceInfo.alarmSwitch.toString() == "true"))
                            }
                        }
                    }

                }
            }
        }

        /**
         * 디바이스 데이터 서버에 업로드 (GET, POST)
         * @author 정준, 정우
         */
        fun deviceInfoUpload(context: Context) {
            var isGetFailed: Boolean = false
            val apiService = RestApiService()
            val getId = PreferenceHelper.get("fbId","").toString()
            val getKeywords: String? = PreferenceHelper.get("Keys", "")
            val getSubscriptions: String? = PreferenceHelper.get("Urls", "")
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
            }
        }

        /**
         * 디바이스 데이터 서버에 업데이트 (PUT)
         * @author 정준
         */
        fun deviceInfoUpdate(context: Context) {
            val apiService = RestApiService()
            val getId = PreferenceHelper.get("fbId","").toString()
            val getKeywords: String? = PreferenceHelper.get("Keys", "")
            val getSubscriptions: String? = PreferenceHelper.get("Urls", "null")
            val getAlarmSwitch: Boolean? = PreferenceHelper.get("alarmSwitch", false)
            val deviceInfo = DeviceInfo(
                id = getId,
                id_method = "InstanceId",
                keywords = getKeywords,
                subscriptions = getSubscriptions,
                alarmSwitch = getAlarmSwitch
            )

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