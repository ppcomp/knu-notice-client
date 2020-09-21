package com.ppcomp.knu.`object`.noticeData

import androidx.paging.DataSource

class NoticeRepository(private val noticeDao: NoticeDao) {
    val factory: DataSource.Factory<Int, Notice> = noticeDao.getNotice()

    suspend fun insert(notice: Notice) {
        noticeDao.insertNotice(notice)
    }

    suspend fun delete(notice: Notice) {
        noticeDao.deleteNotice(notice)
    }
}