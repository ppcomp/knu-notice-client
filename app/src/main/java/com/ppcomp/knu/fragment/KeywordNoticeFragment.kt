package com.ppcomp.knu.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.get
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
import com.ppcomp.knu.`object`.noticeData.NoticeViewModel
import com.ppcomp.knu.`object`.noticeData.dataSource.KeywordNoticeAllDataSource
import com.ppcomp.knu.`object`.noticeData.dataSource.NoticeAllDataSource
import com.ppcomp.knu.adapter.BookmarkAdapter
import com.ppcomp.knu.adapter.NoticeAdapter
import com.ppcomp.knu.utils.PreferenceHelper
import com.ppcomp.knu.utils.RestApi
import kotlinx.android.synthetic.main.activity_main_toolbar.view.*
import kotlinx.android.synthetic.main.activity_subscription.*
import kotlinx.android.synthetic.main.fragment_keyword_notice.*
import kotlinx.android.synthetic.main.fragment_keyword_notice.view.*
import kotlinx.android.synthetic.main.fragment_notice_layout.*
import kotlinx.android.synthetic.main.fragment_notice_layout.view.*
import kotlinx.android.synthetic.main.fragment_notice_item.view.*



/**
 * 키워드가 포함된 공지사항만 보여주는 fragment
 * 리팩토링 - 정우
 * @author 희진
 */
class KeywordNoticeFragment : Fragment() {

    private val mHandler: Handler = Handler()
    private lateinit var mRunnable: Runnable
    private lateinit var keywordRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyResultView: TextView
    private lateinit var searchIcon:ImageView
    private lateinit var radioGroup: RadioGroup
    private lateinit var allListradioButton: RadioButton
    private lateinit var subListradioButton: RadioButton
    private var target: String = ""
    private var searchQuery:String=""
    private val restApi = RestApi.create()
    private val config = PagedList.Config.Builder()
        .setInitialLoadSizeHint(20)     // 초기 로딩 아이템 개수
        .setPageSize(10)                // 한 페이지에 로딩하는 아이템 개수
        .setPrefetchDistance(5)         // n개의 아이템 여유를 두고 로딩
        .setEnablePlaceholders(true)    // default: true
        .build()
    private lateinit var bookmarkViewModel: NoticeViewModel
    private lateinit var adapter: NoticeAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_keyword_notice, container, false)

        searchQuery = PreferenceHelper.get("Keys", "").toString()
        keywordRecyclerView = view.findViewById(R.id.keyword_notice) as RecyclerView
        progressBar = view.findViewById((R.id.keyword_progressbar)) as ProgressBar
        emptyResultView = view.findViewById(R.id.keywordlist_null_view) as TextView
        searchIcon = view.findViewById(R.id.search_icon)
        radioGroup = view.findViewById(R.id.keyword_radio_group) as RadioGroup
        allListradioButton = view.findViewById(R.id.keyword_show_all)
        subListradioButton = view.findViewById(R.id.keyword_show_subs)

        searchIcon.visibility = View.GONE
        progressBar.visibility = View.GONE      //progressbar 숨기기
        emptyResultView.visibility = View.GONE

        bookmarkViewModel = ViewModelProvider(this).get(NoticeViewModel::class.java)
        adapter = NoticeAdapter(bookmarkViewModel) { notice ->
            var link: String? = notice.link
            if (link != null) {
                if (!link.startsWith("http://") && !link.startsWith("https://"))
                    link = "http://$link"
            }
            val intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            requireContext().startActivity(intent)
        }
        keywordRecyclerView.adapter = adapter
        keywordRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        makingView(adapter,
            KeywordNoticeAllDataSource(
                restApi,
                searchQuery,
                target
            ), MutableLiveData())

        view.keyword_swipe.setOnRefreshListener {
            mRunnable = Runnable {
                if (subListradioButton.isChecked) {
                    target = PreferenceHelper.get("Urls","").toString()
                } else if (allListradioButton.isChecked) {
                    target = ""
                }
                makingView(adapter,
                    KeywordNoticeAllDataSource(
                        restApi,
                        searchQuery,
                        target
                    ), MutableLiveData())
                keyword_swipe.isRefreshing = false
            }
            mHandler.postDelayed(mRunnable, 1000)
        }

        radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            if (i == R.id.keyword_show_subs) {
                target = PreferenceHelper.get("Urls","").toString()

            } else if (i == R.id.keyword_show_all) {
                target = ""
            }
            makingView(
                adapter,
                KeywordNoticeAllDataSource(
                    restApi,
                    searchQuery,
                    target
                ), MutableLiveData()
            )
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
                adapter.submitList(null)
                adapter.submitList(it)
                updateViewStatus()
            }
        bookmarkViewModel.getNoticeList().observe(viewLifecycleOwner, Observer {
            //코드가 없어도 bookmarkViewModel 은 변화가 생기면 업데이트 됨
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateViewStatus() {
        searchQuery = PreferenceHelper.get("Keys", "").toString()
        target = PreferenceHelper.get("Urls","").toString()

        if (searchQuery == "") {
            emptyResultView.text = "선택된 키워드가 없습니다. \n [설정 -> 키워드 설정]\n화면에서 설정해주세요."
            emptyResultView.visibility = View.VISIBLE
        } else {
            if (keywordRecyclerView.adapter!!.itemCount == 0 ) {
                emptyResultView.text = "해당 키워드가 검색결과로\n존재하지 않습니다."
                emptyResultView.visibility = View.VISIBLE
            } else if (target == "" && subListradioButton.isChecked){ //선택된 구독리스트가 없을 때 구독 라디오 버튼을 누르면,
                emptyResultView.visibility = View.VISIBLE
                emptyResultView.text = "구독리스트가 없습니다. \n [설정 -> 구독리스트]\n화면에서 설정해주세요."
            } else {
                if (!GlobalApplication.isServerConnect) {
                    emptyResultView.text = "서버 연결에 실패했습니다.\n관리자에게 문의해주세요."
                    emptyResultView.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "서버 연결에 실패했습니다.\n관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
                }
                emptyResultView.visibility = View.GONE
            }
        }
    }
}
