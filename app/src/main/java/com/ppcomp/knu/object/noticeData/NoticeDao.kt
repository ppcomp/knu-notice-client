package com.ppcomp.knu.`object`.noticeData

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

/**
 * Notice Dao(data access object) interface
 * DB에 접근하기 위한 함수들을 선언
 * @author 정준
 */
@Dao
interface NoticeDao {
    @Query("SELECT * FROM notices")
    fun getAll(): LiveData<List<Notice>>

    @Query("DELETE FROM notices")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)    //OnConflictStrategy.REPLACE : 데이터 충돌이 나면 기존데이터를 입력데이터로 교체
    fun insertNotice(notice: Notice)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotice(notice: List<Notice>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateNotice(notice: Notice)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateNotice(notice: List<Notice>)

    @Delete
    fun deleteNotice(notice: Notice)

    @Delete
    fun deleteNotice(notice: List<Notice>)
}