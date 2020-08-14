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
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.Notice

class SearchAdapter(
    val context: Context,               // MainActivity
    val searchList: ArrayList<Notice>,  // Notice 객체 list
    val itemClick: (Notice) -> Unit)    // Notice 객체 클릭시 실행되는 lambda 식
    : RecyclerView.Adapter<SearchAdapter.Holder>() {

    /**
     * 각 Notice 객체를 감싸는 Holder
     * bind 가 자동 호출되며 데이터가 매핑된다.
     * @author jungwoo
     */
    inner class Holder(itemView: View, itemClick: (Notice) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        val noticeLinear = itemView.findViewById<LinearLayout>(R.id.search_Linear)
        val noticeTitle = itemView.findViewById<TextView>(R.id.search_title)
        val noticeBoard = itemView.findViewById<TextView>(R.id.search_board)
        val noticeDate = itemView.findViewById<TextView>(R.id.search_date)
        val noticeAuthor = itemView.findViewById<TextView>(R.id.search_author)
        val noticeReference = itemView.findViewById<TextView>(R.id.search_reference)
        val noticeImage = itemView.findViewById<ImageView>(R.id.search_image)
        val noticeFixedImage = itemView.findViewById<ImageView>(R.id.search_fixed_image)


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
            if(notice.fixed)
            {
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

    /**
     * 화면을 최초로 로딩하여 만들어진 View 가 없는 경우, xml 파일을 inflate 하여 ViewHolder 생성
     * @author jungwoo
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        /* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.fragment_search_item, parent, false)
        return Holder(view, itemClick)
    }

    /**
     * onCreateViewHolder 에서 만든 view 와 실제 입력되는 각각의 데이터 연결
     * @author jungwoo
     */
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(searchList[position], context)
    }

    /**
     * RecyclerView 로 만들어지는 item 의 총 개수 반환
     * @author jungwoo
     */
    override fun getItemCount(): Int {
        return searchList.size
    }

    fun clear() {
        val size: Int = searchList.size
        searchList.clear()
        notifyItemRangeRemoved(0, size)
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