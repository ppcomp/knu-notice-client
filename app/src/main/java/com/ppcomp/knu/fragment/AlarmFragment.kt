package com.ppcomp.knu.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.RxPagedListBuilder
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Subscription
import com.ppcomp.knu.`object`.noticeData.Alarm
import com.ppcomp.knu.`object`.noticeData.Notice
import com.ppcomp.knu.`object`.noticeData.dataSource.KeywordNoticeAllDataSource
import com.ppcomp.knu.activity.SearchableActivity
import com.ppcomp.knu.adapter.AlarmAdapter
import com.ppcomp.knu.adapter.BookmarkAdapter
import com.ppcomp.knu.adapter.NoticeAdapter
import com.ppcomp.knu.utils.PreferenceHelper
import kotlinx.android.synthetic.main.activity_main_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_alarm.*
import kotlinx.android.synthetic.main.fragment_alarm.view.*
import kotlinx.android.synthetic.main.fragment_keyword_notice.*
import kotlinx.android.synthetic.main.fragment_keyword_notice.view.*
import kotlinx.android.synthetic.main.fragment_keyword_notice.view.keyword_swipe

class AlarmFragment : Fragment() {

    private lateinit var alarmRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var noData: TextView
    private var alarmList = arrayListOf<Alarm>()
    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var makeGson: Gson
    private lateinit var getList: String
    private lateinit var listType: TypeToken<ArrayList<Alarm>>
    private lateinit var searchIcon: ImageView

    private lateinit var mRunnable: Runnable
    private val mHandler: Handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alarm, container, false)

        alarmRecyclerView =
            view!!.findViewById(R.id.alarm_notice) as RecyclerView    //recyclerview 가져오기
        progressBar = view!!.findViewById((R.id.alarm_progressbar)) as ProgressBar
        noData = view!!.findViewById((R.id.alarm_null_view)) as TextView
        progressBar.visibility = View.GONE                                //progressbar 숨기기
        makeGson = GsonBuilder().create()
        listType = object : TypeToken<ArrayList<Alarm>>() {}
        searchIcon = view!!.findViewById<ImageView>(R.id.search_icon)
        searchIcon.visibility = View.GONE
        makingView()

        view.trash_icon.setOnClickListener {
            PreferenceHelper.put("alarm", "")
            noData.visibility = View.VISIBLE
            alarmRecyclerView.visibility = View.GONE
        }

        view.alarm_swipe.setOnRefreshListener {
            mRunnable = Runnable {
                alarmList.clear()
                makingView()
                alarm_swipe.isRefreshing = false
            }
            mHandler.postDelayed(mRunnable, 1000)
        }

        return view
    }

    private fun makingView(
    ) {
        getList = PreferenceHelper.get("alarm", "").toString()
        if(getList == "") {
            noData.visibility = View.VISIBLE
            alarmRecyclerView.visibility = View.GONE
        }else {
            noData.visibility = View.GONE
            alarmRecyclerView.visibility = View.VISIBLE
            val getAlarmList : ArrayList<Alarm> = makeGson.fromJson(getList, listType.type)
            for (i in getAlarmList) {
                Log.d("알람2", i.toString())
                alarmList.add(Alarm(i.id, i.date))
            }
            alarmAdapter = AlarmAdapter(requireContext(), alarmList)
            alarmRecyclerView.adapter = alarmAdapter    // LayoutManager 설정. RecyclerView 에서는 필수
            alarmRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            alarmRecyclerView.setHasFixedSize(true)    // 아이템의 변화가 있을 때 recyclerView의 크기가 바뀌지 않으면 true로 설정하는 것이 좋음
        }
    }

}

