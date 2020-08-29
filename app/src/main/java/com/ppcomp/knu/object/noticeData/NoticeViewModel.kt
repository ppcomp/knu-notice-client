package com.ppcomp.knu.`object`.noticeData

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ppcomp.knu.utils.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Notice viewModel
 * @author 정준
 */
class NoticeViewModel(application: Application): AndroidViewModel(application) {

    private val repository: NoticeRepository
    val allNotices: LiveData<List<Notice>>

    init {
        val noticeDao = AppDatabase.getInstance(application, viewModelScope).noticeDao()
        repository = NoticeRepository(noticeDao)
        allNotices = repository.allNotices
    }

    fun insert(notice: Notice) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(notice)
    }
}