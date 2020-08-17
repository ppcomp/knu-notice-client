package com.ppcomp.knu.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import com.ppcomp.knu.GlobalApplication
import com.ppcomp.knu.R
import com.ppcomp.knu.utils.PreferenceHelper
import kotlinx.android.synthetic.main.activity_userinfo_toolbar.*
import kotlinx.android.synthetic.main.activity_userinfo.*
import kotlinx.android.synthetic.main.fragment_setting.*

/**
 * 회원정보 화면 Activity
 * @author 정준
 */
class UserInfoActivity : AppCompatActivity(){
    private lateinit var nickname: String
    private lateinit var subscriptions: String
    private lateinit var thumbnail: String
    private lateinit var keywords: String
    private lateinit var subscriptionsSplit: Array<String>
    private lateinit var keywordsSplit: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinfo)

        btn_logout.setOnClickListener {
            Toast.makeText(this, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
            UserManagement.getInstance().requestLogout(object : LogoutResponseCallback() {
                override fun onCompleteLogout() {
                    GlobalApplication.isLogin = false   //로그인 상태 업데이트
                    val intent = Intent(this@UserInfoActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                }
            })
        }

        nickname = PreferenceHelper.get("nickname","").toString()
        thumbnail = PreferenceHelper.get("thumbnail","").toString()
        subscriptions = PreferenceHelper.get("Subs","").toString().replace("+",", ")
        subscriptionsSplit = subscriptions.split(", ").toTypedArray()
        keywords = PreferenceHelper.get("Keys","").toString().replace("+",", ")
        keywordsSplit = keywords.split(", ").toTypedArray()

        view_userName.text = nickname   //카카오 닉네임 출력
        view_subscription_data.text = subscriptions //구독 목록 출력
        view_subscription_cnt.text = subscriptionsSplit.size.toString() //구독 갯수 출력
        view_keyword_data.text = keywords   //키워드 목록 출력
        view_keyword_cnt.text = keywordsSplit.size.toString()   //키워드 갯수 출력

        Glide.with(this).load(thumbnail).placeholder(R.drawable.nav_maker_ic).into(iv_thumbnail) //카카오 프로필 사진 띄우기
    }
}