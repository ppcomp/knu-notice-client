package com.ppcomp.knu.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ppcomp.knu.GlobalApplication
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.noticeData.Notice
import com.ppcomp.knu.`object`.noticeData.NoticeViewModel
import com.ppcomp.knu.`object`.noticeData.dataSource.NoticeAllDataSource
import com.ppcomp.knu.activity.SearchableActivity
import com.ppcomp.knu.activity.WebViewActivity
import com.ppcomp.knu.adapter.NoticeAdapter
import com.ppcomp.knu.utils.PreferenceHelper
import com.ppcomp.knu.utils.RestApi
import kotlinx.android.synthetic.main.activity_main_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_notice_layout.*
import kotlinx.android.synthetic.main.fragment_notice_layout.view.*


/**
 * 하단 바 '리스트'페이지의  kt
 * 크롤링한 공지사항을 띄워줌
 * 리펙토링 - 정우
 * @author 희진, 정준
 */
class NoticeFragment : Fragment() {
    
    private lateinit var bookmarkViewModel: NoticeViewModel
    private lateinit var noticeRecyclerView: RecyclerView
    private lateinit var emptyResultView: TextView
    private var searchQuery: String = ""
    private var target: String = PreferenceHelper.get("Urls","")!!
    private val restApi = RestApi.create()
    private val config = PagedList.Config.Builder()
        .setInitialLoadSizeHint(20)     // 초기 로딩 아이템 개수
        .setPageSize(10)                // 한 페이지에 로딩하는 아이템 개수
        .setPrefetchDistance(5)         // n개의 아이템 여유를 두고 로딩
        .setEnablePlaceholders(true)    // default: true
        .build()
    private val adapter = NoticeAdapter { notice ->
        var link: String? = notice.link
        if (link != null) {
            if (!link.startsWith("http://") && !link.startsWith("https://"))
                link = "http://$link"
        }
        val intent: Intent = Intent(requireContext(), WebViewActivity::class.java)
        intent.putExtra("link",link)
        intent.putExtra("title",notice.title)
        intent.putExtra("bookmark",notice.bookmark)
        intent.putExtra("notice",notice)
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
        emptyResultView = view.findViewById((R.id.noData)) as TextView
        emptyResultView.visibility = View.GONE

        bookmarkViewModel = ViewModelProvider(requireActivity()).get(NoticeViewModel::class.java)
        adapter.setViewModel(bookmarkViewModel)

        noticeRecyclerView.adapter = adapter
        noticeRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        //RecyclerView item 생성
        if (searchQuery == "") {    //search View 아닐때
            makingView(
                adapter,
                NoticeAllDataSource(
                    restApi,
                    ""
                ), MutableLiveData())
        }
        else {  //search View 일때는 검색아이콘 제거
            view.search_icon.visibility = View.GONE
        }

        //새로고침 리스너
        view.swipe.setOnRefreshListener {
            // Initialize a new Runnable
            val mRunnable = Runnable {
                // Hide swipe to refresh icon animation
                makingView(adapter,
                    NoticeAllDataSource(
                        restApi,
                        searchQuery,
                        target
                    ), MutableLiveData())
                swipe.isRefreshing = false
            }
            Handler().postDelayed(mRunnable, 1000)
        }
        // 검색 리스너
        view.search_icon.setOnClickListener {
            val intent = Intent(requireContext(), SearchableActivity::class.java)
            startActivity(intent)
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
        val builder = RxPagedListBuilder<Int, Notice>(object : DataSource.Factory<Int, Notice>() {
            override fun create(): DataSource<Int, Notice> {
                mutableLiveData?.postValue(dataSource)
                return dataSource
            }
        }, config)
        builder.buildObservable()
            .subscribe {
                adapter.submitList(null)
                adapter.submitList(it)
                updateViewStatus()
            }
        bookmarkViewModel.getNoticeList().observe(this, Observer {
            //코드가 없어도 bookmarkViewModel 은 변화가 생기면 업데이트 됨
        })
    }

    /**
     * RecyclerView 에 상태를 표시하거나 데이터를 표시한다.
     *  - 구독한 사이트가 없다면:
     *      "구독리스트가 없습니다. [설정 -> 구독리스트] 화면에서 설정해주세요."
     *  - 구독한 사이트가 있다면
     *      - 공지가 하나도 없을 때:
     *          - 검색중이라면:
     *              "검색 결과가 없습니다."
     *          - 검색중이 아니라면:
     *              - 서버연결 정상시:
     *                  "게시글이 존재하지 않습니다."
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
            if (noticeRecyclerView.adapter!!.itemCount == 0) {
                if (searchQuery != "") {
                    toastText = "검색 결과가 없습니다."
                    emptyResultView.text = "검색 결과가 없습니다."
                } else {
                    if(GlobalApplication.isServerConnect) { // DataSource 클래스에서 확인한 서버 연결 상태
                        toastText = "게시글이 존재하지 않습니다."
                        emptyResultView.text = "게시글이 존재하지 않습니다."
                    }
                    else {
                        toastText = "서버 연결에 실패했습니다."
                        emptyResultView.text = "서버 연결에 실패했습니다.\n관리자에게 문의해주세요."
                    }
                }
                Toast.makeText(requireContext(), toastText, Toast.LENGTH_SHORT).show()
                emptyResultView.visibility = View.VISIBLE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun searchRun(searchQuery: String, target: String) {
        this.searchQuery = searchQuery
        this.target = target
        if (searchQuery != "") {
            makingView(adapter,
                NoticeAllDataSource(
                    restApi,
                    searchQuery,
                    target
                ), MutableLiveData())
        } else {
            Toast.makeText(requireContext(), "입력된 검색어가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 검색 화면에서 viewModel 설정
     * @author 정준
     */
    fun setBookmarkViewModel(viewModel: NoticeViewModel) {
        this.bookmarkViewModel = viewModel
        adapter.setViewModel(bookmarkViewModel)
    }
}
