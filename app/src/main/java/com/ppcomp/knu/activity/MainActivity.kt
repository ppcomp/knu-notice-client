package com.ppcomp.knu.activity

//import kotlinx.android.synthetic.main.main.*
//parsing 부분
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.kakao.auth.Session
import com.ppcomp.knu.*
import com.ppcomp.knu.fragment.*
import kotlin.math.log


/**
 * 메인화면의 기능을 작성하는 클래스
 * @author 희진, jungwoo
 */
class MainActivity : AppCompatActivity() {

    var settingFragment = SettingFragment()
    var loginFragment = LoginFragment()
    var userInfoFragment = UserInfoFragment()
    var noticeFragment = NoticeFragment()
    var keywordNoticeFragment = KeywordNoticeFragment()
    var activeFragment: Fragment = noticeFragment   //현재 띄워진 프레그먼트(default: noticeFragment)
    var searchFragment = SearchFragment()


    /**
     * 화면생성해주는 메소드
     * @author 희진, 우진, jungwoo, 정준
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        content = findViewById(R.id.frameLayout)
        val navigation = findViewById<BottomNavigationView>(R.id.main_navigationView)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        supportFragmentManager.beginTransaction().apply {       // 모든 프레그먼트 삽입
            add(R.id.frameLayout, settingFragment, settingFragment.javaClass.simpleName).hide(settingFragment)
            add(R.id.frameLayout, loginFragment, loginFragment.javaClass.simpleName).hide(loginFragment)
            add(R.id.frameLayout, userInfoFragment, userInfoFragment.javaClass.simpleName).hide(userInfoFragment)
            add(R.id.frameLayout, noticeFragment, noticeFragment.javaClass.simpleName)
            add(R.id.frameLayout,keywordNoticeFragment,keywordNoticeFragment.javaClass.simpleName).hide(keywordNoticeFragment)
            add(R.id.frameLayout, searchFragment, searchFragment.javaClass.simpleName).hide(searchFragment)
    }.commit()

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
     * 메뉴 클릭시 이동
     * @author 희진, 정준
     */

    private var content: FrameLayout? = null
    var listLocationCount =1
    var keywordlistLocationCount =1

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.setting -> {
                    listLocationCount =0
                    keywordlistLocationCount =0
                    addFragment(settingFragment)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.login -> {
                    listLocationCount =0
                    keywordlistLocationCount=0
                    if (!GlobalApplication.isLogin) {
                        addFragment(loginFragment)
                        return@OnNavigationItemSelectedListener true
                    } else {
                        replaceFragment(userInfoFragment)   //화면갱신
                        addFragment(userInfoFragment)
                        return@OnNavigationItemSelectedListener true
                    }
                }

                R.id.list -> {
                    keywordlistLocationCount=0
                    if(GlobalApplication.isSubsChange) {    //구독리스트에 변경사항이 있으면 화면 갱신
                        replaceFragment(noticeFragment)     //화면갱신
                        GlobalApplication.isSubsChange = false  //변경사항 갱신 후 false로 변경
                        listLocationCount=0
                    }
                    listLocationCount++
                    if(listLocationCount >=2){
                        var recyclerview = noticeFragment.view!!.findViewById(R.id.notice) as RecyclerView
                        recyclerview.scrollToPosition(0)
                    }
                    addFragment(noticeFragment)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.keywordlist -> {
                    listLocationCount =0
                    if(GlobalApplication.iskeywordChange) {    //구독리스트에 변경사항이 있으면 화면 갱신
                        replaceFragment(keywordNoticeFragment)  //화면갱신
                        GlobalApplication.iskeywordChange = false  //변경사항 갱신 후 false로 변경
                        keywordlistLocationCount =0
                    }
                    keywordlistLocationCount++
                    if(keywordlistLocationCount >=2){
                        var recyclerview = keywordNoticeFragment.view!!.findViewById(R.id.keyword_notice) as RecyclerView
                        recyclerview.scrollToPosition(0)
                    }
                    addFragment(keywordNoticeFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.search -> {
                    listLocationCount =0
                    if(GlobalApplication.isSearchChange) {    //구독리스트에 변경사항이 있으면 화면 갱신
                        replaceFragment(searchFragment)  //화면갱신
                        GlobalApplication.isSearchChange = false  //변경사항 갱신 후 false로 변경
                        listLocationCount =0
                    }
                    listLocationCount++
                    if(listLocationCount >=2){
                        var recyclerview = searchFragment.view!!.findViewById(R.id.search_recycler) as RecyclerView
                        recyclerview.scrollToPosition(0)
                    }
                    addFragment(searchFragment)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    /**
     * 하단 바 아이템 누르면 fragment 변경
     * @author 희진, 정준
     */
    fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(0, 0)
            .hide(activeFragment)
            .show(fragment)
            .commit()
        activeFragment = fragment
    }

    /**
     * 화면 갱신에 사용하는 함수
     * @author 정준
     */
    fun replaceFragment(fragment: Fragment) {
        when(fragment) {
            settingFragment -> {
                supportFragmentManager.beginTransaction().apply {
                    remove(settingFragment)
                    settingFragment = SettingFragment()
                    add(R.id.frameLayout, settingFragment, settingFragment.javaClass.simpleName)
                }.commit()
            }
            loginFragment -> {
                supportFragmentManager.beginTransaction().apply {
                    remove(loginFragment)
                    loginFragment = LoginFragment()
                    add(R.id.frameLayout, loginFragment, loginFragment.javaClass.simpleName)
                }.commit()
            }
            userInfoFragment -> {
                supportFragmentManager.beginTransaction().apply {
                    remove(userInfoFragment)
                    userInfoFragment = UserInfoFragment()
                    add(R.id.frameLayout, userInfoFragment, userInfoFragment.javaClass.simpleName)
                }.commit()
            }
            noticeFragment -> {
                supportFragmentManager.beginTransaction().apply {
                    remove(noticeFragment)
                    noticeFragment = NoticeFragment()
                    add(R.id.frameLayout, noticeFragment, noticeFragment.javaClass.simpleName)
                }.commit()
            }
            keywordNoticeFragment -> {
                supportFragmentManager.beginTransaction().apply {
                    remove(keywordNoticeFragment)
                    keywordNoticeFragment = KeywordNoticeFragment()
                    add(R.id.frameLayout, keywordNoticeFragment, keywordNoticeFragment.javaClass.simpleName)
                }.commit()
            }
            searchFragment -> {
                supportFragmentManager.beginTransaction().apply {
                    remove(searchFragment)
                    searchFragment = SearchFragment()
                    add(R.id.frameLayout, searchFragment, searchFragment.javaClass.simpleName)
                }.commit()
            }
        }

    }

    /**
     * 뒤로가기 버튼 누르면 네비게이션 바 close
     * @author 희진
     */
    var BackWait: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() - BackWait >= 2000) {
            BackWait = System.currentTimeMillis()
            Toast.makeText(this, "뒤로가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            super.onBackPressed()
        }
    }

    /**
     * 화면 터치시 키보드 숨김
     * @author 희진
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }

        return super.dispatchTouchEvent(ev)
    }
}

