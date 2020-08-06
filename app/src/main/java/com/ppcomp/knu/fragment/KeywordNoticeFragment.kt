package com.ppcomp.knu.fragment
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat.isNestedScrollingEnabled
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Notice
import com.ppcomp.knu.adapter.NoticeAdapter
import kotlinx.android.synthetic.main.fragment_keyword_notice.*
import kotlinx.android.synthetic.main.fragment_keyword_notice.view.*
import kotlinx.android.synthetic.main.fragment_notice_layout.*
import kotlinx.android.synthetic.main.fragment_notice_layout.view.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * 키워드가 포함된 공지사항만 보여주는 fragment
 * @author 희진
 */
class KeywordNoticeFragment : Fragment() {
    var noticeList = arrayListOf<Notice>()
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private lateinit var recyclerView2 : RecyclerView
    private lateinit var progressBar : ProgressBar
    private lateinit var keywordNullView : TextView
    private lateinit var itemNullView : TextView
    private var mLockRecyclerView  = false           //데이터 중복 안되게 체크하는 변수
    var Url:String=""                                //mainUrl + notice_Url 저장 할 변수
    var nextPage:String=""
    var previousPage:String=""
    var itemcount =0
    var checkcount=0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_keyword_notice, container, false)

        recyclerView2 = view!!.findViewById(R.id.keyword_notice) as RecyclerView    //recyclerview 가져오기
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
                recyclerView2.adapter = Noticeadapter
                keyword_swipe.isRefreshing = false
            }
            mHandler.postDelayed(mRunnable, 2000)
        }

            return view
    }
    fun parsing() {

        mLockRecyclerView = true    //실행 중 중복 사용 막기
        itemcount=0
        if(noticeList.size==0){
            checkcount=0
        }

        val Noticeadapter = NoticeAdapter(requireContext(), noticeList) { notice ->
            var link: String = notice.link
            if (!link.startsWith("http://") && !link.startsWith("https://"))
                link = "http://" + link
            val Intent: Intent = Uri.parse(link).let { webpage ->
                Intent(Intent.ACTION_VIEW, webpage)
            }
            startActivity(Intent)
        }
        recyclerView2.adapter = Noticeadapter
        recyclerView2.scrollToPosition(SCROLL_STATE_IDLE)

        // LayoutManager 설정. RecyclerView 에서는 필수
        var lm = LinearLayoutManager(requireContext())
        recyclerView2.keyword_notice.layoutManager = lm
        recyclerView2.keyword_notice.setHasFixedSize(true)
        // Web 통신
        StrictMode.enableDefaults()

        val preferences = activity!!.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val keywordList = preferences.getString("Keys", "오류")?.split("+")
        var notice_Url = keywordList.toString()
        val keywordSize = keywordList!!.count()

        if(previousPage =="") {
            Url = "http://15.165.178.103/notice/all?page=1"
        }

        val set: MutableSet<String> = mutableSetOf("")
        if(notice_Url =="[]"){  //페이지가 빈 경우
            set.removeAll(set)
            progressBar.setVisibility(View.GONE)
            keywordNullView.setVisibility(View.VISIBLE)
            itemNullView.setVisibility(View.GONE)
        }
        else {
            while ((itemcount == 0 || itemcount <= 10) && Url !="null") {    //저장된 item값이 10개 미만이면 진행
                val noticeStream = URL(Url).openConnection() as HttpURLConnection
                var noticeRead = BufferedReader(InputStreamReader(noticeStream.inputStream, "UTF-8"))
                val noticeResponse = noticeRead.readLine()
                val jObject = JSONObject(noticeResponse)
                val jArray = jObject.getJSONArray("results")

                previousPage = jObject.getString("previous")
                nextPage = jObject.getString("next")
                Url = nextPage        //다음 Url 주소 변경

////         모든 공지 noticeList 에 저장
                for (i in 0 until jArray.length()) {
                    for (j in keywordList) {
                        val obj = jArray.getJSONObject(i)
                        val title = obj.getString("title")
                        if (title.contains(j) == true && set.contains(title) == false) {    //키워드를 포함하고 값 중복x시
                            set.add(title)          //중복된 데이터 저장 x
                            var id = obj.getString("id")
                            val date = obj.getString("date")
                            val author = obj.getString("author")
                            val link = obj.getString("link")
                            var board = id.split("-")
                            var dateArr = date.split("-")
                            var day = dateArr[2].split("T")
                            var days = dateArr[0] + "년 " + dateArr[1] + "월 " + day[0] + "일"
                            val noticeLine =
                                Notice(title, board[0], "게시일: " + days, "작성자: " + author, link)
                            noticeList.add(noticeLine)
                            itemcount = itemcount + 1   //새로 저장되는 아이템
                        }
                    }
                }

            }
            if(checkcount==0){
                recyclerView2.scrollToPosition(0)
            }
            else{
                recyclerView2.scrollToPosition(Noticeadapter.itemCount-itemcount-7)
            }
            Handler().postDelayed({   //스크롤시 progressbar 보이게하고 조금 대기
                progressBar.visibility = View.GONE
                mLockRecyclerView==false
                checkcount++
            }, 0)

        }
        if(noticeList.size ==0 && notice_Url !="[]"){
            keywordNullView.setVisibility(View.GONE)
            itemNullView.setVisibility(View.VISIBLE)
            Toast.makeText(requireContext(), noticeList.size.toString(), Toast.LENGTH_SHORT).show()
        }
    }
    /**
     *  스크롤시 서버의 다음 페이지 정보를 크롤링
     *  @author 희진
     */
    fun scrollPagination(){
        recyclerView2.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1)
                    && newState ==SCROLL_STATE_IDLE ) {  //위치가 맨 밑이며 중복 안되고 멈춘경우
                    if(Url!="null") {
                        progressBar.visibility = View.VISIBLE                           //progressbar 나옴
                        Handler().postDelayed({   //스크롤시 progressbar 보이게하고 조금 대기
                            parsing()
                        }, 700)
                    }
                    else{
                        Toast.makeText(requireContext(), "더 이상 공지가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}

