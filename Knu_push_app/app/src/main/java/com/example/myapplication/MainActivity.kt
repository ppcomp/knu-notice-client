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
import android.widget.Toast
import androidx.annotation.IntegerRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
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
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var noticeList = arrayListOf<Notice>()
    var notice_Url: String = ""
    private lateinit var mHandler: Handler
    private lateinit var mRunnable:Runnable

    /**
     * 화면생성해주는 메소드
     * 생성시 서버에서 받아오는 데이터파싱기능도 실행
     * @author 희진, 우진, jungwoo
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(main_layout_toolbar)                                //toolbar 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)                       //toolbar  보이게 하기
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)   //메뉴 아이콘 지정
        supportActionBar?.setDisplayShowTitleEnabled(false)                     //타이틀 안보이게 하기
        main_navigationView.setNavigationItemSelectedListener(this)

        /**
         * 파싱 기능
         * 새로고침 기능
         * swipe시(위로 끌땅) 새로고침
         * @author 김우진
         */
        parsing()
        mHandler = Handler()
        swipe.setOnRefreshListener {
            // Initialize a new Runnable
            mRunnable = Runnable {
                parsing()
                // Hide swipe to refresh icon animation
                swipe.isRefreshing = false
            }
            mHandler.postDelayed(mRunnable, 2000)
        }
    }

    /**
     * 파싱하는 함수
     */
    fun parsing(){
        noticeList = arrayListOf<Notice>()
        // Adapter 설정, Notice 클릭시 웹 브라우저로 이동하는 lambda 식 선언
        val noticeAdapter = NoticeAdapter(this, noticeList) { notice ->
            var link: String = notice.link
            if (!link.startsWith("http://") && !link.startsWith("https://"))
                link = "http://" + link
            Toast.makeText(this, link, Toast.LENGTH_SHORT).show()
            startActivity(Intent(intent.action, Uri.parse(link)))
        }
        notice.adapter = noticeAdapter

        // LayoutManager 설정. RecyclerView 에서는 필수
        val lm = LinearLayoutManager(this)
        notice.layoutManager = lm
        notice.setHasFixedSize(true)

        // Web 통신
        StrictMode.enableDefaults()

        val loadPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val board_Urls = loadPreferences.getString("Urls", "오류")
        notice_Url = board_Urls.toString()

        val mainUrl = "http://15.165.178.103/notice/all?board="
        val noticeStream = URL(mainUrl + notice_Url).openConnection() as HttpURLConnection
        var noticeRead = BufferedReader(InputStreamReader(noticeStream.inputStream, "UTF-8"))
        val noticeResponse = noticeRead.readLine()
        val jArray = JSONArray(noticeResponse)

        // 모든 공지 noticeList 에 저장
        for (i in 0 until jArray.length()) {
            val obj = jArray.getJSONObject(i)
            val title = obj.getString("title")
            var id = obj.getString("id")
            val date = obj.getString("date")
            val author = obj.getString("author")
            val link = obj.getString("link")
            var board = id.split("-")
            var dateArr = date.split("-")
            var day = dateArr[2].split("T")
            var days = dateArr[0] + "년 " + dateArr[1] + "월 " + day[0] + "일"
            val noticeLine = Notice(title, board[0], "게시일: " + days, "작성자: " + author, link)
            noticeList.add(noticeLine)
        }
    }

    /**
     * 메뉴 클릭시 네비게이션 바 open
     * @author 희진
     */
    override fun onOptionsItemSelected(items: MenuItem): Boolean {
        when (items.itemId) {
            android.R.id.home -> {
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
                 overridePendingTransition(R.anim.slideright,R.anim.slideleft)
             }
             R.id.setting ->{
                 val intent = Intent(this,Setting::class.java)
                 startActivity(intent)
                 overridePendingTransition(R.anim.slideright,R.anim.slideleft)
             }
             R.id.login->{
                 val intent = Intent(this,Login::class.java )
                 startActivity(intent)
                 overridePendingTransition(R.anim.slideright,R.anim.slideleft)
             }
             R.id.license->{
                 val intent = Intent(this,License::class.java)
                 startActivity(intent)
                 overridePendingTransition(R.anim.slideright,R.anim.slideleft)
             }
             R.id.keyword->{
                 val intent = Intent(this,Keyword::class.java)
                 startActivity(intent)
                 overridePendingTransition(R.anim.slideright,R.anim.slideleft)
             }
         }
         return false
     }

    /**
     * 뒤로가기 버튼 누르면 네비게이션 바 close
     * @author 희진
     */
    override fun onBackPressed() {
        if (main_drawer_layout.isDrawerOpen(GravityCompat.START)) {
            main_drawer_layout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }
}

