package com.example.myapplication

//import kotlinx.android.synthetic.main.main.*
//parsing 부분
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.IntegerRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_layout.*
import kotlinx.android.synthetic.main.main_toolbar.*
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


/**
 * 메인화면의 기능을 작성하는 클래스
 * @author 희진, jungwoo
 */
class MainActivity : AppCompatActivity() {

    var noticeList = arrayListOf<Notice>()
    var notice_Url: String = ""
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable

    /**
     * 화면생성해주는 메소드
     * 생성시 서버에서 받아오는 데이터파싱기능도 실행
     * @author 희진, 우진, jungwoo
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        content = findViewById(R.id.frameLayout)
        val navigation = findViewById<BottomNavigationView>(R.id.main_navigationView)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val fragment = LoginFragment()
        addFragment(fragment)
//        setSupportActionBar(main_layout_toolbar)                                //toolbar 지정
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)                       //toolbar  보이게 하기
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)   //메뉴 아이콘 지정
//        supportActionBar?.setDisplayShowTitleEnabled(false)                     //타이틀 안보이게 하기

    }


    /**
     * 메뉴 클릭시 이동
     * @author 희진, 정준
     */
    private var content: FrameLayout? = null

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.setting -> {
                    val fragment = SettingFragment()
                    addFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.login -> {
                    if (!GlobalApplication.isLogin) {
                        val fragment = LoginFragment()
                        addFragment(fragment)
                        return@OnNavigationItemSelectedListener true
                    } else {
                        val fragment = UserInfoFragment()
                        addFragment(fragment)
                        return@OnNavigationItemSelectedListener true
                    }
                }
                R.id.list -> {
                    val fragment = NoticeFragment()
                    addFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    /**
     * 하단 바 아이템 누르면 fragment 변경
     * @author 희진
     */
    fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(0, 0)
            .replace(R.id.frameLayout, fragment, fragment.javaClass.simpleName)
            .commit()
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


