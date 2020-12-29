package com.ppcomp.knu.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ppcomp.knu.R
import com.ppcomp.knu.activity.SearchableActivity
import com.ppcomp.knu.adapter.NoticeTabAdapter
import com.ppcomp.knu.utils.PreferenceHelper
import kotlinx.android.synthetic.main.activity_main_toolbar.view.*


/**
 * 하단 바 '리스트'페이지의  kt
 * 크롤링한 공지사항을 띄워줌
 * 리펙토링 - 정우
 * @author 희진, 정준
 */
class NoticeFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var noticeTabAdapter: NoticeTabAdapter
    private lateinit var trashcan: ImageView
    private var boardNames: List<String> = PreferenceHelper.get("Subs","")!!.split("+")

    @SuppressLint("CheckResult")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notice_layout, container, false)

        viewPager = view.findViewById(R.id.pager)
        noticeTabAdapter = NoticeTabAdapter(this)
        viewPager.adapter = noticeTabAdapter
        trashcan = view!!.findViewById<ImageView>(R.id.trash_icon)
        trashcan.visibility = View.GONE

        // 검색 리스너
        view.search_icon.setOnClickListener {
            val intent = Intent(requireContext(), SearchableActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tabLayout = view.findViewById(R.id.tab_layout) as TabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            if (position == 0) {
                tab.text = "전체"
            } else {
                tab.text = boardNames[position - 1]
            }
        }.attach()
    }
}
