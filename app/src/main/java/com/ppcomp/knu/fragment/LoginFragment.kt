package com.ppcomp.knu.fragment

import RestApiService
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import com.ppcomp.knu.`object`.KakaoUserInfo
import com.ppcomp.knu.activity.MainActivity

/**
 * 하단 바 '로그인'페이지의  kt
 * @author 희진, 정준
 */
class LoginFragment : Fragment() {
    private var callback: SessionCallback = SessionCallback() //카카오에서 제공하는 콜백함수
    private lateinit var kakaoId: String
    private lateinit var kakaoNickname: String
    private lateinit var kakakoThumbnail: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callback = SessionCallback()
        Session.getCurrentSession().clearCallbacks()
        Session.getCurrentSession().addCallback(callback)
        Session.getCurrentSession().checkAndImplicitOpen()  //로그인 이력이 있으면 재접속해도 로그인 유지해줌
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(callback)
    }

    /**
     * 카카오로그인 처리해주는 콜백함수
     * @author 정준
     */
    private inner class SessionCallback : ISessionCallback {

        val pref = activity?.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val ed = pref?.edit()

        override fun onSessionOpened() {
            // 로그인 세션이 열렸을 때

            UserManagement.getInstance().me(object : MeV2ResponseCallback() {
                override fun onSuccess(result: MeV2Response?) {
                    // 로그인이 성공했을 때
                    kakaoId = result!!.id.toString()
                    kakaoNickname = result!!.kakaoAccount.profile.nickname
                    kakakoThumbnail = result!!.kakaoAccount.profile.thumbnailImageUrl
                    Toast.makeText(
                        activity,
                        "로그인 성공! 계정 이름: " + kakaoNickname,
                        Toast.LENGTH_SHORT
                    ).show()
                    GlobalApplication.isLogin = true    //로그인 상태 업데이트
                    userInfoUpload()    //카카오계정 데이터 api서버에 추가
                    ed?.putString("nickname",kakaoNickname) //닉네임 저장
                    ed?.putString("thumbnail",kakakoThumbnail) //썸네일 저장
                    ed?.commit()
                    Log.d("kakaoLogin",result!!.kakaoAccount.profile.thumbnailImageUrl)
                    (activity as MainActivity).addFragment((activity as MainActivity).userInfoFragment)  //UserInfoFragment로 화면전환
                }

                override fun onSessionClosed(errorResult: ErrorResult?) {
                    // 로그인 도중 세션이 비정상적인 이유로 닫혔을 때
                    Toast.makeText(
                        activity,
                        "세션이 닫혔습니다. 다시 시도해주세요 : ${errorResult.toString()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onFailure(errorResult: ErrorResult?) {
                    //로그인에 실패했을 때, 네트워크 불안정한 경우도 여기에 해당
                    var result = errorResult?.errorCode

                    if (result == ApiErrorCode.CLIENT_ERROR_CODE) {
                        Toast.makeText(activity, "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT)
                            .show()
                        activity?.finish()
                    } else {
                        Toast.makeText(
                            activity,
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
                    activity,
                    "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요 : $exception",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * 카카오유저데이터 서버에 업로드
     * @author 정준
     */
    fun userInfoUpload() {
        val pref = this.activity?.getSharedPreferences("pref", Context.MODE_PRIVATE)
        var isGetFailed: Boolean = false
        val apiService = RestApiService()
        val getUID = pref?.getString("UID","")
        val userInfo = KakaoUserInfo(
            id = kakaoId,
//                                        email = "test1234@gmail.com",
            device_id = getUID
        )

        apiService.getKakaoUser(kakaoId) {
            //서버에 데이터가 있는지 확인
            if(it?.id != null) {
                Log.d("kakaoUser_get","id != null")
                isGetFailed = false
            } else {
                Log.d("kakaoUser_get","id = null")
                isGetFailed = true
            }

            if(isGetFailed) {
                //서버에 데이터가 없으면 서버에 데이터 저장
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


}
