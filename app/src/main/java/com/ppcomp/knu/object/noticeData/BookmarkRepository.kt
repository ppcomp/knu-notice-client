package com.ppcomp.knu.`object`.noticeData

import androidx.paging.DataSource

/**
 * Dao에서 데이터를 가져올지 Network에서 데이터를 가져올지 선택하게 해주는 클래스
 * 현재는 Dao에서만 가져옴
 * @author 정준
 */
class BookmarkRepository(private val noticeDao: NoticeDao) {
    val factory: DataSource.Factory<Int, Notice> = noticeDao.getBookmark()    //DB에서 가져온 Paging 데이터소스 펙토리

    suspend fun insert(notice: Notice) {
        noticeDao.insertNotice(notice)
    }

    suspend fun delete(notice: Notice) {
        noticeDao.deleteNotice(notice)
    }
}