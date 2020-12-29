package com.ppcomp.knu.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ppcomp.knu.GlobalApplication
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Notice
import com.ppcomp.knu.`object`.noticeData.NoticeViewModel
import com.ppcomp.knu.activity.WebViewActivity
import com.ppcomp.knu.adapter.BookmarkAdapter
import kotlinx.android.synthetic.main.fragment_bookmark.*
import kotlinx.android.synthetic.main.fragment_bookmark.view.*

/**
 * 북마크 리스트를 보여주는 Fragment
 * @author 정준
 */
class BookmarkFragment : Fragment() {

    private lateinit var noticeRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var noData: TextView
    private lateinit var searchIcon: ImageView
    private lateinit var bookmarkViewModel: NoticeViewModel
    private lateinit var adapter: BookmarkAdapter
    private lateinit var trashcan: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bookmark, container, false)

        noticeRecyclerView = view!!.findViewById(R.id.bookmark_notice) as RecyclerView    //recyclerview 가져오기
        progressBar = view!!.findViewById((R.id.bookmark_progressbar)) as ProgressBar
        noData = view!!.findViewById((R.id.bookmark_null_view)) as TextView
        searchIcon = view!!.findViewById<ImageView>(R.id.search_icon)
        trashcan = view!!.findViewById<ImageView>(R.id.trash_icon)
        progressBar.visibility = View.GONE                                //progressbar 숨기기
        searchIcon.visibility = View.GONE
        trashcan.visibility = View.GONE

        bookmarkViewModel = ViewModelProvider(this).get(NoticeViewModel::class.java)
        adapter = BookmarkAdapter(bookmarkViewModel) { notice ->
            var link: String = notice.link!!
            if (!link!!.startsWith("http://") && !link.startsWith("https://"))
                link = "http://" + link
            val intent: Intent = Intent(requireContext(), WebViewActivity::class.java)
            intent.putExtra("link",link)
            intent.putExtra("title",notice.title)
            intent.putExtra("bookmark",notice.bookmark)
            intent.putExtra("notice",notice)
            requireContext().startActivity(intent)
        }

        noticeRecyclerView.adapter = adapter    // LayoutManager 설정. RecyclerView 에서는 필수
        noticeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        noticeRecyclerView.setHasFixedSize(true)    // 아이템의 변화가 있을 때 recyclerView의 크기가 바뀌지 않으면 true로 설정하는 것이 좋음

        makingView()


        view.bookmark_swipe.setOnRefreshListener {
            // Initialize a new Runnable
            val mRunnable = Runnable {
//                 Hide swipe to refresh icon animation
                makingView()
                bookmark_swipe.isRefreshing = false
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
            noticeRecyclerView.visibility = View.GONE
        }
        else {
            noData.visibility = View.GONE
            noticeRecyclerView.visibility = View.VISIBLE
        }
    }
}
