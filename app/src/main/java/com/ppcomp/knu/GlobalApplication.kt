package com.ppcomp.knu

import android.app.Application
import com.kakao.auth.KakaoSDK
import com.ppcomp.knu.adapter.KakaoSDKAdapter

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
        var isLogin: Boolean = false    //로그인 상태
        var instance: GlobalApplication? = null
    }
}