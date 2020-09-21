package com.ppcomp.knu.`object`.noticeData

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ppcomp.knu.`object`.noticeData.dataSource.NoticeAllDataSource
import com.ppcomp.knu.utils.AppDatabase
import com.ppcomp.knu.utils.RestApi

class NoticeViewModel(application: Application) : AndroidViewModel(application) {
    private val restApi = RestApi.create()
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

    fun getNoticeList() = noticeList

    private fun initPagedListBuilder(config: PagedList.Config):
            LivePagedListBuilder<Int, Notice> {
        val dataSourceFactory = object : DataSource.Factory<Int, Notice>() {
            override fun create(): DataSource<Int, Notice> {
                return NoticeAllDataSource(restApi, "")
            }
        }
        return LivePagedListBuilder<Int, Notice>(dataSourceFactory, config)
    }
}