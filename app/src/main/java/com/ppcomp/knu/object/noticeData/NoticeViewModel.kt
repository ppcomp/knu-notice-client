package com.ppcomp.knu.`object`.noticeData

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ppcomp.knu.utils.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Notice viewModel
 * @author 정준
 */
class NoticeViewModel(application: Application): AndroidViewModel(application) {

    private val repository: NoticeRepository
    private val noticeList: LiveData<PagedList<Notice>>
    private val config = PagedList.Config.Builder()
        .setInitialLoadSizeHint(20)     // 초기 로딩 아이템 개수
        .setPageSize(10)                // 한 페이지에 로딩하는 아이템 개수
        .setPrefetchDistance(5)         // n개의 아이템 여유를 두고 로딩
        .setEnablePlaceholders(true)    // default: true
        .build()

    init {
        val noticeDao = AppDatabase.getInstance(application, viewModelScope).noticeDao()
        repository = NoticeRepository(noticeDao)
        val pagedListBuilder: LivePagedListBuilder<Int, Notice> = LivePagedListBuilder<Int, Notice>(repository.factory, config)
        noticeList = pagedListBuilder.build()
    }

    fun insert(notice: Notice) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(notice)
    }

    fun delete(notice: Notice) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(notice)
    }

    fun getNoticeList() = noticeList

    fun isListNullOrEmpty() = noticeList.value.isNullOrEmpty()
}