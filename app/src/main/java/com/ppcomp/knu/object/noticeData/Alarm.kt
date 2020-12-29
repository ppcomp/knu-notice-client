package com.ppcomp.knu.`object`.noticeData

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

/**
 * 서버로부터 받아오는 Notice 의 구조(매개변수) 및
 * DB에 저장할 Notice 의 구조(매개변수 + 클래스 내부 변수)를 정의
 * @author 정우, 정준
 */
@Parcelize
@Entity(tableName = "notices")
data class Alarm(
    @PrimaryKey
    @Json(name = "id")
    var id: String,
//    @Json(name = "title")
//    var title: String? = null,
//
//    @Json(name = "link")
//    var link: String? = null,
//
    @Json(name = "date")
    var date: String? = null,

    var color: Int = 0) : Parcelable