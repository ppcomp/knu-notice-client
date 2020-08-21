package com.ppcomp.knu.fragment

import android.accessibilityservice.GestureDescription
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Notice
import com.ppcomp.knu.utils.Parsing
import com.ppcomp.knu.utils.PreferenceHelper
import kotlinx.android.synthetic.main.activity_main_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_notice_layout.*
import kotlinx.android.synthetic.main.fragment_notice_layout.view.*


/**
 * 하단 바 '리스트'페이지의  kt
 * 크롤링한 공지사항을 띄워줌
 * 리펙토링 - 정우
 * @author 희진
 */
class NoticeFragment : Fragment() {

    private var noticeList = arrayListOf<Notice>()
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private lateinit var noticeRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyResultView: TextView
    private lateinit var searchNoData: TextView
    private lateinit var search_edits: EditText
    private var url: String = ""    //mainUrl + notice_Url 저장 할 변수
    private var nextPage: String = ""
    private var previousPage: String = ""
    private var searchQuery :String=""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notice_layout, container, false)
        noticeRecyclerView = view!!.findViewById(R.id.notice) as RecyclerView   //recyclerview 가져오기
        progressBar = view!!.findViewById((R.id.progressbar)) as ProgressBar
        emptyResultView = view!!.findViewById((R.id.noData)) as TextView
        searchNoData = view!!.findViewById((R.id.search_noData)) as TextView


        progressBar.visibility = View.GONE                                      //progressbar 숨기기
        emptyResultView.visibility = View.GONE
        searchNoData.visibility = View.GONE
        parsing()

        Parsing.scrollPagination(
            requireContext(),
            noticeRecyclerView,
            progressBar,
            ::parsing
        )

        mHandler = Handler()
        view.swipe.setOnRefreshListener {
            // Initialize a new Runnable
            mRunnable = Runnable {
                // Hide swipe to refresh icon animation
                url = ""
                searchQuery=""
                parsing()
                swipe.isRefreshing = false
            }
            mHandler.postDelayed(mRunnable, 1000)
        }

        view.search_icon.setOnClickListener {
            val searchView = inflater.inflate(R.layout.activity_search_dialog, null)
            var alertDialog = AlertDialog.Builder(requireContext())
                .setTitle("검색어를 입력해주세요.")
                .setPositiveButton("검색") { dialog, which ->
                    search_edits = searchView!!.findViewById(R.id.search_edits) as EditText
                    searchQuery =  search_edits.text.toString()
                    url=""
                    if(searchQuery != "") {
                        noticeList.removeAll(noticeList)
                        parsing()
                    }
                    else{
                        Toast.makeText(requireContext(), "입력된 검색어가 없습니다.", Toast.LENGTH_SHORT).show()
                    }

                }
                .setNeutralButton("취소", null)
                .create()

            alertDialog.setCancelable(false)//  여백 눌러도 창 안없어지게 설정

            alertDialog.setView(searchView)
            alertDialog.show()

        }
        return view
    }

    /**
     * 파싱 기능
     * @author 김우진,희진
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun parsing() {
        val target = PreferenceHelper.get("Urls", "").toString()

        if (target == "") {
            emptyResultView.visibility = View.VISIBLE
        } else{
            emptyResultView.visibility = View.GONE
            searchNoData.visibility = View.GONE
            val parseResult: List<String> = Parsing.parsing(
                requireContext(),
                noticeList,
                noticeRecyclerView,
                progressBar,
                url,
                searchQuery,
                target
            )
            if( noticeList.size ==0 ){
                if(searchQuery!="") {
                    searchNoData.visibility = View.VISIBLE
                } else {
                    Toast.makeText(requireContext(), "게시글이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            } else{
                searchNoData.visibility = View.GONE
            }
            previousPage = parseResult[0]
            url = parseResult[1]
            nextPage = parseResult[2]
        }
    }

}
