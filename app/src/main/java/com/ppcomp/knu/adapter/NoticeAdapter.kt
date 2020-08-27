package com.ppcomp.knu.adapter

import android.os.Build
import android.view.ViewGroup
import androidx.annotation.RequiresApi

import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ppcomp.knu.GlobalApplication
import com.ppcomp.knu.`object`.noticeData.Notice
import com.ppcomp.knu.utils.PreferenceHelper

/**
 * Adapter 리팩토링
 * @author 정우
 */
class NoticeAdapter(
    private val bookmarkList: ArrayList<Notice>,
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
        // 공지리스트를 북마크리스트와 비교하여 공지리스트에 북마크 맵핑
        for (i in 0 until bookmarkList.size) {
            if (getItem(position)!!.link == bookmarkList[i].link)
                getItem(position)!!.bookmark = bookmarkList[i].bookmark
        }

        holder.bindTo(getItem(position))
        holder.noticeBookmark.isChecked = getItem(position)!!.bookmark //북마크 레이아웃에 북마크 맵핑
        holder.noticeBookmark.setOnCheckedChangeListener {  //북마크 버튼 누를시
                _, isChecked ->
            getItem(position)!!.bookmark = isChecked
            if(isChecked) { //버튼이 눌려서 true
                bookmarkList.add(getItem(position)!!)  //북마크 리스트에 추가
            } else {    //버튼이 눌려서 false
                for(i in 0 until bookmarkList.size) {
                    if(getItem(position)!!.link == bookmarkList[i].link) { //북마크리스트에 버튼이 눌린 공지가 있으면 리스트에서 제거
                        bookmarkList.remove(bookmarkList[i])
                        break
                    }
                }
            }
            val gson: Gson = GsonBuilder().create()
            val listType: TypeToken<ArrayList<Notice>> = object : TypeToken<ArrayList<Notice>>() {}
            val bookmarkListJson = gson.toJson(bookmarkList, listType.type)
            PreferenceHelper.put("bookmark",bookmarkListJson)
            GlobalApplication.isFragmentChange = arrayOf(true, true, true)  //북마크리스트 변경사항 확인
        }
    }
}
