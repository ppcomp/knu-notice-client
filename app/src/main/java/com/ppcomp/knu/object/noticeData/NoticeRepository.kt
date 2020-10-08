package com.ppcomp.knu.`object`.noticeData

import android.annotation.SuppressLint
import androidx.paging.DataSource
import com.ppcomp.knu.utils.RestApi
import kotlinx.coroutines.CoroutineScope
import com.ppcomp.knu.`object`.noticeData.DataUtils.Companion.injectDataToNotices
import com.ppcomp.knu.`object`.noticeData.DataUtils.Companion.validateData

class NoticeRepository(
    private val noticeDao: NoticeDao,
    scope: CoroutineScope) {
    val factory: DataSource.Factory<Int, Notice> = noticeDao.getNotice()
    private val restApi = RestApi.create()
    private val cache: NoticeLocalCache = NoticeLocalCache(noticeDao,scope)
    private var lastRequestedPage = 1
    private var isRequestInProgress = true

    @SuppressLint("CheckResult")
    fun requestAndSaveData(query: String, target: String){
        restApi.getNoticeAll(query,target,lastRequestedPage)
            .subscribe { it ->
                if (it.next == null)
                    it.results?.let { noticeList: List<Notice> ->
                        val wrappingList = injectDataToNotices(noticeList)
                        cache.insert(wrappingList)
                        isRequestInProgress = false
                    }
                else
                    it.results?.let { noticeList: List<Notice> ->
                        val wrappingList = injectDataToNotices(noticeList)
                        cache.insert(wrappingList)
                        lastRequestedPage++
                    }
            }
    }
    suspend fun insert(notice: Notice) {
        noticeDao.insertNotice(notice)
    }

    suspend fun delete(notice: Notice) {
        noticeDao.deleteNotice(notice)
    }

    suspend fun validSet() {
        val list = validateData(noticeDao.getAll())
        noticeDao.updateNotice(list)
    }
}