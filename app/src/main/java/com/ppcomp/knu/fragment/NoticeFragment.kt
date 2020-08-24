package com.ppcomp.knu.fragment

import android.accessibilityservice.GestureDescription
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.InputFilter
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Notice
import com.ppcomp.knu.utils.Parsing
import com.ppcomp.knu.utils.PreferenceHelper
import kotlinx.android.synthetic.main.activity_keyword.*
import kotlinx.android.synthetic.main.activity_main_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_notice_layout.*
import kotlinx.android.synthetic.main.fragment_notice_layout.view.*
import java.util.regex.Pattern


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
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyResultView: TextView
    private lateinit var searchNoData: TextView
    private lateinit var search_edits: EditText
    private var url: String = ""    //mainUrl + notice_Url 저장 할 변수
    private var nextPage: String = ""
    private var previousPage: String = ""
    private var searchQuery: String = ""

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

        val jsonList = PreferenceHelper.get("bookmark", "")
        if (jsonList != "")
            bookmarkList = gson.fromJson(jsonList, listType.type) //북마크 리스트 저장

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
                searchQuery = ""
                parsing()
                swipe.isRefreshing = false
            }
            mHandler.postDelayed(mRunnable, 1000)
        }

        view.search_icon.setOnClickListener {
            val searchView = inflater.inflate(R.layout.activity_search_dialog, null)
            search_edits = searchView!!.findViewById(R.id.search_edits) as EditText

            search_edits.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
                val ps: Pattern =
                    Pattern.compile("^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+$")
                if (source == "" || ps.matcher(source).matches()) {
                    return@InputFilter source
                }
                Toast.makeText(requireContext(), "한글, 영문, 숫자만 입력 가능합니다.", Toast.LENGTH_SHORT).show()
                ""
            })

            var alertDialog = AlertDialog.Builder(requireContext(), R.style.DialogTheme)
                .setTitle("검색어를 입력하세요")
                .setNeutralButton("취소", null)
                .setPositiveButton("검색") { dialog, which ->
                    searchRun()
                }.create()

            search_edits.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                    if (event != null) {
                        if (keyCode == KeyEvent.KEYCODE_ENTER) {
                            searchRun()
                            alertDialog.dismiss()
                            return true
                        }
                    }
                    return false
                }
            })
            alertDialog.setCancelable(false)//  여백 눌러도 창 안없어지게 설정
            alertDialog.window?.setLayout(500, 400)  //dialog 크기 지정
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
        } else {
            emptyResultView.visibility = View.GONE
            searchNoData.visibility = View.GONE
            val parseResult: List<String> = Parsing.parsing(
                requireContext(),
                noticeList,
                bookmarkList,
                noticeRecyclerView,
                progressBar,
                url,
                searchQuery,
                target
            )
            if (noticeList.size == 0) {
                if (searchQuery != "") {
                    searchNoData.visibility = View.VISIBLE
                } else {
                    Toast.makeText(requireContext(), "게시글이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                searchNoData.visibility = View.GONE
            }

            previousPage = parseResult[0]
            url = parseResult[1]
            nextPage = parseResult[2]
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun searchRun() {
        searchQuery = search_edits.text.toString()
        url = ""
        if (searchQuery != "") {
            noticeList.removeAll(noticeList)
            parsing()
        } else {
            Toast.makeText(requireContext(), "입력된 검색어가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
