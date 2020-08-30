package com.ppcomp.knu.adapter

import android.os.Build
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.ppcomp.knu.`object`.noticeData.Notice
import com.ppcomp.knu.`object`.noticeData.NoticeViewModel


/**
 * BookmarkFragment의 RecyclerView를 위한 클래스
 * @author 정준
 */
class BookmarkAdapter(
    private val bookmarkViewModel: NoticeViewModel,
    private val onClick: (Notice) -> Unit) : PagedListAdapter<Notice, NoticeViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Notice>() {
            override fun areItemsTheSame(oldItem: Notice, newItem: Notice): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Notice, newItem: Notice): Boolean =
                false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        return NoticeViewHolder(parent, onClick)
    }

    /**
     * onCreateViewHolder 에서 만든 view 와 실제 입력되는 각각의 데이터 연결
     * @author 정준
     */
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.bindTo(getItem(position)!!)
        holder.noticeBookmark.isChecked = getItem(position)!!.bookmark   //북마크 레이아웃에 북마크 맵핑
        holder.noticeBookmark.setOnCheckedChangeListener {  //북마크 버튼 누를시
                _, isChecked ->
            if (!isChecked) { //북마크 체크해제시
                bookmarkViewModel.delete(getItem(position)!!)
            }
        }
    }

}



