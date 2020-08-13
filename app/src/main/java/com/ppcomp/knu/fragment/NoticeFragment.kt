package com.ppcomp.knu.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Notice
import com.ppcomp.knu.adapter.NoticeAdapter
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_notice_layout.*
import kotlinx.android.synthetic.main.fragment_notice_layout.view.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate


/**
 * 하단 바 '리스트'페이지의  kt
 * 크롤링한 공지사항을 띄워줌
 * @author 희진
 */
class NoticeFragment : Fragment() {

    var noticeList = arrayListOf<Notice>()
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private lateinit var noticeRecyclerView: RecyclerView
    private lateinit var thisContext: Context
    private lateinit var progressBar: ProgressBar
    private lateinit var noData: TextView
    private lateinit var listTitle: TextView
    private var mLockRecyclerView = false           //데이터 중복 안되게 체크하는 변수
    var Url: String = ""                                //mainUrl + notice_Url 저장 할 변수
    var nextPage: String = ""
    var previousPage: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    val nowDate: LocalDate = LocalDate.now()
  
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notice_layout, container, false)

        thisContext = container!!.context                                   //context 가져오기
        noticeRecyclerView = view!!.findViewById(R.id.notice) as RecyclerView    //recyclerview 가져오기
        progressBar = view!!.findViewById((R.id.progressbar)) as ProgressBar
        noData = view!!.findViewById((R.id.noData)) as TextView
        progressBar.setVisibility(View.GONE)                                //progressbar 숨기기
        noData.setVisibility(View.GONE)
        val preferences = activity!!.getSharedPreferences("pref", Context.MODE_PRIVATE)
        var board_Urls = preferences.getString("Urls", "")
        if (!board_Urls.equals("")) // 구독리스트가 있을시 안내화면 숨기고 파싱
        {
            noData.setVisibility(View.GONE)
            parsing()
        }
       if(board_Urls == ""){
           noData.setVisibility(View.VISIBLE)
        }
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
                noticeRecyclerView.adapter = Noticeadapter
                swipe.isRefreshing = false
            }
            mHandler.postDelayed(mRunnable, 2000)

        }
        return view
    }

    fun parsing() {
        mLockRecyclerView = true    //실행 중 중복 사용 막기
        progressBar.visibility = View.GONE
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_notice_layout, container, false)

        val Noticeadapter = NoticeAdapter(thisContext, noticeList) { notice ->
            var link: String = notice.link
            if (!link.startsWith("http://") && !link.startsWith("https://"))
                link = "http://" + link
            val Intent: Intent = Uri.parse(link).let { webpage ->
                Intent(Intent.ACTION_VIEW, webpage)
            }
            startActivity(Intent)
        }

        noticeRecyclerView.adapter = Noticeadapter
        // LayoutManager 설정. RecyclerView 에서는 필수
        val lm = LinearLayoutManager(thisContext)
        noticeRecyclerView.notice.layoutManager = lm
        noticeRecyclerView.notice.setHasFixedSize(true)

        // Web 통신
        StrictMode.enableDefaults()

        if (previousPage == "") {   //처음 호출시 혹은 학과가 바뀔 때 실행
            val preferences = activity!!.getSharedPreferences("pref", Context.MODE_PRIVATE)
            var board_Urls = preferences.getString("Urls", "")
            if (board_Urls.equals("")) {
                board_Urls = "오류"
            }
            val notice_Url = board_Urls.toString()
            val mainUrl = "http://15.165.178.103/notice/all?q="
            Url = mainUrl + notice_Url

        }

        if (Url != "null") {  //다음 페이지가 없으면 실행 X
            val noticeStream = URL(Url).openConnection() as HttpURLConnection
            var noticeRead = BufferedReader(InputStreamReader(noticeStream.inputStream, "UTF-8"))
            val noticeResponse = noticeRead.readLine()
            val jObject = JSONObject(noticeResponse)
            val jArray = jObject.getJSONArray("results")

            previousPage = jObject.getString("previous")
            nextPage = jObject.getString("next")
            if (previousPage == "null" && nextPage == "null") {
                Toast.makeText(requireContext(), "게시글이 존재하지 않습니다", Toast.LENGTH_SHORT).show()
            }
            Url = nextPage        //다음 Url 주소 변경

//         모든 공지 noticeList 에 저장
            for (i in 0 until jArray.length()) {
                val obj = jArray.getJSONObject(i)
                val title = obj.getString("title")
                var id = obj.getString("id")
                var date = obj.getString("date")
                var reference = obj.getString("reference")
                val fixed = obj.getString("is_fixed").toBoolean()
                var image : Int = 0
                var fixed_image =0
                if(fixed == true){
                    fixed_image=R.drawable.notice_fixed_icon
                }
                if (reference.equals("null")) {
                    reference = ""
                }
                var days: String = ""
                if (date.equals("null")) {
                    date = ""
                } else {
                    var sf = SimpleDateFormat("yyyy-MM-dd")
                    val diff = Math.abs((sf.parse(nowDate.toString()).getTime() - sf.parse(date).getTime()) / (24*60*60*1000))
                    if(diff <= 5)
                    {
                        image =  R.drawable.notice_new_icon
                    }
                    var dateArr = date.split("-")
                    var day = dateArr[2].split("T")
                    days = dateArr[0] + "년 " + dateArr[1] + "월 " + day[0] + "일"
                }
                var author = obj.getString("author")
                if (author.equals("null")) {
                    author = ""
                }
                val link = obj.getString("link")
                var board = id.split("-")
                val noticeLine = Notice(
                    title,
                    board[0],
                    "게시일: " + days,
                    "작성자: " + author,
                    link,
                    reference,
                    fixed,
                    image,
                    fixed_image

                )
                noticeList.add(noticeLine)
            }

        }
        Handler().postDelayed({
            progressBar.visibility = View.GONE                           //progressbar 숨김
            noticeRecyclerView.scrollToPosition(Noticeadapter.itemCount-11)
        }, 0)
    }

    /**
     *  스크롤시 서버의 다음 페이지 정보를 크롤링
     *  @author 희진
     */
    fun scrollPagination() {
        noticeRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1)
                    && newState == SCROLL_STATE_IDLE ) {  //위치가 맨 밑이며 중복 안되고 멈춘경우
                    if(Url!="null") {
                        progressBar.visibility = View.VISIBLE                           //progressbar 나옴
                        Handler().postDelayed({
                            parsing()
                        }, 500)
                    }
                    else{
                        Toast.makeText(requireContext(), "더 이상 공지가 없습니다.", Toast.LENGTH_SHORT).show()

                    }
                }
            }
        })
    }
}
