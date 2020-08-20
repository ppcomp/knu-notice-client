package com.ppcomp.knu.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Notice
import com.ppcomp.knu.utils.Parsing
import com.ppcomp.knu.utils.PreferenceHelper
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
    private var bookmarkList = arrayListOf<Notice>()
    private var gson: Gson = GsonBuilder().create()
    private var listType: TypeToken<ArrayList<Notice>> = object : TypeToken<ArrayList<Notice>>() {}
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private lateinit var noticeRecyclerView: RecyclerView
    private lateinit var thisContext: Context
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyResultView: TextView
    private var url: String = ""    //mainUrl + notice_Url 저장 할 변수
    private var nextPage: String = ""
    private var previousPage: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notice_layout, container, false)

        noticeRecyclerView = view!!.findViewById(R.id.notice) as RecyclerView   //recyclerview 가져오기
        progressBar = view!!.findViewById((R.id.progressbar)) as ProgressBar
        emptyResultView = view!!.findViewById((R.id.noData)) as TextView
        progressBar.visibility = View.GONE                                      //progressbar 숨기기
        emptyResultView.visibility = View.GONE

        val jsonList = PreferenceHelper.get("bookmark","")
        if(jsonList != "")
            bookmarkList = gson.fromJson(jsonList,listType.type) //북마크 리스트 저장

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
                parsing()
                swipe.isRefreshing = false
            }
            mHandler.postDelayed(mRunnable, 1000)
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
        } else {
            emptyResultView.visibility = View.GONE
            val parseResult: List<String> = Parsing.parsing(
                requireContext(),
                noticeList,
                bookmarkList,
                noticeRecyclerView,
                progressBar,
                url,
                "",
                target
            )
            if (noticeList.size == 0) {
                Toast.makeText(requireContext(), "게시글이 존재하지 않습니다.", Toast.LENGTH_SHORT)
                    .show()
            }
            previousPage = parseResult[0]
            url = parseResult[1]
            nextPage = parseResult[2]
        }
    }
}
