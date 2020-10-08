package com.ppcomp.knu.`object`.noticeData

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ppcomp.knu.utils.AppDatabase
import com.ppcomp.knu.utils.SharedPreferenceLiveData
import com.ppcomp.knu.utils.PreferenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoticeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoticeRepository
    private val noticeList: LiveData<PagedList<Notice>>
    private var query = ""
    private var target: SharedPreferenceLiveData<String>
    private val config = PagedList.Config.Builder()
        .setInitialLoadSizeHint(20)     // 초기 로딩 아이템 개수
        .setPageSize(10)                // 한 페이지에 로딩하는 아이템 개수
        .setPrefetchDistance(5)         // n개의 아이템 여유를 두고 로딩
        .setEnablePlaceholders(true)    // default: true
        .build()

    init {
        val noticeDao = AppDatabase.getInstance(application, viewModelScope).noticeDao()
        repository = NoticeRepository(noticeDao,viewModelScope)
        target  = PreferenceHelper.getLiveData("Urls", "")
        val str = target.getValueFromPreferences("Urls","")
        saveData(query)
        val pagedListBuilder: LivePagedListBuilder<Int, Notice> = LivePagedListBuilder<Int, Notice>(repository.factory, config)
        noticeList = pagedListBuilder.build()
    }

    fun getNoticeList() = noticeList

    fun getTarget() = target

    fun setQuery(q: String) {
        query = q
    }

    fun setValid() = viewModelScope.launch(Dispatchers.IO) {
        repository.validSet()
    }

    fun saveData(q: String, target: String = this.target.getValueFromPreferences("Urls","")) {
        repository.requestAndSaveData(q,target)
    }

//    private fun initPagedListBuilder(config: PagedList.Config):
//            LivePagedListBuilder<Int, Notice> {
//        val dataSourceFactory = object : DataSource.Factory<Int, Notice>() {
//            override fun create(): DataSource<Int, Notice> {
//                return NoticeAllDataSource(restApi, "")
//            }
//        }
//        return LivePagedListBuilder<Int, Notice>(dataSourceFactory, config)
//    }
}