package com.ppcomp.knu.`object`.noticeData

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoticeLocalCache(
    private val noticeDao: NoticeDao,
    private val scope: CoroutineScope) {

    fun insert(noticeList: List<Notice>) = scope.launch(Dispatchers.IO) {
        noticeDao.insertNotice(noticeList)
    }

}