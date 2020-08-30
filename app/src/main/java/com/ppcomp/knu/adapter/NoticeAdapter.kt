package com.ppcomp.knu.adapter

import android.os.Build
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.ppcomp.knu.`object`.noticeData.Notice
import com.ppcomp.knu.`object`.noticeData.NoticeViewModel


/**
 * Adapter 리팩토링
 * @author 정우
 */
class NoticeAdapter(
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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        // 공지리스트를 북마크리스트와 비교하여 공지리스트에 북마크 맵핑 (동작안함)
        if(!bookmarkViewModel.isListNullOrEmpty()) {
            for( i in 0 until bookmarkViewModel.getNoticeList().value!!.size) {
                if(getItem(position)!!.id == bookmarkViewModel.getNoticeList().value!![i]!!.id)
                    getItem(position)!!.bookmark = bookmarkViewModel.getNoticeList().value!![i]!!.bookmark
            }
        }

        holder.bindTo(getItem(position))
        holder.noticeBookmark.isChecked = getItem(position)!!.bookmark //북마크 레이아웃에 북마크 맵핑
        holder.noticeBookmark.setOnCheckedChangeListener {  //북마크 버튼 누를시
                _, isChecked ->
            getItem(position)!!.bookmark = isChecked
            if(isChecked) { //버튼이 눌려서 true
                bookmarkViewModel.insert(getItem(position)!!)
            } else {    //버튼이 눌려서 false
                bookmarkViewModel.delete(getItem(position)!!)
            }
        }
    }
}
