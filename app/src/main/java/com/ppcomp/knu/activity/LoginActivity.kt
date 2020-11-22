package com.ppcomp.knu.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import kotlinx.android.synthetic.main.activity_login.*
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
    private lateinit var myToast: Toast
    private var backWait: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val isNewUser = PreferenceHelper.get("NewUser", true) // 신규 사용자 확인

        callback = SessionCallback()
        Session.getCurrentSession().clearCallbacks()
        Session.getCurrentSession().addCallback(callback)
        Session.getCurrentSession().checkAndImplicitOpen()  //로그인 이력이 있으면 재접속해도 로그인 유지해줌

        val title = findViewById<TextView>(R.id.state_title)
        title.text = "로그인"

        myToast = Toast.makeText(this, "", Toast.LENGTH_SHORT)

        searchIcon = findViewById<ImageView>(R.id.search_icon)
        searchIcon.visibility = View.GONE

        setSupportActionBar(main_layout_toolbar)//toolbar 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(!isNewUser) //toolbar 설정 (신규 유저가 아닐 때만 True)
        supportActionBar?.setDisplayShowTitleEnabled(false) //타이틀 안보이게 하기
        supportActionBar?.setDisplayHomeAsUpEnabled(false)  //뒤로가기 버튼 제거

        exitButton.setOnClickListener {
            ActivityCompat.finishAffinity(this)
            System.exit(0)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceHelper.put("NewUser", false)
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
     * 뒤로가기 버튼 이벤트 설정(스마트폰의 뒤로가기 버튼)
     * @author 정준
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBackPressed() {
        if (System.currentTimeMillis() - backWait >= 2000) {
            backWait = System.currentTimeMillis()
            myToast.setText("뒤로가기 버튼을 한 번 더 누르면 종료됩니다.")
            myToast.show()
        } else {
            moveTaskToBack(true)			// 태스크를 백그라운드로 이동
            finishAndRemoveTask() // 액티비티 종료 + 태스크 리스트에서 지우기
            android.os.Process.killProcess(android.os.Process.myPid())	// 앱 프로세스 종료
        }
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
                        "로그인 성공",
                        Toast.LENGTH_SHORT
                    ).show()
                    GlobalApplication.isLogin = true    //로그인 상태 업데이트
                    GlobalApplication.userInfoUpload(this@LoginActivity)    //카카오계정 데이터 api서버에 추가
                    PreferenceHelper.put("kakaoId",kakaoId)
                    PreferenceHelper.put("nickname",kakaoNickname) //닉네임 저장
                    if(PreferenceHelper.get("NewUser", true) || GlobalApplication.isFirstLogin) { //신규 사용자이면 메인화면으로
                        PreferenceHelper.put("NewUser", false)
                        GlobalApplication.isFirstLogin = false
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else {
                        val intent = Intent(this@LoginActivity, UserInfoActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
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