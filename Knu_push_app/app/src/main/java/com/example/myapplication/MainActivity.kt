package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import kotlinx.android.synthetic.main.main_layout.*
import kotlinx.android.synthetic.main.main_toolbar.*
//parsing 부분
import android.os.StrictMode
import java.net.URL
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection

/**
 * 메인화면의 기능을 작성하는 클래스
 * @author 희진
 */
 class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    var noticeList = arrayListOf<Notice>()

    /**
     * 화면생성해주는 메소드
     * 생성시 서버에서 받아오는 데이터파싱기능도 실행
     * @author 희진, 우진
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main_navigationView.setNavigationItemSelectedListener (this)

        //parsing 부분
        val noticeAdapter = NoticeAdapter(this, noticeList)
        result.adapter = noticeAdapter
        StrictMode.enableDefaults()
        val serverUrl = "http://15.165.178.103/notice/all"
        try {
            val stream = URL(serverUrl).openConnection() as HttpURLConnection
            var read = BufferedReader(InputStreamReader (stream.inputStream,"UTF-8"))
            val response = read.readLine()
            val jArray = JSONArray(response)

            for(i in 0 until jArray.length()) {
                val obj = jArray.getJSONObject(i)
                val bid = obj.getString("bid")
                val title = obj.getString("title")
                val date = obj.getString("date")
                val author = obj.getString("author")
                val line = Notice(bid, title, date, author)
                noticeList.add(line)
            }
        } catch (e: Exception) {
            val line = Notice("오류", "오류", "오류", "오류")
            noticeList.add(line)
        }

        setSupportActionBar(main_layout_toolbar)//toolbar 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//toolbar  보이게 하기
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)//메뉴 아이콘 지정
        supportActionBar?.setDisplayShowTitleEnabled(false) //타이틀 안보이게 하기
        }

    /**
     * 메뉴 클릭시 네비게이션 바 open
     * @author 희진
     */
    override fun onOptionsItemSelected(items: MenuItem): Boolean {
      when(items.itemId){
          android.R.id.home ->{
              main_drawer_layout.openDrawer(GravityCompat.START)
          }
      }
        return super.onOptionsItemSelected(items)
    }

    /**
     * 메뉴 클릭시 이동
     * @author 희진
     */
     override fun onNavigationItemSelected(lists: MenuItem): Boolean {
         when(lists.itemId){
             R.id.sub_list->{
                 val intent = Intent(this,SubList::class.java)
                 startActivity(intent)
             }
             R.id.setting ->{
                 val intent = Intent(this,Setting::class.java)
                 startActivity(intent)
             }
             R.id.login->{
                 val intent = Intent(this,Login::class.java )
                 startActivity(intent)
             }
             R.id.license->{
                 val intent = Intent(this,License::class.java)
                 startActivity(intent)
             }
             R.id.keyword->{
                 val intent = Intent(this,Keyword::class.java)
                 startActivity(intent)
             }
         }
         return false
     }

    /**
     * 뒤로가기 버튼 누르면 네비게이션 바 close
     * @author 희진
     */
    override fun onBackPressed() {
        if(main_drawer_layout.isDrawerOpen(GravityCompat.START)){
            main_drawer_layout.closeDrawers()
        }else{
            super.onBackPressed()
        }
    }

 }

