package com.ppcomp.knu.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE
import androidx.annotation.RequiresApi
import androidx.core.os.postDelayed
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Notice
import com.ppcomp.knu.adapter.NoticeAdapter
import com.ppcomp.knu.adapter.SearchAdapter
import kotlinx.android.synthetic.main.activity_keyword.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_notice_item.*
import kotlinx.android.synthetic.main.fragment_notice_layout.*
import kotlinx.android.synthetic.main.fragment_notice_layout.view.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
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
class SearchFragment : Fragment() {

    var searchList = arrayListOf<Notice>()
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var thisContext: Context
    private lateinit var progressBar: ProgressBar
    private lateinit var search_noData: TextView
    private lateinit var search_button: Button
    private lateinit var search_edit: EditText
    var Url: String = ""//mainUrl + notice_Url 저장 할 변수
    var count: Int = 0
    var nextPage: String = ""
    var previousPage: String = ""
    var getSearchData: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    val nowDate: LocalDate = LocalDate.now()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        progressBar = view!!.findViewById((R.id.progressbar)) as ProgressBar
        search_noData = view!!.findViewById((R.id.search_noData)) as TextView
        search_button = view!!.findViewById(R.id.search_button) as Button
        progressBar.setVisibility(View.GONE)
        thisContext = container!!.context //context 가져오기
        searchRecyclerView =
            view!!.findViewById(R.id.search_recycler) as RecyclerView    //recyclerview 가져오기
        search_edit = view!!.findViewById(R.id.search_edit) as EditText

        search_edit.setOnKeyListener(object : View.OnKeyListener { // 엔터키누르면 검색버튼을 자동으로 누르도록
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (event != null) {
                    if (event.getAction() === KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        search_button.performClick()
                        return true
                    }
                }
                return false
            }
        })


        search_button.setOnClickListener() {
            getSearchData = search_edit.getText().toString()
            if (getSearchData.equals("")) {
                search_noData.setVisibility(View.VISIBLE)
            } else {
                search_noData.setVisibility(View.GONE)
                previousPage = ""
                parsing()
                scrollPagination()
            }
        }

        /**
         * 파싱 기능
         * 새로고침 기능
         * swipe시(위로 끌땅) 새로고침
         * parsing()호출로 새로고침시 다음 페이지의 정보를 가져오는 오류
         * 저장된 recyclerview 만 가져오도록 수정
         * @author 김우진,희진
         */
        mHandler = Handler()
        view.search_swipe.setOnRefreshListener {
            // Initialize a new Runnable
            mRunnable = Runnable {
//                 Hide swipe to refresh icon animation
                val searchAdapter = SearchAdapter(
                    thisContext,
                    searchList
                ) { notice ->
                    var link: String = notice.link
                    if (!link.startsWith("http://") && !link.startsWith("https://"))
                        link = "http://" + link
                    val Intent: Intent = Uri.parse(link).let { webpage ->
                        Intent(Intent.ACTION_VIEW, webpage)
                    }
                    startActivity(Intent)
                }
                searchRecyclerView.adapter = searchAdapter
                search_swipe.isRefreshing = false
            }
            mHandler.postDelayed(mRunnable, 2000)

        }
        return view
    }

    fun parsing() {
        search_edit = view!!.findViewById(R.id.search_edit) as EditText
        progressBar.visibility = View.GONE
        getSearchData = search_edit.getText().toString()

        val searchAdapter = SearchAdapter(thisContext, searchList) { notice ->
            var link: String = notice.link
            if (!link.startsWith("http://") && !link.startsWith("https://"))
                link = "http://" + link
            val Intent: Intent = Uri.parse(link).let { webpage ->
                Intent(Intent.ACTION_VIEW, webpage)
            }
            startActivity(Intent)
        }


        searchRecyclerView.adapter = searchAdapter
        // LayoutManager 설정. RecyclerView 에서는 필수
        val lm = LinearLayoutManager(thisContext)
        searchRecyclerView.layoutManager = lm
        searchRecyclerView.setHasFixedSize(true)

        // Web 통신
        StrictMode.enableDefaults()

        if (previousPage == "") {   //처음 호출시 혹은 학과가 바뀔 때 실행
            if (getSearchData.equals("")) {
                getSearchData = "null"
            }
            val mainUrl = "http://15.165.178.103/notice/search?q="
            Url = mainUrl + getSearchData
            searchAdapter.clear()

        }

        if (Url != "null") {  //다음 페이지가 없으면 실행 X
            val noticeStream = URL(Url).openConnection() as HttpURLConnection
            var noticeRead = BufferedReader(InputStreamReader(noticeStream.inputStream, "UTF-8"))
            val noticeResponse = noticeRead.readLine()
            val jObject = JSONObject(noticeResponse)
            val jArray = jObject.getJSONArray("results")

            previousPage = jObject.getString("previous")
            count = jObject.getInt("count")
            nextPage = jObject.getString("next")
            if (count == 0) {
                Toast.makeText(requireContext(), "검색어에 해당되는 게시글이 존재하지 않습니다", Toast.LENGTH_SHORT)
                    .show()
            }
            Url = nextPage        //다음 Url 주소 변경

//         모든 공지 noticeList 에 저장
            for (i in 0 until jArray.length()) {
                val obj = jArray.getJSONObject(i)

                var title = obj.getString("title") // 검색 키워드 강조
                val start = title.indexOf(getSearchData)
                val end = start + getSearchData.length
                var spannedTitle = ""

                for (k in 0 until start) {
                    spannedTitle += title[k]
                }
                spannedTitle += "<u><strong>" + getSearchData + "</strong></u>"
                for (k in end until title.length) {
                    spannedTitle += title[k]
                }
                title = spannedTitle


                var id = obj.getString("id")
                var date = obj.getString("date")
                var reference = obj.getString("reference")
                val fixed = obj.getString("is_fixed").toBoolean()
                var image: Int = 0
                var fixed_image =0
                if(fixed == true){
                    fixed_image=R.drawable.notice_fixed_pin_icon
                }

                if (reference.equals("null")) {
                    reference = ""
                }
                var days: String = ""
                if (date.equals("null")) {
                    date = ""
                } else {
                    var sf = SimpleDateFormat("yyyy-MM-dd")
                    val diff = Math.abs(
                        (sf.parse(nowDate.toString()).getTime() - sf.parse(date)
                            .getTime()) / (24 * 60 * 60 * 1000)
                    )
                    if (diff <= 5) {
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
                    fixed_image,
                    bookmark = false
                )
                searchList.add(noticeLine)
            }

        }
        Handler().postDelayed({
            progressBar.visibility = View.GONE                           //progressbar 숨김
            searchRecyclerView.scrollToPosition(searchAdapter.itemCount - 10)
        }, 40)
    }

    /**
     *  스크롤시 서버의 다음 페이지 정보를 크롤링
     *  @author 희진
     */
    fun scrollPagination() {
        searchRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1)
                    && newState == SCROLL_STATE_IDLE && progressBar.isAnimating ==false
                ) {  //위치가 맨 밑이며 중복 안되고 멈춘경우
                    if (Url != "null") {
                        progressBar.visibility =
                            View.VISIBLE                           //progressbar 나옴
                        Handler().postDelayed({
                            parsing()
                        }, 500)
                    } else {
                        Toast.makeText(requireContext(), "더 이상 공지가 없습니다.", Toast.LENGTH_SHORT)
                            .show()

                    }
                }
            }
        })
    }
}
