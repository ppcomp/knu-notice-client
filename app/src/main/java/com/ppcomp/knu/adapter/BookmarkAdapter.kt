package com.ppcomp.knu.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ToggleButton
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ppcomp.knu.GlobalApplication
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Notice
import com.ppcomp.knu.utils.PreferenceHelper

/**
 * BookmarkFragment의 RecyclerView를 위한 클래스
 * @author 정준
 */
class BookmarkAdapter(
    private val context: Context,
    private val fragmentView: View,
    private var bookmarkList: ArrayList<Notice>,
    private val itemClick: (Notice) -> Unit)

    : RecyclerView.Adapter<BookmarkAdapter.Holder>() {
    lateinit var bookmarkListJson: String
    private var gson: Gson = GsonBuilder().create()
    private var listType: TypeToken<ArrayList<Notice>> = object : TypeToken<ArrayList<Notice>>() {}
    private val bookmarkRecyclerView: RecyclerView = fragmentView.findViewById(R.id.bookmark_notice)
    private val bookmarkNullView: TextView = fragmentView.findViewById(R.id.bookmark_null_view)

    /**
     * Notice 객체를 RecyclerView에 맵핑해주는 클래스
     * @author 정준
     */
    inner class Holder(itemView: View, itemClick: (Notice) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val noticeLinear = itemView.findViewById<LinearLayout>(R.id.noticeLinear)
        private val noticeTitle = itemView.findViewById<TextView>(R.id.title)
        private val noticeBoard = itemView.findViewById<TextView>(R.id.board)
        private val noticeDate = itemView.findViewById<TextView>(R.id.date)
        private val noticeAuthor = itemView.findViewById<TextView>(R.id.author)
        private val noticeReference = itemView.findViewById<TextView>(R.id.reference)
        private val noticeImage = itemView.findViewById<ImageView>(R.id.image)
        private val noticeFixedImage = itemView.findViewById<ImageView>(R.id.fixed_image)
        val noticeBookmark = itemView.findViewById<ToggleButton>(R.id.toggle_bookmark)

        @RequiresApi(Build.VERSION_CODES.N)
        fun bind (notice: Notice, context: Context) {
            val spannedTitle = Html.fromHtml(notice.title, Html.FROM_HTML_MODE_LEGACY)
            noticeTitle.text = spannedTitle
            noticeBoard.text = notice.board
            noticeDate.text = notice.date
            noticeAuthor.text = notice.author
            noticeReference.text = notice.reference

            if(notice.image == 0) {
                noticeImage.setVisibility(View.GONE);
            }else {
                noticeImage.setImageResource(notice.image)
            }
            if(notice.fixed) {
                noticeFixedImage.setImageResource(notice.fixed_image)
                noticeLinear.setBackgroundResource(R.drawable.notice_fixed_item_line)
            }

            val hash = notice.board.hashCode()
            val r = (hash and 0xFF0000 shr 16)
            val g = (hash and 0x00FF00 shr 8)
            val b = (hash and 0x0000FF)
            val hsv = FloatArray(3)
            Color.colorToHSV(Color.rgb(r,g,b), hsv)
            hsv[1] += (100F-hsv[1])/2
            val color = Color.HSVToColor(hsv)
            noticeBoard.setTextColor(color)
            itemView.setOnClickListener { itemClick(notice) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.fragment_notice_item, parent, false)
        return Holder(view, itemClick)
    }

    /**
     * onCreateViewHolder 에서 만든 view 와 실제 입력되는 각각의 데이터 연결
     * @author 정준
     */
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(bookmarkList[position], context)
        holder.noticeBookmark.isChecked = bookmarkList[position].bookmark   //북마크 레이아웃에 북마크 맵핑
        holder.noticeBookmark.setOnCheckedChangeListener {  //북마크 버튼 누를시
                buttonView, isChecked ->
            bookmarkList[position].bookmark = isChecked
            if (!isChecked) { //북마크 체크해제시
                bookmarkList.remove(bookmarkList[position]) //북마크리스트에서 제거
                notifyItemRemoved(position)
                if(bookmarkList.isEmpty()) {    //북마크리스트가 비었으면 TextView 나오게 변경
                    bookmarkNullView.visibility = View.VISIBLE
                    bookmarkRecyclerView.visibility = View.GONE
                }
            }
            bookmarkListJson = gson.toJson(bookmarkList, listType.type)
            PreferenceHelper.put("bookmark",bookmarkListJson)
            GlobalApplication.isFragmentChange = arrayOf(true, true, true)  //북마크리스트 변경사항 확인
        }
    }

    /**
     * RecyclerView 로 만들어지는 item 의 총 개수 반환
     * @author 정준
     */
    override fun getItemCount(): Int {
        return bookmarkList.size
    }


    /**
     * 밑의 함수를 오버라이딩하여 리사이클러뷰 재사용 문제 해결
     * @author 정준
     */
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}



