package com.ppcomp.knu

import RestApiService
import android.app.Application
import android.content.Context
import android.util.Log
import com.facebook.stetho.Stetho
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.kakao.auth.KakaoSDK
import com.ppcomp.knu.`object`.UserInfo
import com.ppcomp.knu.`object`.DeviceInfo
import com.ppcomp.knu.`object`.Subscription
import com.ppcomp.knu.adapter.KakaoSDKAdapter
import com.ppcomp.knu.utils.LoadingDialog
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
        var isLaunchApp: Boolean = true    //앱 시작할 때 로그인 확인
        var isNewUser: Boolean = false

        /**
         * Check available version from server.
         * @return true if available.
         * false if not available(need update).
         * @author 정우
         */
        fun checkVersion(): Boolean {
            val apiService = RestApiService()
            val res = apiService.getVersion() ?: return true
            return if (res.availableVersionCode <= BuildConfig.VERSION_CODE) {
                if (res.latest!!.split("-")[0].toInt() == BuildConfig.VERSION_CODE) {
                    Log.d("checkVersion", "최신버전")
                }
                true
            } else {
                false
            }
        }

        fun userInfoCheck(context: Context) {
            val apiService = RestApiService()
            val getId = PreferenceHelper.get("fbId","")
            val getKakaoId = PreferenceHelper.get("kakaoId","").toString()
            val userInfo = UserInfo(
                id = getKakaoId,
                device = getId
            )
            val dialog = LoadingDialog(context)
            dialog.show()
            apiService.getUser(context,getKakaoId) {
                //서버에 데이터가 있는지 확인 (GET)
                if(it?.id != null) {
                    Log.d("kakaoUser_get","id != null")
                } else {
                    Log.d("kakaoUser_get","id = null")
                    GlobalApplication.isNewUser = true  //서버에 정보 없으면 신규유저
                }
            }

            dialog.dismiss()
        }

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
                device = getId
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
                    getSyncDeviceId = userInfo.device.toString()
                    apiService.getDevice(context, getSyncDeviceId) { deviceInfo ->
                        //서버에 저장된 기기Id의 데이터 다운 (GET)
                        if(deviceInfo?.id != null) {
                            PreferenceHelper.put("Keys",deviceInfo.keywords.toString())
                            PreferenceHelper.put("subCodes",deviceInfo.subscriptions.toString())
                            PreferenceHelper.put("alarmSwitchSub",(deviceInfo.alarmSwitchSub.toString() == "true"))
                            PreferenceHelper.put("alarmSwitchKey",(deviceInfo.alarmSwitchKey.toString() == "true"))
                            updateSharedPreferences(deviceInfo.subscriptions.toString())
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
            val getSubscriptions: String? = PreferenceHelper.get("subCodes", "")
            val getAlarmSwitchSub: Boolean? = PreferenceHelper.get("alarmSwitchSub", false)
            val getAlarmSwitchKey: Boolean? = PreferenceHelper.get("alarmSwitchKey", false)
            val deviceInfo = DeviceInfo(
                id = getId,
                id_method = "InstanceId",
                keywords = getKeywords,
                subscriptions = getSubscriptions,
                alarmSwitchSub = getAlarmSwitchSub,
                alarmSwitchKey = getAlarmSwitchKey
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
                    //서버에 데이터가 있으면 데이터 업데이트 (PUT)
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

        /**
         * 디바이스 데이터 서버에 업데이트 (PUT)
         * @author 정준
         */
        fun deviceInfoUpdate(context: Context) {
            val apiService = RestApiService()
            val getId = PreferenceHelper.get("fbId","").toString()
            val getKeywords: String? = PreferenceHelper.get("Keys", "")
            val getSubscriptions: String? = PreferenceHelper.get("subCodes", "null")
            val getAlarmSwitchSub: Boolean? = PreferenceHelper.get("alarmSwitchSub", false)
            val getAlarmSwitchKey: Boolean? = PreferenceHelper.get("alarmSwitchKey", false)
            val deviceInfo = DeviceInfo(
                id = getId,
                id_method = "InstanceId",
                keywords = getKeywords,
                subscriptions = getSubscriptions,
                alarmSwitchSub = getAlarmSwitchSub,
                alarmSwitchKey = getAlarmSwitchKey
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

        /**
         * Urls 를 바탕으로 SharedPreferences 의 subList 와 Subs 를 업데이트
         * @author 정우
         */
        private fun updateSharedPreferences(codes: String) {
            val listType = object : TypeToken<ArrayList<Subscription>>() {}
            var strConcat = PreferenceHelper.get("subList", "").toString()
            val makeGson = GsonBuilder().create()
            val subList: ArrayList<Subscription> = makeGson.fromJson(strConcat, listType.type)
            val subNameList = ArrayList<String>()
            val subCodeList = ArrayList<String>()
            for (code in codes.split("+")) {
                for (sub in subList) {
                    if (sub.code == code) {
                        sub.checked = true
                        subNameList.add(sub.name)
                        subCodeList.add(sub.code)
                    } else {
                        sub.checked = false
                    }
                }
            }

            strConcat = makeGson.toJson(subList, listType.type)
            PreferenceHelper.put("subList", strConcat)
            PreferenceHelper.put("subNames", subNameList.joinToString("+"))
            PreferenceHelper.put("subCodes", subCodeList.joinToString("+"))
        }
    }
}