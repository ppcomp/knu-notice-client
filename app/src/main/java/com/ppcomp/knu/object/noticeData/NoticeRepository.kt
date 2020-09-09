package com.ppcomp.knu.`object`.noticeData

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ppcomp.knu.utils.AppDatabase

/**
 * Dao에서 데이터를 가져올지 Network에서 데이터를 가져올지 선택하게 해주는 클래스
 * 현재는 Dao에서만 가져옴
 * @author 정준
 */
class NoticeRepository(private val noticeDao: NoticeDao) {
    val factory: DataSource.Factory<Int, Notice> = noticeDao.getDataSource()    //DB에서 가져온 Paging 데이터소스 펙토리

    suspend fun insert(notice: Notice) {
        noticeDao.insertNotice(notice)
    }

    suspend fun delete(notice: Notice) {
        noticeDao.deleteNotice(notice)
    }
}