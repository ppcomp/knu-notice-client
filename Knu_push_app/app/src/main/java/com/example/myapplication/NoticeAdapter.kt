package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class NoticeAdapter(val context: Context, val noticeList: ArrayList<Notice>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        /* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view: View = LayoutInflater.from(context).inflate(R.layout.notice_item, null)

        /* 위에서 생성된 view를 res-layout-main_lv_item.xml 파일의 각 View와 연결하는 과정이다. */
        val noticeBid = view.findViewById<TextView>(R.id.bid)
        val noticeTitle = view.findViewById<TextView>(R.id.title)
        val noticeDate = view.findViewById<TextView>(R.id.date)
        val noticeAuthor = view.findViewById<TextView>(R.id.author)

        /* ArrayList<Dog>의 변수 dog의 이미지와 데이터를 ImageView와 TextView에 담는다. */
        val notice = noticeList[position]
        noticeBid.text = notice.bid
        noticeTitle.text = notice.title
        noticeDate.text = notice.date
        noticeAuthor.text = notice.author

        return view
    }
    override fun getItem(position: Int): Any {
        return noticeList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return noticeList.size
    }
}