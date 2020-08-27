package com.ppcomp.knu.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ppcomp.knu.GlobalApplication
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.noticeData.Notice
import com.ppcomp.knu.adapter.NoticeAdapter
import com.ppcomp.knu.`object`.noticeData.dataSource.NoticeAllDataSource
import com.ppcomp.knu.`object`.noticeData.dataSource.NoticeSearchDataSource
import com.ppcomp.knu.utils.PreferenceHelper
import com.ppcomp.knu.utils.RestApi
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
    private val mHandler: Handler = Handler()
    private lateinit var mRunnable: Runnable
    private lateinit var noticeRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyResultView: TextView
    private lateinit var searchNoData: TextView
    private lateinit var search_edits: EditText
    private var url: String = ""    //mainUrl + notice_Url 저장 할 변수
    private var searchQuery: String = ""
    private val restApi = RestApi.create()
    private val config = PagedList.Config.Builder()
        .setInitialLoadSizeHint(20)     // 초기 로딩 아이템 개수
        .setPageSize(10)                // 한 페이지에 로딩하는 아이템 개수
        .setPrefetchDistance(5)         // n개의 아이템 여유를 두고 로딩
        .setEnablePlaceholders(true)    // default: true
        .build()
    private val adapter = NoticeAdapter(bookmarkList) { notice ->
        var link: String? = notice.link
        if (link != null) {
            if (!link.startsWith("http://") && !link.startsWith("https://"))
                link = "http://$link"
        }
        val intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        requireContext().startActivity(intent)
    }

    @SuppressLint("CheckResult")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notice_layout, container, false)
        noticeRecyclerView = view.findViewById(R.id.notice) as RecyclerView   //recyclerview 가져오기
        progressBar = view.findViewById((R.id.progressbar)) as ProgressBar
        emptyResultView = view.findViewById((R.id.noData)) as TextView
        searchNoData = view.findViewById((R.id.search_noData)) as TextView

        progressBar.visibility = View.GONE                                      //progressbar 숨기기
        emptyResultView.visibility = View.GONE
        searchNoData.visibility = View.GONE

        val jsonList = PreferenceHelper.get("bookmark", "")
        if (jsonList != "")
            bookmarkList = gson.fromJson(jsonList, listType.type) //북마크 리스트 저장

//        DividerItemDecoration(recyclerView.context, LinearLayout.VERTICAL).apply {
//            setDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.divider)!!)
//            recyclerView.addItemDecoration(this)
//            recyclerView.setHasFixedSize(true)
//        }

        noticeRecyclerView.adapter = adapter
        noticeRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        makingView(adapter,
            NoticeAllDataSource(
                restApi
            ), MutableLiveData())

        view.swipe.setOnRefreshListener {
            // Initialize a new Runnable
            mRunnable = Runnable {
                // Hide swipe to refresh icon animation
                url = ""
                searchQuery = ""
                makingView(adapter,
                    NoticeAllDataSource(
                        restApi
                    ), MutableLiveData())
                swipe.isRefreshing = false
            }
            mHandler.postDelayed(mRunnable, 1000)
        }

        view.search_icon.setOnClickListener {
            searchDialog(inflater)
        }
        return view
    }

    @SuppressLint("CheckResult")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun makingView(
        adapter: NoticeAdapter,
        dataSource: PageKeyedDataSource<Int, Notice>,
        mutableLiveData: MutableLiveData<PageKeyedDataSource<Int, Notice>>?
    ) {
        val builder = RxPagedListBuilder<Int, Notice>(object: DataSource.Factory<Int, Notice>() {
            override fun create(): DataSource<Int, Notice> {
                mutableLiveData?.postValue(dataSource)
                return dataSource
            }
        }, config)
        builder.buildObservable()
            .subscribe {
                adapter.submitList(it)
                updateViewStatus()
            }
    }

    /**
     * RecyclerView 에 상태를 표시하거나 데이터를 표시한다.
     *  - 구독한 사이트가 없다면:
     *      "구독리스트가 없습니다. [설정 -> 구독리스트] 화면에서 설정해주세요."
     *  - 구독한 사이트가 있다면
     *      - 공지가 하나도 없을 때:
     *          - 검색중이라면:
     *              "검색어가 없습니다."
     *          - 검색중이 아니라면:
     *              - 서버연결 정상시:
     *                  "구독리스트가 없습니다. [설정 -> 구독리스트] 화면에서 설정해주세요."
     *                  toast - "게시글이 존재하지 않습니다."
     *              - 서버연결 비정상시:
     *                  "서버 연결에 실패했습니다. 관리자에게 문의해주세요."
     *                  toast - "서버 연결에 실패했습니다."
     * @author 김우진,희진,정우
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateViewStatus() {
        val target = PreferenceHelper.get("Urls", "").toString()
        var toastText = ""
        if (target == "") {
            emptyResultView.text = "구독리스트가 없습니다. \n [설정 -> 구독리스트]\n화면에서 설정해주세요."
            emptyResultView.visibility = View.VISIBLE
        } else {
            emptyResultView.visibility = View.GONE
            searchNoData.visibility = View.GONE
            if (noticeRecyclerView.adapter!!.itemCount == 0) {
                if (searchQuery != "") {
                    searchNoData.visibility = View.VISIBLE
                } else {
                    if(GlobalApplication.isServerConnect) { // DataSource 클래스에서 확인한 서버 연결 상태
                        toastText = "게시글이 존재하지 않습니다."
                        emptyResultView.text = "구독리스트가 없습니다. \n [설정 -> 구독리스트]\n화면에서 설정해주세요."
                    }
                    else {
                        toastText = "서버 연결에 실패했습니다."
                        emptyResultView.text = "서버 연결에 실패했습니다.\n관리자에게 문의해주세요."
                    }
                    Toast.makeText(requireContext(), toastText, Toast.LENGTH_SHORT).show()
                    emptyResultView.visibility = View.VISIBLE
                }
            } else {
                searchNoData.visibility = View.GONE
                emptyResultView.visibility = View.GONE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun searchDialog(inflater: LayoutInflater) {
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun searchRun() {
        searchQuery = search_edits.text.toString()
        url = ""
        adapter.submitList(null)
        if (searchQuery != "") {
            noticeList.removeAll(noticeList)
            makingView(adapter,
                NoticeSearchDataSource(
                    restApi,
                    searchQuery
                ), MutableLiveData())
        } else {
            Toast.makeText(requireContext(), "입력된 검색어가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
