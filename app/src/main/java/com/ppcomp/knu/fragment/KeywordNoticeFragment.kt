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
import kotlinx.android.synthetic.main.fragment_keyword_notice.*
import kotlinx.android.synthetic.main.fragment_keyword_notice.view.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate

/**
 * 키워드가 포함된 공지사항만 보여주는 fragment
 * @author 희진
 */
class KeywordNoticeFragment : Fragment() {
    var noticeList = arrayListOf<Notice>()
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private lateinit var keywordRecyclerView : RecyclerView
    private lateinit var progressBar : ProgressBar
    private lateinit var keywordNullView : TextView
    private lateinit var itemNullView : TextView
    var Url:String=""                                //mainUrl + notice_Url 저장 할 변수
    var nextPage:String=""
    var previousPage:String=""

    @RequiresApi(Build.VERSION_CODES.O)
    val nowDate: LocalDate = LocalDate.now()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_keyword_notice, container, false)

        keywordRecyclerView = view!!.findViewById(R.id.keyword_notice) as RecyclerView    //recyclerview 가져오기
        progressBar = view!!.findViewById((R.id.keyword_progressbar)) as ProgressBar
        keywordNullView = view!!.findViewById(R.id.keyword_null_view) as TextView
        itemNullView =view!!.findViewById(R.id.item_null_view) as TextView

        progressBar.setVisibility(View.GONE)                                //progressbar 숨기기
        keywordNullView.setVisibility(View.GONE)
        itemNullView.setVisibility(View.GONE)

        parsing()
        scrollPagination()

        mHandler = Handler()
        view.keyword_swipe.setOnRefreshListener {
            // Initialize a new Runnable
            mRunnable = Runnable {
//                 Hide swipe to refresh icon animation
                val Noticeadapter = NoticeAdapter(
                    requireContext(),
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
                keywordRecyclerView.adapter = Noticeadapter
                keyword_swipe.isRefreshing = false
            }
            mHandler.postDelayed(mRunnable, 2000)
        }

        return view
    }

    fun parsing() {
        keywordNullView.setVisibility(View.GONE)
        val Noticeadapter = NoticeAdapter(requireContext(), noticeList) { notice ->
            var link: String = notice.link
            if (!link.startsWith("http://") && !link.startsWith("https://"))
                link = "http://" + link
            val Intent: Intent = Uri.parse(link).let { webpage ->
                Intent(Intent.ACTION_VIEW, webpage)
            }
            startActivity(Intent)
        }
        keywordRecyclerView.adapter = Noticeadapter
        keywordRecyclerView.scrollToPosition(SCROLL_STATE_IDLE)

        // LayoutManager 설정. RecyclerView 에서는 필수
        var lm = LinearLayoutManager(requireContext())
        keywordRecyclerView.keyword_notice.layoutManager = lm
        keywordRecyclerView.keyword_notice.setHasFixedSize(true)
        // Web 통신
        StrictMode.enableDefaults()

        val preferences = activity!!.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val keywordList = preferences.getString("Keys", "오류")
        val boardList = preferences.getString("Urls", "오류")
        var keyword_item = keywordList.toString()
        var board_item = boardList.toString()

        if (keyword_item == "") {  //키워드 설정이 안된경우
            noticeList.removeAll(noticeList)
            progressBar.setVisibility(View.GONE)
            keywordNullView.setVisibility(View.VISIBLE) //문구 수정 필요
            itemNullView.setVisibility(View.GONE)
        } else if (board_item == "") {    //구독리스트 설정이 안된경우
            noticeList.removeAll(noticeList)
            progressBar.setVisibility(View.GONE)
            keywordNullView.setVisibility(View.GONE)
            itemNullView.setVisibility(View.GONE)
        } else {
            if (previousPage == "") {
                Url =
                    "http://15.165.178.103/notice/search?q=" + keyword_item + "&target=" + board_item
            }
            var noticeStream = URL(Url).openConnection() as HttpURLConnection
            var noticeRead = BufferedReader(InputStreamReader(noticeStream.inputStream, "UTF-8"))
            val noticeResponse = noticeRead.readLine()
            val jObject = JSONObject(noticeResponse)
            val jArray = jObject.getJSONArray("results")

            previousPage = jObject.getString("previous")
            nextPage = jObject.getString("next")
            Url = nextPage        //다음 Url 주소 변경
////         모든 공지 noticeList 에 저장
            for (i in 0 until jArray.length()) {
                val obj = jArray.getJSONObject(i)
                var title = obj.getString("bold_title")
                var id = obj.getString("id")
                var date = obj.getString("date")
                val fixed = obj.getString("is_fixed").toBoolean()
                var reference = obj.getString("reference")
                var image: Int = 0
                var fixed_image = 0
                if (fixed == true) {
                    fixed_image = R.drawable.notice_fixed_pin_icon
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
                        image = R.drawable.notice_new_icon
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
            if (noticeList.size == 0) {      //해당하는 아이템이 없을 때 화면 설정
                noticeList.removeAll(noticeList)
                keywordNullView.setVisibility(View.GONE)
                itemNullView.setVisibility(View.VISIBLE)
            }
        }
        Handler().postDelayed({   //스크롤시 progressbar 보이게하고 조금 대기
            progressBar.visibility = View.GONE
            keywordRecyclerView.scrollToPosition(Noticeadapter.itemCount - 11)
        }, 40)
    }

    /**
     *  스크롤시 서버의 다음 페이지 정보를 크롤링
     *  @author 희진
     */
    fun scrollPagination(){
        keywordRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1)
                    && newState ==SCROLL_STATE_IDLE && progressBar.isAnimating ==false ) {  //위치가 맨 밑이며 progressBar가 GONE이며 멈춘경우
                    if(Url!="null") {
                        progressBar.visibility = View.VISIBLE   //progressbar 나옴
                        Handler().postDelayed({   //스크롤시 progressbar 보이게하고 조금 대기
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