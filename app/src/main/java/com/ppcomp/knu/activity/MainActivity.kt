package com.ppcomp.knu.activity

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
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

    var settingFragment = SettingFragment()
    var bookmarkFragment = BookmarkFragment()
    var noticeFragment = NoticeFragment()
    var keywordNoticeFragment = KeywordNoticeFragment()
    var activeFragment: Fragment = noticeFragment   //현재 띄워진 프레그먼트(default: noticeFragment)
    var searchFragment = SearchFragment()


    /**
     * 메인 레이아웃과 fragment 화면 생성
     * 모든 fragment는 이 함수에서 초기화가 되어야 함
     * @author 희진, 우진, jungwoo, 정준
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        content = findViewById(R.id.frameLayout)
        val navigation = findViewById<BottomNavigationView>(R.id.main_navigationView)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        supportFragmentManager.beginTransaction().apply {       // 모든 프레그먼트 삽입
            add(R.id.frameLayout, noticeFragment, noticeFragment.javaClass.simpleName)
            add(R.id.frameLayout, keywordNoticeFragment, keywordNoticeFragment.javaClass.simpleName).hide(keywordNoticeFragment)
            add(R.id.frameLayout, searchFragment, searchFragment.javaClass.simpleName).hide(searchFragment)
            add(R.id.frameLayout, bookmarkFragment, bookmarkFragment.javaClass.simpleName).hide(bookmarkFragment)
            add(R.id.frameLayout, settingFragment, settingFragment.javaClass.simpleName).hide(settingFragment)
        }.commit()

    }
    
    /**
     * 하단 메뉴 클릭시 fragment 전환
     * @author 희진, 정준
     */
    private var content: FrameLayout? = null
    var listLocationCount =1
    var keywordlistLocationCount =1
    var searchLocationCount =1
    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.list -> {
                    keywordlistLocationCount=0
                    searchLocationCount=0
                    if(GlobalApplication.isFragmentChange[0]) { //구독리스트, 북마크리스트에 변경사항이 있으면 화면 갱신
                        replaceFragment(noticeFragment) //화면갱신
                        GlobalApplication.isFragmentChange[0] = false   //갱신후 false로 변경
                        listLocationCount=0
                    }
                    listLocationCount++             //해당 fragment에 들어가면 count++
                    if(listLocationCount >=2){      //count가 2 이상이면 scroll이 맨 위로 이동
                        var recyclerview = noticeFragment.view!!.findViewById(R.id.notice) as RecyclerView
                        recyclerview.scrollToPosition(0)
                    }
                    addFragment(noticeFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.keywordlist -> {
                    listLocationCount =0
                    searchLocationCount=0
                    if(GlobalApplication.isFragmentChange[1]) { //구독리스트, 키워드 리스트, 북마크리스트에 변경사항이 있으면 화면 갱신
                        replaceFragment(keywordNoticeFragment)  //화면갱신
                        GlobalApplication.isFragmentChange[1] = false   //갱신후 false로 변경
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
                    keywordlistLocationCount=0
                    if(GlobalApplication.isFragmentChange[2]) { //북마크리스트에 변경사항이 있으면 화면 갱신
                        replaceFragment(searchFragment) //화면갱신
                        GlobalApplication.isFragmentChange[2] = false   //갱신후 false로 변경
                        searchLocationCount =0
                    }
                    searchLocationCount++
                    if(searchLocationCount >=2){
                        var recyclerview = searchFragment.view!!.findViewById(R.id.search_recycler) as RecyclerView
                        recyclerview.scrollToPosition(0)
                    }
                    addFragment(searchFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bookmark -> {
                    listLocationCount =0
                    keywordlistLocationCount=0
                    searchLocationCount=0
                    if(GlobalApplication.isFragmentChange[3]) { //
                        replaceFragment(bookmarkFragment)
                        GlobalApplication.isFragmentChange[3] = false
                    }
                    addFragment(bookmarkFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.setting -> {
                    listLocationCount =0
                    keywordlistLocationCount =0
                    searchLocationCount=0
                    addFragment(settingFragment)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    /**
     * fragment 전환에 사용하는 함수
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
     * fragment 화면갱신에 사용하는 함수
     * @author 정준
     */
    fun replaceFragment(fragment: Fragment) {
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
            searchFragment -> {
                supportFragmentManager.beginTransaction().apply {
                    remove(searchFragment)
                    searchFragment = SearchFragment()
                    add(R.id.frameLayout, searchFragment, searchFragment.javaClass.simpleName)
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

