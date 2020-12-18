package com.ppcomp.knu.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Subscription
import com.ppcomp.knu.`object`.noticeData.NoticeViewModel
import com.ppcomp.knu.activity.WebViewActivity
import com.ppcomp.knu.adapter.AlarmAdapter
import com.ppcomp.knu.adapter.BookmarkAdapter
import com.ppcomp.knu.utils.PreferenceHelper
import kotlinx.android.synthetic.main.fragment_alarm.*
import kotlinx.android.synthetic.main.fragment_bookmark.*
import kotlinx.android.synthetic.main.fragment_bookmark.bookmark_swipe
import kotlinx.android.synthetic.main.fragment_bookmark.view.*
class AlarmFragment : Fragment() {

    private lateinit var alarmRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var noData: TextView
    private lateinit var searchIcon: ImageView
    private lateinit var bookmarkViewModel: NoticeViewModel
    private lateinit var adapter: BookmarkAdapter
    private lateinit var getList: String
    private lateinit var listType: TypeToken<ArrayList<Subscription>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alarm, container, false)

        alarmRecyclerView = view!!.findViewById(R.id.alarm_notice) as RecyclerView    //recyclerview 가져오기
        progressBar = view!!.findViewById((R.id.alarm_progressbar)) as ProgressBar
        noData = view!!.findViewById((R.id.alarm_null_view)) as TextView
        //searchIcon = view!!.findViewById<ImageView>(R.id.search_icon)
        progressBar.visibility = View.GONE                                //progressbar 숨기기
        //searchIcon.visibility = View.GONE

        listType = object : TypeToken<ArrayList<Subscription>>() {}
        getList = PreferenceHelper.get("subList", "").toString();
        bookmarkViewModel = ViewModelProvider(this).get(NoticeViewModel::class.java)
        adapter = AlarmAdapter(bookmarkViewModel) { alarm ->
            var link: String = alarm.link!!
            if (!link!!.startsWith("http://") && !link.startsWith("https://"))
                link = "http://" + link
            val intent: Intent = Intent(requireContext(), WebViewActivity::class.java)
            intent.putExtra("link",link)
            intent.putExtra("title",alarm.title)
            intent.putExtra("notice",alarm)
            requireContext().startActivity(intent)
        }

        alarmRecyclerView.adapter = adapter    // LayoutManager 설정. RecyclerView 에서는 필수
        alarmRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        alarmRecyclerView.setHasFixedSize(true)    // 아이템의 변화가 있을 때 recyclerView의 크기가 바뀌지 않으면 true로 설정하는 것이 좋음

        makingView()


        view.bookmark_swipe.setOnRefreshListener {
            // Initialize a new Runnable
            val mRunnable = Runnable {
//                 Hide swipe to refresh icon animation
                makingView()
                alarm_swipe.isRefreshing = false
            }
            Handler().postDelayed(mRunnable, 2000)
        }
        return view
    }

    /**
     * bookmarkViewModel 에 있는 리스트를 adapter에 맵핑하고 리스트 변화 감시
     * @author 정준
     */
    private fun makingView() {
        bookmarkViewModel.getNoticeList().observe(viewLifecycleOwner, Observer { it ->
            if (it != null) adapter.submitList(it)
            updateViewStatus()
        })
    }

    /**
     *  View 상태 업데이트
     *  @author 정준
     */
    private fun updateViewStatus() {
        if(bookmarkViewModel.isListNullOrEmpty()) { //리스트가 비어있으면
            noData.visibility = View.VISIBLE
            alarmRecyclerView.visibility = View.GONE
        }
        else {
            noData.visibility = View.GONE
            alarmRecyclerView.visibility = View.VISIBLE
        }
    }

}
