package com.ppcomp.knu.fragment
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.util.Log
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
import com.ppcomp.knu.utils.Parsing
import com.ppcomp.knu.utils.PreferenceHelper
import kotlinx.android.synthetic.main.fragment_keyword_notice.*
import kotlinx.android.synthetic.main.fragment_keyword_notice.view.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import kotlin.math.abs

/**
 * 키워드가 포함된 공지사항만 보여주는 fragment
 * 리팩토링 - 정우
 * @author 희진
 */
class KeywordNoticeFragment : Fragment() {
    private var noticeList = arrayListOf<Notice>()
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private lateinit var keywordRecyclerView : RecyclerView
    private lateinit var progressBar : ProgressBar
    private lateinit var keywordNullView : TextView
    private lateinit var itemNullView : TextView
    private var url:String=""   //mainUrl + notice_Url 저장 할 변수
    private var nextPage:String=""
    private var previousPage:String=""

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

        progressBar.visibility = View.GONE      //progressbar 숨기기
        keywordNullView.visibility = View.GONE
        itemNullView.visibility = View.GONE

        parsing()
        scrollPagination()

        mHandler = Handler()
        view.keyword_swipe.setOnRefreshListener {
            // Initialize a new Runnable
            mRunnable = Runnable {
            // Hide swipe to refresh icon animation
                keyword_swipe.isRefreshing = false
            }
            mHandler.postDelayed(mRunnable, 2000)
        }

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    fun parsing() {
        val searchQuery = PreferenceHelper.get("Keys", "오류").toString()
        val target = PreferenceHelper.get("Urls", "오류").toString()
        if (searchQuery == "") {                        // 검색어(키워드)가 없는 경우
            noticeList.removeAll(noticeList)
            progressBar.visibility = View.GONE
            keywordNullView.visibility = View.VISIBLE  //문구 수정 필요
            itemNullView.visibility = View.GONE
        } else if (target == "") {                      // 검색 대상 학과가 지정되지 않은 경우
            noticeList.removeAll(noticeList)
            progressBar.visibility = View.GONE
            keywordNullView.visibility = View.GONE
            itemNullView.visibility = View.GONE
        } else {
            keywordNullView.visibility = View.GONE
            val parseResult: List<String> = Parsing.parsing(
                requireContext(),
                noticeList,
                keywordRecyclerView,
                url,
                searchQuery,
                target
            )
            Handler().postDelayed({         //스크롤시 progressbar 보이게하고 조금 대기
                progressBar.visibility = View.GONE
                keywordRecyclerView.scrollToPosition(noticeList.size - 11)
            }, 40)
            if (noticeList.size == 0) {     //해당하는 아이템이 없을 때 화면 설정
                noticeList.removeAll(noticeList)
                keywordNullView.visibility = View.GONE
                itemNullView.visibility = View.VISIBLE
            }
            previousPage = parseResult[0]
            url = parseResult[1]
            nextPage = parseResult[2]
        }
    }

    /**
     *  스크롤시 서버의 다음 페이지 정보를 크롤링
     *  @author 희진
     */
    private fun scrollPagination(){
        keywordRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if ((!recyclerView.canScrollVertically(1) && newState==SCROLL_STATE_IDLE) &&
                    !progressBar.isAnimating
                ) {  //위치가 맨 밑이며 progressBar가 GONE이며 멈춘경우
                    if(url!="null") {
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