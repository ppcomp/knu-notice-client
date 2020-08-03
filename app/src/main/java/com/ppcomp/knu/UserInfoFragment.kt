package com.ppcomp.knu

import android.content.Context
import android.os.Bundle

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback

import kotlinx.android.synthetic.main.fragment_user_info.*


/**
 * 회원정보를 보여주는 프래그먼트
 * @author 정준
 */
class UserInfoFragment : Fragment() {
    private lateinit var nickname: String
    private lateinit var subscriptions: String
    private lateinit var thumbnail: String
    private lateinit var keywords: String
    private lateinit var subscriptionsSplit: Array<String>
    private lateinit var keywordsSplit: Array<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_info, container, false)
    }

    /**
     * 로그아웃 버튼 누를시 로그아웃 실행 후 LoginFragment로 전환 기능 및
     * 각종 데이터 출력 기능
     * @author 정준
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_logout.setOnClickListener {
            Toast.makeText(activity, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
            UserManagement.getInstance().requestLogout(object : LogoutResponseCallback() {
                override fun onCompleteLogout() {
                    GlobalApplication.isLogin = false   //로그인 상태 업데이트
                    (activity as MainActivity).addFragment(LoginFragment())
                }
            })
        }

        val pref = activity?.getSharedPreferences("pref", Context.MODE_PRIVATE)
        this.nickname = pref?.getString("nickname","").toString()
        this.thumbnail = pref?.getString("thumbnail","").toString()
        this.subscriptions = pref?.getString("Subs","").toString().replace("+",", ")
        this.subscriptionsSplit = subscriptions.split(", ").toTypedArray()
        this.keywords = pref?.getString("Keys","").toString().replace("+",", ")
        this.keywordsSplit = keywords.split(", ").toTypedArray()

        view_userName.text = nickname   //카카오 닉네임 출력
        view_subscription_data.text = subscriptions //구독 목록 출력
        view_subscription_cnt.text = subscriptionsSplit.size.toString() //구독 갯수 출력
        view_keyword_data.text = keywords   //키워드 목록 출력
        view_keyword_cnt.text = keywordsSplit.size.toString()   //키워드 갯수 출력

        Glide.with(this).load(thumbnail).placeholder(R.drawable.nav_madeby_icon).into(iv_thumbnail) //카카오 프로필 사진 띄우기
    }
}