package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.main_layout.*
import kotlinx.android.synthetic.main.main_layout.view.*
import org.json.JSONArray
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
    var notice_Url: String = ""
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private lateinit var recyclerView1 : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.main_layout, container, false)

        noticeList = arrayListOf<Notice>()
        recyclerView1 = view!!.findViewById(R.id.notice) as RecyclerView    //recyclerview 가져오기

        // Adapter 설정, Notice 클릭시 웹 브라우저로 이동하는 lambda 식 선언
        recyclerView1.adapter = NoticeAdapter(requireContext(), noticeList) { notice ->
            var link: String = notice.link
            if (!link.startsWith("http://") && !link.startsWith("https://"))
                link = "http://" + link
            Toast.makeText(requireContext(), link, Toast.LENGTH_SHORT).show()
            startActivity(Intent(Intent().action, Uri.parse(link)))
        }

        // LayoutManager 설정. RecyclerView 에서는 필수
        recyclerView1.layoutManager = LinearLayoutManager(requireContext())
        // Web 통신
        StrictMode.enableDefaults()

        val mainUrl = "http://15.165.178.103/notice/all?board="
        val noticeStream = URL(mainUrl + notice_Url).openConnection() as HttpURLConnection
        var noticeRead =
            BufferedReader(InputStreamReader(noticeStream.inputStream, "UTF-8"))
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
        /**
         * 파싱 기능
         * 새로고침 기능
         * swipe시(위로 끌땅) 새로고침
         * @author 김우진
         */
        mHandler = Handler()
        view.swipe.setOnRefreshListener {
            // Initialize a new Runnable
            mRunnable = Runnable {
//                 Hide swipe to refresh icon animation

                noticeList = arrayListOf<Notice>()
                recyclerView1 = view!!.findViewById(R.id.notice) as RecyclerView
                // Adapter 설정, Notice 클릭시 웹 브라우저로 이동하는 lambda 식 선언
                recyclerView1.adapter = NoticeAdapter(requireContext(), noticeList) { notice ->
                    var link: String = notice.link
                    if (!link.startsWith("http://") && !link.startsWith("https://"))
                        link = "http://" + link
                    Toast.makeText(requireContext(), link, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(Intent().action, Uri.parse(link)))
                }
                // LayoutManager 설정. RecyclerView 에서는 필수
                recyclerView1.layoutManager = LinearLayoutManager(requireContext())
                // Web 통신
                StrictMode.enableDefaults()

                val mainUrl = "http://15.165.178.103/notice/all?board="
                val noticeStream = URL(mainUrl + notice_Url).openConnection() as HttpURLConnection
                var noticeRead =
                    BufferedReader(InputStreamReader(noticeStream.inputStream, "UTF-8"))
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
                swipe.isRefreshing = false
            }
            mHandler.postDelayed(mRunnable, 2000)

        }
        return view
    }
}
