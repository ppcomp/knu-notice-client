package com.ppcomp.knu.`object`.NoticeData

import com.squareup.moshi.Json

/**
 * 서버로부터 받아오는 Notice 의 구조를 정의할 포맷.
 * @author 정우
 */
data class Notice (
    @Json(name = "id")
    var id: String,

    @Json(name = "title")
    var title: String? = null,

    @Json(name = "link")
    var link: String? = null,

    @Json(name = "date")
    var date: String? = null,

    @Json(name = "author")
    var author: String? = null,

    @Json(name = "reference")
    var reference: String? = null,

    @Json(name = "is_fixed")
    var isFixed: Boolean ) {

    var board: String = ""
    var image: Int = 0
    var fixedImage: Int = 0
    var bookmark: Boolean = false
}