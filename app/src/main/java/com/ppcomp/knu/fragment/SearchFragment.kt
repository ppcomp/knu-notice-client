package com.ppcomp.knu.fragment

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Notice
import com.ppcomp.knu.utils.Parsing
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*


/**
 * 하단 바 '리스트'페이지의  kt
 * 크롤링한 공지사항을 띄워줌
 * 리펙토링 - 정우
 * @author 희진
 */
class SearchFragment : Fragment() {

    private var noticeList = arrayListOf<Notice>()
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyResultView: TextView
    private lateinit var searchButton: Button
    private lateinit var search_edit: EditText
    private var url: String = ""    //mainUrl + notice_Url 저장 할 변수
    private var nextPage: String = ""
    private var previousPage: String = ""
    private var searchQuery: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        progressBar = view!!.findViewById((R.id.progressbar)) as ProgressBar
        emptyResultView = view!!.findViewById((R.id.search_noData)) as TextView
        searchButton = view!!.findViewById(R.id.search_button) as Button
        progressBar.visibility = View.GONE
        searchRecyclerView =
            view!!.findViewById(R.id.search_recycler) as RecyclerView   //recyclerview 가져오기
        search_edit = view!!.findViewById(R.id.search_edit) as EditText

        search_edit.setOnKeyListener(object : View.OnKeyListener {      // 엔터키누르면 검색버튼을 자동으로 누르도록
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (event != null) {
                    if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        searchButton.performClick()
                        return true
                    }
                }
                return false
            }
        })

        searchButton.setOnClickListener() {
            searchQuery = search_edit.text.toString()
            if (searchQuery == "") {
                emptyResultView.visibility = View.VISIBLE
            } else {
                emptyResultView.visibility = View.GONE
                url = ""
                parsing()
                Parsing.scrollPagination(
                    requireContext(),
                    searchRecyclerView,
                    progressBar,
                    ::parsing
                )
            }
        }

        mHandler = Handler()
        view.search_swipe.setOnRefreshListener {
            // Initialize a new Runnable
            mRunnable = Runnable {
                // Hide swipe to refresh icon animation
                search_swipe.isRefreshing = false
            }
            mHandler.postDelayed(mRunnable, 2000)

        }
        return view
    }

    /**
     * 파싱 기능
     * @author 김우진, 희진, 정우
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun parsing() {
        search_edit = view!!.findViewById(R.id.search_edit) as EditText
        searchQuery = search_edit.text.toString()

        val parseResult: List<String> = Parsing.parsing(
            requireContext(),
            noticeList,
            searchRecyclerView,
            progressBar,
            url,
            searchQuery,
            ""
        )
        if (noticeList.size == 0) {
            Toast.makeText(requireContext(), "검색어에 해당되는 게시글이 존재하지 않습니다", Toast.LENGTH_SHORT)
                .show()
        }
        previousPage = parseResult[0]
        url = parseResult[1]
        nextPage = parseResult[2]
    }
}
