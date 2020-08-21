package com.ppcomp.knu.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.auth.ApiErrorCode
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import com.ppcomp.knu.GlobalApplication
import com.ppcomp.knu.R
import com.ppcomp.knu.utils.PreferenceHelper
import kotlinx.android.synthetic.main.activity_main_toolbar.*

/**
 * 로그인 화면 Activity
 * @author 정준
 */

class LoginActivity : AppCompatActivity() {
    private var callback: SessionCallback = SessionCallback() //카카오에서 제공하는 콜백함수
    private lateinit var kakaoId: String
    private lateinit var kakaoNickname: String
    private lateinit var kakakoThumbnail: String
    private lateinit var searchIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        callback = SessionCallback()
        Session.getCurrentSession().clearCallbacks()
        Session.getCurrentSession().addCallback(callback)
        Session.getCurrentSession().checkAndImplicitOpen()  //로그인 이력이 있으면 재접속해도 로그인 유지해줌

        val title = findViewById<TextView>(R.id.state_title)
        title.text = "로그인"

        searchIcon = findViewById<ImageView>(R.id.search_icon)
        searchIcon.visibility = View.GONE

        setSupportActionBar(main_layout_toolbar)//toolbar 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.move_back_ic)//뒤로가기 아이콘 지정
        supportActionBar?.setDisplayShowTitleEnabled(false) //타이틀 안보이게 하기
    }

    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(callback)
    }

    /**
     * 로그인 후 액티비티로 결과데이터 받아오는 메소드
     * @author 정준
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // 카카오 간편로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 카카오로그인 처리해주는 콜백함수
     * @author 정준
     */
    private inner class SessionCallback : ISessionCallback {

        override fun onSessionOpened() {
            // 로그인 세션이 열렸을 때
            UserManagement.getInstance().me(object : MeV2ResponseCallback() {
                override fun onSuccess(result: MeV2Response?) {
                    // 로그인이 성공했을 때
                    kakaoId = result!!.id.toString()
                    kakaoNickname = result!!.kakaoAccount.profile.nickname
                    kakakoThumbnail = result!!.kakaoAccount.profile.thumbnailImageUrl
                    Toast.makeText(
                        this@LoginActivity,
                        "로그인 성공! 계정 이름: " + kakaoNickname,
                        Toast.LENGTH_SHORT
                    ).show()
                    GlobalApplication.isLogin = true    //로그인 상태 업데이트
                    GlobalApplication.KakaoUserInfoUpload()    //카카오계정 데이터 api서버에 추가
                    PreferenceHelper.put("kakaoId",kakaoId)
                    PreferenceHelper.put("nickname",kakaoNickname) //닉네임 저장
                    PreferenceHelper.put("thumbnail",kakakoThumbnail) //썸네일 저장

                    val intent = Intent(this@LoginActivity, UserInfoActivity::class.java)
                    startActivity(intent)
                    finish()


                }

                override fun onSessionClosed(errorResult: ErrorResult?) {
                    // 로그인 도중 세션이 비정상적인 이유로 닫혔을 때
                    Toast.makeText(
                        this@LoginActivity,
                        "세션이 닫혔습니다. 다시 시도해주세요 : ${errorResult.toString()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onFailure(errorResult: ErrorResult?) {
                    //로그인에 실패했을 때, 네트워크 불안정한 경우도 여기에 해당
                    var result = errorResult?.errorCode

                    if (result == ApiErrorCode.CLIENT_ERROR_CODE) {
                        Toast.makeText(this@LoginActivity, "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT)
                            .show()
                        this@LoginActivity?.finish()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "로그인 도중 오류가 발생했습니다: " + errorResult?.errorMessage,
                            Toast.LENGTH_SHORT
                        )
                    }
                }
            })
        }

        override fun onSessionOpenFailed(exception: KakaoException?) {
            // 로그인 세션이 정상적으로 열리지 않았을 때
            if (exception != null) {
                com.kakao.util.helper.log.Logger.e(exception)
                Toast.makeText(
                    this@LoginActivity,
                    "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요 : $exception",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}