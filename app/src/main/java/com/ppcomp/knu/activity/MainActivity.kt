package com.ppcomp.knu.activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ppcomp.knu.*
import com.ppcomp.knu.fragment.*


/**
 * 메인화면 클래스
 * @author 희진, jungwoo
 */
class MainActivity : AppCompatActivity() {

    private var noticeFragment = NoticeFragment()
    private var keywordNoticeFragment = KeywordNoticeFragment()
    private var bookmarkFragment = BookmarkFragment()
    private var settingFragment = SettingFragment()
    private var alarmFragment = AlarmFragment()
    private var activeFragment: Fragment = noticeFragment   //현재 띄워진 프레그먼트(default: noticeFragment)

    /**
     * 메인 레이아웃과 fragment 화면 생성
     * 모든 fragment는 이 함수에서 초기화 되어야 함
     * @author 희진, 우진, jungwoo, 정준
     */
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        content = findViewById(R.id.frameLayout)
        val navigation = findViewById<BottomNavigationView>(R.id.main_navigationView)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        supportFragmentManager.beginTransaction().apply {       // 모든 프레그먼트 삽입
            add(R.id.frameLayout, noticeFragment, noticeFragment.javaClass.simpleName)
            add(R.id.frameLayout, keywordNoticeFragment, keywordNoticeFragment.javaClass.simpleName).hide(keywordNoticeFragment)
            add(R.id.frameLayout, bookmarkFragment, bookmarkFragment.javaClass.simpleName).hide(bookmarkFragment)
            add(R.id.frameLayout, settingFragment, settingFragment.javaClass.simpleName).hide(settingFragment)
            add(R.id.frameLayout, alarmFragment, alarmFragment.javaClass.simpleName).hide(alarmFragment)
        }.commit()


    }
    
    /**
     * 하단 메뉴 클릭시 fragment 전환
     * isFragmentChange 변수는 각 fragment화면에 갱신될 일이 생기면 true로 지정 됨
     * isFragmentChange[0]: notice, isFragmentChange[1]: keywordNotice, isFragmentChange[2]: bookmark
     * @author 희진, 정준
     */
    private var content: FrameLayout? = null
    private var setScrollTop = -1   // 0:notice, 1:keywordNotice, 2:bookmark
    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.noticelist -> {
                    if(GlobalApplication.isFragmentChange[0]) { //구독리스트, 북마크리스트에 변경사항이 있으면 화면 갱신
                        replaceFragment(noticeFragment) //화면갱신
                        GlobalApplication.isFragmentChange[0] = false
                    }
                    if(setScrollTop == 0){      //setScrollTop이 0이면 scroll이 맨 위로 이동
                        (noticeFragment.view?.findViewById(R.id.notice) as RecyclerView).apply {
                            scrollToPosition(0)
                        }
                    }
                    setScrollTop = 0
                    addFragment(noticeFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.keywordlist -> {
                    if(GlobalApplication.isFragmentChange[1]) { //구독리스트, 키워드 리스트, 북마크리스트에 변경사항이 있으면 화면 갱신
                        replaceFragment(keywordNoticeFragment)  //화면갱신
                        GlobalApplication.isFragmentChange[1] = false
                    }
                    if(setScrollTop == 1){      //setScrollTop이 1이면 scroll이 맨 위로 이동
                        (keywordNoticeFragment.view?.findViewById(R.id.keyword_notice) as RecyclerView).apply {
                            scrollToPosition(0)
                        }
                    }
                    setScrollTop = 1
                    addFragment(keywordNoticeFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bookmark -> {
                    if(GlobalApplication.isFragmentChange[2]) { //북마크리스트에 변경사항이 있으면 화면 갱신
                        replaceFragment(bookmarkFragment)   //화면갱신
                        GlobalApplication.isFragmentChange[2] = false
                    }
                    if(setScrollTop == 2){      //setScrollTop이 2이면 scroll이 맨 위로 이동
                        (bookmarkFragment.view?.findViewById(R.id.bookmark_notice) as RecyclerView).apply {
                            scrollToPosition(0)
                        }
                    }
                    setScrollTop = 2
                    addFragment(bookmarkFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.setting -> {
                    setScrollTop = -1
                    addFragment(settingFragment)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.alarm -> {
                    replaceFragment(alarmFragment)
                    setScrollTop = 3
                    addFragment(alarmFragment)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    /**
     * fragment 전환에 사용하는 함수
     * @author 희진, 정준
     */
    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(0, 0)
            .hide(activeFragment)
            .show(fragment)
            .commit()
        activeFragment = fragment
    }

    /**
     * fragment 갱신에 사용하는 함수
     * fragment가 추가되면 이곳에 추가된 fragment 코드 넣어줘야 함
     * @author 정준
     */
    private fun replaceFragment(fragment: Fragment) {
        when(fragment) {
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
            bookmarkFragment -> {
                supportFragmentManager.beginTransaction().apply {
                    remove(bookmarkFragment)
                    bookmarkFragment = BookmarkFragment()
                    add(R.id.frameLayout, bookmarkFragment, bookmarkFragment.javaClass.simpleName)
                }.commit()
            }
            settingFragment -> {
                supportFragmentManager.beginTransaction().apply {
                    remove(settingFragment)
                    settingFragment = SettingFragment()
                    add(R.id.frameLayout, settingFragment, settingFragment.javaClass.simpleName)
                }.commit()
            }
            alarmFragment -> {
                supportFragmentManager.beginTransaction().apply {
                    remove(alarmFragment)
                    alarmFragment = AlarmFragment()
                    add(R.id.frameLayout, alarmFragment, alarmFragment.javaClass.simpleName)
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

