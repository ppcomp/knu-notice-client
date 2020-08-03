package com.ppcomp.knu.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Notice
import com.ppcomp.knu.adapter.NoticeAdapter
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.main_layout.*
import kotlinx.android.synthetic.main.main_layout.view.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


/**
 * 하단 바 '리스트'페이지의  kt
 * 크롤링한 공지사항을 띄워줌
 * @author 희진
 */
class NoticeFragment : Fragment() {

    var noticeList = arrayListOf<Notice>()
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private lateinit var recyclerView1 : RecyclerView
    private lateinit var thisContext: Context
    private lateinit var progressBar : ProgressBar
    private var mLockRecyclerView  = false           //데이터 중복 안되게 체크하는 변수
    var Url:String=""                                //mainUrl + notice_Url 저장 할 변수
    var nextPage:String=""
    var previousPage:String=""
    var checkPageCount =0
    var scrollPosition=0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.main_layout, container, false)

        thisContext = container!!.context                                   //context 가져오기
        recyclerView1 = view!!.findViewById(R.id.notice) as RecyclerView    //recyclerview 가져오기
        progressBar = view!!.findViewById((R.id.progressbar)) as ProgressBar
        progressBar.setVisibility(View.GONE)                                //progressbar 숨기기

        parsing()
        scrollPagination()
        /**
         * 파싱 기능
         * 새로고침 기능
         * swipe시(위로 끌땅) 새로고침
         * parsing()호출로 새로고침시 다음 페이지의 정보를 가져오는 오류
         * 저장된 recyclerview 만 가져오도록 수정
         * @author 김우진,희진
         */
        mHandler = Handler()
        view.swipe.setOnRefreshListener {
            // Initialize a new Runnable
            mRunnable = Runnable {
//                 Hide swipe to refresh icon animation
                val Noticeadapter = NoticeAdapter(
                    thisContext,
                    noticeList
                ) { notice ->
                    var link: String = notice.link
                    if (!link.startsWith("http://") && !link.startsWith("https://"))
                        link = "http://" + link
                    val Intent: Intent = Uri.parse(link).let { webpage ->
                        Intent(Intent.ACTION_VIEW, webpage)
                    }
                    startActivity(Intent)
                }
                recyclerView1.adapter = Noticeadapter
                swipe.isRefreshing = false
            }
            mHandler.postDelayed(mRunnable, 2000)

        }
        return view
    }

    fun parsing() {
        mLockRecyclerView = true    //실행 중 중복 사용 막기

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.main_layout, container, false)

        val Noticeadapter = NoticeAdapter(
            thisContext,
            noticeList
        ) { notice ->
            var link: String = notice.link
            if (!link.startsWith("http://") && !link.startsWith("https://"))
                link = "http://" + link
            val Intent: Intent = Uri.parse(link).let { webpage ->
                Intent(Intent.ACTION_VIEW, webpage)
            }
            startActivity(Intent)
        }

        recyclerView1.adapter = Noticeadapter
        // LayoutManager 설정. RecyclerView 에서는 필수
        val lm = LinearLayoutManager(thisContext)
        recyclerView1.notice.layoutManager = lm
        recyclerView1.notice.setHasFixedSize(true)

        // Web 통신
        StrictMode.enableDefaults()

        if(previousPage =="" ) {   //처음 호출시 혹은 학과가 바뀔 때 실행
            val preferences = activity!!.getSharedPreferences("pref", Context.MODE_PRIVATE)
            val board_Urls = preferences.getString("Urls", "오류")
            val notice_Url = board_Urls.toString()
            val mainUrl = "http://15.165.178.103/notice/all?q="
            Url = mainUrl + notice_Url
            scrollPosition=0
            checkPageCount=0
        }

        if(Url!="null") {  //다음 페이지가 없으면 실행 X
            val noticeStream = URL(Url).openConnection() as HttpURLConnection
            var noticeRead = BufferedReader(InputStreamReader(noticeStream.inputStream, "UTF-8"))
            val noticeResponse = noticeRead.readLine()
            val jObject = JSONObject(noticeResponse)
            val jArray = jObject.getJSONArray("results")

            previousPage = jObject.getString("previous")
            nextPage = jObject.getString("next")
            Url = nextPage        //다음 Url 주소 변경

//         모든 공지 noticeList 에 저장
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
                val noticeLine = Notice(
                    title,
                    board[0],
                    "게시일: " + days,
                    "작성자: " + author,
                    link
                )
                noticeList.add(noticeLine)
            }
            if(checkPageCount==0){                  //스크롤 누르지 않았을 때 스크롤 위치
                recyclerView1.scrollToPosition(0)
            }
            else {                                  //스크롤시 위치 지정
                scrollPosition = 3 + (10*(checkPageCount-1))
                recyclerView1.scrollToPosition(scrollPosition)
            }
        }
        Handler().postDelayed({   //스크롤시 progressbar 보이게하고 조금 대기
            progressBar.visibility = View.GONE
            mLockRecyclerView = false
            checkPageCount++
        }, 500)
    }

    /**
     *  스크롤시 서버의 다음 페이지 정보를 크롤링
     *  @author 희진
     */
    fun scrollPagination(){
        recyclerView1.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1)
                    && newState == SCROLL_STATE_IDLE && mLockRecyclerView == false ) {  //위치가 맨 밑이며 중복 안되고 멈춘경우
                    if(Url!="null") {
                        progressBar.visibility = View.VISIBLE                           //progressbar 나옴
                        parsing()
                    }
                    else{
                        Toast.makeText(requireContext(), "더 이상 공지가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }


}
