package com.ppcomp.knu.`object`.noticeData

import androidx.lifecycle.LiveData

/**
 * Dao에서 데이터를 가져올지 Network에서 데이터를 가져올지 선택하게 해주는 클래스
 * 현재는 Dao에서만 가져옴
 * @author 정준
 */
class NoticeRepository(private val noticeDao: NoticeDao) {
    val allNotices: LiveData<List<Notice>> = noticeDao.getAll()

    suspend fun insert(notice: Notice) {
        noticeDao.insertNotice(notice)
    }
}