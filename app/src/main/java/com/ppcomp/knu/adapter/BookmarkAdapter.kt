package com.ppcomp.knu.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.Html
import android.util.Log
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
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Notice

/**
 * 즐겨찾기 리사이클뷰 어뎁터
 * @author 정준
 */
class BookmarkAdapter(
    val context: Context,
    var bookmarkList: ArrayList<Notice>,
    val itemClick: (Notice) -> Unit)
    : RecyclerView.Adapter<BookmarkAdapter.Holder>() {
    var makeGson: Gson = GsonBuilder().create()
    var listType: TypeToken<ArrayList<Notice>> = object : TypeToken<ArrayList<Notice>>() {}

    inner class Holder(itemView: View, itemClick: (Notice) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        val noticeLinear = itemView.findViewById<LinearLayout>(R.id.noticeLinear)
        val noticeTitle = itemView.findViewById<TextView>(R.id.title)
        val noticeBoard = itemView.findViewById<TextView>(R.id.board)
        val noticeDate = itemView.findViewById<TextView>(R.id.date)
        val noticeAuthor = itemView.findViewById<TextView>(R.id.author)
        val noticeReference = itemView.findViewById<TextView>(R.id.reference)
        val noticeImage = itemView.findViewById<ImageView>(R.id.image)
        val noticeFixedImage = itemView.findViewById<ImageView>(R.id.fixed_image)
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
     * @author jungwoo, 정준
     */
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val ed = pref.edit()

        bookmarkList = makeGson.fromJson(pref.getString("bookmark",""),listType)
        

        holder.bind(bookmarkList[position], context)

        holder.noticeBookmark.isChecked = bookmarkList.get(position).bookmark

        holder.noticeBookmark.setOnCheckedChangeListener {
                buttonView, isChecked ->
            bookmarkList.get(position).bookmark = isChecked
            Log.d("bookmark",bookmarkList.get(position).bookmark.toString())
        }
    }

    /**
     * RecyclerView 로 만들어지는 item 의 총 개수 반환
     * @author jungwoo, 정준
     */
    override fun getItemCount(): Int {
        return bookmarkList.size
    }


    /**
     * 밑의 함수를 오버라이딩하여 리사이클러뷰 재사용 문제 해결
     * @author 상은
     */
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}


