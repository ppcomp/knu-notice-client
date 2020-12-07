package com.ppcomp.knu.adapter

import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.ppcomp.knu.`object`.noticeData.Notice
import com.ppcomp.knu.`object`.noticeData.NoticeViewModel
import kotlinx.android.synthetic.main.fragment_notice_item.view.*


/**
 * Adapter 리팩토링
 * @author 정우, 정준
 */
class NoticeAdapter(
    private val onClick: (Notice) -> Unit) : PagedListAdapter<Notice, NoticeViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Notice>() {
            override fun areItemsTheSame(oldItem: Notice, newItem: Notice): Boolean =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Notice, newItem: Notice): Boolean =
                oldItem.bookmark == newItem.bookmark
        }
    }

    private lateinit var bookmarkViewModel: NoticeViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        return NoticeViewHolder(parent, onClick)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val notice: Notice = getItem(position)!!

        // 공지리스트를 북마크리스트와 비교하여 공지리스트에 북마크 맵핑
        if(!bookmarkViewModel.isListNullOrEmpty()) {
            for( i in 0 until bookmarkViewModel.getNoticeList().value!!.size) {
                if(notice.id == bookmarkViewModel.getNoticeList().value!![i]!!.id)
                    notice.bookmark = bookmarkViewModel.getNoticeList().value!![i]!!.bookmark
            }
        }

        holder.bindTo(notice)
        holder.noticeBookmark.isChecked = notice.bookmark //북마크 레이아웃에 북마크 맵핑
        holder.noticeBookmark.setOnCheckedChangeListener {  //북마크 버튼 누를시
                _, isChecked ->
            notice.bookmark = isChecked
            if(isChecked) { //버튼이 눌려서 true
                bookmarkViewModel.insert(notice)   //DB에 아이템 추가
            } else {    //버튼이 눌려서 false
                bookmarkViewModel.delete(notice)   //DB에서 아이템 제거
            }
        }
        holder.itemView.setOnClickListener {
            onClick(notice)
            notice.isFixed = true
            holder.noticeImage.visibility = View.GONE
        }
    }

    fun setViewModel(viewModel: NoticeViewModel) {
        this.bookmarkViewModel = viewModel
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
