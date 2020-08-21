package com.ppcomp.knu.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Notice
import com.ppcomp.knu.adapter.BookmarkAdapter
import com.ppcomp.knu.utils.PreferenceHelper
import kotlinx.android.synthetic.main.fragment_bookmark.*
import kotlinx.android.synthetic.main.fragment_bookmark.view.*
import java.time.LocalDate

/**
 * 즐겨찾기추가한 리스트를 보여주는 Fragment
 * @author 정준
 */
class BookmarkFragment : Fragment() {

    private var bookmarkList = arrayListOf<Notice>()
    private var gson: Gson = GsonBuilder().create()
    private var listType: TypeToken<ArrayList<Notice>> = object : TypeToken<ArrayList<Notice>>() {}
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private lateinit var noticeRecyclerView: RecyclerView
    private lateinit var thisContext: Context
    private lateinit var progressBar: ProgressBar
    private lateinit var noData: TextView
    private lateinit var searchIcon: ImageView
  
    @RequiresApi(Build.VERSION_CODES.O)
    val nowDate: LocalDate = LocalDate.now()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_bookmark, container, false)

        thisContext = container!!.context                                   //context 가져오기
        noticeRecyclerView = view!!.findViewById(R.id.bookmark_notice) as RecyclerView    //recyclerview 가져오기
        progressBar = view!!.findViewById((R.id.bookmark_progressbar)) as ProgressBar
        noData = view!!.findViewById((R.id.bookmark_null_view)) as TextView
        searchIcon = view!!.findViewById<ImageView>(R.id.search_icon)
        progressBar.visibility = View.GONE                                //progressbar 숨기기
        searchIcon.visibility = View.GONE

        val jsonList = PreferenceHelper.get("bookmark","[]")
        Log.d("bookmarkTest",jsonList)
        if(jsonList != "[]") {  //북마크리스트가 비어있지않으면
            noData.visibility = View.GONE
            noticeRecyclerView.visibility = View.VISIBLE    //recyclerView 출력
            bookmarkList = gson.fromJson(jsonList, listType.type) //북마크 리스트 저장
        }
        else {
            noData.visibility = View.VISIBLE    //텍스트 출력
            noticeRecyclerView.visibility = View.GONE
        }

        val bookmarkAdapter = BookmarkAdapter(thisContext, view, bookmarkList) { notice ->
            var link: String = notice.link
            if (!link.startsWith("http://") && !link.startsWith("https://"))
                link = "http://" + link
            val Intent: Intent = Uri.parse(link).let { webpage ->
                Intent(Intent.ACTION_VIEW, webpage)
            }
            startActivity(Intent)
        }

        noticeRecyclerView.adapter = bookmarkAdapter
        // LayoutManager 설정. RecyclerView 에서는 필수
        val lm = LinearLayoutManager(thisContext)
        noticeRecyclerView.layoutManager = lm
        noticeRecyclerView.setHasFixedSize(true)

        mHandler = Handler()
        view.bookmark_swipe.setOnRefreshListener {
            // Initialize a new Runnable
            mRunnable = Runnable {
//                 Hide swipe to refresh icon animation
                val bookmarkAdapter = BookmarkAdapter(
                    thisContext,
                    view,
                    bookmarkList
                ) { notice ->
                    var link: String = notice.link
                    if (!link.startsWith("http://") && !link.startsWith("https://"))
                        link = "http://" + link
                    val Intent: Intent = Uri.parse(link).let { webpage ->
                        Intent(Intent.ACTION_VIEW, webpage)
                    }
                    startActivity(Intent)
                }
                noticeRecyclerView.adapter = bookmarkAdapter
                bookmark_swipe.isRefreshing = false
            }
            mHandler.postDelayed(mRunnable, 2000)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}
