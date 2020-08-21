package com.ppcomp.knu.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Notice
import com.ppcomp.knu.adapter.NoticeAdapter
import kotlinx.android.synthetic.main.fragment_keyword_notice.view.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import kotlin.math.abs

/**
 * 싱글턴 패턴의 Parsing 클래스
 * @author 정우
 */
class Parsing private constructor() {

    companion object {

        @Volatile
        private var INSTANCE: Parsing? = null
        private var url: String = ""

        /**
         * SplashActivity 에서 처음에 한 번만 호출하면 됨
         * @author 정우
         */
        fun getInstance(): Parsing =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Parsing().also {
                    INSTANCE = it
                }
            }

        /**
         * 주어진 주소에 대한 요청을 받아와 주어진 view에 데이터를 매칭시킨다.
         * 1. 검색 용도가 아니라 학과만 필터링하고 싶다면 -> ex. searchQuery: "", target: main
         * 2. 모든 학과 대상으로 검색하고 싶다면 -> ex. searchQuery: "foo bar", target: all or ""
         *
         * @param context 이 함수를 호출하는 Context
         * @param noticeList 서버로부터 받아온 공지를 저장할 배열
         * @param view 저장된 공지를 매핑시킬 view
         * @param progressBar 로딩중일동안 보여질 ProgressBar
         * @param currentUrl 서버로 요청할 주소
         * @param searchQuery 검색어(키워드)
         * @param target 검색 대상 학과
         *
         * @return List<String>
         *     List<String>[0]: prevPage
         *     List<String>[1]: currentPage
         *     List<String>[2]: nextPage
         *
         * @author 정우
         */
        @SuppressLint("SimpleDateFormat")
        @RequiresApi(Build.VERSION_CODES.O)
        fun parsing(
            context: Context,
            noticeList: ArrayList<Notice>,
            view: RecyclerView,
            progressBar: ProgressBar,
            currentUrl: String,
            searchQuery: String,
            target: String
        ): List<String> {
            val noticeAdapter = NoticeAdapter(context, noticeList) { notice ->
                var link: String = notice.link
                if (!link.startsWith("http://") && !link.startsWith("https://"))
                    link = "http://$link"
                val intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                context.startActivity(intent)
            }
            view.adapter = noticeAdapter
            view.scrollToPosition(AbsListView.OnScrollListener.SCROLL_STATE_IDLE)

            // LayoutManager 설정. RecyclerView 에서는 필수
            var lm = LinearLayoutManager(context)
            view.layoutManager = lm
            view.setHasFixedSize(true)

            // Web 통신
            StrictMode.enableDefaults()

            val nowDate: LocalDate = LocalDate.now()
            url = currentUrl
            var previousPage: String = ""
            var nextPage: String = ""

            if (url == "" || url == "null") {
                noticeAdapter.clear()
                if (searchQuery == "") {
                    url = "http://15.165.178.103/notice/all?q=$target"
                } else {
                    url = "http://15.165.178.103/notice/search?q=$searchQuery&target=$target"
                }
            }
            val noticeStream = URL(url).openConnection() as HttpURLConnection
            val noticeRead = BufferedReader(InputStreamReader(noticeStream.inputStream, "UTF-8"))
            val noticeResponse = noticeRead.readLine()
            val jObject = JSONObject(noticeResponse)
            val jArray = jObject.getJSONArray("results")

            previousPage = jObject.getString("previous")
            nextPage = jObject.getString("next")
            url = nextPage        //다음 Url 주소 변경
            // 모든 공지 noticeList 에 저장
            for (i in 0 until jArray.length()) {
                ///////////////////////////////// Bind variables ///////////////////////////////////
                val obj = jArray.getJSONObject(i)
                var title = ""
                try {
                    title = obj.getString("bold_title").toString()
                } catch (e: JSONException) {
                    title = obj.getString("title").toString()
                }
                Log.d("########", title)
                val id = obj.getString("id").toString()
                var boardName = id.split("-")[0]
                var days: String = ""
                var author = obj.getString("author").toString()
                val link = obj.getString("link").toString()
                var reference = obj.getString("reference").toString()
                val fixed = obj.getString("is_fixed").toBoolean()
                var image: Int = 0
                var fixedImage = 0
                var date = obj.getString("date")
                /////////////////////////////////// End binding ////////////////////////////////////

                ////////////////////////////////// Data cleansing //////////////////////////////////
                title = title.replace("<", "&lt;").replace(">", "&gt;")
                if (fixed) {
                    fixedImage = R.drawable.notice_fixed_pin_icon
                }
                if (author == "null") {
                    author = ""
                }
                if (reference == "null") {
                    reference = ""
                }
                if (date == "null") {
                    date = ""
                } else {
                    val sf = SimpleDateFormat("yyyy-MM-dd")
                    val diff = abs(
                        (sf.parse(nowDate.toString()).time - sf.parse(date).time) /
                                (24 * 60 * 60 * 1000)
                    )
                    if (diff <= 5) {
                        image = R.drawable.notice_new_icon
                    }
                    val dateArr = date.split("-")
                    val day = dateArr[2].split("T")
                    days = dateArr[0] + "년 " + dateArr[1] + "월 " + day[0] + "일"
                }
                //////////////////////////////// End cleansing /////////////////////////////////////

                noticeList.add(
                    Notice(
                        title,
                        boardName,
                        "게시일: $days",
                        "작성자: $author",
                        link,
                        reference,
                        fixed,
                        image,
                        fixedImage
                    )
                )

            } ///////////////////////////////////// End for ////////////////////////////////////////

            //스크롤시 progressbar 보이게하고 조금 대기
            Handler().postDelayed({
                progressBar.visibility = View.GONE
                view.scrollToPosition(noticeList.size - 11)
            }, 40)
            return listOf(previousPage, url, nextPage)
        }

        /**
         *  스크롤시 서버의 다음 페이지 정보를 크롤링
         *  @author 희진
         */
        fun scrollPagination(
            context: Context,
            view: RecyclerView,
            progressBar: ProgressBar,
            parsing: () -> Unit
        ) {
            view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if ((!recyclerView.canScrollVertically(1) && newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) &&
                        !progressBar.isAnimating
                    ) {  //위치가 맨 밑이며 progressBar가 GONE이며 멈춘경우
                        if (url != "null") {
                            progressBar.visibility = View.VISIBLE   //progressbar 나옴
                            Handler().postDelayed({   //스크롤시 progressbar 보이게하고 조금 대기
                                parsing()
                            }, 500)
                        }
                    }
                }
            })
        }
    }
}