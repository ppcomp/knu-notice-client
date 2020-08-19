package com.ppcomp.knu.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
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

    var bookmarkList = arrayListOf<Notice>()
    var makeGson: Gson = GsonBuilder().create()
    var listType: TypeToken<ArrayList<Notice>> = object : TypeToken<ArrayList<Notice>>() {}
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private lateinit var noticeRecyclerView: RecyclerView
    private lateinit var thisContext: Context
    private lateinit var progressBar: ProgressBar
    private lateinit var noData: TextView

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
        progressBar.setVisibility(View.GONE)                                //progressbar 숨기기
        noData.setVisibility(View.GONE)

        bookmarkList = makeGson.fromJson(PreferenceHelper.get("bookmark",""),listType.type) //북마크 리스트 저장

        val bookmarkAdapter = BookmarkAdapter(thisContext, bookmarkList) { notice ->
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
