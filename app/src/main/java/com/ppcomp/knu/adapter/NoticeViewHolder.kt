package com.ppcomp.knu.adapter

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
import com.ppcomp.knu.R
import com.ppcomp.knu.`object`.noticeData.Notice

/**
 * RecyclerView 의 각 요소에 데이터(Notice)를 binding 한다.
 * @author 정우
 */
class NoticeViewHolder(parent: ViewGroup, private val onClick: (Notice) -> Unit) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.fragment_notice_item, parent, false)
) {

    val noticeLinear = itemView.findViewById<LinearLayout>(R.id.noticeLinear)
    val noticeTitle = itemView.findViewById<TextView>(R.id.title)
    val noticeBoard = itemView.findViewById<TextView>(R.id.board)
    val noticeDate = itemView.findViewById<TextView>(R.id.date)
    val noticeAuthor = itemView.findViewById<TextView>(R.id.author)
    val noticeReference = itemView.findViewById<TextView>(R.id.reference)
    val noticeImage = itemView.findViewById<ImageView>(R.id.image)
    val noticeFixedImage = itemView.findViewById<ImageView>(R.id.fixed_image)
    val noticeBookmark = itemView.findViewById<ToggleButton>(R.id.toggle_bookmark)
    val factor: Float = itemView.context.resources.displayMetrics.density

    @RequiresApi(Build.VERSION_CODES.N)
    fun bindTo(notice: Notice?) {
        val spannedTitle = Html.fromHtml(notice?.title, Html.FROM_HTML_MODE_LEGACY)
        noticeTitle.text = spannedTitle
        noticeBoard.text = notice?.board
        noticeDate.text = notice?.date
        noticeAuthor.text = notice?.author
        noticeReference.text = notice?.reference

        if(notice?.image != 0) {
            noticeImage.visibility = View.VISIBLE
            noticeImage.setImageResource(notice?.image!!)
        } else {
            noticeImage.visibility = View.GONE
        }
        if(notice.isFixed) {
            noticeFixedImage.visibility = View.VISIBLE
            noticeFixedImage.layoutParams.height = (20*factor).toInt()
            noticeFixedImage.layoutParams.width = (20*factor).toInt()
            noticeFixedImage.setImageResource(notice.fixedImage)
            noticeLinear.setBackgroundResource(R.drawable.notice_fixed_item_line)
        } else {
            noticeFixedImage.visibility = View.GONE
        }

        noticeBoard.setTextColor(notice.color)
        itemView.setOnClickListener { onClick(notice) }
    }
}