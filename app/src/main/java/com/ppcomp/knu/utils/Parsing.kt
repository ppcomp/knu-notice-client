package com.ppcomp.knu.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.StrictMode
import android.view.View
import android.widget.AbsListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Notice
import com.ppcomp.knu.adapter.NoticeAdapter
import kotlinx.android.synthetic.main.fragment_keyword_notice.view.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import kotlin.math.abs

class Parsing private constructor() {

    companion object {

        @Volatile
        private var INSTANCE: Parsing? = null

        fun getInstance(): Parsing =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Parsing().also {
                    INSTANCE = it
                }
            }

        @RequiresApi(Build.VERSION_CODES.O)
        fun parsing(
            context: Context,
            noticeList: ArrayList<Notice>,
            view: RecyclerView,
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
            view.keyword_notice.layoutManager = lm
            view.keyword_notice.setHasFixedSize(true)

            // Web 통신
            StrictMode.enableDefaults()

            val nowDate: LocalDate = LocalDate.now()
            var url: String = currentUrl
            var previousPage: String = ""
            var nextPage: String = ""

            if (url == "") {
                url = "http://15.165.178.103/notice/search?q=$searchQuery&target=$target"
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
                val obj = jArray.getJSONObject(i)
                val title = obj.getString("bold_title")
                val id = obj.getString("id")
                var boardName = id.split("-")[0]
                var days: String = ""
                var author = obj.getString("author")
                val link = obj.getString("link")
                var reference = obj.getString("reference")
                val fixed = obj.getString("is_fixed").toBoolean()
                var image: Int = 0
                var fixedImage = 0
                var date = obj.getString("date")

                if (author == "null") {
                    author = ""
                }
                if (fixed) {
                    fixedImage = R.drawable.notice_fixed_pin_icon
                }
                if (reference == "null") {
                    reference = ""
                }
                if (date == "null") {
                    date = ""
                } else {
                    val sf = SimpleDateFormat("yyyy-MM-dd")
                    val diff = abs(
                        (sf.parse(nowDate.toString()).time - sf.parse(date).time) / (24 * 60 * 60 * 1000)
                    )
                    if (diff <= 5) {
                        image = R.drawable.notice_new_icon
                    }
                    val dateArr = date.split("-")
                    val day = dateArr[2].split("T")
                    days = dateArr[0] + "년 " + dateArr[1] + "월 " + day[0] + "일"
                }

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
            }
            return listOf(previousPage, url, nextPage)
        }
    }
}