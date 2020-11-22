package com.ppcomp.knu.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import com.ppcomp.knu.GlobalApplication
import com.ppcomp.knu.R
import com.ppcomp.knu.utils.PreferenceHelper
import kotlinx.android.synthetic.main.activity_main_toolbar.*
import kotlinx.android.synthetic.main.activity_userinfo.*

/**
 * 회원정보 화면 Activity
 * @author 정준
 */
class UserInfoActivity : AppCompatActivity(){
    private lateinit var nickname: String
    private lateinit var subscriptions: String
    private lateinit var keywords: String
    private lateinit var searchIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinfo)

        btn_logout.setOnClickListener {
            Toast.makeText(this, "로그아웃되었습니다.\n로그인 이후에 서비스 이용 가능합니다.", Toast.LENGTH_SHORT).show()
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
        subscriptions = PreferenceHelper.get("Subs","").toString().replace("+",", ")
        keywords = PreferenceHelper.get("Keys","").toString().replace("+",", ")

        view_userName.text = nickname+"님 반갑습니다"   //카카오 닉네임 출력
        view_subscription_data.text = subscriptions //구독 목록 출력
        view_keyword_data.text = keywords   //키워드 목록 출력

        val title = findViewById<TextView>(R.id.state_title)
        title.text = "회원 정보"

        searchIcon = findViewById<ImageView>(R.id.search_icon)
        searchIcon.visibility = View.GONE

        setSupportActionBar(main_layout_toolbar)//toolbar 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //toolbar 설정
        supportActionBar?.setDisplayShowTitleEnabled(false) //타이틀 안보이게 하기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)   //뒤로가기 버튼 보이기


    }


    /**
     * 툴바 뒤로가기 버튼 동작설정
     * @author 정준
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
             android.R.id.home ->
                 finish()
        }
        return super.onOptionsItemSelected(item)
    }
}