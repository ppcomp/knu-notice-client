package com.ppcomp.knu.fragment
import android.annotation.SuppressLint
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Notice
import com.ppcomp.knu.utils.Parsing
import com.ppcomp.knu.utils.PreferenceHelper
import kotlinx.android.synthetic.main.activity_main_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_keyword_notice.*
import kotlinx.android.synthetic.main.fragment_keyword_notice.view.*
import kotlinx.android.synthetic.main.fragment_notice_layout.*
import kotlinx.android.synthetic.main.fragment_notice_layout.view.*

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
    private lateinit var searchIcon:ImageView
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
        searchIcon = view!!.findViewById<ImageView>(R.id.search_icon)

        searchIcon.visibility = View.GONE
        progressBar.visibility = View.GONE      //progressbar 숨기기
        keywordNullView.visibility = View.GONE
        itemNullView.visibility = View.GONE

        parsing()
        Parsing.scrollPagination(
            requireContext(),
            keywordRecyclerView,
            progressBar,
            ::parsing
        )

        mHandler = Handler()
        view.keyword_swipe.setOnRefreshListener {
            // Initialize a new Runnable
            mRunnable = Runnable {
                // Hide swipe to refresh icon animation
                url = ""
                parsing()
                keyword_swipe.isRefreshing = false
            }
            mHandler.postDelayed(mRunnable, 1000)
        }
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    private fun parsing() {
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
                progressBar,
                url,
                searchQuery,
                target
            )
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
}